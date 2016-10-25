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

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.manager.dialog.FileResourceResultDialog;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog.ResourceType;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ShowSamAction extends Action {

	private FileInfoVO file;

	private ServerInfo serverInfo;

	private ResourceType rType;

	@Override
	public String getText() {
		return rType.toString();
	}

	public ShowSamAction(ServerInfo serverInfo, FileInfoVO fileInfo, ResourceType rType) {
		this.serverInfo = serverInfo;
		this.rType = rType;
		this.file = fileInfo;
	}

	@Override
	public void run() {
		FileResourceResultDialog resultListDialog = new FileResourceResultDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo, file, SelectFileResourceDialog.ShowTop, rType, ",");
		resultListDialog.open();
	}
}
