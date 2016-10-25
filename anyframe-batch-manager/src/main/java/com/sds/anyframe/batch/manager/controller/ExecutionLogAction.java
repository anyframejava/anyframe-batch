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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;
import com.sds.anyframe.batch.manager.view.logEditor.LogEditorInput;
import com.sds.anyframe.batch.manager.view.logEditor.ReadOnlyLogEditor;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ExecutionLogAction extends Action {
	public static final int BYTES_PER_PAGE = 200000; //200k

	private String jobFileName;

	private ServerInfo serverInfo;

	private String title;

	@Override
	public String getText() {
		if(title != null)
			return title;
		return "View job log";
	}

	public ExecutionLogAction() {
	}

	public ExecutionLogAction(ServerInfo serverInfo, String jobFileName) {
		this.serverInfo = serverInfo;
		this.jobFileName = jobFileName;
	}
	
	public ExecutionLogAction(ServerInfo serverInfo, String jobFileName, String title) {
		this.serverInfo = serverInfo;
		this.jobFileName = jobFileName;
		this.title = title;
	}

	@Override
	public void run() {
		try {
			PageRequest pageRequest = new PageRequest();
			pageRequest.setStartRowNumber(1);
			pageRequest.setPageSize(10);
			pageRequest.pageNo = 1;

			PageSupport logService = (PageSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "vsamViewer",
							PageSupport.class.getName());
			pageRequest.setPageSize(BYTES_PER_PAGE);

			byte[] result = null;
			try {
				pageRequest.setParameter(jobFileName);
				pageRequest = logService.getBottomPage(pageRequest);
				result = (byte[]) pageRequest.getResult();
			} catch (Exception e) {
				MessageDialog.openWarning(new Shell(), "Batch Manager", e
						.getMessage());
				return;
			}
			
			if (result == null) {
				MessageDialog.openInformation(null, "Batch Manager", "The log file is empty");
				return;
			}
			
			IEditorInput input = new LogEditorInput(serverInfo, pageRequest, logService, result);
			try {
				ReadOnlyLogEditor logEditor = (ReadOnlyLogEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, ReadOnlyLogEditor.ID);
				logEditor.showNumbers();
			} catch (PartInitException e) {
				MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
				e.printStackTrace();
			}
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", AgentUtils
					.getStackTraceString(e), e);
		}
	}
}
