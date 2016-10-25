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

import java.io.IOException;

import org.apache.log4j.Logger;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class RuntimeInterceptor {
	private static Logger log = Logger.getLogger(RuntimeInterceptor.class);
	
	protected abstract void connectToHessianService() throws IOException,	ClassNotFoundException, Exception ;

	protected boolean reConnection() throws Exception {
		try {
			connectToHessianService();
			return true;
		} catch (Exception e) {
			log.error("There is no live agent server neither Primary nor Secondary agent.");
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
