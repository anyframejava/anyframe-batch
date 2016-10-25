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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.service.HessianObjectAccessor;
import com.sds.anyframe.batch.agent.service.JobLauncher;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class RemoteJobLauncherImpl {
	private static final int _SUCCESS = 0;

	private static final int _3000 = 3000;

	private static final Logger logger = Logger
			.getLogger(RemoteJobLauncherImpl.class);

	private String ip;

	private String pPort;

	private String secondaryPort;

	private String servers;

	private String serviceExecutionId;

	public RemoteJobLauncherImpl() {
	}

	public RemoteJobLauncherImpl(String serviceExecutionId) {
		this.serviceExecutionId = serviceExecutionId;
	}
	
	public static void main(String[] args) {
		if(args == null || args.length <2) {
			System.err.println("at least 2 arguements are needed");
			System.exit(1);
		}
		
		RemoteJobLauncherImpl launcher = new RemoteJobLauncherImpl();
		launcher.setServers(args[0]);
		
		List<String> argList = new ArrayList<String>();
		for(int i=1; i<args.length; i++) {
			argList.add(args[i]);
		}
		
		try {
			int ret = launcher.launchOnSynchronization(0,argList.toArray(new String[argList.size()]));
			
			System.exit(ret);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	public int launchOnSynchronization(int timeout, String... args) {
		return launchInternal(timeout, true, args);
	}

	public void launch(String... args) {
		launchInternal(_3000, false, args);
	}
	
	public void launch(int timeout, String... args) {
		launchInternal(timeout, false, args);
	}
	
	private int launchInternal(int timeout, boolean synchronization, String... args) {
		createJobExecutionId(args);
		logger.info(serviceExecutionId);

		if (ip != null) {
			String tmp = ip + ":" + pPort;
			if (secondaryPort != null)
				tmp += "; " + ip + ":" + secondaryPort;
			if (servers == null)
				servers = tmp;
			else
				servers = tmp + "; " + servers;
		}

		try {
			JobLauncher jobLauncher = (JobLauncher) HessianObjectAccessor
			.getRemoteObject(servers, "jobLauncher", JobLauncher.class
					.getName(), timeout, true);
			
			if(!synchronization)
				jobLauncher.launch(args);
			else
				return jobLauncher.launchOnSynchronization(args);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		logger.info("Remote Batch Job called successfully.");
		
		return _SUCCESS;
	}

	private void createJobExecutionId(String... args) {
		String jobId = Long.toString(Thread.currentThread().getId());
		
		int index = args[0].lastIndexOf("/");

		if (index > -1)
			jobId  = args[0].substring(index + 1, args[0].length()) + "-" + jobId; 
		
		String detail = getJobInformation(args);
		
		if (serviceExecutionId != null)
			serviceExecutionId = serviceExecutionId + "-" + jobId;
		else
			serviceExecutionId = jobId;
		
		serviceExecutionId += detail;
	}

	private String getJobInformation(String... args) {
		StringBuilder jobInfo = new StringBuilder();

		if (args.length > 1) {
			jobInfo.append("-");
			jobInfo.append("[");
			for (int i = 1; i < args.length; i++) {
				jobInfo.append(args[i]);
				if(i < (args.length-1))
					jobInfo.append(",");
			}
			jobInfo.append("]");
		}

		return jobInfo.toString();
	}

	/**
	 * This method is deprecated. Use setServers() method instead of this.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * This method is deprecated. Use setServers() method instead of this.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void setPrimaryPort(String port) {
		this.pPort = port;
	}

	/**
	 * This method is deprecated. Use setServers() method instead of this.
	 */
	@Deprecated
	public String getIp() {
		return this.ip;
	}

	/**
	 * This method is deprecated. Use setServers() method instead of this.
	 */
	@Deprecated
	public void setSecondaryPort(String port) {
		this.secondaryPort = port;
	}

	/**
	 * This method used to set multiple servers to provide fail over mode.
	 * 
	 * @param string e.g. setServers("ip:port;ip2:port;ip3:port")
	 */
	public void setServers(String string) {
		servers = string;
	}

}
