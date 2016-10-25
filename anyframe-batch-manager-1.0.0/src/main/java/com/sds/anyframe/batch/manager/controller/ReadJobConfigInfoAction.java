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

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;
import com.sds.anyframe.batch.manager.view.support.EditorLauncher;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ReadJobConfigInfoAction extends Action {
	private final String ip;
	private final JobInfo jobInfo;
	private JobTreeNode jobTreeNode;
	private String jobName;
	@Override
	public String getText() {
		return "Show job configuration";
	}
	public ReadJobConfigInfoAction(ServerInfo serverInfo, String jobName, JobInfo jobInfo, JobTreeNode jobTreeNode) {
		this.ip = serverInfo.getAddress();
		this.jobInfo = jobInfo;
		this.jobTreeNode = jobTreeNode;
		this.jobName = jobName;
	}
	@Override
	public void run() {
		try {
			PageRequest pageRequest = new PageRequest();
			pageRequest.setStartRowNumber(1);
			pageRequest.setPageSize(10);
			pageRequest.pageNo = 1;
			
			PageSupport xmlViewer = (PageSupport)ProxyHelper.getProxyInterface(ip, "xmlViewer", PageSupport.class.getName());

			pageRequest.setParameter(jobInfo.getJobPath());
			
			pageRequest = xmlViewer.getPage(pageRequest);
			byte[] xmlBuf = (byte[])pageRequest.getResult();
			
			EditorLauncher.openXMLEditor(jobName, xmlBuf, ip, jobInfo, jobTreeNode);
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
	}
	
}
