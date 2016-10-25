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

package com.sds.anyframe.batch.aop.intercept;

import java.text.DecimalFormat;

import org.hyperic.sigar.SigarException;

import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.properties.ClientConfigurations;
import com.sds.anyframe.batch.performance.JvmManager;
import com.sds.anyframe.batch.performance.StandardCpuManager;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PerformanceManager {
	StandardCpuManager cpu;
	JvmManager jvm = new JvmManager();
	Performance performance = new Performance();
	
	public PerformanceManager() {
	}
	
	public Performance getPerformance() throws SigarException {
		
		if(ClientConfigurations.getConfigurations().isSupportCpuUsage()) {
			if(cpu == null)
				cpu = new StandardCpuManager();
			
			double currentCpuUsage = cpu.getCurrentCpuUsage();
			double totalCpuUsage = cpu.getTotalCpuUsage();
			
			performance.setCurrentCpuUsage(roundTwoDecimals(currentCpuUsage));
			performance.setTotalCpuUsage(roundTwoDecimals(totalCpuUsage));
		}

		performance.setActiveThreadCount(jvm.getActiveThreadCount());
		performance.setFreeMemory(jvm.getFreeMemory());
		performance.setTotalMemory(jvm.getTotalMemory());

		return performance;
	}

	static double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
}
