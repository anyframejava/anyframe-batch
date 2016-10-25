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

package com.sds.anyframe.batch.agent.model;

import java.io.Serializable;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JobInfoKey implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private long pid;

	public JobInfoKey(String ip, long pid) {
		this.ip = ip;
		this.pid = pid;
	}

	public long getPid() {
		return pid;
	}

	public String getIp() {
		return ip;
	}

	@Override
	public String toString() {
		return String.format("ip : %s, pid : %d", ip, pid);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + (int) (pid ^ (pid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JobInfoKey))
			return false;
		JobInfoKey other = (JobInfoKey) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (pid != other.pid)
			return false;
		return true;
	}

}
