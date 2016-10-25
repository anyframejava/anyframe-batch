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

package com.sds.anyframe.batch.manager.dialog;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.model.Resource;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ResourceAndFile {

	Resource resource;
	FileInfoVO file;
	
	public ResourceAndFile(Resource resource, FileInfoVO fileInfoVO) {
		this.resource = resource;
		this.file = fileInfoVO;
	}

	public FileInfoVO getFile() {
		return file;
	}

	public void setFile(FileInfoVO file) {
		this.file = file;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
