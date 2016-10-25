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
package com.sds.anyframe.batch.agent.dao;

import java.util.List;

import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.model.StepStatus;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public interface IStepDao {
	void insertStep(Step step);

	void updateStepAsRunning(long jobSeq, String stepId);
	
	void updateStepAsCompleted(long jobSeq, String stepId);

	void updateStepAsStopped(long jobSeq, String stepId);
	
	void updateStepAsFailed(long jobSeq, String stepId, String errorMessage);
	
	Step getStep(long jobSte, String stepId);
	
	StepStatus getLastStepStatus(long jobSte, String stepId);
	
	List<Step> getExecuteSteps(long jobSeq);

	void updatePerformanceAndTransactedCount(Performance performance,
			Step currentStep);
}
