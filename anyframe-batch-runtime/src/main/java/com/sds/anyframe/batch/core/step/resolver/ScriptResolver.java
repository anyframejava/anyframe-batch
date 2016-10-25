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

package com.sds.anyframe.batch.core.step.resolver;

import java.util.Set;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface ScriptResolver {

	/**
	 * 실행하고자 하는 원본 Shell Script 파일 (Called by Framework)
	 * 
	 * @param scripts 스크립트의 각 line 들로 구성 
	 */
	void setScript(String scripts);

	/**
	 * {@link #setScript(String[])} 로 설정된 스크립트가 실행해도 되는 유효한 스크립트인지 판단
	 * 
	 * @return true: 
	 */
	boolean isValid();

	Set<String> getReadFiles();
	Set<String> getWriteFiles();
	Set<String> getRewriteFiles();
}
