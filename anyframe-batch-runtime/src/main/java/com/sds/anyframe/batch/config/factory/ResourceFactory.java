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

package com.sds.anyframe.batch.config.factory;

import java.util.Map;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.exception.JobConfigurationException;
import com.sds.anyframe.batch.infra.support.AbstractResourceFactoryBean;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ResourceFactory {
	
	private Map<String, AbstractResourceFactoryBean> resourceTypeMap;
	
	public void setResourceTypeMap(
			Map<String, AbstractResourceFactoryBean> resourceTypeMap) {
		this.resourceTypeMap = resourceTypeMap;
	}

	public Object getResourceHandler(BatchResource resourceDef) throws Exception {
		
		String resourceType = resourceDef.getAttribute("type");
		AbstractResourceFactoryBean resourceFactory = resourceTypeMap.get(resourceType);
	
		if(resourceFactory == null) {
			throw new JobConfigurationException("unknown resource type[" + resourceType + "]");
		}
		resourceFactory.setResource(resourceDef);
		return resourceFactory.getObject();
	}
	
}
