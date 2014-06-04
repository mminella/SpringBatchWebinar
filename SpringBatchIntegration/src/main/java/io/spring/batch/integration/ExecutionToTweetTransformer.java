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
package io.spring.batch.integration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.integration.annotation.Transformer;
import org.springframework.social.twitter.api.TweetData;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author mminella
 */
@Component
public class ExecutionToTweetTransformer {

	@Transformer(inputChannel = "jobRequests", outputChannel = "statusTweets")
	public TweetData transform(JobExecution execution) {
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SS");
		StringBuilder builder = new StringBuilder();

		builder.append(execution.getJobInstance().getJobName());

		BatchStatus evaluatedStatus = endingBatchStatus(execution);
		if(evaluatedStatus == BatchStatus.COMPLETED || evaluatedStatus.compareTo(BatchStatus.STARTED) > 0) {
			builder.append(" has completed with a status of " + execution.getStatus().name() + " at " + formatter.format(new Date()));
		} else {
			builder.append(" has started at " + formatter.format(new Date()));
		}

		return new TweetData(builder.toString());
	}

	private BatchStatus endingBatchStatus(JobExecution execution) {
		BatchStatus status = execution.getStatus();
		Collection<StepExecution> stepExecutions = execution.getStepExecutions();

		if(stepExecutions.size() > 0) {
			for (StepExecution stepExecution : stepExecutions) {
				if(stepExecution.getStatus().equals(BatchStatus.FAILED)) {
					status = BatchStatus.FAILED;
					break;
				} else {
					status = BatchStatus.COMPLETED;
				}
			}
		}

		return status;
	}
}
