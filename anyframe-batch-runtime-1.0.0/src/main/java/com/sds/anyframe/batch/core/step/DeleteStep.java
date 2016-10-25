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

package com.sds.anyframe.batch.core.step;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.ExitStatus;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class DeleteStep extends AnyframeAbstractStep {

	private static final Log logger = LogFactory.getLog(DeleteStep.class);

	@Override
	protected ExitStatus doExecute(StepExecution stepExecution) throws Exception {

		List<BatchResource> resources = this.getResources();
		
		for (BatchResource resource : resources) {
			String url = resource.getUrl();
			File file = new File(url);
			
			if(!file.exists()) {
				logger.info("file does not exist: " + url);
				continue;
			}
			
			boolean bSuccess = file.delete();
			
			if(bSuccess) {
				logger.info("succeed to delete file : " + url);
			} else {
				throw new BatchRuntimeException("fail to delete file : " + url);
			}
		}
		
		return ExitStatus.CONTINUABLE;
	}
	
}
