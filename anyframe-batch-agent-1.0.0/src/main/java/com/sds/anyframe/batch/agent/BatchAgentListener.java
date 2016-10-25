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
package com.sds.anyframe.batch.agent;

import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;

import key.PASSWORD_KEY;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;

import com.sds.anyframe.batch.agent.cluster.BatchAgent;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class BatchAgentListener extends ContextLoaderListener {

	private final Logger log = Logger.getLogger("BatchAgentLogger");

	private static BatchAgent batchAgent;

	private ScheduledExecutorService scheduler;

	private static Date startedTime = new Date();
	
	static {
		System.setProperty("BATCH_KEY", PASSWORD_KEY.BATCH_PASSWORD_KEY);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
		 
		try {
			 batchAgent = BatchAgent.createServer();
			 
			showServerInfo();
			
			if(batchAgent.getConfiguration().isCheckMeta()){
				log.info("checking metadata (Job,Step,Resource)......");
				checkMetadata();
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new  RuntimeException(e1);
		}

		if (!PropertyProvider.isSchedulerOn)
			return;
		
		log.info("---------------------------------------------------------------------------------------------");
		log.info("Batch Agent has been started successfully");
		log.info("Batch Agent is ready");
				
	}

	private void showServerInfo() throws Exception {
		log.info("#############################################################################################");
		log.info("Anyframe Enterprise Batch Agent");
		log.info("");
		log.info("Batch Agent Server Configuration [" + PropertyConstants.SERVER_PROPERTIES_FILE + "]");

		Properties prop = PropertiesUtil.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);
		Set<Object> keySet = prop.keySet();
		TreeSet<Object> treeSet = new TreeSet<Object>(keySet);
		for (Object key : treeSet) {
			log.info(key + "=" + prop.getProperty((String) key));
		}

    	boolean result = BatchAgentConditionManager.validateDatabase();
		
		log.info("Batch Agent Database Connection Test:  " + result );
		
		log.info("#########################################################");
		log.info("Runtime variables");
		log.info("");
		
		prop = PropertiesUtil.getProperties(PropertyConstants.RUNTIME_PROPERTIES_FILE);
		if(prop == null || prop.isEmpty())
			throw new Exception("Can not read the runtime resource file for " + PropertyConstants.RUNTIME_PROPERTIES_FILE);
		keySet = prop.keySet();
		treeSet = new TreeSet<Object>(keySet);
		for (Object key : treeSet) {
			log.info(key + "=" + prop.getProperty((String) key));
		}
		
		log.info("#########################################################");
		log.info("Policy variables");
		log.info("");
		
		prop = PropertiesUtil.getProperties(PropertyConstants.POLICY_PROPERTIES_FILE);
		if(prop == null || prop.isEmpty())
			throw new Exception("Can not read the policy resource file for " + PropertyConstants.POLICY_PROPERTIES_FILE);
		
		keySet = prop.keySet();
		treeSet = new TreeSet<Object>(keySet);
		for (Object key : treeSet) {
			log.info(key + "=" + prop.getProperty((String) key));
		}
		log.info("#########################################################");
		
		log.info("Agent management's condtion");
		log.info("#########################################################");
		log.info("Agent Type");
		if(AgentManagement.getAgentCondition().isSystemAgent())
			log.info("System Agent");
		else
			log.info("Normal Agent");
		
		log.info("#########################################################");
	}
	
	private void checkMetadata() throws Exception{
		//1. SystemAgent인지 판단
		//2. 쿼리 수행 후 대상건에 대해서 Logging
		//3. UPDATE쿼리 수행
		if(AgentManagement.getAgentCondition().isSystemAgent()){
			String result = BatchAgentConditionManager.checkWeirdJob();
			if (StringUtils.isNotEmpty(result)){
				log.info("There are weird Jobs");
				log.info(result);
		
				int updateJob = BatchAgentConditionManager.clearWeirdJob();
				int updateStep = BatchAgentConditionManager.clearWeirdStep();
				int updateResource = BatchAgentConditionManager.clearWeirdResource();
				
				log.info("Clear Weird Job Result="+ updateJob);
				log.info("Clear Weird Step Result="+ updateStep);
				log.info("Clear Weird Resource Result="+ updateResource);
			}
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
		log.debug("context destroyed started...");
		if(batchAgent != null) {
			batchAgent.stop();
			log.debug("jGroups channel is opened : " + batchAgent.getChannel().isOpen());
		}
		if(scheduler != null) {
			scheduler.shutdown();
			log.debug("garbage job handling scheduler is shutdown : " + scheduler.isShutdown());
		}
	}

	public static Date getStartedTime() {
		return startedTime;
	}

	public static BatchAgent getBatchAgent() {
		return batchAgent;
	}
}