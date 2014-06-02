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
package io.spring.batch.jsr.listener;

import org.springframework.beans.factory.annotation.Autowired;

import javax.batch.api.BatchProperty;
import javax.batch.api.listener.AbstractJobListener;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @author mminella
 */
public class DatabaseIntializer extends AbstractJobListener {

	@Autowired
	private DataSource dataSource;

	@Inject
	@BatchProperty
	private String scriptPath;

	@Override
	public void beforeJob() throws Exception {

		InputStream resourceAsStream = this.getClass().getResourceAsStream(scriptPath);

		BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

		Connection con = dataSource.getConnection();
		Statement stmt = con.createStatement();

		String sql = reader.readLine();

		while(sql != null) {
			stmt.execute(sql);
			sql = reader.readLine();
		}

		stmt.close();
		con.close();
	}
}
