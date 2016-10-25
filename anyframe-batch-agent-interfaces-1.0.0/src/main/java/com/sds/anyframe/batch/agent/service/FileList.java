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

import java.io.FileNotFoundException;
import java.util.List;

import com.sds.anyframe.batch.agent.model.FileInfoVO;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface FileList {
	public List<FileInfoVO> getList(String jobPath, String start, String end);
	public List<FileInfoVO> getList2(List<String> files);
	public List<FileInfoVO> getSearchList(String jobPath, String fileName, String start, String end) throws FileNotFoundException;
	public String getBaseDir();
}
