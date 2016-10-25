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

package com.sds.anyframe.batch.manager.view;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ServerInfo {
	private String serverName;
	private String address;
	private String workingDirectory;
	
	public ServerInfo(String ip) {
		this.address = ip;
	}
	public ServerInfo() {
		// TODO Auto-generated constructor stub
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String ip) {
		this.address = ip;
	}
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
	

}
