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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.repeat.ExitStatus;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepLoggingListener implements StepExecutionListener{

	private static final Log LOGGER = LogFactory.getLog(StepLoggingListener.class);

	public void beforeStep(StepExecution stepExecution) {
		
		String stepName = stepExecution.getStepName();
		
		LOGGER.info("");
		LOGGER.info("######################################################################################");
		LOGGER.info("## Start Step");
		LOGGER.info("##  - ID   : " + stepName);
		LOGGER.info("######################################################################################");
		LOGGER.info("");
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		
		String stepName = stepExecution.getStepName();
		Date startTime = stepExecution.getStartTime();
		
		LOGGER.info("");
		LOGGER.info("######################################################################################");
		LOGGER.info("## End Step - Completed");
		LOGGER.info("##  - ID   : " + stepName);
		LOGGER.info("##  - Elapsed Time : " + (System.currentTimeMillis() - startTime.getTime()) + " ms");
		LOGGER.info("######################################################################################");
		LOGGER.info("");
		
		return null;
	}

	public ExitStatus onErrorInStep(StepExecution stepExecution, Throwable e) {
		
		String stepName = stepExecution.getStepName();
		Date startTime = stepExecution.getStartTime();
		
		LOGGER.info("");
		LOGGER.info("######################################################################################");
		LOGGER.info("## End Step - Faild");
		LOGGER.info("##  - ID   : " + stepName);
		LOGGER.info("##  - Elapsed Time : " + (System.currentTimeMillis() - startTime.getTime()) + " ms");
		LOGGER.info("######################################################################################");
		LOGGER.info("");
		return null;
	}

}
