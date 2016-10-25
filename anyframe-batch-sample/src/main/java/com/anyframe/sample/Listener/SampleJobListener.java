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

package com.anyframe.sample.Listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class SampleJobListener implements JobExecutionListener {
	
	private static final Log LOGGER = LogFactory.getLog(SampleJobListener.class);

	
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		LOGGER.info("After Job");
	}

	
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		LOGGER.info("Before Job");
	}

	
	public void onError(JobExecution jobExecution, Throwable e) {
		// TODO Auto-generated method stub
		LOGGER.info("On Error Job");
	}

	
	public void onInterrupt(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

}
