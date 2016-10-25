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

package com.sds.anyframe.batch.infra.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemReader;
import com.sds.anyframe.batch.infra.database.support.NamedParameterUtils;
import com.sds.anyframe.batch.infra.database.support.ParsedSql;
import com.sds.anyframe.batch.infra.database.support.QueryHelper;
import com.sds.anyframe.batch.infra.database.support.QueryManager;
import com.sds.anyframe.batch.util.ClassUtil;

/**
 * Normal SQL Reader 
 * Connection will be assigned for each SQL Reader.
 * 
 *  @author Hyoungsoon Kim 
 * 
 */

public class SQLReader extends AbstractItemReader implements ItemStream {
	public static final int VALUE_NOT_SET = -1;
	
	static private final Object NOT_NULL = new Object();
	private static Log staticQueryLogger = LogFactory.getLog("StaticQueryLogger");
	private static Log runningQueryLogger = LogFactory.getLog("RunningQueryLogger");
	
	private DataSource dataSource = null;
	private Connection connection = null;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	private String sqlQuery = null;
	private String queryPath = "";
	
	private SQLExceptionTranslator exceptionTranslator;
	
	private int fetchSize = VALUE_NOT_SET;

	private boolean bShowRunningQuery = false;
	private boolean bShowStaticQuery = true;

	private boolean initialized = false;
	
	private boolean useVo = false;
	private Class<?> voClass = null;
	
	private Object[] sqlParams = null;
	private List<String> parameterNames = null;


	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public String getURL() {
		return this.dataSource.toString();
	}
	
	@Override
	public boolean deleteResource() {
		throw new BatchRuntimeException("can not delete database resource");
	}
	
	public void setVo(Object vo) {
		if(vo == null) {
			this.useVo = false;
			this.voClass = null;
	
		} else {
			this.useVo = true;
			this.voClass = vo.getClass();
		}
	}
	
	@Override
	public void loadSQL(String queryID) {

		String query = QueryManager.getQueryByID(queryPath, queryID);

		if(query == null) {
			throw new BatchRuntimeException("query [id=" + queryID + "] does not exist in query file[" + queryPath + "]");
		}

		setQueryInternal(query);
	}
	
	@Override
	public void setQuery(String query) {
		if(BatchDefine.IS_CUSTOM_SQL_ALLOWED)
			setQueryInternal(query);
		else
			throw new BatchRuntimeException("IS_CUSTOM_SQL_ALLOWED is false");
	}
	
	@SuppressWarnings("unchecked")
	private void setQueryInternal(String query) {
		// clear all data from previous operation.
		reset();
		
		if(bShowStaticQuery)
			staticQueryLogger.debug("set SQL query.\n" + query);
		
		this.sqlQuery = query;
		
		// get connection lazily in order for unused readers not to get connection from database
		if (connection == null) {
			if(BatchDefine.READER_DB_SHARE_CONNECTION)
				connection = DataSourceUtils.getConnection(dataSource);
			else {
				try {
					connection = dataSource.getConnection();	// each connection
				}
				catch (SQLException ex) {
					throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
				}
			}
		}
			
		JdbcUtils.closeStatement(preparedStatement);
		preparedStatement = null;
		
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sqlQuery);
		
		if(parsedSql.getNamedParameterCount() > 0) {
			this.parameterNames  = parsedSql.getParameterNames();
			sqlQuery = NamedParameterUtils.substituteNamedParameters(parsedSql,null);
		}
			
		try {
			preparedStatement = this.connection.prepareStatement(sqlQuery,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
					ResultSet.HOLD_CURSORS_OVER_COMMIT);
			
			if (fetchSize != VALUE_NOT_SET) {
				preparedStatement.setFetchSize(fetchSize);
				preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
			}
			
		} catch (SQLException ex) {
			JdbcUtils.closeStatement(preparedStatement);
			preparedStatement = null;
			throw getExceptionTranslator().translate("invalid SQL.", sqlQuery, ex);
		}
	}
	
	@Override
	public void setSqlParameters(Object... parameters) throws SQLException {
		Assert.notNull(preparedStatement, "Load query first");
		
		reset();

		if(parameters == null || parameters.length == 0)
			return;
		
		if(parameters.length == 1 && ClassUtil.isValueObject(parameters[0])) {
			sqlParams = (Object[]) transform.encodeVo(parameters[0], preparedStatement, parameterNames);
			
		} else {
			sqlParams = parameters;
			for(int index = 1; index <= parameters.length; index++) {
				preparedStatement.setObject(index, parameters[index]);
			}
		}
		
		// log running query
		if(bShowRunningQuery && runningQueryLogger.isDebugEnabled()){
			runningQueryLogger.debug("Running SQL Query:\n " + QueryHelper.bindParameterToQuery(sqlQuery, sqlParams));
		}
	}

	@Override
	public ResultSet getResultSet() throws Exception {
		if (!initialized) {
			if(preparedStatement == null) {
				throw new NullPointerException("SQL query must be set in advance");
			}
			
			try {
				resultSet = preparedStatement.executeQuery();
			} catch (SQLException se) {
				String query = sqlParams == null ? sqlQuery : QueryHelper.bindParameterToQuery(sqlQuery, sqlParams);
				runningQueryLogger.error("Error SQL Query:\n" + query);
				throw se;
				
			}
			initialized = true;
		}
		return this.resultSet;
	}
	
	public Object read() throws UnexpectedInputException, ParseException, Exception {
		
		getResultSet();
			
		if (!resultSet.next()) {
			return null;
		}
		
		increaseItemCount();

		if(this.useVo)
			return this.transform.decodeVo(resultSet, this.voClass);
		else
			return NOT_NULL;
			
	}
	
	@Override
	public void reset(){
		
		this.transform.clear();
		
		if (resultSet != null) {
			JdbcUtils.closeResultSet(resultSet);
			resultSet = null;
		}

		initialized = false;
	}
	
	@Override
	public void close(ExecutionContext executioncontext) throws ItemStreamException {
		initialized = false;
		
		if (resultSet != null) {
			JdbcUtils.closeResultSet(resultSet);
			resultSet = null;
		}
		if (preparedStatement != null) {
			JdbcUtils.closeStatement(preparedStatement);
			preparedStatement = null;
		}
		if (connection != null) {
			JdbcUtils.closeConnection(connection);
			connection = null;
		}
	}
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	@Override
	public void open(ExecutionContext executioncontext) throws ItemStreamException {
		
	}
	
	@Override
	public void setQueryPath(String path) {
		this.queryPath  = path;
	}

	@Override
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	
	@Override
	public void showQueryLog(boolean showLog) {
		bShowRunningQuery  = showLog;
	}
	
	public void showStaticQuery(boolean bShow) {
		this.bShowStaticQuery = bShow;
	}
	
	protected SQLExceptionTranslator getExceptionTranslator() {
		if (exceptionTranslator == null) {
			if (dataSource != null) {
				exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(
						dataSource);
			} else {
				exceptionTranslator = new SQLStateSQLExceptionTranslator();
			}
		}
		return exceptionTranslator;
	}
	
	/**
	 * raw APIs
	 */
	
	@Override
	public byte[] getBytes(int index) throws Exception {
		return this.resultSet.getBytes(index+1);
	}
	
	@Override
	public int getInt(int index) throws Exception {
		return this.resultSet.getInt(index+1);
	}

	@Override
	public String getString(int index) throws Exception {
		return this.resultSet.getString(index+1);
	}
	
	@Override
	public BigDecimal getBigDecimal(int index) throws Exception {
		return this.resultSet.getBigDecimal(index+1);
	}
	
	public Boolean getBoolean(int index) throws Exception {
		return this.resultSet.getBoolean(index+1);
	}
	
	public Timestamp getTimestamp(int index) throws Exception {
		return this.resultSet.getTimestamp(index+1);
	}
	
	public Time getTime(int index) throws Exception {
		return this.resultSet.getTime(index+1);
	}
	
	public Character getCharacter(int index) throws Exception {
		return this.resultSet.getString(index+1).charAt(0);
	}
	
	public Byte getByte(int index) throws Exception {
		return this.resultSet.getByte(index+1);
	}
	
	public Date getDate(int index) throws Exception {
		return this.resultSet.getDate(index+1);
	}
	
	public Double getDouble(int index) throws Exception {
		return this.resultSet.getDouble(index+1);
	}
	
	public Float getFloat(int index) throws Exception {
		return this.resultSet.getFloat(index+1);
	}
	
	public Long getLong(int index) throws Exception {
		return this.resultSet.getLong(index+1);
	}
	
	public BigInteger getBigInteger(int index) throws Exception {
		return BigInteger.valueOf(this.resultSet.getLong(index+1));
	}
	
	public Short getShort(int index) throws Exception {
		return this.resultSet.getShort(index+1);
	}

}
