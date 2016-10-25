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

package com.anyframe.core.vo.transform.bytes.io;

import java.io.ByteArrayOutputStream;

/**
 * VO의 property 중에서 가변길이의 Collection/Array를 지원하기 위해 ByteArrayOutputStream을 확장
 * byte[]의 사이즈를 늘려주는 expand() method를 추가
 * 
 * @author prever.kang
 *
 */
public class ExpandableByteArrayOutputStream extends ByteArrayOutputStream {
	public ExpandableByteArrayOutputStream() {
		super();
	}

	public ExpandableByteArrayOutputStream(int size) {
		super(size);
	}

	public byte[] getBuf() {
		return buf;
	}

	public int getCount() {
		return count;
	}

	/**
	 * 입력된 크기만큼 byte][]의 사이즈를 늘려준다.
	 * 
	 * @param addtionalSize 늘리고자 하는 크기
	 */
	public void expand(int addtionalSize) {
		int newcount = buf.length + addtionalSize;

		byte newbuf[] = new byte[newcount];
		System.arraycopy(buf, 0, newbuf, 0, buf.length);
		buf = newbuf;
	}
}
