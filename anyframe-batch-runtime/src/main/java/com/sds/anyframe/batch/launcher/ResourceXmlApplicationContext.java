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

package com.sds.anyframe.batch.launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ResourceXmlApplicationContext extends
		AbstractXmlApplicationContext {

	private List<Resource> configResources;
	
	public ResourceXmlApplicationContext() {
	}

	public ResourceXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}

	public ResourceXmlApplicationContext(Resource configResource) throws BeansException {
		this(new Resource[] {configResource}, true, null);
	}

	public ResourceXmlApplicationContext(Resource[] configResources) throws BeansException {
		this(configResources, true, null);
	}

	public ResourceXmlApplicationContext(Resource[] configResources, ApplicationContext parent) throws BeansException {
		this(configResources, true, parent);
	}

	public ResourceXmlApplicationContext(Resource[] configResources, boolean refresh) throws BeansException {
		this(configResources, refresh, null);
	}

	public ResourceXmlApplicationContext(Resource[] configResources, boolean refresh, ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigResources(Arrays.asList(configResources));
		if (refresh) {
			refresh();
		}
	}
	
	@Override
	protected Resource[] getConfigResources() {
		if(configResources == null)
			return null;
		
		return configResources.toArray(new Resource[configResources.size()]);
	}
	
	public void setConfigResources(List<Resource> configResources) {
		this.configResources = new ArrayList<Resource>(configResources);
	}
	
	public void addConfigResource(Resource configResource) {
		this.configResources.add(configResource);
	}
	
	
	protected Resource getResourceByPath(String path) {
		if (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}
}
