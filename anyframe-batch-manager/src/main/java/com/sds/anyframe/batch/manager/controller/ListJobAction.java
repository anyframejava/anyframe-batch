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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.manager.core.StringUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;
import com.sds.anyframe.batch.manager.service.JobTreeNodeInfo;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ListJobAction extends Action {
	
	
	private String jobId;
	private ServerInfo server;
	private TreeViewer viewer;

	public ListJobAction() {
	}

	public ListJobAction(ServerInfo serverInfo, String jobId, TreeViewer viewer) {
		this.server = serverInfo;
		this.jobId = jobId;
		this.viewer = viewer;
	}

	public void run() {
		List<JobInfo> jobList;
		if(StringUtil.isEmptyOrNull(jobId)) {
			jobList = JobTreeNodeInfo.newAllPackageInstance(server).getJobList();
		} else {
			jobList = JobTreeNodeInfo.newAllJobInstance(server, jobId).getJobList();
		}
		if(jobList == null)
			return;
		
		// Tree 구조로 만든다.
		JobTreeNode root = JobTreeMaker.convertJobTreeNode(jobList); 

		// 화면에 표시
		viewer.setInput(root);
		
		// Tree 기본 확장 (1: 업무구분, 2: Package, 3. Job)
		viewer.expandToLevel(2);
	}
}
