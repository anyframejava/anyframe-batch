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

package com.sds.anyframe.batch.agent.management;

import java.io.Serializable;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AgentCondition implements Serializable{
	
	private static final long serialVersionUID = -3417533842133462538L;
	
	private boolean systemAgent;
	
	private ClusteredCondition clusteredCondition = new ClusteredCondition();
	
	public void setSystemAgent(boolean b) {
		systemAgent = b;
	}

	public boolean isSystemAgent() {
		return systemAgent;
	}

	@Override
	public String toString() {
		return String.format("system agent = %s, Clustered Condtions=%s",
				Boolean.valueOf(systemAgent), clusteredCondition.toString());
	}

	public ClusteredCondition getClusteredCondition() {
		return clusteredCondition;
	}

	public synchronized void setClusteredCondition(ClusteredCondition clusteredCondition) {
		this.clusteredCondition = clusteredCondition;
	}
}
