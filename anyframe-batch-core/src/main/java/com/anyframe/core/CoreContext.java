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

package com.anyframe.core;

import com.anyframe.core.event.EventDistributor;
import com.anyframe.core.event.impl.EventDistributorImpl;
import com.anyframe.core.vo.meta.MetadataManager;
import com.anyframe.core.vo.meta.impl.MetadataManagerImpl;

/**
 * Framework CoreContext 
 * provides factory API for MetadataManager and EventDistributor. To get the reference of Object use this class.
 * 
 * @author prever.kang
 *
 */
public class CoreContext {
	private static final EventDistributor eventDistributor = EventDistributorImpl.getInstance();

	private static MetadataManager metaManager = MetadataManagerImpl.getInstance();
	
	public void setMetaManager(MetadataManager metaManager) {
		CoreContext.metaManager = metaManager;
	}

	public static MetadataManager getMetaManager() {
		return metaManager;
	}
	
	public static EventDistributor getEventDistributor() {
		return eventDistributor;
	}
}
