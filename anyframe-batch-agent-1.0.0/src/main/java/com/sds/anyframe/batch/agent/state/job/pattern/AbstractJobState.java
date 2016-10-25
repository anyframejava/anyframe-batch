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

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.bean.BeanFactory;
import com.sds.anyframe.batch.agent.dao.IJobDao;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.state.job.IJobState;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public abstract class AbstractJobState implements IJobState {
	private final Logger log = Logger.getLogger(getClass());
	
	protected IJobDao jobDao;
	
	protected STATES state;
	
	public AbstractJobState(STATES state) {
		this.state = state;
		jobDao = (IJobDao)BeanFactory.getBean(BeanConstants.JOB_DAO);
	}
	
	
	public Job startJob(Job job) throws Exception {
		throw new UnsupportedOperationException();
	}

	
	public void waitJobToExecute(Job job) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	
	public void jobExecuted(Job job) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	
	public void completedJob(Job job) throws Exception {
		throw new UnsupportedOperationException();	
	}
	
	
	public void stopJob(Job job) throws Exception {
		jobDao.updateJobAsStopped(job.getJobSeq());
		job.setJobStatus(JobStatus.STOPPED);
		log.info("Job Status(STOPPED) - " + AgentUtils.toJobString(job));
	}

	
	public void failJob(Job job) {
		job.setJobStatus(JobStatus.FAILED);
		jobDao.updateJobAsFailed(job.getJobSeq(), job.getExitMessage());
		log.info("Job Status(FAILED) - " + AgentUtils.toJobString(job));
	}

	
	public STATES getState() {
		return state;
	}
	
}
