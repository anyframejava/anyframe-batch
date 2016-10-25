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

import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.bean.BeanFactory;
import com.sds.anyframe.batch.agent.dao.IJobDao;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateBlocking;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateCompleted;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateFailed;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateInitial;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateReady;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateRunning;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateStopped;
import com.sds.anyframe.batch.agent.state.job.pattern.JobStateWaiting;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateFactory {

	
	static IJobDao jobDao;

	public static IJobDao getJobDao() {
		if(jobDao == null)
			jobDao = (IJobDao) BeanFactory.getBean(BeanConstants.JOB_DAO);
		return jobDao;
	}
	
	
	public static IJobState getState(JobStatus jobStatus) {
		switch (jobStatus) {
		case INITIAL:
			return new JobStateInitial();
		case BLOCKING:
			return new JobStateBlocking();
		case WAITING:
			return new JobStateWaiting();
		case READY:
			return new JobStateReady();
		case RUNNING:
			return new JobStateRunning();
		case COMPLETED:
			return new JobStateCompleted();
		case STOPPED:
			return new JobStateStopped();
		case FAILED:
			return new JobStateFailed();	
		default:
			break;
		}
		return null;
	}

}
