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

import com.sds.anyframe.batch.agent.TimeOutException;
import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.bean.BeanFactory;
import com.sds.anyframe.batch.agent.dao.IJobDao;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobStateStopped extends AbstractJobState {
	IJobDao jobDao;

	public JobStateStopped() {
		super(STATES.STOPPED);
		this.jobDao = (IJobDao) BeanFactory.getBean(BeanConstants.JOB_DAO);
	}

	@Override
	public Job startJob(Job job) throws Exception {
		throw new UnsupportedOperationException(JobStateStopped.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void waitJobToExecute(Job job) throws Exception {
		throw new UnsupportedOperationException(JobStateStopped.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}
	
	@Override
	public void jobExecuted(Job job) throws TimeOutException {
		throw new UnsupportedOperationException(JobStateStopped.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void completedJob(Job job) throws Exception {
		throw new UnsupportedOperationException(JobStateStopped.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}

	@Override
	public void stopJob(Job job)
			throws Exception {
		super.stopJob(job);
	}

	@Override
	public void failJob(Job job) {
		throw new UnsupportedOperationException(JobStateStopped.class.getName()
				+ " do not support this operaton[" + AgentUtils.toJobString(job) + "]");
	}
}
