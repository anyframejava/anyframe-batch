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

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.repeat.ExitStatus;

import com.sds.anyframe.batch.common.util.ParameterReplacer;
import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.define.BatchDefine;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepUrlReplaceListener implements StepExecutionListener{

	public ExitStatus afterStep(StepExecution stepExecution) {
		return null;
	}

	public void beforeStep(StepExecution stepExecution) {
		
		@SuppressWarnings("unchecked")
		List<BatchResource> resources = (List<BatchResource>) stepExecution
				.getExecutionContext().get(BatchDefine.STEP_RESOURCE_LIST);

		for(BatchResource resource : resources) {
			String url = resource.getUrl();
			resource.setUrlOrg(url);
			url = ParameterReplacer.replaceParameters(url);
			
			resource.setUrl(url);
			
			if(resource.hasChildren()) {
				
				for(BatchResource childResource : resource.getChildResource()) {
					String subUrl = childResource.getUrl();
					childResource.setUrlOrg(subUrl);
					subUrl = ParameterReplacer.replaceParameters(subUrl);
					
					childResource.setUrl(subUrl);
				}
			}
		}
	}

	public ExitStatus onErrorInStep(StepExecution stepExecution, Throwable e) {
		return null;
	}

}
