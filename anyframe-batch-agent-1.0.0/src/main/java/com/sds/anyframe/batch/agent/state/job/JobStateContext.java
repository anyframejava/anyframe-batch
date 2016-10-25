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
package com.sds.anyframe.batch.agent.state.job;

import java.util.Map;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.cluster.BatchAgent;
import com.sds.anyframe.batch.agent.cluster.KillMessage;
import com.sds.anyframe.batch.agent.cluster.MessageManager;
import com.sds.anyframe.batch.agent.dao.IJobDao;
import com.sds.anyframe.batch.agent.exception.NotAllowedOperatorException;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.state.job.IJobState.STATES;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.agent.utils.BatchUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateContext implements IJobObservable {

	private final Logger log = Logger.getLogger(getClass());

	IJobObserver jobObserver;

	IJobDao jobDao;

	
	public void notifyObserver(Job job, STATES jobState) throws Exception {
		if (jobObserver != null)
			jobObserver.jobChanged(job, jobState);
	}

	
	public void setJobObserver(IJobObserver jobObserver) {
		this.jobObserver = jobObserver;
	}

	public Job startJob(Job job) throws Exception {
		try {
			IJobState jobState = JobStateFactory.getState(JobStatus.INITIAL);
			jobState.startJob(job);
			return job;
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void waitJobToExecute(Job job) throws Exception {
		try {
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			IJobState jobState = JobStateFactory.getState(jobStatus);
			
			jobState.waitJobToExecute(job);

			if (jobState.getState() == STATES.BLOCKING) {
				//Do change STATE to WAITING from BLOCKING in this method in the case of BLOCKING state.
				jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
				jobState = JobStateFactory.getState(jobStatus);
				
				jobState.waitJobToExecute(job);
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void jobExecuted(Job job) throws Exception {
		try {
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			
			IJobState jobState = JobStateFactory.getState(jobStatus);
			jobState.jobExecuted(job);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void completedJob(Job job) throws Exception {
		try {
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			
			IJobState jobState = JobStateFactory.getState(jobStatus);
			jobState.completedJob(job);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void failJob(Job job) throws Exception {
		try {
			notifyObserver(job, STATES.FAILED);

			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			IJobState jobState = JobStateFactory.getState(jobStatus);
			
			jobState.failJob(job);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void killAndStop(Job job, String operator) throws Exception {
		try {
			throwExceptionIfNotOperator(operator, PropertyProvider.policy.canKillJob());
			
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());

			if (jobStatus == JobStatus.COMPLETED)
				throw new Exception("Job has already been completed["
						+ AgentUtils.toJobString(job));

			if(job.getIp().equals(AgentUtils.getIp()))
				BatchUtils.killProcess(job, operator);
			else
				killProcessOnRemote(job, operator);
			
			stopJob(job);
		} catch (Exception e) {
			log.error(e.getMessage() + "[" + AgentUtils.toJobString(job) + "]");
			throw e;
		}
	}
	
	public void stopJob(Job job) throws Exception {
		try {
			JobStatus jobStatus = jobDao.getLastJobStatus(job.getJobSeq());
			IJobState jobState = JobStateFactory.getState(jobStatus);

			if (jobState.getState() == STATES.COMPLETED)
				throw new Exception("Job has already been completed["
						+ AgentUtils.toJobString(job));
			
			notifyObserver(job, STATES.STOPPED);

			jobState.stopJob(job);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	private void killProcessOnRemote(Job job, String killerIp) throws Exception {
		try {
			KillMessage killMessage = new KillMessage(job, killerIp);
			MessageManager messageManager = BatchAgent.createServer()
					.getMessageManager();

			messageManager.killProcessOnRemote(killMessage);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void updatePerformance(Job job) {
		jobDao.updatePerformance(job);
	}

	public void setJobDao(IJobDao jobDao) {
		this.jobDao = jobDao;
	}

	public void unBlockJob(Job job, String operator) throws Exception {
		try {
			throwExceptionIfNotOperator(operator, PropertyProvider.policy.canUnblockJob());
			
			log.info("This job has been unblocked by " + operator + "[" + AgentUtils.toJobString(job) + "]");
			
			job.setJobStatus(JobStatus.WAITING);
			jobDao.unBlockJob(job.getJobSeq());
			log.info("Job Status(WAITING) -  " + AgentUtils.toJobString(job));
		} catch (Exception e) {
			log.error(e.getMessage() + "[" + AgentUtils.toJobString(job) + "]");
			throw e;
		}
	}

	@SuppressWarnings("rawtypes")
	private void throwExceptionIfNotOperator(String operator, boolean policy) throws Exception {
		Map operators = PropertyProvider.policy.getOperators();
		if(operators.size() == 0) {
			if(!policy) {
				throw new Exception("You can not execute this operation because BatchAgent's policy does not allow it");
			}
			return;
		}
		
		if(!operators.containsKey(operator)) {
			throw new NotAllowedOperatorException("This operator[" + operator + "] is not allowed to run this request");
		}
	}
}
