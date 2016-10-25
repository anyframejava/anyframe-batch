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

package com.sds.anyframe.batch.agent.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.Step;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AgentUtils {
	public static final String SEPARATOR = System.getProperty("file.separator");
	
	// Must be private.
	private static String IP;
	static {
		try {
			IP = InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get Process Id
	 * 
	 * @return process id
	 */
	public static long getPid() {
		String processName = ManagementFactory.getRuntimeMXBean().getName();
		return Long.valueOf(processName.split("@")[0]);
	}

	/**
	 * Get local host address
	 * 
	 * @return local ip
	 */
	public static String getIp() {
		return IP;
	}

	public static String toStepString(Step step) {
		return String.format("job id: %s, pid: %s, step id: %s, job seq : %s, status: %s, resources: %s %s", step
				.getJobId(), step.getPid(), step.getStepId(), step.getJobSeq(), step.getStepStatus(), step
				.getResources(), step.getIp());
	}

	public static String toJobString(Job job) {
		return String.format("job id: %s, pid: %s, job seq: %s, status: %s, ip: %s, log files: %s", job.getJobId(), job.getPid(), job
				.getJobSeq(), job.getJobStatus(), job.getIp(), job.getLogFiles());
	}

	public static String getStackTraceString(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
	
	public static String getInput(InputStream inputStream)
			throws InterruptedException, IOException {
		int available = inputStream.available();
		if (available == 0)
			return null;
		byte[] buf = new byte[available];

		inputStream.read(buf);

		String str = new String(buf);
		return str;
	}
}
