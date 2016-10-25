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

package com.anyframe.core.vo.transform.bytes;

import com.anyframe.core.vo.charset.ICharsetEncoder.Padding;

/**
 * VO에서 가변길이 Collection/Array property를 지원하기 위해 추가.
 * <br>(http://dev.anyframejava.org/jira/browse/EEONL-320 참고)
 * <ul>
 * <li>maxDigit : 가변길이 property의 실제 길이 정보를 표현할 자릿수 
 * <li>padding : property의 실제 길이를 표시하고 나머지 빈칸을 채울 character
 * </ul>
 * 예를 들어, maxDigit=4, padding=0 이고 가변길이 property의 실제 길이가 39인 경우 byte array에 들어가는 값은 '0039'가 된다.
 * @author Jeryeon Kim
 */
public class ConversionConfiguration {

	/** digit of maximum length for variable length byte encoding/decoding */
	private int maxDigit = 5;
	/** padding of list length for variable length byte encoding/decoding */
	private Padding padding = Padding.ZERO;

	public ConversionConfiguration() {
	}

	public ConversionConfiguration(int maxDigit, Padding padding) {
		this.maxDigit = maxDigit;
		this.padding = padding;
	}

	public int getMaxDigit() {
		return maxDigit;
	}

	public void setMaxDigit(int maxDigit) {
		this.maxDigit = maxDigit;
	}

	public Padding getPadding() {
		return padding;
	}

	public void setPadding(Padding padding) {
		this.padding = padding;
	}
}
