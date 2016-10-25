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
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobDetailInfoAction extends Action {

	
	private ServerInfo serverInfo;
	private Job job;
	private String title;

	public JobDetailInfoAction(ServerInfo serverInfo,Job job, String title){
		this.serverInfo = serverInfo;
		this.job = job;
		this.title = title;
	}
	

	public JobDetailInfoAction(){
	}
	
	
	@Override
	public void run() {
		JobDetailInfoDialog dialog = new JobDetailInfoDialog(
				PlatformUI.getWorkbench().getDisplay()
						.getActiveShell(), serverInfo, job);
		dialog.open();
	}

	@Override
	public String getText() {
		if(title != null)
			return title;
		return "View Job Detail Info";
	}


}
