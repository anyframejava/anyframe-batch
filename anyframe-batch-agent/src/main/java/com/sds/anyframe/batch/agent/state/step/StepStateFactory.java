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
package com.sds.anyframe.batch.agent.state.step;

import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.bean.BeanFactory;
import com.sds.anyframe.batch.agent.dao.IStepDao;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateCompleted;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateFailed;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateReady;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateRunning;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateStopped;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class StepStateFactory {

	static IStepDao stepDao;

	public static IStepDao getStepDao() {
		if(stepDao == null)
			stepDao = (IStepDao) BeanFactory.getBean(BeanConstants.STEP_DAO);
		return stepDao;
	}

	public static IStepState getState(StepStatus stepStatus) {
		switch (stepStatus) {
		case READY:
			return new StepStateReady();
		case RUNNING:
			return new StepStateRunning();
		case COMPLETED:
			return new StepStateCompleted();
		case STOPPED:
			return new StepStateStopped();
		case FAILED:
			return new StepStateFailed();	
		default:
			break;
		}
		return null;
	}

}
