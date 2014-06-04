package io.spring.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * @author mminella
 */
@MessagingGateway(name="jobListenerGateway")
public interface JobStatusListener extends JobExecutionListener {

	@Override
	@Gateway(requestChannel = "jobRequests")
	void beforeJob(JobExecution jobExecution);

	@Override
	@Gateway(requestChannel = "jobRequests")
	void afterJob(JobExecution jobExecution);
}
