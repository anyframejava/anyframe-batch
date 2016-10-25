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

public class BatchProgramType {
	private String TypeId;
	String TypeName;
	String BatchServiceClassTemplateName;
	String BatchServiceQueryTemplateName;
	int minWriter;
	int maxWriter;
	int minReader;
	int maxReader;

	public String getTypeName() {
		return TypeName;
	}
	public void setTypeName(String typeName) {
		TypeName = typeName;
	}
	public String getBatchServiceClassTemplateName() {
		return BatchServiceClassTemplateName;
	}
	public void setBatchServiceClassTemplateName(String batchServiceClassTemplateName) {
		BatchServiceClassTemplateName = batchServiceClassTemplateName;
	}
	public String getBatchServiceQueryTemplateName() {
		return BatchServiceQueryTemplateName;
	}
	public void setBatchServiceQueryTemplateName(String batchServiceQueryTemplateName) {
		BatchServiceQueryTemplateName = batchServiceQueryTemplateName;
	}
	public void setTypeId(String typeId) {
		TypeId = typeId;
	}
	public String getTypeId() {
		return TypeId;
	}
	public int getMinWriter() {
		return minWriter;
	}
	public void setMinWriter(int minWriter) {
		this.minWriter = minWriter;
	}
	public int getMaxWriter() {
		return maxWriter;
	}
	public void setMaxWriter(int maxWriter) {
		this.maxWriter = maxWriter;
	}
	public int getMinReader() {
		return minReader;
	}
	public void setMinReader(int minReader) {
		this.minReader = minReader;
	}
	public int getMaxReader() {
		return maxReader;
	}
	public void setMaxReader(int maxReader) {
		this.maxReader = maxReader;
	}
}
