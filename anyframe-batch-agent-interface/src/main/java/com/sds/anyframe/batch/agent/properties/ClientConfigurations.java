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

package com.sds.anyframe.batch.agent.properties;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.PropertyConstants;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;
import com.sds.anyframe.batch.agent.util.TimeUnits;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ClientConfigurations {
	private final Logger log = Logger.getLogger(getClass());

	private String secondaryPort = "15019";

	// Hours
	private long resourceQueuingTime = 10 * TimeUnits.HOURS;
	
	private long resourceCheckInterval = 10;

	private static int updateInterval = 10;

	private static boolean isBatchAgentOn; // remote call switch to batch agent

	private static String primaryBatchAgent;

	private static boolean supportCpuUsage = true;
	
	private static ClientConfigurations configurations = null;

	private ClientConfigurations() {
		Properties prop = PropertiesUtil
				.getProperties(PropertyConstants.BATCH_PROPERTIES_FILE);
		if (prop != null && prop.size() > 0) {
			if(prop.getProperty(PropertyConstants.BATCH_PING_INTERVAL_SEC) != null)
				updateInterval = Integer.valueOf(prop
					.getProperty(PropertyConstants.BATCH_PING_INTERVAL_SEC));
			
			if(prop.getProperty(PropertyConstants.BATCH_AGENT_ON) != null)
				isBatchAgentOn = Boolean.valueOf(prop
					.getProperty(PropertyConstants.BATCH_AGENT_ON));
			primaryBatchAgent = prop
					.getProperty(PropertyConstants.BATCH_AGENT_MAIN);
			
			if(prop.getProperty(PropertyConstants.BATCH_AGENT_SECONDARY_PORT) != null)
				secondaryPort = prop.getProperty(PropertyConstants.BATCH_AGENT_SECONDARY_PORT);
			
			if(prop.getProperty(PropertyConstants.BATCH_SUPPORT_CPUUSAGE) != null)
				supportCpuUsage = Boolean.valueOf(prop
						.getProperty(PropertyConstants.BATCH_SUPPORT_CPUUSAGE));
			
			if(prop.getProperty(PropertyConstants.RESOURCE_CHECK_INTERVAL_SECONDS) != null)
				resourceCheckInterval = Long.valueOf(prop.getProperty(PropertyConstants.RESOURCE_CHECK_INTERVAL_SECONDS));
			
			if (prop.getProperty(PropertyConstants.BATCH_RESOURCE_QUEUEINGTIME) != null) {
				int resourceQueueingTime = Integer.parseInt(prop.getProperty(PropertyConstants.BATCH_RESOURCE_QUEUEINGTIME));

				if (resourceQueueingTime > 0)
					this.resourceQueuingTime = resourceQueueingTime	* TimeUnits.HOURS;
			}
		
			log.debug("primaryBatchAgent : " + primaryBatchAgent
					+ ", ping interval : " + updateInterval
					+ ",  is batch agent on :" + isBatchAgentOn);
		} else
			log.debug("Batch properties is null");
	}

	public static ClientConfigurations getConfigurations() {
		if (configurations == null)
			configurations = new ClientConfigurations();
		return configurations;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public boolean isBatchAgentOn() {
		return isBatchAgentOn;
	}

	public String getPrimaryBatchAgent() {
		return primaryBatchAgent;
	}

	public String getSecondaryPort() {
		return secondaryPort;
	}

	public boolean isSupportCpuUsage() {
		return supportCpuUsage;
	}

	public long getResourceCheckInterval() {
		return resourceCheckInterval;
	}
	
	public long getResourceQueuingTime() {
		return resourceQueuingTime;
	}
}
