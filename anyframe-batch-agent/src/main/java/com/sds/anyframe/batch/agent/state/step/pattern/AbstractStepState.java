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
package com.sds.anyframe.batch.agent.state.step.pattern;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.bean.BeanFactory;
import com.sds.anyframe.batch.agent.dao.IResourceDao;
import com.sds.anyframe.batch.agent.dao.IStepDao;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.state.step.IStepState;
import com.sds.anyframe.batch.agent.util.AgentUtils;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public abstract class AbstractStepState implements IStepState {
	private final Logger log = Logger.getLogger(getClass());
	
	protected IStepDao stepDao;
	IResourceDao resourceDao;

	protected STATES state;
	
	public AbstractStepState(STATES state) {
		this.state = state;
		stepDao = (IStepDao) BeanFactory.getBean(BeanConstants.STEP_DAO);
		resourceDao = (IResourceDao) BeanFactory.getBean(BeanConstants.RESOURCE_DAO);
	}
	
	
	public STATES getState() {
		return state;
	}
	
	
	public Step startStep(Step step) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	
	public void stepExecuted(Step step) {
		throw new UnsupportedOperationException();
	}
	
	
	public void completeStep(Step step) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	
	public void stopStep(Step step) throws Exception {
		step.setStepStatus(StepStatus.STOPPED);
		stepDao.updateStepAsStopped(step.getJobSeq(), step.getStepId());
		log.info("Step Status(STOPPED) - " + AgentUtils.toStepString(step));
	}
	
	
	public void failStep(Step step) throws Exception {
		step.setStepStatus(StepStatus.FAILED);
		stepDao.updateStepAsFailed(step.getJobSeq(), step.getStepId(), step.getExitMessage());
		log.info("Step Status(FAILED) - " + AgentUtils.toStepString(step));
	}

}
