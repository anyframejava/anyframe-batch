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

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.sds.anyframe.batch.agent.util.AgentUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StandardCpuManager{
	
	private Sigar sigar;
	private ProcCpu procCpu;
	
	/** The average load. */
	private double averageLoad;
	
	private double totalLoad;
	private int count = -1;
	
	public StandardCpuManager() {
		sigar = new Sigar();
		
	}
	
	/**
	 * A snapshot of the load that the Virtual Machine is placing on all processors in the host computer. If the host contains
	 * multiple processors, the value represents a snapshot of the average load.
	 * @throws SigarException 
	 * @returns The value is returned as a double, where 1.0 represents 100% load (no idle time) and 0.0 represents 0% load (pure idle time) 
	 */
	public double getCurrentCpuUsage() throws SigarException {
		
		procCpu = sigar.getProcCpu(AgentUtils.getPid());
		
		averageLoad = procCpu.getPercent(); 
		
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
