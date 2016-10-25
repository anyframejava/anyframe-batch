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

package com.sds.anyframe.batch.common.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 배치 작업을 실행할 때 환경변수로 전달된 값 또는  아래와 같이 job 하위에 {@code <parameter>} 
 * 엘리먼트로 선언된 파라메터의 값을 프로그램 내에서 조회하기 위한 유틸리티 클래스
 * 
 * <pre>{@code <job>
 *   <parameters>
 *     <parameter key="jobParam1>value1</parameter>
 *     <parameter key="jobParam2>value2</parameter>
 *     <parameter key="jobParam3>value3</parameter>
 *   </parameters>
 *   
 *   <step id="sample" name="sample" type="java">
 *   ...
 *   ...
 * </job>}</pre>
 *
 * 만약 환경변수로 전달된 파라메터와 Job Configuration에서 선언된 파라메터의 key가 같은 경우
 * Job Configuration에서 선언된 파라메터로 등록된다.
 * 
 * @see StepParameterUtil
 * @author Hyoungsoon Kim 
 */

public class ParamUtil {

	// paramMap should be ConcurrentHashMap because it could be used in multi-thread 
	private static final Map<String, String> paramMap = new ConcurrentHashMap<String, String>();
	
	/**
	 * 현재 배치작업의 파라메터에 새로운 값을 등록한다. 만약 {@code key}에 해당하는 값이 이미 등록되어 있는 경우
	 * 기존값은 새로운 값으로 변경된다.
	 * 
	 * @param key 파라메터 키
	 * @param value 파라메터 값
	 */
	public static void addParameter(String key, String value) {
		paramMap.put(key, value);
	}
	
	/**
	 * 현재 배치작업의 파라메터 중 {@code key}에 해당하는 값을 반환한다.
	 * 만약 {@code key}에 해당하는 파라메터가 없는 경우 null을 반환한다.
	 * 
	 * @param key 파라메터의 키 값
	 * @return {@code key}에 해당하는 파라메터 값  
	 */
	public static String getParameter(String key) {
		return paramMap.get(key);
	}
	
	/**
	 * 현재 배치작업에 등록된 모든 파라메터를 반환한다.
	 * 
	 * @return 현재 배치작업에 등록된 파라메터를 담은 Map
	 */
	public static Map<String, String> getAll() {
		return Collections.unmodifiableMap(paramMap);
	}
	
	/**
	 * 현재 배치작업에 등록된 파라메터 중 {@code key}에 해당하는 파라메터가 있는지 확인한다
	 * 
	 * @param key 파라메터 키
	 * @return {@code key}에 해당하는 파라메터가 있는 경우 true, 없는 경우 false
	 */
	public static boolean containsKey(String key) {
		return paramMap.containsKey(key);
	}
}
