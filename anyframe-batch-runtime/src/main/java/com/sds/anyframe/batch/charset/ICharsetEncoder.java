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

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface ICharsetEncoder  extends ICharsetCoder{

	public enum Align {
		RIGHT,
		LEFT
	}

	public enum Padding {
		ZERO('0'),
		SPACE(' ');
		
		private char padChar;
		
		private Padding(char ch) {
			this.padChar = ch;
		}
		
		public char toChar() {return this.padChar;}
	}
	
	/**
	 * Unicode인 {@code chars}를 해당하는 byte로 인코딩 할 때 필요한 byte 수를 계산한다.
	 * 
	 * @param chars		encoding 할 char 배열
	 * @param offset	encoding 할 char 배열의 시작 위치
	 * @param length	encoding 할 char 배열의 길이
	 * @return			encoding 에 필요한 byte 수
	 */
	public int getByteLength(char[] chars, int offset, int length);
	
	public byte[] encodeChar(char[] chars, int offset, int length);
	//
	public void encodeChar(char[] chars, int charOffset, int charLength, 
            byte[] bytes, int byteOffset, int byteLength,
            Padding pad);
	
	public void encodeChar(char[] chars, int charOffset, int charLength, 
            byte[] bytes, int byteOffset, int byteLength,
            Padding pad, Align align);
	
	public void encodeNumber(char[] chars, int charOffset, int charLength, 
            byte[] bytes, int byteOffset, int byteLength,
            Padding pad, Align align);
	//
	public void encodeInteger(int intVal, Padding pad, Align align, 
			byte[] bytes, int offset, int byteLen);
	public void encodeBigDecimal(BigDecimal decimal, int scale,
			boolean bScale, Padding pad, Align align, byte[] bytes,
			int offset, int byteLen);
	
}
