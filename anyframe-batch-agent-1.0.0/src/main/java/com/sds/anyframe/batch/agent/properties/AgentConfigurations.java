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
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;
import com.sds.anyframe.batch.agent.util.TimeUnits;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class AgentConfigurations {
	private final Logger log = Logger.getLogger(getClass());

	// Hours
	private long jobExecutionOrderWaitingTimeout = 2 * TimeUnits.HOURS;

	private long jobLimitCheckInterval = 5;

	public boolean isCheckMeta() {
		return checkMeta;
	}

	public int getRunningMaxJobBefore() {
		return runningMaxJobBefore;
	}

	private String groupName = "batch_job_cluster";

	private int blockingTimeout = 2 * TimeUnits.HOURS;

	private long blockingCheckInterval = 5;
	
	private static AgentConfigurations configurations = null;
	
	private boolean checkMeta = false;
	
	private int runningMaxJobBefore = 7;

	private AgentConfigurations() {
		Properties prop = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);
		if (!prop.isEmpty()) {
			if (prop.getProperty(PropertyConstants.BATCH_AGENT_CHANNEL_GROUP_NAME) != null)
				groupName = prop
						.getProperty(PropertyConstants.BATCH_AGENT_CHANNEL_GROUP_NAME);
			
			if (prop.getProperty(PropertyConstants.READY_CHECK_INTERVAL_SECONDS) != null)
				jobLimitCheckInterval = Long
						.valueOf(prop
								.getProperty(PropertyConstants.READY_CHECK_INTERVAL_SECONDS));
			
			try {
				if (prop.getProperty(PropertyConstants.BATCH_RUNNING_JOB_MAX_COUNT) != null)
					setJobExecutionLimits(Integer
							.parseInt(prop
									.getProperty(PropertyConstants.BATCH_RUNNING_JOB_MAX_COUNT)));
				
				if (prop.getProperty(PropertyConstants.BATCH_RUNNING_JOB_MAX_BEFORE) != null) {
					int beforeDays = Integer.parseInt(prop.getProperty(PropertyConstants.BATCH_RUNNING_JOB_MAX_BEFORE));
					if (beforeDays > 0)
						runningMaxJobBefore = beforeDays;
				}
				
				if (prop.getProperty(PropertyConstants.BATCH_JOB_QUEUEINGTIME) != null) {
					int queueingTime = Integer
							.parseInt(prop
									.getProperty(PropertyConstants.BATCH_JOB_QUEUEINGTIME));
					if (queueingTime > 0)
						jobExecutionOrderWaitingTimeout = queueingTime
								* TimeUnits.HOURS;
				}
				
				if (prop.getProperty(PropertyConstants.BATCH_JOB_BLOCKING_TIMEOUT) != null) {
					int blockingTimeoutTmp = Integer.parseInt(prop.getProperty(PropertyConstants.BATCH_JOB_BLOCKING_TIMEOUT));
					if (blockingTimeoutTmp > 0)
						blockingTimeout = blockingTimeoutTmp * TimeUnits.HOURS;
				}
				
				if (prop.getProperty(PropertyConstants.BATCH_CHECK_META) != null) {
					checkMeta = Boolean.parseBoolean( prop.getProperty(PropertyConstants.BATCH_CHECK_META) );
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}
	}

	public static AgentConfigurations getConfigurations() {
		if (configurations == null)
			configurations = new AgentConfigurations();
		return configurations;
	}

	public void setJobExecutionLimits(int jobLimits) {
		AgentManagement.getAgentCondition().getClusteredCondition().setJobExecutionLimits(jobLimits);
	}

	public long getJobExecutionOrderWaitingTimeout() {
		return jobExecutionOrderWaitingTimeout;
	}

	public long getBlockingCheckInterval() {
		return blockingCheckInterval ;
	}

	public void setBlockingCheckInterval(long blockingCheckInterval) {
		this.blockingCheckInterval = blockingCheckInterval;
	}
	
	public void setBlockingTimeout(int blockingTimeout) {
		this.blockingTimeout = blockingTimeout;
	}
	
	public int getBlockingTimeout() {
		return blockingTimeout;
	}

	public long getJobLimitCheckInterval() {
		return jobLimitCheckInterval;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setJobExecutionOrderWaitingTimeout(
			long jobExecutionOrderWaitingTimeout) {
		this.jobExecutionOrderWaitingTimeout = jobExecutionOrderWaitingTimeout;
	}

}
