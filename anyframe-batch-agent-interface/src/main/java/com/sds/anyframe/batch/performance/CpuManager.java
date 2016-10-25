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

package com.sds.anyframe.batch.performance;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

@SuppressWarnings("restriction")
public class CpuManager {
	/** The check time. */
	private long lastCheckedTime;
	/** The last cpu time. */
	private long lastCpuTime;
	/** The average load. */
	private double averageLoad;
	/** The operation system MBean. */
	private OperatingSystemMXBean mBean;

	private double totalLoad;
	private int count = -1;
	private int availableProcessors;

	public CpuManager() {
		// Obtain MBean
		mBean = (OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();
		
		lastCheckedTime = System.nanoTime();
		// Get latest process CPU time
		lastCpuTime = mBean.getProcessCpuTime();
		
		availableProcessors = mBean.getAvailableProcessors();
	}
	
	/**
	 * A snapshot of the load that the Virtual Machine is placing on all processors in the host computer. If the host contains
	 * multiple processors, the value represents a snapshot of the average load.
	 * @returns The value is returned as a double, where 1.0 represents 100% load (no idle time) and 0.0 represents 0% load (pure idle time) 
	 */
	public double getCurrentCpuUsage() {
		long currCpuTime = mBean.getProcessCpuTime();
		long currTime = System.nanoTime();
		
		averageLoad = (currCpuTime - lastCpuTime + 0.) / ((currTime - lastCheckedTime) * availableProcessors);
		
		averageLoad *= (availableProcessors +0.);
		
		// Since System.nanoTime() may return negative values we need to add this check 
		averageLoad = (averageLoad < 0 ? averageLoad * (-1) : averageLoad);
		lastCheckedTime = currTime;
		lastCpuTime = currCpuTime;
		
		totalLoad += averageLoad;
		
		count++;
		
		return averageLoad;
	}

	public double getTotalCpuUsage() {
		if(count == 0)
			return 0;
		
		return totalLoad / (count+0.);
	}
}
