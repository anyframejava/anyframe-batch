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

import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class StepStateReady extends AbstractStepState {
	private final Logger log = Logger.getLogger(getClass());

	public StepStateReady() {
		super(STATES.READY);
	}

	@Override
	public Step startStep(Step step) throws Exception {

		step.setStepStatus(StepStatus.READY);
		stepDao.insertStep(step);
		
		log.info("Step Status(READY) - " + AgentUtils.toStepString(step));

		//Lock resources using by this step.
		resourceDao.insertResources(step);
		return step;
	}

	@Override
	public void stepExecuted(Step step) {
		step.setStepStatus(StepStatus.RUNNING);
		stepDao.updateStepAsRunning(step.getJobSeq(), step.getStepId());
		log.info("Step Status(RUNNING) - " + AgentUtils.toStepString(step));
	}

	@Override
	public void completeStep(Step step) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(StepStateReady.class.getName()
				+ " do not support this operaton[" + AgentUtils.toStepString(step) + "]");
	}

	@Override
	public void stopStep(Step step) throws Exception {
		super.stopStep(step);
	}

	@Override
	public void failStep(Step step) throws Exception {
		super.failStep(step);
	}
	
}
