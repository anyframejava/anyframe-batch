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
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ClearFailedException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.FlushFailedException;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemWriter;
import com.sds.anyframe.batch.infra.database.support.NamedParameterUtils;
import com.sds.anyframe.batch.infra.database.support.ParsedSql;
import com.sds.anyframe.batch.infra.database.support.QueryHelper;
import com.sds.anyframe.batch.infra.database.support.QueryManager;
import com.sds.anyframe.batch.util.ClassUtil;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class SQLWriter extends AbstractItemWriter implements ItemStream {

	private static Log staticQueryLogger = LogFactory.getLog("StaticQueryLogger");
	private static Log runningQueryLogger = LogFactory.getLog("RunningQueryLogger");
	
	private DataSource dataSource = null;
	private PreparedStatement preparedStatement = null;	
	private String sqlQuery;	
	private int sqlRowCount = 0;
	
	private String queryPath = null;

	private SQLExceptionTranslator exceptionTranslator;

	private boolean bShowRunningQuery = false;
	private boolean bShowStaticQuery = true;

	private Object[] sqlParams = null;
	private List<Object> setterParamsList = null;
	private List<String> parameterNames = null;
	
	private boolean useSetter = false;
	private boolean batchUpdate = false;
	
	public int getSqlRowCount() {
		return sqlRowCount;
	}
	
	public void setBatchUpdate(Boolean batchUpdate) {
		this.batchUpdate = batchUpdate;
	}
	
	public boolean isBatchUpdate() {
		return batchUpdate;
	}

	public void setBatchUpdate(boolean batchUpdate) {
		this.batchUpdate = batchUpdate;
	}

	@Override
	public String getURL() {
		return this.dataSource.toString();
	}
	
	@Override
	public boolean deleteResource() {
		throw new BatchRuntimeException("can not delete database resource");
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
		flush();
		
		if(bShowStaticQuery)
			staticQueryLogger.debug("set SQL query.\n" + query);
		
		this.sqlQuery = query;
		
		Connection con = DataSourceUtils.getConnection(dataSource);
		
		JdbcUtils.closeStatement(preparedStatement);
		preparedStatement = null;
		
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sqlQuery);
		
		if(parsedSql.getNamedParameterCount() > 0) {
			this.parameterNames = parsedSql.getParameterNames();
			sqlQuery = NamedParameterUtils.substituteNamedParameters(parsedSql,null);
		}
		
		try {
			preparedStatement = con.prepareStatement(sqlQuery);
			
		} catch (SQLException ex) {
			JdbcUtils.closeStatement(preparedStatement);
			preparedStatement = null;
			throw getExceptionTranslator().translate("invalid SQL. ", sqlQuery, ex);
		}
	}
	
	@Deprecated
	public void setSqlParameters(Object... parameters) throws SQLException {
		
		if(preparedStatement == null) {
			throw new BatchRuntimeException("Query is not loaded. load it first with loadSQL()");
		}
		
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
		
	}

	public void write() throws Exception {
		
		if(preparedStatement == null) {
			throw new BatchRuntimeException("Query is not loaded. load it first with loadSQL()");
		}
		
		try {
			if(batchUpdate) {
				preparedStatement.addBatch();
			}else {
				sqlRowCount=preparedStatement.executeUpdate();
			}
			increaseItemCount();
			// log running query
			if(bShowRunningQuery && runningQueryLogger.isDebugEnabled()){
				convertSqlParams();
				
				runningQueryLogger.debug("Running SQL Query:\n" + QueryHelper.bindParameterToQuery(sqlQuery, sqlParams));
			}
		} catch (Exception e) {
			convertSqlParams();
			runningQueryLogger.error("Error SQL Query:\n" + QueryHelper.bindParameterToQuery(sqlQuery, sqlParams));
			throw e;
		} finally{
			if(setterParamsList!=null){
				setterParamsList.clear();
			}
		}

	}
	
	public void write(Object vo) throws Exception{
		
		if(preparedStatement == null) {
			throw new BatchRuntimeException("Query is not loaded. load it first with loadSQL()");
		}
		
		sqlParams = (Object[]) transform.encodeVo(vo, preparedStatement, parameterNames);
		
		write();
	}
	
	@Override
	public void clear() throws ClearFailedException {
		try {
			// 한번도 write가 수행되지 않은 경우, preparedStatement == null
			if(preparedStatement == null)
				return;
			if(batchUpdate) {
				preparedStatement.clearBatch();
			}
			sqlRowCount=0;
			
		} catch (SQLException e) {
			throw new ClearFailedException("clear error.", e);
		}
	}

	@Override
	public void flush() throws FlushFailedException {
		try {
			// 한번도 write가 수행되지 않은 경우, preparedStatement == null
			if(preparedStatement == null)
				return;
			
			if(batchUpdate) {
				preparedStatement.executeBatch();
			}
			sqlRowCount=0;
		} catch (SQLException e) {
			throw new FlushFailedException("flush error.", e);
		}
		
	}

	@Override
	public void close(ExecutionContext executioncontext) throws ItemStreamException {
		if(preparedStatement != null) {
			JdbcUtils.closeStatement(preparedStatement);
			preparedStatement = null;
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public void setQueryPath(String path) {
		this.queryPath = path;
	}

	@Override
	public void showQueryLog(boolean showLog) {
		bShowRunningQuery = showLog;
	}
	
	public void showStaticQuery(boolean bShow) {
		this.bShowStaticQuery = bShow;
	}
	
	private SQLExceptionTranslator getExceptionTranslator() {
		if (this.exceptionTranslator == null) {
			if (dataSource != null) {
				exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
			}
			else {
				exceptionTranslator = new SQLStateSQLExceptionTranslator();
			}
		}
		return exceptionTranslator;
	}
	
	/**
	 * raw APIs
	 */
	
	@Override
	public void setBytes(int index, byte[] value) throws Exception {
		this.preparedStatement.setBytes(index+1, value);
		addSetterParamsList(value);
	}

	
	@Override
	public void setInt(int index, int value) throws Exception {
		this.preparedStatement.setInt(index+1, value);
		addSetterParamsList(value);
	}
	@Override
	public void setBigDecimal(int index, BigDecimal value) throws Exception {
		this.preparedStatement.setBigDecimal(index+1, value);
		addSetterParamsList(value);
	}
	
	@Override
	public void setString(int index, String value) throws Exception {
		this.preparedStatement.setString(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setBoolean(int index, Boolean value) throws Exception {
		this.preparedStatement.setBoolean(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setShort(int index, Short value) throws Exception {
		this.preparedStatement.setShort(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setBigInteger(int index, BigInteger value) throws Exception {
		this.preparedStatement.setLong(index+1, ((BigInteger)value).longValue());
		addSetterParamsList(value);
	}
	
	public void setLong(int index, Long value) throws Exception {
		this.preparedStatement.setLong(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setFloat(int index, Float value) throws Exception {
		this.preparedStatement.setFloat(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setDouble(int index, Double value) throws Exception {
		this.preparedStatement.setDouble(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setDate(int index, Date value) throws Exception {
		this.preparedStatement.setDate(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setByte(int index, Byte value) throws Exception {
		this.preparedStatement.setByte(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setCharacter(int index, Character value) throws Exception {
		this.preparedStatement.setString(index+1, ((Character)value).toString());
		addSetterParamsList(value);
	}
	
	public void setTime(int index, Time value) throws Exception {
		this.preparedStatement.setTime(index+1, value);
		addSetterParamsList(value);
	}
	
	public void setTimestamp(int index, Timestamp value) throws Exception {
		this.preparedStatement.setTimestamp(index+1, value);
		addSetterParamsList(value);
	}
	
	private void addSetterParamsList(Object value){
		if(setterParamsList==null){
			setterParamsList = new ArrayList<Object>();
			useSetter=true;
		}
		setterParamsList.add(value);
	}
	
	private void convertSqlParams(){ 
		if(useSetter){
			sqlParams=setterParamsList.toArray();
		}
	}
}
