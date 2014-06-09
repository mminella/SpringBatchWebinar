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

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mminella
 */
public class FileItemReader implements ItemReader {

	@Inject
	@BatchProperty
	private String fileName;

	private BufferedReader reader;

	private long count;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		assert fileName != null && fileName.length() > 0;

		reader = new BufferedReader(new FileReader(new File(fileName)));

		if(checkpoint != null) {
			Long oldCount = (Long) ((Map) checkpoint).get("recordCount");

			if(oldCount != null) {
				long curCount = oldCount.longValue();

				for(long i = 0; i < curCount; i++) {
					reader.readLine();
				}

				count = curCount;
			}
		}
	}

	@Override
	public void close() throws Exception {
		if(reader != null) {
			reader.close();
		}
	}

	@Override
	public Object readItem() throws Exception {
		String line = reader.readLine();
		System.out.println("read line: " + line);

		if(line != null) {
			String[] fields = line.split(",");

			Customer cust = new Customer();
			cust.setCustomerName(fields[0]);
			cust.setQty(Integer.parseInt(fields[1]));

			return cust;
		} else {
			return null;
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		HashMap<String, Object> checkpoint = new HashMap<String, Object>();
		checkpoint.put("recordCount", count);

		return checkpoint;
	}
}
