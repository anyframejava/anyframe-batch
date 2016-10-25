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

import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class EncoderUTF8 extends AbstractEncoder {

	// package only constructor
	EncoderUTF8() {
		super("UTF-8");
	}
	
	@Override
	public void encodeChar(char[] chars, int charOffset, int charLength, 
			               byte[] bytes, int byteOffset, int byteLength, 
			               Padding pad, Align align) {
		
		int curPos = charOffset;
		int endPos = charOffset + charLength;
		int endByte = byteOffset + byteLength;
		
		int c = 0;
		int nByte = 0;
		
		while(curPos < endPos) {

			c = chars[curPos++];
			if ((c >= 0x0000) && (c <= 0x007F))
				nByte = 1;
			else if (c > 0x07FF)
				nByte = 3;
			else
				nByte = 2;
			
			if(byteOffset + nByte > endByte) {	// check buffer overflow
				if(this.onOverFlow == CodingErrorAction.REPORT)
					throw BufferOverFlowException.newInstance(chars, charOffset, charLength, byteLength);
				else
					return;
			}
			
			// check supplementary character
			if ( '\uD800' <= c &&  c <= '\uDBFF') { // high surrogate range
				int row = chars[curPos+1];
				if('\uDC00' <= row && row <= '\uDFFF')	// check row surrogate
					curPos++; // skip row surrogate point
				else {
					if(this.onMalformed == CodingErrorAction.REPORT)
						throw MalformedInputException.newInstance(chars, charOffset, charLength, curPos-charLength);
				}
				
				if(this.onUnmappable == CodingErrorAction.REPLACE)
					bytes[byteOffset++] = (byte)this.replacement;
				else if(this.onUnmappable == CodingErrorAction.REPORT)
					throw UnmappableCharacterException.newInstance(chars, charOffset, charLength, curPos-charLength);
				else if(this.onUnmappable == CodingErrorAction.IGNORE)
					continue;
			}
			
			switch(nByte) {
			case 1:
				bytes[byteOffset++] = (byte) c;
				break;
			case 2:
				bytes[byteOffset++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytes[byteOffset++] = (byte) (0x80 | ((c >> 0) & 0x3F));
				break;
			case 3:
				bytes[byteOffset++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytes[byteOffset++] = (byte) (0x80 | ((c >> 6)  & 0x3F));
				bytes[byteOffset++] = (byte) (0x80 | ((c >> 0)  & 0x3F));
				break;
			}
		}
		
		// padding (right padding only)
		if(endByte-byteOffset > 0)
			doPadding(bytes, byteOffset, endByte-byteOffset, pad);
	}
	
	/**
	 * UTF16인 {@code chars}를 UTF-8로 인코딩 했을 때 필요한 byte 수를 계산한다.
	 * <br/>0x0000 ≤ 1 byte ≤ 0x007F
	 * <br/>0x0080 ≤ 2 byte ≤ 0x07FF
	 * <br/>0x0800 ≤ 3 byte
	 * 
	 * @param chars		encoding 할 char 배열
	 * @param offset	encoding 할 char 배열의 시작 위치
	 * @param length	encoding 할 char 배열의 길이
	 * @return			encoding 한 byte 배열의 길이
	 */
	@Override
	public int getByteLength(char[] chars, int offset, int length) {

		int len = 0;
		length += offset;
		for (int i = offset; i < length; i++) {
			int c = chars[i];
			if ((c >= 0x0000) && (c <= 0x007F)) {
				len++;
			} else if (c > 0x07FF) {
				len += 3;
			} else {
				len += 2;
			}
		}
		return len;
	}
}
