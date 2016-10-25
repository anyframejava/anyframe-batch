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
import java.util.HashMap;
import java.util.Map;


/**
 * Job Configuration에 정의된 Step 파라메터를 접근하기 위한 유틸리티 클래스.
 * 아래와 같이 step 하위에 {@code <parameter>} 엘리먼트로 선언된 파라메터의 값을 @id를 key로 획득할 수 있다.
 * <pre>
 * {@code
 * <step id="stepId" name="stepName" type="java">
 *     <parameters>
 *         <parameter id="param1">value1</parameter>
 *         <parameter id="param2">value2</parameter>
 *         <parameter id="param3">value3</parameter>
 *     </parameters>
 * </step>}</pre>
 *
 * @author Hyoungsoon Kim  
 */

public class StepParameterUtil {
	
	private static ThreadLocal<Map<String, String>> threadParam = new ThreadLocal<Map<String, String>>();

	/**
	 * 현재 스텝의 파라메터에 새로운 값을 등록한다. 만약 {@code key}에 해당하는 값이 이미 등록되어 있는 경우
	 * 기존값은 새로운 값으로 변경된다.
	 * 
	 * @param key 파라메터 키
	 * @param value 파라메터 값
	 */
	public static void addStepParam(String key, String value) {
		Map<String, String> paramMap = threadParam.get();
		
		if(paramMap == null) {
			paramMap = new HashMap<String, String>();
			threadParam.set(paramMap);
		}
		
		paramMap.put(key, value);
	}

	/**
	 * 현재 스텝의 파라메터 중 {@code key}에 해당하는 값을 반환한다.
	 * 만약 {@code key}에 해당하는 파라메터가 없는 경우 null을 반환한다.
	 * 
	 * @param key 파라메터의 키 값
	 * @return {@code key}에 해당하는 파라메터 값  
	 */
	public static String getStepParam(String key) {
		Map<String, String> paramMap = threadParam.get();
		
		if(paramMap == null)
			return null;
		
		return paramMap.get(key);
	}
	
	
	/**
	 * 현재 스텝에 등록된 모든 파라메터를 반환한다.
	 * 
	 * @return 현재 스탭에 등록된 파라메터를 담은 Map
	 */
	public static Map<String, String> getAllParms() {
		return Collections.unmodifiableMap(threadParam.get());
	}

	/**
	 * 현재 스텝에 등록된 파라메터 중 {@code key}에 해당하는 파라메터가 있는지 확인한다
	 * 
	 * @param key 파라메터 키
	 * @return {@code key}에 해당하는 파라메터가 있는 경우 true, 없는 경우 false
	 */
	public static boolean containsKey(String key) {
		Map<String, String> paramMap = threadParam.get();
		
		if(paramMap == null)
			return false;
		
		return paramMap.containsKey(key);
	}
	
	/**
	 * 현재 스텝에 등록된 모든 파라메터를 제거한다.
	 * 
	 */
	public static void clear() {
		
		Map<String, String> paramMap = threadParam.get();
		
		if(paramMap != null)
			paramMap.clear();
	}
	
}
