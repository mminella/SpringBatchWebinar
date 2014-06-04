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

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.annotation.Transformer;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

/**
 * @author mminella
 */
@Component
public class TweetToJobTransformer implements ApplicationContextAware {

	private ApplicationContext context;

	@Transformer(inputChannel = "jobTweets", outputChannel = "jobChannel")
	public JobLaunchRequest transform(Tweet tweet) {

		System.out.println("Creating request");

		String[] tweetParams = tweet.getText().split(" ");
		Job job = (Job) context.getBean(tweetParams[0]);

		System.out.println("Job = " + job.getName());

		JobParametersBuilder paramsBuilder = new JobParametersBuilder();

		for(int i = 1; i < tweetParams.length; i++) {
			String [] param = tweetParams[1].split("=");
			paramsBuilder.addString(param[0], param[1]);
		}

		System.out.println("Parameters = " + paramsBuilder.toString());

		JobLaunchRequest request = new JobLaunchRequest(job, paramsBuilder.toJobParameters());

		return request;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
