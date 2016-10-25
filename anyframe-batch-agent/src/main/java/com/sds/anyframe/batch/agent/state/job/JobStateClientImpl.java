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

import com.sds.anyframe.batch.agent.model.Job;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateClientImpl implements IJobStateClient {

	JobStateContext jobStateContext;

	public JobStateClientImpl(JobStateContext jobStateContext) {
		this.jobStateContext = jobStateContext;
	}

	
	public Job startJob(Job job)throws Exception {
		return jobStateContext.startJob(job);
	}

	
	public void jobExecuted(Job job) throws Exception {
		jobStateContext.jobExecuted(job);
	}

	
	public void waitJobToExecute(Job job) throws Exception {
		jobStateContext.waitJobToExecute(job);
	}

	
	public void completedJob(Job job) throws Exception {
		jobStateContext.completedJob(job);
		
	}

	
	public void failJob(Job job)throws Exception  {
		jobStateContext.failJob(job);
	}

	
	public void updatePerformance(Job job) {
		jobStateContext.updatePerformance(job);
	}

	
	public void killAndStop(Job job, String killerIp) throws Exception {
		jobStateContext.killAndStop(job, killerIp);
	}
	
	
	public void stopJob(Job job) throws Exception {
		jobStateContext.stopJob(job);
	}

	
	public void unBlockJob(Job job, String clientIp) throws Exception {
		jobStateContext.unBlockJob(job, clientIp);
	}

}
