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
		/*
		char[] chars = charSetName.toCharArray();
		int size = 0;
		int ichar = 0;
		
		for (int i = 0; i < chars.length; i++) {
			ichar = (int)chars[i];
			switch(ichar) {
			case 45 :	// -
			case 95 :	// _
				break;
			default :
				size += (ichar & 0x1F);
			}
		}
		
		switch(size) {
		case 71 : // UTF-8
			return new EncoderUTF8();
		case 58 : // EUC-KR
		case 76 :
		case 80 :
		case 109 :
		case 198 :
			return new EncoderEUC_KR();
		case 102 : // MS949
		case 177 :
		case 201 :
			return new EncoderMS949();
		default :
			return new EncoderDefault(charSetName);
		}
		*/
	}
	
	public static ICharsetDecoder newCharsetDecoder(String charSetName) {
		if (null == charSetName)
			throw new NullPointerException();
	
		return new DecoderDefault(charSetName);
		
		/*
		char[] chars = charSetName.toCharArray();
		int size = 0;
		int ichar = 0;
		
		for (int i = 0; i < chars.length; i++) {
			ichar = (int)chars[i];
			switch(ichar) {
			case 45 :	// -
			case 95 :	// _
				break;
			default :
				size += (ichar & 0x1F);
			}
		}
		
		AbstractDecoder decoder = null;
		
		switch(size) {
		case 71 : // UTF-8
			decoder = new DecoderUTF8();
			break;
		case 58 : // EUC-KR
		case 76 :
		case 80 :
		case 109 :
		case 198 :
			decoder = new DecoderEUC_KR();
			break;
		case 102 : // MS949
		case 177 :
		case 201 :
			decoder = new DecoderMS949();
			break;
		default :
			decoder = new DecoderDefault(charSetName);
			break;
		}
		
		return decoder;
		*/
	}
}
