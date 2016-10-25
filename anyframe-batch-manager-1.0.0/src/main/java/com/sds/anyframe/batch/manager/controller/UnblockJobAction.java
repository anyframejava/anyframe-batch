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

package com.sds.anyframe.batch.manager.controller;

import org.eclipse.jface.action.Action;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.state.job.IJobStateClient;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.view.IJobChangedObserver;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class UnblockJobAction extends Action {

	private IJobStateClient jobStateClient;
	private Job job;
	private IJobChangedObserver jobObserver;

	@Override
	public String getText() {
		return "Unblock this job";
	}

	public UnblockJobAction(IJobStateClient jobStateClient, Job job, IJobChangedObserver jobObserver) {
		this.jobStateClient = jobStateClient;
		this.job = job;
		this.jobObserver = jobObserver;
	}

	@Override
	public void run() {
		try {
			jobStateClient.unBlockJob(job, AgentUtils.getIp());
			jobObserver.jobChanged();
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", e.getMessage(), e);
		}
	}
}
