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
package io.spring.batch.jsr.writer;

import io.spring.batch.jsr.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author mminella
 */
public class JdbcItemWriter extends AbstractItemWriter {

	private static final String SQL = "INSERT INTO CUSTOMER VALUES(?, ?)";

	@Autowired
	private DataSource dataSource;
	private Connection connection;
	private PreparedStatement preparedStatement;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		connection = dataSource.getConnection();
		preparedStatement = connection.prepareStatement(SQL);
	}

	@Override
	public void close() throws Exception {
		if(preparedStatement != null) {
			preparedStatement.close();
		}

		if(connection != null) {
			connection.close();
		}
	}

	@Override
	public void writeItems(List<Object> items) throws Exception {
		for (Object item : items) {
			Customer customer = (Customer) item;

			preparedStatement.setString(1, customer.getCustomerName());
			preparedStatement.setInt(2, customer.getQty());

			preparedStatement.addBatch();
		}

		preparedStatement.executeBatch();
	}
}
