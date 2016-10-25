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

package com.sds.anyframe.batch.agent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.sds.anyframe.batch.agent.bean.BeanConstants;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class BatchAgentConditionManager {

	public static boolean validateDatabase() throws Exception {
		Properties properties = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);

		

		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) context
				.getBean(BeanConstants.DATA_SOURCE));

		String sql = properties
				.getProperty(PropertyConstants.BATCH_AGENT_VALIDATIONSQL);
		
		return validateSql(jdbcTemplate, sql);

	}

	private static Boolean validateSql(JdbcTemplate jdbcTemplate, String sql)
			throws Exception {
		Object result = jdbcTemplate.query(sql, new Object[] {},
				new ResultSetExtractor() {
					
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						boolean next = rs.next();
						if (next)
							return true;
						return false;
					}
				});
		return (Boolean) result;
	}
	
	public static String checkWeirdJob() throws Exception {
		Properties properties = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);

		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) context
				.getBean(BeanConstants.DATA_SOURCE));

		String sql = properties
				.getProperty(PropertyConstants.BATCH_AGENT_CHECKWEIRDJOBSQL);
		
		return checkWeirdJobSql(jdbcTemplate, sql);

	}
	
	private static String checkWeirdJobSql(JdbcTemplate jdbcTemplate, String sql)
		throws Exception {
			Object result = jdbcTemplate.query(sql, new Object[] {},
					new ResultSetExtractor() {
						
						public Object extractData(ResultSet rs)
								throws SQLException, DataAccessException {
								
							    StringBuffer weirdJobName = new StringBuffer(); 
								while(rs.next()){
									weirdJobName.append(rs.getString(2));
									weirdJobName.append("(");
									weirdJobName.append(rs.getString(1));
									weirdJobName.append(")");
									weirdJobName.append(" ");
								}
								return weirdJobName.toString();
						}
					});
			return (String) result;
	}
	
	public static int clearWeirdJob() throws Exception {
		Properties properties = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);

		

		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) context
				.getBean(BeanConstants.DATA_SOURCE));

		String sql = properties
				.getProperty(PropertyConstants.BATCH_AGENT_CLEARWEIRDJOBSQL);
		
		return updateSql(jdbcTemplate, sql);

	}
	
	public static int clearWeirdStep() throws Exception {
		Properties properties = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);

		

		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) context
				.getBean(BeanConstants.DATA_SOURCE));

		String sql = properties
				.getProperty(PropertyConstants.BATCH_AGENT_CLEARWEIRDSTEPSQL);
		
		return updateSql(jdbcTemplate, sql);

	}
	
	
	public static int clearWeirdResource() throws Exception {
		Properties properties = PropertiesUtil
				.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);

		

		final WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

		JdbcTemplate jdbcTemplate = new JdbcTemplate((DataSource) context
				.getBean(BeanConstants.DATA_SOURCE));

		String sql = properties
				.getProperty(PropertyConstants.BATCH_AGENT_CLEARWEIRDRESOURCESQL);
		
		return updateSql(jdbcTemplate, sql);

	}
	
	private static int updateSql(JdbcTemplate jdbcTemplate, String sql)
	throws Exception {
		Object result = jdbcTemplate.update(sql);
		return (Integer) result;
	}
}
