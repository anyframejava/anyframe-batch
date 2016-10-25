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

public class Performance implements Serializable {

	private static final long serialVersionUID = -2660508720444254404L;

	int activeThreadCount;
	private double currentCpuUsage;
	private double totalCpuUsage;
	private long freeMemory;
	private long totalMemory;

	public int getActiveThreadCount() {
		return activeThreadCount;
	}

	public void setActiveThreadCount(int threadActiveCount) {
		this.activeThreadCount = threadActiveCount;
	}

	public void setCurrentCpuUsage(double cpuUsage) {
		this.currentCpuUsage = cpuUsage;
	}

	public double getCurrentCpuUsage() {
		return currentCpuUsage;
	}

	public void setTotalCpuUsage(double totalCpuUsage) {
		this.totalCpuUsage = totalCpuUsage;
	}

	public double getTotalCpuUsage() {
		return totalCpuUsage;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("totalCpuUsage = ").append(totalCpuUsage).append(
				", currentCpuUsage=").append(currentCpuUsage).append(
				", activeThreadCount=").append(activeThreadCount).append(
				", totalMemory=").append(totalMemory).append(", freeMemory=")
				.append(freeMemory);
		
		return toString.toString();
	}
}
