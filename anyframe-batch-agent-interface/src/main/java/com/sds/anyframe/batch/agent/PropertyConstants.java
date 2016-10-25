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

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PropertyConstants {
	public static final String BATCH_PROPERTIES_FILE = "batch.properties";
	public static final String JDBC_PROPERTIES_FILE = "jdbc.properties";
	public static final String SERVER_PROPERTIES_FILE = "server.properties";
	public static final String RUNTIME_PROPERTIES_FILE = "runtime.properties";
	public static final String POLICY_PROPERTIES_FILE = "agent.policy";
	public static final String SECURITY_PROPERTIES_FILE = "security.properties";
	
	public static final String BATCH_AGENT_MAIN = "batch.agent.main";
	public static final String BATCH_PING_INTERVAL_SEC = "batch.ping.interval.sec";
	public static final String BATCH_AGENT_ON = "batch.agent.on";
	public static final String BATCH_AGENT_CHANNEL_GROUP_NAME = "batch.agent.channel.group.name";
	public static final String BATCH_RUNNING_JOB_MAX_COUNT = "batch.running.job.max.count";
	public static final String BATCH_RUNNING_JOB_MAX_BEFORE = "batch.running.job.max.before";
	public static final String RESOURCE_CHECK_INTERVAL_SECONDS = "resource.check.interval.seconds";
	public static final String READY_CHECK_INTERVAL_SECONDS = "ready.check.interval.seconds";
	public static final String BATCH_JOB_BLOCKING_TIMEOUT = "batch.job.blockingTimeout";
	public static final String BATCH_CHECK_META = "batch.check.meta";
	
	public static final String BATCH_AGENT_SECONDARY_PORT = "batch.agent.port.secondary";
	public static final String BATCH_AGENT_VALIDATIONSQL = "batch.agent.database.validationSql";
	public static final String BATCH_AGENT_CHECKWEIRDJOBSQL = "batch.agent.database.checkweirdjobSql";
	public static final String BATCH_AGENT_CLEARWEIRDJOBSQL = "batch.agent.database.clearweirdjobSql";
	public static final String BATCH_AGENT_CLEARWEIRDSTEPSQL = "batch.agent.database.clearweirdstepSql";
	public static final String BATCH_AGENT_CLEARWEIRDRESOURCESQL = "batch.agent.database.clearweirdresourceSql";
	
	public static final String BATCH_RUNNING_SIMULATION_MODE = "batch.running.simulation.mode";
	
	public static final String RUNTIME_BASE_PATH = "runtime.basePath";
	public static final String RUNTIME_LOG_PATH = "runtime.logPath";
	public static final String RUNTIME_DATA_SOURCE_CONTEXT = "runtime.dataSourceContext";
	public static final String RUNTIME_EXECUTE_SHELL = "runtime.executeShell";
	public static final String RUNTIME_SHELL_PATH = "runtime.shellPath";
	public static final String RUNTIME_SHELL = "runtime.shell";
	public static final String RUNTIME_SHELL_OPTION = "runtime.shell.option";
	public static final String RUNTIME_KILL_SHELL = "runtime.killShell";
	public static final String RUNTIME_BUILD_PATH = "runtime.buildPath";
	public static final String RUNTIME_ENV_KEYWORD = "runtime.envKeyword";
	public static final String RUNTIME_SAM_PATH = "runtime.samPath";
	
	public static final String POLICY_KILL_JOB = "policy.killJob";
	public static final String POLICY_RUN_JOB = "policy.runJob";
	public static final String POLICY_UNBLOCK_JOB = "policy.unblockJob";
	public static final String POLICY_OPERATORS = "policy.operators";
	public static final String POLICY_JOB_FAIL_ON_SAME_JOB_RUNNING = "policy.jobFailOnSameJobRunning";
	public static final String POLICY_USER_COMMAND_CLASS = "policy.userCommandClass";
	public static final String POLICY_USER_COMMAND_URL = "policy.userCommandUrl";
	
	public static final String BATCH_JOB_QUEUEINGTIME = "batch.job.queueingtime";
	public static final String BATCH_RESOURCE_QUEUEINGTIME = "batch.resource.queueingtime";
	
	public static final String SECURITY_USERID = "security.userid";
	public static final String SECURITY_PASSWORD = "security.password";
	
	public static final String BATCH_SUPPORT_CPUUSAGE = "batch.monitor.supportCpuUsage";
}
