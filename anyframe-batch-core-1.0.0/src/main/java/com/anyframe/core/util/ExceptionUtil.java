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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception 객체에서 Statck Trace의 내용을 String으로 뽑아내기 위한 Utility 클래스이다.
 * 
 * @author Junbo Jang
 *
 */
public final class ExceptionUtil {

	/**
	 * Exception의 statck trace 내용을 String으로 확인한다.
	 * @param e
	 * 			내용을 확인할 Exception 객체.
	 * @return
	 * 			Exception의 내용을 담은 String.
	 */
	public static String getCauseStackTraceString(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		if(e.getCause() != null) {
			e.getCause().printStackTrace(new PrintWriter(stringWriter));
		} else {
			e.printStackTrace(new PrintWriter(stringWriter));
		}
		return stringWriter.toString();
	}

	/**
	 * Exception의 statck trace 내용을 String으로 확인한다.
	 * @param e
	 * 			내용을 확인할 Exception 객체.
	 * @return
	 * 			Exception의 내용을 담은 String.
	 */
	public static String getStackTraceString(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

}
