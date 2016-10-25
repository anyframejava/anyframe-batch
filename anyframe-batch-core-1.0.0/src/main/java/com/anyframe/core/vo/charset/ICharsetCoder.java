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

package com.anyframe.core.vo.charset;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface ICharsetCoder {
	
	// default behavior of encoder/decoder on coding error 
	public static CodingErrorAction CHARSET_OVERFLOW   = CodingErrorAction.REPORT;
	public static CodingErrorAction CHARSET_MALFORMED  = CodingErrorAction.REPORT;
	public static CodingErrorAction CHARSET_UNMAPPABLE = CodingErrorAction.REPLACE;

	
	public Charset getCharset();
	
	public void onBufferOverFlow(CodingErrorAction action);
	public void onMalformedInput(CodingErrorAction action);
	public void onUnmappableCharacter(CodingErrorAction action);
	
}
