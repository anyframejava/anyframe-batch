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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ParameterReplacer {
	Log logger = LogFactory.getLog(ParameterReplacer.class);

	private static final String DEFAULT_PLACEHOLDER_PREFIX = "$";
	
	/** 쉘 변수 패턴*/
	private static final Pattern PATTERN = Pattern.compile("(\\$\\{\\w*\\})|(\\$\\w*( |\\$)?)");
	
	/**
	 * replaceParameters 수정 버전
	 * <li> regular expression 적용
	 * <li> standard parameter form (${}) 지원
	 * <li> 문자열 끝에 선언된 변수를 치환하지 못했던 문제 해결
	 * <li> 연속된 변수 선언 시 치환하지 못했던 문제 해결
	 * <p>
	 * <pre>
	 * case1. 표준 유형: 괄호를 사용하는 경우 ex) ${MY_DIR}
	 * '\\$\\{'    : '${'문자로 시작
	 * '\\w*'      : Name은 소문자[a-z], 대문자[A-Z], Underscore('_'), 숫자[0-9]로 구성
	 *               \\w는 [a-zA-Z_0-9]와 동일함
	 * '\\}'       : '}'로 끝남
	 * 
	 * case 2. SLI 적용 유형: 괄호 사용 안함  ex) $MY_DIR
	 * '\\$'       : '$'문자로 시작
	 * '\\w*'      : Name은 소문자[a-z], 대문자[A-Z], Underscore('_'), 숫자[0-9]로 구성
	 *               \\w는 [a-zA-Z_0-9]와 동일함
	 * '( !\\$) ?' : 끝에 공백문자(' ') 또는 '$'가 올 수도 있다. (output에서는 제거됨. 생명 특화 부분)
	 * </pre>
	 * @param source
	 * @return
	 */
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
			return variable;
		
		// 괄호({ })가 쌓여 있는 경우 제거
		if(input.startsWith("{") && input.endsWith("}"))
			input = input.substring(1, input.length()-1);
		
		if(StringUtils.isEmpty(input))
			return variable;
		
		// first, lookup ParamUtil
		if(ParamUtil.containsKey(input))
			return ParamUtil.getParameter(input);
		
		// second, lookup system env
		if(System.getenv().containsKey(input))
			return System.getenv().get(input);
		
		// finally, lookup system properties
		if(System.getProperty(input) != null)
			return System.getProperty(input);
		
		// if match nothing, return inputValue;
		return variable;
		
	}
}

