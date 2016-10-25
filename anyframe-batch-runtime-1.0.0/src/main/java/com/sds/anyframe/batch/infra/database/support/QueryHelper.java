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

package com.sds.anyframe.batch.infra.database.support;

import org.apache.commons.lang.StringUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class QueryHelper {

	public static String bindParameterToQuery(String sqlQuery, Object[] params) {
		if(params == null || params.length == 0)
			return sqlQuery;
		
		String bindQuery = sqlQuery;
		try {
			for (int i = 0; i < params.length; i++) {
				bindQuery = StringUtils.replace(bindQuery, "?", "'" + params[i] + "'", 1);
			}
			return bindQuery;
			
		} catch (Exception e) {
			return sqlQuery;
		}
	}
}
