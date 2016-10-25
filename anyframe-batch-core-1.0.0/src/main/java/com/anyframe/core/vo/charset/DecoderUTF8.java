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

public class DecoderUTF8 extends AbstractDecoder {

	
	// package only constructor
	DecoderUTF8() {
		super("UTF-8");
	}
	
	/*
	 * UTF-8
	 * 1) U-00000000 - U-0000007F: 0xxxxxxx
	 * 2) U-00000080 - U-000007FF: 110xxxxx 10xxxxxx
	 * 3) U-00000800 - U-0000FFFF: 1110xxxx 10xxxxxx 10xxxxxx
	 * 4) U-00010000 - U-001FFFFF: 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
	 * 5) U-00200000 - U-03FFFFFF: 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
	 * 6) U-04000000 - U-7FFFFFFF: 1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
	 */
	
	/**
	 * UTF-8 charSet 에서 1byte 의 경우 0xxxxxxx, 2byte 의 경우 110xxxxx, 3byte 의 경우 1110xxxx
	 * 로 시작하므로 비트연산자(>>)를 통해 byte 배열을 문자열로 변환할 수 있다.
	 * 
	 * @param bytes			decoding 할 byte 배열
	 * @param byteOffset	decoding 할 byte 배열의 시작 위치
	 * @param byteLength	decoding 할 byte 크기
	 * @param chars			decoding 하여 저장될 char 배열
	 * @param charOffset	decoding 하여 저장될 char 배열의 시작 위치
	 * @param charLength	decoding 하여 저장될 char 배열의 크기
	 */
	
	@Override
	public void decodeChar(byte[] bytes, int byteOffset, int byteLength, 
			               char[] chars, int charOffset, int charLength) {
		int char1, char2, char3;
		int curPos = byteOffset;
		int endPos = byteOffset + byteLength;
		int charCnt = 0;
		char ch = 0;
		
		while (curPos < endPos) {

			// check buffer overFlow
			if(charCnt == charLength) {
				if(this.onOverFlow == CodingErrorAction.REPORT)	// throw exception
					throw BufferOverFlowException.newInstance(bytes, byteOffset, byteLength, charLength);
				else	// slice excess
					return;
			}

			char1 = bytes[curPos++] & 0xff;
			switch (char1 >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				ch = (char) char1;
				break;
			case 12:
			case 13:
			{
				if(curPos >= endPos) {	// end of input buffer
					curPos++;
					
					if(this.onMalformed == CodingErrorAction.REPORT)
						throw MalformedInputException.newInstance(bytes, byteOffset, byteLength, endPos);
					else if(this.onMalformed == CodingErrorAction.REPLACE)
						ch = this.getReplaceChar();
					else if(this.onMalformed == CodingErrorAction.IGNORE)
						continue;
					
				} else {
					char2 = bytes[curPos++];
					if ((char2 & 0xC0) != 0x80) // check reading byte 10xxxxxx
						throw MalformedInputException.newInstance(bytes,
								byteOffset, byteLength, curPos - byteOffset);

					ch = (char) (((char1 & 0x1F) << 6) | (char2 & 0x3F));
				}
				break;
			}
			case 14:
			{
				if(curPos+1 >= endPos) {	// end of input buffer
					curPos += 2;
					
					if(this.onMalformed == CodingErrorAction.REPORT)
						throw MalformedInputException.newInstance(bytes, byteOffset, byteLength, endPos);
					else if(this.onMalformed == CodingErrorAction.REPLACE)
						ch = this.getReplaceChar();
					else if(this.onMalformed == CodingErrorAction.IGNORE)
						continue;
					
				} else {
					char2 = bytes[curPos++];
					if ((char2 & 0xC0) != 0x80) // check reading byte 10xxxxxx
						throw MalformedInputException.newInstance(bytes,
								byteOffset, byteLength, curPos - byteOffset);
	
					char3 = bytes[curPos++];
					if ((char3 & 0xC0) != 0x80) // check reading byte 10xxxxxx
						throw MalformedInputException.newInstance(bytes,
								byteOffset, byteLength, curPos - byteOffset);
	
					ch = (char) (((char1 & 0x0F) << 12)
							| ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
				}
				break;
			}
			default:
				throw MalformedInputException.newInstance(bytes, byteOffset,
						byteLength, curPos - byteOffset);
			}
			
			chars[charOffset + charCnt++] = ch;
		}
	}

	@Override
	public int getCharLength(byte[] bytes, int offset, int length) {
		int charCount = 0;
		int curPos = offset;
		int endPos = offset + length;

		while (curPos < endPos) {
			switch ((bytes[curPos] & 0xff) >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				curPos++;
				break;
			case 12:
			case 13:
				curPos += 2;
				break;
			case 14:
				curPos += 3;
				break;
			default:
				throw MalformedInputException.newInstance(bytes, offset, length, curPos - offset);
			}
			
			charCount++;
		}
		
		if(curPos != endPos) {
			if(this.onMalformed == CodingErrorAction.REPORT)
				throw MalformedInputException.newInstance(bytes, offset, length, curPos-offset);
			else if(this.onMalformed == CodingErrorAction.IGNORE)
				charCount--;
		}
		
		return charCount;
	}
}
