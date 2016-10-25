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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.TableInfo;
import com.sds.anyframe.batch.agent.service.DBHandleSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.SelectDatabaseResourceDialog;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SelectDatabaseResourceDialogAction extends Action {
	Log log = LogFactory.getLog(SelectDatabaseResourceDialogAction.class);
	private final ServerInfo serverInfo;
	
	@Override
	public String getText() {
		return "Database";
	}
	
	public SelectDatabaseResourceDialogAction(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	@Override
	public void run() {
		SelectDatabaseResourceDialog selectResourceDatebaseDialog = new SelectDatabaseResourceDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo);
		selectResourceDatebaseDialog.open();
	}
	
	public DBHandleSupport getDBHandleSupport(ServerInfo serverInfo){
		try {
			return (DBHandleSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "dbHandle", DBHandleSupport.class.getName());
		}
		catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		return null;
	}

	public List<String> getDataSourceList(DBHandleSupport dBHandleSupport) throws Exception {
		return dBHandleSupport.getDataSourceList();
	}

	public List<TableInfo> getDBMetaInfo(DBHandleSupport dBHandleSupport, String dataSource, String tableName, String Searchtype) {
		try {
			return dBHandleSupport.getDBMetaInfo(dataSource, tableName, Searchtype);
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
		return null;
	}
}
