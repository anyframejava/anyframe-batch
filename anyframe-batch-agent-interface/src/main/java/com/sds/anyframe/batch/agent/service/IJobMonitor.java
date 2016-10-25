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

package com.sds.anyframe.batch.agent.service;

import java.util.Date;
import java.util.List;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Resource;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface IJobMonitor {

	public static final String SERVICE_NAME = "jobMonitor";
	
	List<Job> listJob(String ip, Date startDate, Date endDate, JobStatus status, String jobId, PageRequest pageRequest) throws Exception;

	List<Job> getStepsInSelectedJob(Job job);
	
	Resource getOwnerOfLockedResource(String resourceName);
	
	Job getDetailJob(Job job);
}
