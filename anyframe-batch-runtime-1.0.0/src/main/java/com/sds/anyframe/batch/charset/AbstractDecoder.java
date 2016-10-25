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

import java.math.BigDecimal;
import java.util.Arrays;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AbstractDecoder extends AbstractCharsetCoder implements ICharsetDecoder {
	
	private char replacement = ' '; //'\uFFFD';
	
	protected AbstractDecoder(String charsetName) {
		super(charsetName);
	}
	
	public void setReplaceChar(char ch) {
		this.replacement = ch;
	}
	
	public char getReplaceChar() {
		return this.replacement;
	}
	
	abstract public int getCharLength(byte[] bytes, int offset, int length);
	
	abstract public void decodeChar(byte[] bytes, int byteOffset, int byteLength, 
			char[] chars, int charOffset, int charLength);
	
	public String decodeChar(byte[] bytes, int offset, int length) {
		int charLength = getCharLength(bytes, offset, length);
		char[] chars = new char[charLength];
		
		decodeChar(bytes, offset, length, chars, 0, charLength);
		
		return new String(chars);
	}
	
	public int decodeInt(byte[] bytes, int offset, int length) {

		int sPos = indexOf(bytes, offset, length);
		
		// all data is padding
		if (sPos == offset + length)	
			return 0;

		int ePos = lastIndexOf(bytes, offset, length);
		
		boolean negative = false;
		
		switch ((int)bytes[sPos]) {
		case 45 :				// -
			negative = true;
		case 43 :				// +
			sPos++;
		}

		int num = 0;
		int tmp = 0;
		while (sPos < ePos+1) {
			if ((tmp = ((int) bytes[sPos++] - 48)) > -1 && tmp < 10)
				num = ((num << 3) + (num << 1)) + tmp;	//num = num * 10 + tmp;
			else {
				char[] chars = new char[length];
				for(int index=0; index<length; index++)
					chars[index] = (char) bytes[index+offset];
				throw new NumberFormatException(Arrays.toString(chars));
			}
		}

		if (negative)
			num *= -1;

		return num;
	}
	
	public BigDecimal decodeBigDecimal(byte[] bytes, int offset, int length) {

		int sPos = indexOf(bytes, offset, length);
		
		// all data is padding
		if (sPos == offset + length)	
			return BigDecimal.ZERO;
		
		int ePos = lastIndexOf(bytes, offset, length);

		int digitCount = ePos - sPos + 1;
		char[] chars = new char[digitCount];
		
		for(int index=0; sPos < ePos+1; sPos++, index++)
			chars[index] = (char) bytes[sPos];

		return new BigDecimal(chars);
	}
	
	/**
	 * {@code bytes} 의 range(offset ~ offset+len) 범위의 시작 위치로부터  
	 * left-padding 이 아닌 첫번째 offset 위치를 반환한다.
	 * <p>
	 * left-padding 은 ' ' 또는  '0' 이다.
	 * <pre>
	 * '000123' -> 3
	 * '   123' -> 3
	 * '  0123' -> 3
	 * </pre>
	 * 
	 */
	private int indexOf(byte[] bytes, int offset, int length) {
		int index = offset;

		while (index < offset + length && (bytes[index] == ' ' || bytes[index] == '0'))
			index++;

		return index;
	}
	
	/**
	 * {@code bytes} 의 range(offset ~ offset+len) 범위의 끝 위치로부터  
	 * right-padding 이 아닌 마지막 offset 위치를 반환한다.
	 * <p>
	 * right padding 은 ' ' 이다.
	 * <pre>
	 * '001200' -> 5
	 * '  12  ' -> 3
	 * ' 0123 ' -> 4
	 * </pre>
	 * 
	 */
	private int lastIndexOf(byte[] bytes, int offset, int length) {
		int index = offset + length - 1;

		while(index >= offset && bytes[index] == ' ')
			index--;

		return index;
	}
	
}
