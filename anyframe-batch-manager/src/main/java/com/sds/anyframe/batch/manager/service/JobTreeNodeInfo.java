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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Shell;
import org.springframework.remoting.RemoteConnectFailureException;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.model.PageResult;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;
import com.sds.anyframe.batch.manager.view.support.ErrorMessageDialog;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobTreeNodeInfo {
	private PageRequest pageRequest = new PageRequest();
	private PageSupport jobService;
	private final Log log = LogFactory.getLog(JobTreeNodeInfo.class);
	private ServerInfo server;
	private final ErrorMessageDialog emd = new ErrorMessageDialog(new Shell());
	public JobTreeNodeInfo() {

	}

	public static JobTreeNodeInfo newAllJobInstance(ServerInfo serverInfo, String searchValue) {
		return new JobTreeNodeInfo(serverInfo, searchValue, false, null);
	}
	
	public static JobTreeNodeInfo newJobOfPackageInstance(ServerInfo serverInfo, String searchValue, String searchBaseDir) {
		return new JobTreeNodeInfo(serverInfo, searchValue, false, searchBaseDir);
	}

	public static JobTreeNodeInfo newAllPackageInstance(ServerInfo serverInfo) {
		return new JobTreeNodeInfo(serverInfo, "", true, null);
	}
	
	private JobTreeNodeInfo(ServerInfo serverInfo, String searchValue, boolean searchDirOnly, String searchBaseDir) {
		setPageRequest();
		this.server = serverInfo;
		
		pageRequest.add("searchValue", searchValue);
		pageRequest.add("searchDirOnly", searchDirOnly);
		pageRequest.add("searchBaseDir", searchBaseDir);		// e.g. "/anyframe/apps/afee/batch-poc/build/sli/co/tp/tpsmp"
		
		try {
			jobService = (PageSupport) ProxyHelper.getProxyInterface(server.getAddress(), "xmlFileList", PageSupport.class.getName());
		}
		catch (Exception e) {
			MessageUtil.showMessage(server.getAddress() + ":" + AgentUtils.getStackTraceString(e), "Batch Manager");
		}
	}
	
	private void setPageRequest() {
		pageRequest.setStartRowNumber(1);
		pageRequest.setPageSize(10);
		pageRequest.pageNo = 1;
	}

	@SuppressWarnings("unchecked")
	public List<JobInfo> getJobList() {
		//아래 코드는 위의 생성자와 연관이 있는데 Ui클래스 구조가 상당 복잡하당.
		if(jobService == null)
			return null;
		List<JobInfo> pageResultList = null;
		try {
			pageRequest = jobService.getPage(pageRequest);
			pageResultList = (List<JobInfo>) pageRequest.getResult();
		} catch (RemoteConnectFailureException cone){
			MessageUtil.showErrorMessage("Batch Manager", "Cannot connect to " + server.getServerName(), cone);
			cone.printStackTrace();
		} catch (Exception e) {
			emd.setMessage(AgentUtils.getStackTraceString(e));
			emd.open();
		}
		return pageResultList;
	}

	@SuppressWarnings("unchecked")
	public void getJobIdName(String jobUrl) {
		try {
			PageSupport xmlViewer = (PageSupport) ProxyHelper.getProxyInterface(server.getAddress(), "xmlViewer", PageSupport.class.getName());
			pageRequest.setParameter(jobUrl);
			
			pageRequest = xmlViewer.getPage(pageRequest);
			
			List pageResultList = (List) pageRequest.getResult();
			
			for (Object item : pageResultList) {
				PageResult result = (PageResult) item;
				int rowNum = result.getRowNum();
				String line = result.getLine();
				log.info("rowNum : " + rowNum + ", line : " + line);
			}
		} catch (Exception e) {
			emd.setMessage(AgentUtils.getStackTraceString(e));
			emd.open();
		}
	}
}
