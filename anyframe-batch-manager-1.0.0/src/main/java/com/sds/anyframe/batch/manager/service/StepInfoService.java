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

package com.sds.anyframe.batch.manager.service;

import java.util.List;
import java.util.Map;

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class StepInfoService {
	private PageRequest pageRequest = new PageRequest();
	private PageSupport stepService;
	
	public StepInfoService() {

	}

	public StepInfoService(ServerInfo serverInfo, String xmlFileName) {
		setPageRequest();
		pageRequest.add("xmlFileName", xmlFileName);
		try {
			stepService = (PageSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "stepInfoList", PageSupport.class.getName());
		}
		catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
	}

	private void setPageRequest() {
		pageRequest.setStartRowNumber(1);
		pageRequest.setPageSize(10);
		pageRequest.pageNo = 1;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getStepList() {
		try {
			pageRequest = stepService.getPage(pageRequest);
			return (List<Map<String, String>>) pageRequest.getResult();
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
			return null;
		}
	}

}
