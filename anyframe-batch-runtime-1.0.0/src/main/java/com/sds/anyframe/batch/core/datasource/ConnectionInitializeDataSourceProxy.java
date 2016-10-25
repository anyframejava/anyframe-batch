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

package com.sds.anyframe.batch.core.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import com.sds.anyframe.batch.common.util.ParameterReplacer;

/**
 * DataSource로 부터 connection 을 획득할 때 {@code this.queries}에 설정된 쿼리를 수행하여
 * 초기화 작업을 수행하는 Proxy DataSource. 주로 Session 정보를 등록하는 쿼리 또는 DB 특화된
 * 최적화를 수행하는 쿼리 등을 적용하여 사용한다.
 * 
 * @author hsoon.kim
 *
 */
public class ConnectionInitializeDataSourceProxy extends DelegatingDataSource {

	private static final Log LOGGER = LogFactory.getLog(ConnectionInitializeDataSourceProxy.class);
	
	private boolean initialize = false;
	private List<String> queries = null;
	
	@Override
	public Connection getConnection() throws SQLException {
		DataSource ds = getTargetDataSource();
		Connection con = ds.getConnection();
		
		if(this.initialize) {
			initializeConnection(con);
		}
		
		return con;
	}

	public void setInitialize(boolean bInit) {
		this.initialize = bInit;
	}
	
	public void setQueries(List<String> queries) {
		this.queries = queries;
	}

	private void initializeConnection(Connection con) throws SQLException {
		if(queries == null)
			return;
		
		Statement stmt = null;
		String executeQuery = null;
		
		try {
			stmt = con.createStatement();
			for(String query : queries){
				
				//2012.2.1 zzazazan: replace variables in query at runtime. ex) ${STEP_NAME}
				query = ParameterReplacer.replaceParameters(query);
				
				executeQuery = query;
				stmt.execute(query);
			}
			
		} catch (SQLException e) {
			LOGGER.error("Fail to initialize connection. executing query is \n" + executeQuery);
			throw e;
			
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
}
