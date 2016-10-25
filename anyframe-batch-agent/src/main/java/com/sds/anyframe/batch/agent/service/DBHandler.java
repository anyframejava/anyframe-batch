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
package com.sds.anyframe.batch.agent.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sds.anyframe.batch.agent.dao.DataSourceManager;
import com.sds.anyframe.batch.agent.model.ColumnInfo;
import com.sds.anyframe.batch.agent.model.TableInfo;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class DBHandler extends Page implements DBHandleSupport {
	private final Logger logger = Logger.getLogger(DBHandler.class);
	private static final int PAGE_SIZE = 100;

	public List<String> getDataSourceList() throws Exception {
		return DataSourceManager.getDataSourceList();
	}

	@Override
	public PageRequest getTopPage(PageRequest request) throws Exception {
		request.startRowNumber = 0;
		request.pageNo = 1;

		setTotalRows(request);

		if (request.totalRowNumber <= 0)
			return request;

		return getPage(request);
	}

	@Override
	public PageRequest getBottomPage(PageRequest request) throws Exception {
		request.setResult(null);

		setTotalRows(request);

		if (request.totalRowNumber <= 0)
			return request;

		request.pageNo = request.totalPageCount;

		return getPage(request);
	}

	private void setTotalRows(PageRequest request) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> parameters = (Map<String, Object>) request
				.getParameter();

		DataSource ds = DataSourceManager.getDataSource((String) parameters
				.get("dataSource"));
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

		String selectedTableName = (String) parameters.get("table");

		request.totalRowNumber = jdbcTemplate
				.queryForInt("SELECT COUNT(0) FROM " + selectedTableName);

		if (request.totalRowNumber <= 0)
			return;

		request.totalPageCount = request.totalRowNumber % PAGE_SIZE == 0 ? request.totalRowNumber
				/ PAGE_SIZE
				: request.totalRowNumber / PAGE_SIZE + 1;
	}

	public List<TableInfo> getDBMetaInfo(String dataSource,
			String tableNamePattern, String type) throws Exception {
		String[] types = type == null ? null
				: type.equals("ALL") ? new String[] { "TABLE", "VIEW", "ALIAS",
						"SYNONYM" } : new String[] { type.toUpperCase() };
		if (tableNamePattern.equals(""))
			throw new IllegalArgumentException(
					"Please, type a table name to search.");
		String tableName = "%" + tableNamePattern.toUpperCase() + "%";
		DataSource ds = DataSourceManager.getDataSource(dataSource);

		List<TableInfo> tableList = new ArrayList<TableInfo>();

		Connection conn = null;
		ResultSet tables = null;
		ResultSet columns = null;
		ResultSet primaryKeys = null;

		try {
			conn = ds.getConnection();
			DatabaseMetaData metaData = conn.getMetaData();
			tables = metaData.getTables(null, null, tableName, types);

			while (tables.next()) {
				TableInfo table = new TableInfo();
				table.setName(tables.getString("TABLE_NAME"));
				table.setRemarks(tables.getString("REMARKS"));
				table.setType(tables.getString("TABLE_TYPE"));
				table.setDataSource(dataSource);

				primaryKeys = metaData.getPrimaryKeys(null, null, table
						.getName());
				Map<String, String> pkMap = new HashMap<String, String>();
				while (primaryKeys.next()) {
					pkMap.put(primaryKeys.getString("COLUMN_NAME"), primaryKeys
							.getString("COLUMN_NAME"));
				}
				columns = metaData.getColumns(null, null, table.getName(), "%");

				List<ColumnInfo> columnList = new ArrayList<ColumnInfo>();
				while (columns.next()) {
					ColumnInfo column = new ColumnInfo();
					column.setName(columns.getString("COLUMN_NAME"));
					column.setType(columns.getInt("DATA_TYPE"));
					column.setTypeName(columns.getString("TYPE_NAME"));
					column.setDisplaySize(columns.getInt("COLUMN_SIZE"));
					column.setLabel(columns.getString("REMARKS"));
					if (pkMap.containsKey(column.getName()))
						column.setPk(true);
					columnList.add(column);
				}
				columns.close();
				primaryKeys.close();
				table.setColumns(columnList);
				tableList.add(table);
			}
		} finally {
			try {
				if (primaryKeys != null)
					primaryKeys.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (columns != null)
					columns.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (tables != null)
					tables.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PageRequest getPage(PageRequest request) throws Exception {
		logger.info("Database Service has been requested with following options:"
				+ request.toString());

		
		request.setResult(null);
		request.setPageSize(PAGE_SIZE);

		Map<String, Object> parameters = (Map<String, Object>) request
				.getParameter();
		List<ColumnInfo> columns = (List<ColumnInfo>) parameters
				.get("sortingColumnList");

		String columnArrayString = StringUtils.join(columns, ", ");
		String selectedTableName = (String) parameters.get("table");

		String sortingColumn = columnArrayString.equals("") ? "ROWID"
				: columnArrayString;
		String orderby = (String) parameters.get("order");
		DataSource ds = DataSourceManager.getDataSource((String) parameters
				.get("dataSource"));
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

		request.startRowNumber = (request.pageNo - 1) * request.pageSize;

		StringBuffer sql = new StringBuffer();
		sql.append("\nSELECT * ");
		sql.append("\nFROM ");
		sql.append("\n                (SELECT  ROWNUM RNUM, A.* ");
		sql.append("\n                 FROM ");
		sql.append("\n                         (SELECT  * ");
		sql.append("\n                         FROM    " + selectedTableName);
		sql.append("\n                         ORDER BY " + sortingColumn + " "
				+ orderby);
		sql.append("\n                         ) A ");
		sql.append("\n                ) B ");
		sql.append("\nWHERE  RNUM BETWEEN " + request.pageSize + " * ("
				+ request.pageNo + " - 1) + 1 AND " + request.pageSize + " * "
				+ request.pageNo);
		logger.debug(sql);
		List<ListOrderedMap> list = jdbcTemplate.queryForList(sql.toString());
		request.setResult(list);
		return request;
	}

}
