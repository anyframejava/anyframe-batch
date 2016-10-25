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

import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.database.SQLReader;
import com.sds.anyframe.batch.infra.database.SQLWriter;
import com.sds.anyframe.batch.vo.transform.Transform;
import com.sds.anyframe.batch.vo.transform.jdbc.TransformJDBC;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class DatabaseResourceFactoryBean extends AbstractResourceFactoryBean {

	private static final int FETCH_SIZE = 1000;

	public DatabaseResourceFactoryBean() {
		this.transformClass = TransformJDBC.class;
	}
	
	public Object getObject() throws Exception {
		
		String queryPath = resource.getAttribute(QUERY_FILE);
		Boolean batchUpdate = Boolean.parseBoolean(resource.getAttribute(BATCH_UPDATE));
		
		Transform transform = this.transformClass.newInstance();
		Mode mode = resource.getMode();
		
		switch(mode) {
		case READ: {
			SQLReader reader = new SQLReader();
			
			reader.setTransform(transform);
			reader.setQueryPath(queryPath);
			reader.setFetchSize(FETCH_SIZE);
			reader.setItemCountRef(resource.getCountReference());

			return reader;
		}
		case WRITE: {
			SQLWriter writer = new SQLWriter();
			
			writer.setTransform(transform);
			writer.setQueryPath(queryPath);
			writer.setBatchUpdate(batchUpdate);
			writer.setItemCountRef(resource.getCountReference());
			
			return writer;
		}
		default:
			throw new BatchRuntimeException("@type[" + mode + "] is invalid for database");
		}
	}

}
