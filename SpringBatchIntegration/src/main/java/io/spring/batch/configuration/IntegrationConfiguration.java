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

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.integration.twitter.inbound.TimelineReceivingMessageSource;
import org.springframework.integration.twitter.outbound.StatusUpdatingMessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

/**
 * @author mminella
 */
@Component
public class IntegrationConfiguration {

	@Value("${twitter.oauth.consumerKey}")
	private String consumerKey;

	@Value("${twitter.oauth.consumerSecret}")
	private String consumerSecret;

	@Value("${twitter.oauth.accessToken}")
	private String accessToken;

	@Value("${twitter.oauth.accessTokenSecret}")
	private String accessTokenSecret;

	@Bean
	public TwitterTemplate  twitterTemplate() {
		return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
	}

	@Bean
	@InboundChannelAdapter(value = "twitterChannel", poller = @Poller(fixedRate = "60000"))
	public TimelineReceivingMessageSource inboundChannelAdapter() throws Exception {
		TimelineReceivingMessageSource source = new TimelineReceivingMessageSource(twitterTemplate(), "twitter");

		return source;
	}

	@Bean
	protected DirectChannel twitterChannel() {
		return new DirectChannel();
	}

	@Bean
	protected DirectChannel jobTweets() {
		return new DirectChannel();
	}

//	@Bean
//	protected DirectChannel jobChannel() {
//		return new DirectChannel();
//	}
//
//	@Bean
//	protected DirectChannel jobRequests() {
//		return new DirectChannel();
//	}

	@Bean
	protected DirectChannel statusTweets() {
		DirectChannel channel = new DirectChannel();
		channel.setComponentName("statusTweets");

		return channel;
	}

	@Bean
	protected PropertiesPersistingMetadataStore metadataStore() throws Exception {
		PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();
		store.setBaseDirectory("/tmp/someDirectory");
		store.afterPropertiesSet();

		return store;
	}

	@Bean
	@ServiceActivator(inputChannel = "jobChannel", outputChannel = "nullChannel")
	protected JobLaunchingMessageHandler launcher(JobLauncher jobLauncher) {
		return new JobLaunchingMessageHandler(jobLauncher);
	}

	@Bean
	protected StatusUpdatingMessageHandler statusHandler() {
		return new StatusUpdatingMessageHandler(twitterTemplate());
	}

	@Bean
	protected EventDrivenConsumer statusConsumer(SubscribableChannel statusTweets) {
		return new EventDrivenConsumer(statusTweets, statusHandler());
	}
}
