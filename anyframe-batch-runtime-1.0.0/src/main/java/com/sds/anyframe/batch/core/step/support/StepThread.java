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

package com.sds.anyframe.batch.core.step.support;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepThread extends Thread {

	private Step step = null;
	private StepExecution stepExecution = null;
	private Exception exception = null;

	public void setStep(Step step) {
		this.step = step;
	}
	
	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	@Override
	public void run() {
		try {
			this.step.execute(stepExecution);
		}
		catch (Throwable e) {
			exception = new Exception("error occur on thread " + step.getName(), e);
		}
		
	}

	public Exception getException() {
		return this.exception;
	}
}
