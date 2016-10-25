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

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.TimeOutException;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.properties.AgentConfigurations;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateBlocking extends AbstractJobState {

	public JobStateBlocking() {
		super(STATES.BLOCKING);
	}

	private final Logger log = Logger.getLogger(getClass());

	private AgentConfigurations configurations = AgentConfigurations.getConfigurations();

	@Override
	public Job startJob(Job job) throws IllegalStateException {
		throw new UnsupportedOperationException(JobStateBlocking.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void waitJobToExecute(Job job) throws Exception {
		waitOnBlocking(job);
	}

	@Override
	public void jobExecuted(Job job) throws TimeOutException {
		throw new UnsupportedOperationException(JobStateBlocking.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void completedJob(Job job) throws Exception {
		throw new UnsupportedOperationException(JobStateBlocking.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void stopJob(Job job)
			throws Exception {
		super.stopJob(job);
	}

	@Override
	public void failJob(Job job) {
		super.failJob(job);
	}
	
	private void waitOnBlocking(Job job) throws TimeOutException {
		long started = System.currentTimeMillis();
		
		while(AgentManagement.isBlocking()) {
			
			try {
				TimeUnit.SECONDS.sleep(configurations.getBlockingCheckInterval());
			} catch (InterruptedException e) {
				log.error(e);
			}
			
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			
			// Unblocked by Batch Manager.
			if (jobStatus == JobStatus.WAITING)
				return;
			
			throwIllegalStateExceptionIfTheJobArealyHasBeenStopped(job, jobStatus);
			
			log.info("Job is waiting for blocking[" + AgentUtils.toJobString(job));
			
			if (System.currentTimeMillis() - started > (configurations.getBlockingTimeout()/* 2 hours by default*/)) {
				String msg = "Blocking timeout exception occured(2 hours)";
				log.error(msg);
				throw new TimeOutException(msg);
			}
		}

		//Double check.
		JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
		throwIllegalStateExceptionIfTheJobArealyHasBeenStopped(job, jobStatus);
		
		job.setJobStatus(JobStatus.WAITING);
		jobDao.unBlockJob(job.getJobSeq());
		log.info("Job Status(WAITING) -  " + AgentUtils.toJobString(job));
	}

	private void throwIllegalStateExceptionIfTheJobArealyHasBeenStopped(
			Job job, JobStatus jobStatus) {
		//Stopped by administrator.
		if (jobStatus != JobStatus.BLOCKING) {
			job.setJobStatus(jobStatus);
			throw new IllegalStateException(
					"This job has been changed while blocking["
							+ AgentUtils.toJobString(job) + "]");
		}
	}

}
