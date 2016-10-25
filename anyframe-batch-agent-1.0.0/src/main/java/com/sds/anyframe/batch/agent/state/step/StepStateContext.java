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

import java.util.List;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.dao.IStepDao;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.state.job.IJobObserver;
import com.sds.anyframe.batch.agent.state.job.IJobState;
import com.sds.anyframe.batch.agent.state.step.IStepState.STATES;
import com.sds.anyframe.batch.agent.state.step.pattern.StepStateReady;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class StepStateContext implements IJobObserver, IStepObservable {

	private static Logger log = Logger.getLogger(StepStateContext.class);

	IStepDao stepDao;
	private IStepObserver observer;

	public Step startStep(Step step) throws Exception {
		try {
			StepStateReady stepStateReady = new StepStateReady();
			
			step = stepStateReady.startStep(step);

			return step;
		} catch (Exception e) {
			log.error(e);
			
			throw new Exception(AgentUtils.getStackTraceString(e));
		}
	}

	public boolean lockResources(Step step) throws Exception {
		return (Boolean) notifyObserver(step, STATES.READY);
	}
	
	public void stepExecuted(Step step) throws Exception {
		try {
			StepStatus stepStatus = stepDao.getLastStepStatus(step.getJobSeq(), step.getStepId());
			IStepState finalState = StepStateFactory.getState(stepStatus); // must be StepStateReady.	
			
			finalState.stepExecuted(step);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void completeStep(Step step) throws Exception {
		try {
			notifyObserver(step, STATES.COMPLETED);

			StepStatus stepStatus = stepDao.getLastStepStatus(step.getJobSeq(), step.getStepId());
			IStepState finalState = StepStateFactory.getState(stepStatus); // must be StepStateRunning
			
			finalState.completeStep(step);
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	public void failStep(Step step) throws Exception {
		try {
			StepStatus stepStatus = stepDao.getLastStepStatus(step.getJobSeq(), step.getStepId());
			IStepState finalState = StepStateFactory.getState(stepStatus); // must be StepStateRunning
			
			//This check should be done if the last step has been completed and the job has been failed.
			if (finalState.getState() == STATES.COMPLETED) {
				log.warn("This step could not be failed, the last step's state is 'COMPLETED'["
						+ AgentUtils.toStepString(step));
				return;
			}
			
			notifyObserver(step, STATES.FAILED);
			
			finalState.failStep(step);
			
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
		
	}
	
	private void stopStep(long jobSeq) throws Exception {
		try {
			List<Step> steps = stepDao.getExecuteSteps(jobSeq);

			if (steps == null || steps.isEmpty())
				return;

			for(Step step : steps) {
				IStepState finalState = StepStateFactory.getState(step.getStepStatus());
				
				if (finalState.getState() == IStepState.STATES.COMPLETED ||
					finalState.getState() == IStepState.STATES.FAILED) {
					log.warn("Can not stop this step, step's state is '" + finalState.getState() + "'["
							+ AgentUtils.toStepString(step));
					continue;
				}
				
				notifyObserver(step, STATES.STOPPED);
				
				finalState.stopStep(step);
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
	}

	
	public void jobChanged(Job job, IJobState.STATES jobState) throws Exception {
		if (jobState == IJobState.STATES.STOPPED)
			stopStep(job.getJobSeq());
		else if (jobState == IJobState.STATES.FAILED)
			; // do nothing
		else
			throw new IllegalStateException(
					"Job Observer received illegal state");
	}

	public void setStepDao(IStepDao stepDao) {
		this.stepDao = stepDao;
	}

	
	public Object notifyObserver(Step step, STATES stepState) throws Exception {		
			return observer.stepChanged(step, stepState);		
	}

	
	public void setStepObserver(IStepObserver observer) {
		this.observer = observer;
	}

	public void updatePerformanceAndTransactedCount(Performance performance,
			Step currentStep) {
		stepDao.updatePerformanceAndTransactedCount(performance, currentStep);
	}

}
