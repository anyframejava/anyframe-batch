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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.util.XMLUtil;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobResourceInfoImpl implements JobResourceInfo {
	private Logger log = Logger.getLogger(JobResourceInfoImpl.class);
	private static Properties prop = new Properties();
	
	private static final String DEFAULT_PLACEHOLDER_PREFIX = "$";

	private static final Pattern PATTERN = Pattern.compile("(\\$\\{\\w*\\})|(\\$\\w*( |\\$)?)");
	
	
	{
		Map<String, String> getenv = System.getenv();
		for (Entry<String, String> entry : getenv.entrySet()) {
			prop.setProperty(entry.getKey(), entry.getValue());
		}

		String commonEnvFile = PropertyProvider.runtimeBasePath
				+ "/config/common/common.env";
		log.debug("common env file : " + commonEnvFile);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(commonEnvFile)));

			while (reader.ready()) {
				String line = reader.readLine();
				String trimToEmpty = StringUtils.trimToEmpty(line);
				// Scan exported environment variables from common.env file
				if (StringUtils.startsWith(trimToEmpty,
						PropertyProvider.envKeyword)) {
					String keyValue = StringUtils.trimToEmpty(StringUtils
							.substringAfter(trimToEmpty,
									PropertyProvider.envKeyword + " "));
					if (StringUtils.contains(keyValue, "=")) {
						String key = StringUtils.substringBefore(keyValue, "=");
						String value = StringUtils
								.substringAfter(keyValue, "=");
						prop.setProperty(key.trim(), value.trim());
					}
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> resourcesInJob(String jobFile) throws Exception {
		List<String> resourcesInEachStep = resourcesInStepByType(jobFile);
		List<String> result = new ArrayList<String>();
		
		for (String filePath : resourcesInEachStep) {
			String resultString = filePath;
			resultString = replaceParameters(filePath);
			result.add(resultString);
		}
		
		log.debug("parsed string list : " + result);
		return result;
	}
	
	
	public static String replaceParameters(String source) {

		String input = source;
		Matcher m = PATTERN.matcher(input);
		
		int mark = 0;
		int start, end;
		StringBuilder sb = new StringBuilder();
		while(m.find()) {
			start = m.start();
			end   = m.end();
			
			sb.append(input.substring(mark, start));
			sb.append(getReplacement(m.group()));
			mark = end;
		}
		
		sb.append(input.substring(mark));
		return sb.toString();
	}
	
	private static String getReplacement(String variable) {
	
		if(variable == null)
			return "";
	
		// prefix 및 surfix 의 '$' 문자 제거
		String input = variable.replace(DEFAULT_PLACEHOLDER_PREFIX, "");
		
		//끝에 있는 공백(' ') 제거
		input = input.trim();
		
		if(input == null || StringUtils.isEmpty(input))
			return "";
		
		// 괄호({ })가 쌓여 있는 경우 제거
		if(input.startsWith("{") && input.endsWith("}"))
			input = input.substring(1, input.length()-1);
		
		if(StringUtils.isEmpty(input))
			return "";
		
		if(prop.getProperty(input) != null)
			return prop.getProperty(input);
		
		// if match nothing, return inputValue;
		return variable;
		
	}


	private List<String> resourcesInStepByType(String jobFile) throws Exception {
		XMLUtil xml = new XMLUtil();

		List<String> typeList = xml.getNodeAttList(jobFile, "//step", "type");
		List<String> resources = new ArrayList<String>();
		for (String type : typeList) {
			if (type.equalsIgnoreCase("SHELL")) {
				List<String> resourcesFromCDATA = xml.getResourcesFromShell(
						jobFile, "//step[@type='shell']");
				resources.addAll(resourcesFromCDATA);
				log.debug("cdata : " + resourcesFromCDATA);
			} else if (type.equalsIgnoreCase("PARALLEL")) {
				List<String> list = xml.getNodeAttList(jobFile,
						"//step/thread/resource", "url");
				resources.addAll(list);
				log.debug("parallel : " + list);
			} else if (type.equalsIgnoreCase("JAVA")
					|| type.equalsIgnoreCase("DELETE")) {
				List<String> list = xml.getNodeAttList(jobFile,
						"//step/resource", "url");
				resources.addAll(list);
				log.debug("java/delete : " + list);
			}
		}
		// 중복 제거
		Set<String> set = new HashSet<String>(resources);
		set.remove(null);
		set.remove("");
		String[] array = set.toArray(new String[0]);
		Arrays.sort(array);
		List<String> result = Arrays.asList(array);
		log.debug(result);
		return result;
	}
}
