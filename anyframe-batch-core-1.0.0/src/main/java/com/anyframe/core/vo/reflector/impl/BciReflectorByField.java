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

package com.anyframe.core.vo.reflector.impl;

import java.nio.charset.Charset;

import org.apache.commons.lang.NotImplementedException;

import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.transform.RawDataType;
import com.anyframe.core.vo.transform.vo.bci.FieldAccess;

/**
 * Invokes method for getter and setter of POJO VO.
 * 
 * @author prever.kang
 *
 */
public class BciReflectorByField implements Reflector {

	private FieldAccess fieldAccess;

	public BciReflectorByField(Class<?> clazz) {
		this.fieldAccess = FieldAccess.get(clazz);
	}

	public <T> T getValue(Object obj, int index) {
		return (T) fieldAccess.get(obj, index);
	}

	public <T> T getValue(Object obj, String name) {
		return (T) fieldAccess.get(obj, name);
	}

	public Object[] getValues(Object obj) {
		// used by ByteEncoder.encodeVo() : VO의 값을 byte로 변환 시에 값을 가져올 때 사용
		throw new NotImplementedException("BciReflectorByField does not support getValues(Object obj) method.");
	}

	public <T> void setValue(Object obj, int index, T value) {
		fieldAccess.set(obj, index, value);
	}

	public <T> void setValue(Object obj, String name, T value) {
		fieldAccess.set(obj, name, value);
	}

	public void setValues(Object obj, Object[] values) {
		throw new NotImplementedException("BciReflectorByField does not support setValues(Object obj, Object[] values) method.");
	}

	public boolean isSupportedDataType(Object obj, RawDataType type, Charset charset) {
		return false;
	}

	public Object getRawData(Object obj) {
		return null;
	}

}
