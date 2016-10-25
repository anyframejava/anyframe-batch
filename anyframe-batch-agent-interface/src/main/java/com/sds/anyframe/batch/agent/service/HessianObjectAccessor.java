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

package com.sds.anyframe.batch.agent.service;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.sds.anyframe.batch.util.FailOverUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class HessianObjectAccessor {
	private static final Logger logger = Logger.getLogger(HessianObjectAccessor.class);
	
	private static final String CONTEXT_PATH = "batchagent";

	private HessianObjectAccessor() {
		new AssertionError("cannot create this instance.");
	}

	public static Object getRemoteObject(String servers, String urlMapping,
			String interfaceName) throws IOException, ClassNotFoundException, Exception {
		return getRemoteObject(servers, urlMapping, interfaceName, -1, false);
	}
	
	public static Object getRemoteObject(String servers, String urlMapping,
			String interfaceName, int timeout, boolean applyRandom) throws IOException, ClassNotFoundException, Exception {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		
		if(timeout > 0)
			factory.setReadTimeout(timeout);
		
		String context_service = CONTEXT_PATH + "/" + urlMapping + ".do";
		
		String url = FailOverUtils.getAvailableServerUrl(servers, context_service, timeout,	applyRandom);

		factory.setServiceUrl(url);
		try {
			factory.setServiceInterface(Class.forName(interfaceName));
		}
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		factory.afterPropertiesSet();
		return factory.getObject();
	}

}
