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

import java.util.Properties;

import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.agent.util.BooleanUtils;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PropertyProvider {

	public static boolean isSchedulerOn = true;
	public static long interval_minutes = 5;
	
	public static String runtimeBasePath;
	public static String runtimeLogPath;
	public static String runtimeDataSourceContext;
	public static String runtimeSamPath;
	//  three variables which are shown below must be changed using property for extensibility
	public static String shellPath = "/config/common/";
	public static String shell;
	public static String shellOption;
	public static String killShell = "batchjobkill.sh";
	public static String executeShell = "ex.sh";
	public static String buildPath = "build";
	public static String envKeyword = "export";
	public static String userCommandClass;
	public static String userCommandUrl;
	public static boolean jobFailOnSameJobRunning = true;
	
	public static Policy policy = new Policy();
	
	static {
		loadProperties();
	}

	private static void loadProperties() {
		loadRuntimeConfigurations();
		loadAgentPolicy();
	}

	private static void loadAgentPolicy() {
		Properties prop = PropertiesUtil.getProperties(PropertyConstants.POLICY_PROPERTIES_FILE);
		if(prop != null && !prop.isEmpty()) {
			if(prop.getProperty(PropertyConstants.POLICY_KILL_JOB) != null)
				policy.canKillJob(BooleanUtils.getBoolean(prop.getProperty(PropertyConstants.POLICY_KILL_JOB), true));
			
			if(prop.getProperty(PropertyConstants.POLICY_RUN_JOB) != null)
				policy.canRunJob(BooleanUtils.getBoolean(prop.getProperty(PropertyConstants.POLICY_RUN_JOB), true));
			
			if(prop.getProperty(PropertyConstants.POLICY_UNBLOCK_JOB) != null)
				policy.setUnblockJob(BooleanUtils.getBoolean(prop.getProperty(PropertyConstants.POLICY_UNBLOCK_JOB), false));
			
			if(prop.getProperty(PropertyConstants.POLICY_OPERATORS) != null)
				policy.setOperators(prop.getProperty(PropertyConstants.POLICY_OPERATORS));
			
			userCommandClass = prop.getProperty(PropertyConstants.POLICY_USER_COMMAND_CLASS);
			userCommandUrl = prop.getProperty(PropertyConstants.POLICY_USER_COMMAND_URL);
			if(prop.getProperty(PropertyConstants.POLICY_JOB_FAIL_ON_SAME_JOB_RUNNING) != null)
				jobFailOnSameJobRunning = BooleanUtils.getBoolean(prop.getProperty(PropertyConstants.POLICY_JOB_FAIL_ON_SAME_JOB_RUNNING), true);
		}
		else
			System.err.println("Can not read " + PropertyConstants.POLICY_PROPERTIES_FILE + " from classpath");
		
		policy.setCommand(PropertyProvider.executeShell);
		policy.setBuildPath(buildPath);
		policy.setFileSeparator(AgentUtils.SEPARATOR);
		policy.setSamRootPath(runtimeSamPath);
		
	}

	private static void loadRuntimeConfigurations() {
		Properties prop = PropertiesUtil.getProperties(PropertyConstants.RUNTIME_PROPERTIES_FILE);
		if(prop != null && !prop.isEmpty()) {
			runtimeBasePath = prop.getProperty(PropertyConstants.RUNTIME_BASE_PATH);
			runtimeLogPath = prop.getProperty(PropertyConstants.RUNTIME_LOG_PATH);
			runtimeDataSourceContext = prop.getProperty(PropertyConstants.RUNTIME_DATA_SOURCE_CONTEXT);
			
			String killShell = prop.getProperty(PropertyConstants.RUNTIME_KILL_SHELL);
			if(killShell != null && killShell.length() > 0)
				PropertyProvider.killShell = killShell;
			
			String executeShell = prop.getProperty(PropertyConstants.RUNTIME_EXECUTE_SHELL);
			if(executeShell != null && executeShell.length() > 0)
				PropertyProvider.executeShell = executeShell;
			
			String shellPath = prop.getProperty(PropertyConstants.RUNTIME_SHELL_PATH);
			if(shellPath != null && shellPath.length() > 0)
				PropertyProvider.shellPath = shellPath;
			
			String shell = prop.getProperty(PropertyConstants.RUNTIME_SHELL);
			if(shell != null && shell.length() > 0)
				PropertyProvider.shell = shell;
			
			String shellOption = prop.getProperty(PropertyConstants.RUNTIME_SHELL_OPTION);
			if(shellOption != null && shellOption.length() > 0)
				PropertyProvider.shellOption = shellOption;
			
			String buildDir = prop.getProperty(PropertyConstants.RUNTIME_BUILD_PATH);
			if(buildDir != null && buildDir.length() > 0)
				PropertyProvider.buildPath = buildDir;
			
			String envKeyword = prop.getProperty(PropertyConstants.RUNTIME_ENV_KEYWORD);
			if(envKeyword != null && envKeyword.length() > 0)
				PropertyProvider.envKeyword = envKeyword;
			
			String samPath = prop.getProperty(PropertyConstants.RUNTIME_SAM_PATH);
			if(samPath != null && samPath.length() > 0)
				PropertyProvider.runtimeSamPath = samPath;
		}
		else
			System.err.println("Can not read " + PropertyConstants.RUNTIME_PROPERTIES_FILE + " from classpath");
	}
}
