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

/******************************************************************************
 * 나중에 Core와 통합을 용이하게 하기위해 Core 참조 부분을 본 Package에 옮겨둠! 
 * 본 Package의 Class들은 수정/개선 하지 말 것!
 * bonobono, 090706
 ******************************************************************************/

package com.sds.anyframe.batch.manager.core;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class StringUtil {
	public static String null2str(String str) {
		return str == null ? "" : str;
	}
	
	public static String emptyToNull(String str) {
		if(str == null)
			return null;
		else if(str.length() == 0)
			return null;
		return str;
	}
	
	public static String getFileNameWithExtention(String name){
		if(name==null) return null;
		int lastDotPos = name.lastIndexOf('.');
		if (lastDotPos != -1) {
			return name.substring(0,lastDotPos);
		} else {
			return name;
		}
	}

	public static String unqualifyJavaName(String name) {
		if(name == null) return null;
		int lastDotPos = name.lastIndexOf('.');
		if (lastDotPos != -1) {
			return name.substring(lastDotPos + 1);
		} else {
			return name;
		}
	}

	public static String getPacknageName(String qualifiedName) {
		if(qualifiedName == null) return null;
		int pos = qualifiedName.lastIndexOf('.');
		if (pos > 0)
			return qualifiedName.substring(0, pos);
		return "";
	}

	public static String unqualifySpringName(String name) {
		int lastDotPos = name.indexOf('/');
		if (lastDotPos != -1) {
			return name.substring(lastDotPos + 1);
		} else {
			return name;
		}
	}

	public static String toLowerFirstLetter(String data) {
		String firstLetter = data.substring(0, 1).toLowerCase();
		String restLetters = data.substring(1);
		return firstLetter + restLetters;
	}

	public static String capitalizeFirstLetter(String str) {
		String firstLetter = str.substring(0, 1).toUpperCase();
		return firstLetter + str.substring(1);
	}

	public static String milisecondToSecond(long milisecond) {
		return milisecond != 0 ? Double.valueOf(milisecond / 1000.0).toString()
				: "0";
	}

	// will move to AFUtils for reuse in other class
	public static String toUnderScore(String varname) {
		if (varname.indexOf("_") > -1)
			return varname;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < varname.length(); i++) {
			char ch = varname.charAt(i);
			if (Character.isUpperCase(ch)) {
				sb.append('_');
				sb.append(ch);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString().toUpperCase();
	}

	// ##### bonobono : this methods are derived from <Anyframe Core>
	public static String toCamelCase(String targetString, char posChar) {
		StringBuffer result = new StringBuffer();
		boolean nextUpper = false;
		String allLower = targetString.toLowerCase();

		for (int i = 0; i < allLower.length(); i++) {
			char currentChar = allLower.charAt(i);
			if (currentChar == posChar) {
				nextUpper = true;
			} else {
				if (nextUpper) {
					currentChar = Character.toUpperCase(currentChar);
					nextUpper = false;
				}
				result.append(currentChar);
			}
		}
		return result.toString();
	}

	public static String toCamelCase(String underScore) {
		return toCamelCase(underScore, '_');
	}

	// ##### bonobono : 090218
	public static boolean isEmptyOrNull(String str) {
		return (str == null || str.length() == 0) ? true : false;
	}
	
	// ##### bonobono : 090401
	/**
	 * package��� class���� �Է¹޾� Qualified Name�� ������ ��ȯ�Ѵ�.
	 * ������ �Ұ��� �ÿ��� empty String("")�� ��ȯ�Ѵ�. 
	 */
	public static String generateQualifiedName(String packageName, String className) {

		// className�� ������ Qualified �� �Ұ�
		if(isEmptyOrNull(className))	return "";
		// packageName ������ className�� �� Qualified Name
		if(isEmptyOrNull(packageName))	return className.trim();
		
		return packageName.trim() + "." + className.trim();
	}
	
	public static String[] resolveFieldGenericType(String genericType){
		int indexOf = genericType.indexOf('<');
		if (indexOf > -1) {
			String listFieldRefClassName = genericType.substring(indexOf + 1, genericType.length() - 1);
			String fldNm = genericType.substring(0, indexOf);
			String[] resolvedTypes = new String[2];
			resolvedTypes[0] = fldNm;
			resolvedTypes[1] = listFieldRefClassName;
			return resolvedTypes;
		}
		return null;
	}

	public static String[] resolveGenericType(String genericType) {
		// java.util.List<sample.SampleVO>
		int indexOf = genericType.indexOf('<');
		if (indexOf > -1) {
			String listFieldRefClassName = genericType.substring(indexOf + 1, genericType.length() - 1);
			String listTypeName = genericType.substring(0, indexOf);
			int lastIndexOf = listTypeName.lastIndexOf('.');
			String prmEngNm = listTypeName.substring(lastIndexOf + 1, listTypeName.length());
			String pkgEngNm = listTypeName.substring(0, lastIndexOf - 1);
			String[] resolvedTypes = new String[3];
			resolvedTypes[0] = prmEngNm;
			resolvedTypes[2] = listFieldRefClassName;
			resolvedTypes[1] = pkgEngNm;
			return resolvedTypes;
		}
		return null;
	}
}
