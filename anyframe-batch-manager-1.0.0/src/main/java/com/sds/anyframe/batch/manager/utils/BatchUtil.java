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

package com.sds.anyframe.batch.manager.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.swt.layout.GridData;

import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.model.Parameter;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchUtil {
	public static GridData getWidthSizedGridData(int width) {
		GridData data = new GridData();
		data.widthHint = width;
		return data;
	}

	public static GridData getWidthHorizontalSpanGridData(int span) {
		GridData data = new GridData();
		data.horizontalSpan = span;
		return data;
	}

	public static GridData getWidthSizedHorizontalSpanGridData(int width,
			int span) {
		GridData data = new GridData();
		data.widthHint = width;
		data.horizontalSpan = span;
		return data;
	}
	

	/**
	 * 
	 * @return 현재날짜를 YYYYMMDD의 형식으로 리턴
	 */
	public static String todayDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return convertDateFormatUsingHyphen(dateFormat.format(new Date()));
	}
	
	/**
	 * 
	 * @param date
	 *            날짜 포맷을 변경하고자 하는 String
	 * @return yyyymmdd 를 yyyy-mm-dd로 변경하여 리턴
	 */
	public static String convertDateFormatUsingHyphen(String date) {
		StringBuilder resultDate = new StringBuilder();
		if (date.length() == 8) {
			for (int i = 0; i < date.length(); i++) {
				String c = date.charAt(i) + "";
				if (i == 3 || i == 5) {
					c = c + "-";
				}
				resultDate.append(c);
			}
		}

		return resultDate.toString();
	}

	/**
	 * 
	 * @param date
	 *            날짜 포맷을 변경하고자 하는 String
	 * @return yyyy-mm-dd 를 yyyymmdd로 변경
	 */
	public static String convertDateFormatWithoutHyphen(String date) {
		
		return StringUtils.defaultIfEmpty(date.replace("-", ""), BatchUtil
				.todayDate());
	}

	/**
	 * 주어진 단어에 숫자가 포함되어 있는지 여부를 판단한다.
	 * 
	 * @param String에
	 *            숫자가 포함되어 있는지 여부를 check할 원문
	 * @return 숫자만 있으면 true, 그외는 false를 return한다.
	 */
	public static boolean isNum(String str) {
		if (str == null)
			return false;
		byte[] ori = str.getBytes();
		if (ori == null)
			return false;
		boolean result = false;
		for (int i = 0; i < ori.length; i++) { // 문자인 경우. 특수문자도 문자로 취급
			if (ori[i] > 0x2F && ori[i] < 0x3A) {
				result = false;
			} else { // 숫자인 경우
				return true;
			}
		}
		return result;
	}

	/**
	 * get environment variables from preference store to this string format,
	 * key=value
	 * 
	 * @return key=value key2=value2 ..
	 */
	public static String environmentVarialbesFromPreference() {
		StringBuilder result = new StringBuilder();
		String[] names = BatchActivator.getDefault().getPluginPreferences()
				.propertyNames();
		for (String name : names) {
			String value = BatchActivator.getDefault().getPreferenceStore()
					.getString(name);
			result.append(name).append("=").append(value).append(" ");
		}
		return result.toString();
	}

	public static String monthBeforeDate() {
		Date date = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);
		date = cal.getTime();

		String today = new SimpleDateFormat("yyyy-MM-dd").format(date);
		int y = Integer.valueOf(StringUtils.split(today, "-")[0]);
		int m = Integer.valueOf(StringUtils.split(today, "-")[1]);
		int d = Integer.valueOf(StringUtils.split(today, "-")[2]);
		String result = String.format("%4d-%02d-%02d", y, m, d);
		return result;
	}

	public static String getElapsedTime(Date startDate, Date endDate) {
		long elapsedTime = endDate.getTime() - startDate.getTime();
		long remainedMilli = elapsedTime % 1000;
		if(remainedMilli < 0)
			remainedMilli = 0;

		long sec = (elapsedTime/1000);
		
		long remaindSec = sec%60;
		long hour = sec/3600;
		long min = (sec%3600)/60;

		String result = String.format("%d:%02d:%02d.%d", hour, min, remaindSec, remainedMilli);
		
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar instance = Calendar.getInstance();
		instance.set(2001,10,10, 11,11,12);
		Calendar instance2 = Calendar.getInstance();
		instance2.set(2001,10,12, 12,59,44);
		
		long elapsedTime = (instance2.getTimeInMillis()+124) - (instance.getTimeInMillis()+245);
		long remainedMilli = elapsedTime % 1000;

		long sec = (elapsedTime/1000);
		
		long remaindSec = sec%60;
		long hour = sec/3600;
		long min = (sec%3600)/60;

		String result = String.format("%d:%02d:%02d.%d", hour, min, remaindSec, remainedMilli);
		System.out.println(result);
	}

	public static List<Parameter> loadParameters() {
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable variables[] = manager.getValueVariables();
		
		List<Parameter> parameters = new ArrayList<Parameter> ();
		
		for (IValueVariable value: variables){
			String name = value.getName();
			if(name.startsWith(BatchConstants.PREF_USER_PARAMETER)) {
				name = name.replace(BatchConstants.PREF_USER_PARAMETER+".", "");
				parameters.add(new Parameter(name, value.getValue()));
			}
		}
		
		return parameters;
	}
	
	public static long byteToKillo(long bytes) {
		bytes = bytes / 1000; //killo
		return bytes;
	}
	
	public static long byteToMega(long bytes) {
		bytes = bytes / 1000000; //Mega
		return bytes;
	}

	public static boolean isWeiredJob(Date lastUpdated) {
		long term = System.currentTimeMillis() - lastUpdated.getTime();
		
		if(term > (1000 * 60 * 5))
			return true;
		return false;
	}
}
