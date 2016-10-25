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

package com.sds.anyframe.batch.agent.security;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class Policy implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean killJob = true; // default must be true

	private boolean runJob = true;	// default must be true

	private boolean unblockJob = false;
	
	private String command;

	private String buildPath;

	private String fileSeparator = "/";

	private String samRootPath;
	
	@SuppressWarnings("rawtypes")
	private Map operators = new Hashtable();
	
	public boolean canKillJob() {
		return killJob;
	}

	public void canKillJob(boolean killJob) {
		this.killJob = killJob;
	}

	public boolean canRunJob() {
		return runJob;
	}

	public void canRunJob(boolean runJob) {
		this.runJob = runJob;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setBuildPath(String buildDir) {
		this.buildPath = buildDir;
	}

	public String getBuildPath() {
		return buildPath;
	}

	public void setFileSeparator(String separator) {
		this.fileSeparator = separator;
	}

	public String getFileSeparator() {
		return fileSeparator;
	}

	public String getSamRootPath() {
		return samRootPath;
	}

	public void setSamRootPath(String samRootPath) {
		this.samRootPath = samRootPath;
	}

	@SuppressWarnings("rawtypes")
	public Map getOperators() {
		return operators;
	}

	@SuppressWarnings("unchecked")
	public void setOperators(String operators) {
		if(operators != null) {
			String[] split = operators.split(";");
			for(String operator: split) {
				this.operators.put(operator, operator);
			}
		}
	}

	public boolean canUnblockJob() {
		return unblockJob;
	}

	public void setUnblockJob(boolean unblockJob) {
		this.unblockJob = unblockJob;
	}
}
