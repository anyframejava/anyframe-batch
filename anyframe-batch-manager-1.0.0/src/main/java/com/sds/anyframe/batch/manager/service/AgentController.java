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

import com.sds.anyframe.batch.agent.management.AgentCondition;
import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.service.AgentManager;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class AgentController {
	
	static AgentManager agentManager = null;
	
	public static Policy getPolicy(ServerInfo server) {
		try {
			agentManager = (AgentManager) ProxyHelper.getProxyInterface(server.getAddress(), AgentManager.SERVICE_NAME, AgentManager.class.getName());
			return agentManager.getPolicy();
		}
		catch (Exception e) {
			agentManager = null;
			MessageUtil.showMessage(server.getAddress() + ":" + AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		return null;
	}
	
	public static AgentCondition getAgentCondition(ServerInfo server) {
		try {
			agentManager = (AgentManager) ProxyHelper.getProxyInterface(server.getAddress(), AgentManager.SERVICE_NAME, AgentManager.class.getName());
			return agentManager.getAgentCondition();
		}
		catch (Exception e) {
			agentManager = null;
			MessageUtil.showMessage(server.getAddress() + ":" + AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		return null;
	}
}
