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

import java.math.BigDecimal;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class DecoderDefault extends AbstractDecoder {
	CharsetDecoder decoder = null;
	
	DecoderDefault(String charsetName) {
		super(charsetName);
		Charset charSet = Charset.forName(charsetName);
		this.decoder = charSet.newDecoder();
	}
	
	@Override
	public String decodeChar(byte[] bytes, int offset, int length) {
		ByteBuffer in = ByteBuffer.wrap(bytes, offset, length);
		CharBuffer out;
		try {
			out = decoder.decode(in);
			return out.toString();
			
		}catch (java.nio.charset.MalformedInputException e) {
			throw new MalformedInputException(e);
		}catch (java.nio.charset.UnmappableCharacterException e) {
			throw new UnmappableCharacterException(e);
		}catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void decodeChar(byte[] bytes, int byteOffset, int byteLength,
			               char[] chars, int charOffset, int charLength) {

		ByteBuffer in  = ByteBuffer.wrap(bytes, byteOffset, byteLength);
		CharBuffer out = CharBuffer.wrap(chars, charOffset, charLength);
		
		decoder.reset();
		CoderResult result = decoder.decode(in, out, true);
		
		if(result.isOverflow() && this.onOverFlow == CodingErrorAction.REPORT)
			throw new BufferOverflowException();
		
		if(result.isMalformed())
			throw new MalformedInputException();
		
		if(result.isUnmappable())
			throw new MalformedInputException();

	}
	
	@Override
	public int getCharLength(byte[] bytes, int offset, int length) {
		
		ByteBuffer in = ByteBuffer.wrap(bytes, offset, length);
		CharBuffer out;
		try {
			out = decoder.decode(in);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}
		
		return out.length();
	}
	
	public int decodeInt(byte[] bytes, int offset, int length) {
		String intStr = decodeChar(bytes, offset, length);
		return Integer.parseInt(intStr);
	}
	
	public BigDecimal decodeBigDecimal(byte[] bytes, int offset, int length) {
		String bigStr = decodeChar(bytes, offset, length);
		return new BigDecimal(bigStr);
	}
	
	@Override
	public void onMalformedInput(CodingErrorAction action) {
		super.onMalformedInput(action);
		decoder.onMalformedInput(action);
	}
	
	@Override
	public void onUnmappableCharacter(CodingErrorAction action) {
		super.onUnmappableCharacter(action);
		decoder.onUnmappableCharacter(action);
	}
	
	public void setReplaceChar(char ch) {
		super.setReplaceChar(ch);
		decoder.replaceWith(String.valueOf(ch));
	}
}
