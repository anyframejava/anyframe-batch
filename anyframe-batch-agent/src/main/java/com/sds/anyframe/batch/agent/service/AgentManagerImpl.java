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
package com.sds.anyframe.batch.agent.service;

import java.util.List;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.management.AgentCondition;
import com.sds.anyframe.batch.agent.management.ClusteredCondition;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.security.Policy;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class AgentManagerImpl implements AgentManager {
	
	public void setClusteredCondition(List<ClusteredCondition> servers) throws Exception {
		AgentManagement.setClusterOption(servers);
	}
	
	
	public Policy getPolicy() throws Exception {
		return PropertyProvider.policy;
	}

	
	public AgentCondition getAgentCondition() {
		return AgentManagement.getAgentCondition();
	}
	
}
