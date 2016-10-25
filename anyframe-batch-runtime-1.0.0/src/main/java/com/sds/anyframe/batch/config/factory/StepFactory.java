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

package com.sds.anyframe.batch.config.factory;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import com.sds.anyframe.batch.core.step.AnyframeAbstractStep;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepFactory {
	
	
	private JobRepository jobRepository;
	
	private PlatformTransactionManager transactionManager;

	private List<StepExecutionListener> coreListeners;
	
	private Map<String, Class<AnyframeAbstractStep>> stepTypeMap;
	
	
	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setCoreListeners(List<StepExecutionListener> listeners) {
		this.coreListeners = listeners;
	}

	public void setStepTypeMap(Map<String, Class<AnyframeAbstractStep>> stepTypeMap) {
		this.stepTypeMap = stepTypeMap;
	}

	public Object getStep(String stepType) throws Exception {
		
		Class<AnyframeAbstractStep> stepClass = stepTypeMap.get(stepType);
		
		AnyframeAbstractStep step = stepClass.newInstance();
		
		step.setJobRepository(jobRepository);
		
		step.setTransactionManager(transactionManager);
		
		if(step instanceof StepExecutionListener)
			step.registerStepExecutionListener((StepExecutionListener) step);
		
		// resister framework core listeners
		for(StepExecutionListener listener : coreListeners) {
			step.registerStepExecutionListener(listener);
		}
		
		return step;
	}

}
