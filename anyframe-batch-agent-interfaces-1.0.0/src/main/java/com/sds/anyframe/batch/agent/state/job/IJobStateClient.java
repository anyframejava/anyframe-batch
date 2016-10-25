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

package com.sds.anyframe.batch.agent.state.job;

import com.sds.anyframe.batch.agent.model.Job;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface IJobStateClient {
	
	public static final String SERVICE_NAME = "jobStateClient";

	Job startJob(Job job) throws Exception;

	void jobExecuted(Job job) throws Exception;

	void waitJobToExecute(Job job)throws Exception;
	
	void completedJob(Job job)throws Exception;
	
	void killAndStop(Job job, String killerIp) throws Exception;
	
	void stopJob(Job job) throws Exception;

	void failJob(Job job) throws Exception;

	void updatePerformance(Job job);
	
	void unBlockJob(Job job, String clientIp) throws Exception;

}
