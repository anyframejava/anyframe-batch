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

package com.sds.anyframe.batch.core.step;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.ExitStatus;

import com.sds.anyframe.batch.core.step.support.StepThread;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ParallelStep extends AnyframeAbstractStep {

	List<Step> steps = new ArrayList<Step>();

	public void setSteps(List<Step> steps) {
		this.steps = steps;
		
		// check max number of thread limit
		if(BatchDefine.PARALLEL_MAX_THREAD > 0) {
			if(this.steps.size() > BatchDefine.PARALLEL_MAX_THREAD) {
				throw new BatchRuntimeException("Max number of parallel step is limited to " + BatchDefine.PARALLEL_MAX_THREAD);
			}
		}
	}
	
	@Override
	protected ExitStatus doExecute(StepExecution stepExecution) throws Exception {

		List<StepThread> threads = new ArrayList<StepThread>();
		
		// initialize thread
		for(Step step : this.steps) {
			StepExecution newStepExecution = stepExecution.getJobExecution().createStepExecution(step);
			
			StepThread thread = new StepThread();
			thread.setStep(step);
			thread.setStepExecution(newStepExecution);
			threads.add(thread);
		}
		
		// start thread
		for(StepThread thread : threads) {
			thread.start();
		}
		
		// waiting for completion
		for(StepThread thread : threads) {
			thread.join();
		}
		
		// check and rethrow thread's exception
		for(StepThread thread : threads) {
			Exception exception = thread.getException();
			if(exception != null) {
				throw exception;	// rethrow
			}
			
		}
		
		return ExitStatus.FINISHED;
	}
	
}
