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
package io.spring.batch.jsr.reader;

import io.spring.batch.jsr.domain.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * @author mminella
 */
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {
	@Override
	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
		Customer cust = new Customer();

		cust.setCustomerName(fieldSet.readString(0));
		cust.setQty(fieldSet.readInt(1));

		return cust;
	}
}
