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
package com.sds.anyframe.batch.agent.properties;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.util.PropertiesUtil;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class SqlProperties {
	private final Logger log = Logger.getLogger(getClass());

	private static String jobSequenceSql;

	private static SqlProperties configurations = null;

	private SqlProperties() {
		Properties prop = PropertiesUtil.getProperties("sql.properties");
		if (!prop.isEmpty() && prop.size() > 0) {
			try {
				jobSequenceSql = prop.getProperty("job.jobSequence");

			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
			}
		}
	}

	public static SqlProperties getSqlProperties() {
		if (configurations == null)
			configurations = new SqlProperties();
		return configurations;
	}

	public static String getJobSequenceSql() {
		return jobSequenceSql;
	}
}
