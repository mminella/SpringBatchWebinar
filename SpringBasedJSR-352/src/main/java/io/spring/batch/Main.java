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
package io.spring.batch;

import org.springframework.util.Assert;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.util.List;
import java.util.Properties;

/**
 * @author Michael Minella
 */
public class Main {

	private static final JobOperator operator = BatchRuntime.getJobOperator();

	/**
	 * Arguements:
	 *
	 * <ul>
	 *     <li>jobName - The name of the job as required by the {@link javax.batch.operations.JobOperator}</li>
	 *     <li>job parameters - A list of key=value pairs to be passed to the job as job parameters</li>
	 * </ul>
	 *
	 * @param args arguments used to execute the job
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Assert.notEmpty(args);
		Properties props = parseProperties(args);

		long executionId = operator.start(args[0], props);

		while(!isItDoneYet(executionId)) {
			Thread.sleep(1000);
		}

		reportResults(executionId);
	}

	private static boolean isItDoneYet(long executionId) {
		BatchStatus batchStatus = operator.getJobExecution(executionId).getBatchStatus();
		return batchStatus.compareTo(BatchStatus.STOPPED) > 0;
	}

	private static Properties parseProperties(String[] args) {
		Properties props = new Properties();

		if(args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				String [] prop = args[i].split("=");
				props.setProperty(prop[0], prop[1]);
			}
		}
		return props;
	}

	private static void reportResults(long executionId) {
		JobExecution jobExecution = operator.getJobExecution(executionId);
		List<StepExecution> stepExecutions = operator.getStepExecutions(jobExecution.getExecutionId());

		System.out.println("***********************************************************");
		System.out.println(String.format("%s finished with a status of %s.", jobExecution.getJobName(), jobExecution.getBatchStatus()) );
		System.out.println("Steps executed:");
		for (StepExecution stepExecution : stepExecutions) {
			System.out.println(String.format("\t%s : %s" , stepExecution.getStepName(), stepExecution.getBatchStatus()));

			for (Metric metric : stepExecution.getMetrics()) {
				System.out.println(String.format("\t\t%s : %s", metric.getType(), metric.getValue()));
			}
		}
		System.out.println("***********************************************************");

	}
}
