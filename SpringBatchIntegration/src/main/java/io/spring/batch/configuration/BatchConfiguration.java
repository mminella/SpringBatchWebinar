/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.configuration;

import io.spring.batch.domain.Customer;
import io.spring.batch.listener.JobStatusListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import javax.sql.DataSource;

/**
 * @author mminella
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	protected FlatFileItemReader<Customer> reader(@Value("#{jobParameters['fileName']}") Resource fileName) throws Exception {
		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();
		defaultLineMapper.setLineTokenizer(new DelimitedLineTokenizer());
		defaultLineMapper.setFieldSetMapper(new FieldSetMapper<Customer>() {
			@Override
			public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
				Customer cust = new Customer();

				cust.setCustomerName(fieldSet.readString(0));
				cust.setQty(fieldSet.readInt(1));

				return cust;
			}
		});

		defaultLineMapper.afterPropertiesSet();

		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		reader.setLineMapper(defaultLineMapper);
		reader.setResource(fileName);
		reader.afterPropertiesSet();

		return reader;
	}

	@Bean
	protected JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource);
		writer.setSql("INSERT INTO CUSTOMER VALUES(:customerName, :qty)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>());
		writer.afterPropertiesSet();

		return writer;
	}

	@Bean
	protected Step step(DataSource dataSource) throws Exception {
		return stepBuilderFactory.get("step1")
					   .<Customer, Customer>chunk(5)
					   .reader(reader(null))
					   .writer(writer(dataSource))
					   .build();
	}

	@Bean
	protected Job fileToDatabase(Step step1, JobStatusListener listener) {
		return jobBuilderFactory.get("fileToDatabase").listener(listener).start(step1).build();
	}
}