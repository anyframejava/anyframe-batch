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

import org.eclipse.jface.action.Action;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.service.FileList;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class FileServiceAction extends Action {

	public final String SERVICE_NAME = "resourceList";

	
	private ServerInfo serverInfo;
	private FileList fileService;
	
	public FileServiceAction(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	private void getServiceInterface() {
		try {
			if (fileService == null)
				fileService = (FileList) ProxyHelper.getProxyInterface(
						serverInfo.getAddress(), SERVICE_NAME,
						FileList.class.getName());
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
	}

	public List<FileInfoVO> getFiles(List<String> files) {
		getServiceInterface();
		return fileService.getList2(files);
	}

	public List<FileInfoVO> searchFiles(String samFilePath, String samFileName,
			String fromDate, String toDate) {
		try {
			getServiceInterface();
			return fileService.getSearchList(samFilePath, samFileName,
					fromDate, toDate);
		} catch (FileNotFoundException e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		return null;
	}

}
