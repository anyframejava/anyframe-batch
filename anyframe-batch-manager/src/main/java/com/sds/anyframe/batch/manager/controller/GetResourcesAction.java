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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.service.JobResourceInfo;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class GetResourcesAction extends Action {
	Log log = LogFactory.getLog(GetResourcesAction.class);
	private ServerInfo serverInfo;
	private JobInfo jobInfo;
	private Policy policy;

	public GetResourcesAction(ServerInfo serverInfo,
			JobInfo jobInfo, Policy policy) {
		this.serverInfo = serverInfo;
		this.jobInfo = jobInfo;
		this.policy = policy;
	}

	@Override
	public String getText() {
		return "File";
	}

	@Override
	public void run() {
		SelectFileResourceDialog selectResourceFileDialog = new SelectFileResourceDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo, jobInfo, policy);
		selectResourceFileDialog.open();
	}
	
	public List<String> resourcesInJob() {
		try {
			JobResourceInfo resources = (JobResourceInfo) ProxyHelper.getProxyInterface(serverInfo.getAddress(),
							JobResourceInfo.SERVICE_NAME, JobResourceInfo.class
									.getName());
			return resources.resourcesInJob(jobInfo.getJobPath());
			
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		return null;
	}
}
