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

import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.transform.RawDataType;

/**
 * AejoReflector invokes proxy of AbstractVo to get and set the object of Field.
 * 
 * @author prever.kang
 *
 */
public class AejoReflector implements Reflector {

	private static AejoReflector reflector = new AejoReflector();

	private AejoReflector() {
	}
	
	public <T> void setValue(Object obj, int index, T value) {
		((AbstractVo)obj).getProxy().setValue(index, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Object obj, int index) {
		return (T) ((AbstractVo)obj).getProxy().getValue(index);
	}

	public <T> void setValue(Object obj, String name, T value) {
		((AbstractVo)obj).getProxy().setValue(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Object obj, String name) {
		return (T) ((AbstractVo)obj).getProxy().getValue(name);
	}

	public Object[] getValues(Object obj) {
		return ((AbstractVo)obj).getProxy().getValues();
	}

	public void setValues(Object obj, Object[] values) {
		((AbstractVo)obj).getProxy().setValues(values);
	}

	public boolean isSupportedDataType(Object obj, RawDataType type, Charset charset) {
		return ((AbstractVo)obj).getProxy().isSupportedDataType(RawDataType.BYTE, charset);
	}

	public Object getRawData(Object obj) {
		 return ((AbstractVo)obj).getProxy().getRawData();
	}

	public static Reflector getInstance() {
		return reflector;
	}
}
