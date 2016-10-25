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

public interface ICharsetDecoder extends ICharsetCoder{

	public int getCharLength(byte[] bytes, int offset, int length);
	
	public String decodeChar(byte[] bytes, int offset, int length);
	
	public void decodeChar(byte[] bytes, int byteOffset, int byteLength,
			           char[] chars, int charOffset, int charLength);
	
	/**
	 * {@code bytes}데이터를 int로 decoding 한다.
	 * <p><b>{@code bytes}에 포함된 space 와 '0'은 trim함</b>
	 * <p>
	 * byte[] 배열 -03 => int 로 변환할 경우
	 * <p>
	 * 결과값 : -3
	 * 
	 * @param bytes		int type 으로 변환할 byte 배열
	 * @param offset	int type 으로 변환할 byte 배열의 시작 위치
	 * @param length	int type 으로 변환할 byte 배열의 길이
	 * @return			변환된 int 값
	 */
	public int decodeInt(byte[] bytes, int offset, int length);
	
	/**
	 * {@code bytes}데이터를 BigDecimal로 decoding 한다.
	 * <p><b>{@code bytes}에 포함된 좌우 space 는 trim</b>
	 * <p>
	 * byte[] 배열 -012.30 => BigDecimal 로 변환할 경우
	 * <br/>
	 * 결과값 : -12.30
	 * 
	 * @param bytes		BigDecimal type 으로 변환할 byte 배열
	 * @param offset	BigDecimal type 으로 변환할 byte 배열의 시작 위치
	 * @param length	BigDecimal type 으로 변환할 byte 배열의 길이
	 * @return			변환된 BigDecimal 값
	 */
	public BigDecimal decodeBigDecimal(byte[] bytes, int offset, int length);
	
}
