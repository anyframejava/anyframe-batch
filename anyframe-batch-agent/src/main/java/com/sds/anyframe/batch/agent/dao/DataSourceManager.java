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
package com.sds.anyframe.batch.agent.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sds.anyframe.batch.agent.PropertyProvider;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class DataSourceManager {
	private static String JDBC_CFG_FILE;
	private static ApplicationContext context;
	
	static void init() throws Exception{
		if(context != null)
			return;
		
		if(PropertyProvider.runtimeDataSourceContext == null){
			JDBC_CFG_FILE = "classpath:data-source-context.xml";
		} else{
			JDBC_CFG_FILE = "file:"+PropertyProvider.runtimeDataSourceContext;
		}
		context = new FileSystemXmlApplicationContext(JDBC_CFG_FILE);
	}
	
	
	public static List<String> getDataSourceList() throws Exception{
		init();
		List<String> dsList = new ArrayList<String>();
		String[] beanIds = context.getBeanDefinitionNames();
		for (String dataSource : beanIds) {
			if(context.getBean(dataSource) instanceof DataSource) dsList.add(dataSource);
		}
		return dsList;
	}


	public static DataSource getDataSource(String dataSource) throws Exception {
		init();
		return (DataSource) context.getBean(dataSource);
	}
}
