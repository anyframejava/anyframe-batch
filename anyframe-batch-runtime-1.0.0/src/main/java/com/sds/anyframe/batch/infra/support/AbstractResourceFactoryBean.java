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

package com.sds.anyframe.batch.infra.support;

import org.springframework.beans.factory.FactoryBean;
import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.vo.transform.Transform;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AbstractResourceFactoryBean implements FactoryBean {

	protected static final String ID = "id";
	protected static final String URL = "url";
	
	protected static final String QUERY_FILE = "query_file";
	protected static final String BATCH_UPDATE = "batchUpdate";
	protected static final String CHAR_SET = "charset";
	protected static final String TRIM = "trim";
	protected static final String FIXED = "fixed";
	protected static final String BUFFER_SIZE = "buffersize";
	protected static final String DELETE_EMPTY = "deleteEmpty";
	protected static final String MAX_SIZE = "maxsize";
	protected static final String ESCAPE = "escape"; 
	
	protected static final String LINE_SEPARATOR = "linesep";
	protected static final String LINE_SEPARATOR_SYSTEM = System.getProperty("line.separator");
	protected static final String LINE_SEPARATOR_SYSTEM_PRINT = LINE_SEPARATOR_SYSTEM.compareTo("\n")==0 ? "\\n" : "\\r\\n";
	
	protected static final String COLUMN_SEPARATOR = "colsep";
	protected static final String COLUMN_SEPARATOR_DEFAULT = ",";
	
	
	protected BatchResource resource = null;
	protected Class<? extends Transform> transformClass;

	public void setResource(BatchResource resource) {
		this.resource = resource;
	}
	
	public void setTransformClass(Class<? extends Transform> transformClass) {
		this.transformClass = transformClass;
	}

	public boolean isSingleton() {
		return false;
	}
	

	public Class<?> getObjectType() {
		return null;
	}
	
	protected String getLineSeparator(String separator) {
		
		if(separator.compareToIgnoreCase("CR") == 0 ||
		   separator.compareToIgnoreCase("MAC") == 0 ||
		   separator.compareToIgnoreCase("\\r") == 0)
			return "\r";
		
		if(separator.compareToIgnoreCase("LF") == 0 ||
		   separator.compareToIgnoreCase("UNIX") == 0 ||
		   separator.compareToIgnoreCase("\\n") == 0)
			return "\n";
		
		if(separator.compareToIgnoreCase("CRLF") == 0 ||
	       separator.compareToIgnoreCase("DOS") == 0 ||
	       separator.compareToIgnoreCase("WINDOWS") == 0 ||
		   separator.compareToIgnoreCase("\\r\\n") == 0 )
			return "\r\n";
		
		return separator;
		
	}
}
