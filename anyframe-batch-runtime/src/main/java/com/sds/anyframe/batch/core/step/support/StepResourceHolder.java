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

package com.sds.anyframe.batch.core.step.support;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.config.BatchResource.Type;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepResourceHolder {

	private static List<BatchResource> resources = null;
	private static final Log LOGGER = LogFactory.getLog(StepResourceHolder.class);
	
	public static void setResource(List<BatchResource> res) {
		//resources = new ArrayList<BatchResource>(res);
		resources = res;
	}
	
	public static void clear() {
		resources = null;
	}
	
	public static void deleteFiles() {
		if(resources == null)
			return;
		
		Set<String> deleteFiles = new HashSet<String>();
			
		for(BatchResource resource : resources) {
			String url = resource.getUrl();
			Mode mode = resource.getMode();
			Type type = resource.getType();
			
			if(type == Type.FILE &&
			   mode == Mode.WRITE ||
			   mode == Mode.DELETE) {
				
				deleteFiles.add(url);
			}
			
		}

		for (String url : deleteFiles) {
			
			File file = new File(url);

			if(file.exists()) {
				if(!file.delete()) {
					LOGGER.info("Fail to delete an incomplete file: " + url);
					LOGGER.info("Register an incomplete file to delete on exit");
					file.deleteOnExit();
				} else {
					LOGGER.info("Succeed to delete an incomplete file: " + url);
				}
			} else {
				LOGGER.info("Incomplete file does not exist: " + url);
			}

		}
	}
}
