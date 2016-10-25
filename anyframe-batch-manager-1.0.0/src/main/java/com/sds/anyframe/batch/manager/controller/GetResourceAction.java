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

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog.ResourceType;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class GetResourceAction {
	Log log = LogFactory.getLog(GetResourceAction.class);

	private static final int LINES_PER_PAGE = 100;
	private static final int BYTES_PER_PAGE = 20000;

	private ResourceType resourceType;
	private PageRequest request;

	public GetResourceAction() {
	}

	public PageSupport getPageSupport(ServerInfo serverInfo,
			String filename, ResourceType resourceType) {
		this.resourceType = resourceType;

		PageSupport vsamService = null;
		try {
			if (resourceType == ResourceType.VSAM) {
				vsamService = (PageSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "vsamViewer",
								PageSupport.class.getName());
				request.setPageSize(BYTES_PER_PAGE); // 20000byte
			} else {
				vsamService = (PageSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "samViewer",
								PageSupport.class.getName());
				request.setPageSize(LINES_PER_PAGE); // 100 lines
			}
			request.setParameter(filename);
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		return vsamService;
	}

	public PageRequest getFileResource(PageSupport fileResource, int type) throws Exception {
		try {
			if (type == SelectFileResourceDialog.ShowTop) {
				request = fileResource.getTopPage(request);
				return request;
			} else if (type == SelectFileResourceDialog.ShowBottom) {
				request = fileResource.getBottomPage(request);
				return request;
			}
		} catch (Exception e) {
			request.setResult(null);
			MessageUtil.showMessage(e.getMessage(),
					"Batch Manager");
		}
		return request;
	}

	public PageRequest getPageByNo(PageSupport fileResource, int pageNo) {
		try {
			request.pageNo = pageNo;
			request = fileResource.pageByNo(request);
			return request;
		} catch (NumberFormatException e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
			e.printStackTrace();
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
			e.printStackTrace();
		}
		return null;
	}

	public PageRequest getPreviousPage(PageSupport fileResource) {
		try {
			if (resourceType == ResourceType.SAM) {
				request = fileResource.previous(request);
				return request;
			}
			else if (resourceType == ResourceType.VSAM) {
				request = fileResource.previous(request);
				return request;
			}
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		return null;
	}

	public PageRequest getNextPage(PageSupport fileResource) {
		try {
			if (resourceType == ResourceType.SAM) {
				request = fileResource.next(request);
				return request;
			}
			else if (resourceType == ResourceType.VSAM) {
				request = fileResource.next(request);
				return request;
			}
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		return null;
	}

	public void setPageRequest(PageRequest request) {
		this.request = request;
	}
}
