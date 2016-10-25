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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import com.sds.anyframe.batch.agent.service.JobFileHandler;
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

public class JobFileDeleteAction extends Action {
	private final String filePath;
	private final Log log = LogFactory.getLog(JobFileDeleteAction.class);
	private final String ip;
	private final TreeViewer treeViewer;
	private final String jobName;
	
	@Override
	public String getText() {
		return "Delete Temp File";
	}
	
	@Override
	public void run() {
		try {
			JobFileHandler jobFileHandler = (JobFileHandler)ProxyHelper.getProxyInterface(ip, "jobFileHandler", JobFileHandler.class.getName());

			TreeItem item = treeViewer.getTree().getSelection()[0];
			JobTreeNode treeNode = (JobTreeNode)item.getData();
			
			try {

				if(!EditorLauncher.closeByNameAfterConfirm(jobName)){
					return;
				}
				
				jobFileHandler.delete(filePath);
				log.info(filePath + " was successfully deleted");
				JobTreeNode parentTreeNode = treeNode.getParent();
				parentTreeNode.getChildren().remove(treeNode);
				treeViewer.remove(treeNode);
				treeViewer.refresh();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
	}
	public JobFileDeleteAction(ServerInfo serverInfo, String filePath, TreeViewer treeViewer, String jobName) {
		this.ip = serverInfo.getAddress();
		this.filePath = filePath;
		this.treeViewer = treeViewer;
		this.jobName = jobName;
	}
	

}
