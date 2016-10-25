/*
 * Copyright 2010-2012 Samsung SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sds.anyframe.batch.agent.state.job.pattern;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.TimeOutException;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.properties.AgentConfigurations;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.agent.util.TimeUnits;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateWaiting extends AbstractJobState {

	private final Logger log = Logger.getLogger(getClass());

	private static final int _MYSELF = 1;
	
	private AgentConfigurations configurations = AgentConfigurations
			.getConfigurations();
	

	public JobStateWaiting() {
		super(STATES.WAITING);
	}

	@Override
	public Job startJob(Job job) throws IllegalStateException {
		throw new UnsupportedOperationException(JobStateWaiting.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void waitJobToExecute(Job job) throws Exception {
		throwIllegalStateExceptionIfTheSameJobIsAlreadyRunning(job);
		
		waitOnExecutionOrder(job);

		//Double check.
		throwIllegalStateExceptionIfTheJobAlreadyHasBeenStopped(job);
		
		job.setJobStatus(JobStatus.READY);
		jobDao.updateJobAsReady(job.getJobSeq());
		log.info("Job Status(READY) -  " + AgentUtils.toJobString(job));
	}


	@Override
	public void jobExecuted(Job job) throws TimeOutException {
		throw new UnsupportedOperationException(JobStateWaiting.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void completedJob(Job job) throws Exception {
		throw new UnsupportedOperationException(JobStateWaiting.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");

	}

	@Override
	public void stopJob(Job job) throws Exception {
		super.stopJob(job);
	}

	@Override
	public void failJob(Job job) {
		super.failJob(job);
	}

	private void waitOnExecutionOrder(Job job) throws Exception {
		int order = jobDao.getRunningJobCount(job);

		long wait_start = System.currentTimeMillis();

		while (order > AgentManagement.getAgentCondition().getClusteredCondition().getJobExecutionLimits()) {
			log.info("Job waiting, my execution order is " + order + " of "
					+ AgentManagement.getAgentCondition().getClusteredCondition().getJobExecutionLimits() + " , "
					+ AgentUtils.toJobString(job));
			
			TimeUnit.SECONDS.sleep(configurations.getJobLimitCheckInterval());

			if ((order = jobDao.getRunningJobCount(job)) < AgentManagement.getAgentCondition().getClusteredCondition().getJobExecutionLimits()) {
				break;
			}

			throwIllegalStateExceptionIfTheJobAlreadyHasBeenStopped(job);
			
			if (System.currentTimeMillis() - wait_start > (configurations
					.getJobExecutionOrderWaitingTimeout())) {
				String msg = "Job execution order waiting timeout exception occured("
						+ (configurations.getJobExecutionOrderWaitingTimeout() / TimeUnits.HOURS)
						+ " hours)";

				log.error(msg);
				throw new TimeOutException(msg);
			}
		}
	}

	private void throwIllegalStateExceptionIfTheSameJobIsAlreadyRunning(Job job) {
		if(!PropertyProvider.jobFailOnSameJobRunning)
			return;
		
		if(job.isAllowConcurrentRunning()) {
			log.info("This job has been requested with the option 'alow concurrent running'[" + AgentUtils.toJobString(job) + "]");
			return;
		}
		
		List<Job> jobs = jobDao.getSameJob(job.getJobId());

		if (jobs.size() > _MYSELF)
			throw new IllegalStateException(
					"The same job which has the same job id in the domain is already running["
					+ AgentUtils.toJobString(job) + "]");
	}
	
	private void throwIllegalStateExceptionIfTheJobAlreadyHasBeenStopped(Job job) {
		JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
		if (jobStatus != JobStatus.WAITING) {
			job.setJobStatus(jobStatus);
			throw new IllegalStateException(
					"This job has been changed while waiting for execution order["
					+ AgentUtils.toJobString(job) + "]");
		}
	}
}
