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

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.service.FileList;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.SelectLogFileDialog;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SelectLogFileDialogAction extends Action {
	Log log = LogFactory.getLog(SelectLogFileDialogAction.class);
	private final ServerInfo serverInfo;
	private final JobInfo jobInfo;
	
	@Override
	public String getText() {
		return "Show execution logs";
	}
	@Override
	public void run() {
		SelectLogFileDialog logFileSelectDialog = new SelectLogFileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo, jobInfo);
		logFileSelectDialog.open();
	}
	
	public List<FileInfoVO> getFileListToDialog(FileList logFileList, String jobFileDirectory, String fileName, String fromDate, String toDate) {
		try {
			return logFileList.getSearchList(jobFileDirectory, fileName, fromDate, toDate);
		} catch (FileNotFoundException e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
		return null;
	}
	
	public List<FileInfoVO> getFileList(FileList logFileList,	String jobFileName, String fromDate, String toDate) {
		return logFileList.getList(jobFileName, fromDate, toDate);
	}
	
	
	public FileList getLogFileList(ServerInfo serverInfo) {
		try {
			return (FileList) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "logList", FileList.class.getName());
		}
		catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		return null;
	}
	
	public SelectLogFileDialogAction(ServerInfo serverInfo, JobInfo jobInfo) {
		this.serverInfo = serverInfo;
		this.jobInfo = jobInfo;
	}

}
