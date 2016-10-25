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
package com.sds.anyframe.batch.agent.managment;

import java.util.List;

import com.sds.anyframe.batch.agent.cluster.BatchAgent;
import com.sds.anyframe.batch.agent.management.AgentCondition;
import com.sds.anyframe.batch.agent.management.ClusteredCondition;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class AgentManagement {
	private static AgentCondition agentCondition = new AgentCondition();
	
	public static boolean isBlocking() {
		return agentCondition.getClusteredCondition().isBlocking();
	}
	
	public static void setClusterOption(List<ClusteredCondition> targets) throws Exception {
		BatchAgent.sendClusteredConditions(targets);
	}
	
	public static AgentCondition getAgentCondition() {
		return agentCondition;
	}

	public static void setSystemAgent(boolean b) {
		agentCondition.setSystemAgent(b);
	}
}