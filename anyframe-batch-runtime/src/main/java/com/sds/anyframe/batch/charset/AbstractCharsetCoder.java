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

package com.sds.anyframe.batch.charset;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AbstractCharsetCoder implements ICharsetCoder{

	/**
	 * decoding 시 invalid byte sequence input 에 대한 처리 기준
	 * <li>REPORT  : throw exception
	 * <li>REPLACE : replace it with replacementChar
	 * <li>IGNORE  : ignore input data 
	 * <pre>ex) EUC-KR 문자열의 경우
	 * [-33, -73, -22]는 invalid byte sequence 임
	 * </pre>
	 */
	protected CodingErrorAction onMalformed = CHARSET_MALFORMED;
	
	protected CodingErrorAction onUnmappable = CHARSET_UNMAPPABLE;

	protected CodingErrorAction onOverFlow = CHARSET_OVERFLOW;
	
	private Charset charset;

	public AbstractCharsetCoder(String charsetName) {
		this.charset = Charset.forName(charsetName);
	}
	
	public Charset getCharset () {
		return this.charset;
	}
	
	public void onBufferOverFlow(CodingErrorAction action) {
		this.onOverFlow = action;
	}

	public void onMalformedInput(CodingErrorAction action) {
		this.onMalformed = action;
	}

	public void onUnmappableCharacter(CodingErrorAction action) {
		this.onUnmappable = action;
	}
	
	public static ICharsetEncoder newCharsetEncoder(String charSetName) {
		if (null == charSetName)
			throw new NullPointerException();
	
		return new EncoderDefault(charSetName);
	}
	
	public static ICharsetDecoder newCharsetDecoder(String charSetName) {
		if (null == charSetName)
			throw new NullPointerException();
	
		return new DecoderDefault(charSetName);
		
	}
}
