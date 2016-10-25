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

package com.sds.anyframe.batch.manager.model;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class Parameter {

	protected String fNewName;
	protected String fNewValue;
	
	public String getName() {
		return fNewName;
	}

	public void setName(String name) {
		fNewName = name;
	}

	public String getValue() {
		return fNewValue;
	}

	public void setValue(String value) {
		fNewValue = value;
	}

	public Parameter(String name, String value) {
		fNewName = name;
		fNewValue = value;
	}
}
