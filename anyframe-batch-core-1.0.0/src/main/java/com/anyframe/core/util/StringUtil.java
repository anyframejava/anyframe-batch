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

package com.anyframe.core.util;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StringUtil {
	public static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0)
			return str;
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize)
			sb.append(Character.toUpperCase(str.charAt(0)));
		else
			sb.append(Character.toLowerCase(str.charAt(0)));

		sb.append(str.substring(1));

		return sb.toString();
	}

	public static String getAccessorName(String fieldName) {
		char[] nameChar = fieldName.toCharArray();

		// TODO by jr : !Character.isUpperCase(nameChar[1])를 체크하는 이유 - 서학모
		if (nameChar.length > 0
				&& Character.isLowerCase(nameChar[0])
				&& (nameChar.length == 1 || !Character.isUpperCase(nameChar[1]))) {
			fieldName = changeFirstCharacterCase(fieldName, true);
		}
		return fieldName;
	}
}
