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

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.MethodHolder;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.proxy.NoIndexOfFieldNameException;
import com.anyframe.core.vo.reflector.NoSuchMethodRuntimeException;
import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.transform.RawDataType;

/**
 * Invokes method for getter and setter of POJO VO.
 * 
 * @author prever.kang
 *
 */
public class PojoReflector implements Reflector {
	private VoMeta meta;

	public PojoReflector(VoMeta meta) {
		this.meta = meta;
	}

	public <T> void setValue(Object obj, int index, T value) {
		invoke(getSetterMethod(getFieldName(index)), obj, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Object obj, int index) {
		return (T) invoke(getGetterMethod(getFieldName(index)), obj);
	}

	public <T> void setValue(Object obj, String name, T value) {
		invoke(getSetterMethod(name), obj, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Object obj, String name) {
		return (T) invoke(getGetterMethod(name), obj);
	}

	private Method getSetterMethod(String name) {
		MethodHolder method = meta.getMethods().get(name);
		if(method == null)
			throw new NoSuchMethodRuntimeException("Field name: " + meta.getVoClass().getName()+"."+ name);
		else
			return method.getSetter();
	}

	private Method getGetterMethod(String name) {
		MethodHolder method = meta.getMethods().get(name);
		if(method == null)
			throw new NoSuchMethodRuntimeException("Field name: " + meta.getVoClass().getName()+"."+ name);
		else
			return method.getGetter();
	}

	@SuppressWarnings("unchecked")
	private <T> T invoke(Method method, Object obj) {
		try {
			return (T) method.invoke(obj, null);
		} catch (Exception e) {
			throw new VoMethodInvokeRunitmeException(e);
		}
	}

	private void invoke(Method method, Object obj, Object value) {
		try {
			method.invoke(obj, new Object[] {value});
		} catch (Exception e) {
			throw new VoMethodInvokeRunitmeException(e);
		}
	}

	protected String getFieldName(int index) {
		return meta.getFields().get(index).getFieldName();
	}

	protected int getFieldIndex(String name) {
		Integer index = meta.getNamedMap().get(name);

		if(index == null) //Throw an exception for unmatched filed name
			throw new NoIndexOfFieldNameException(name);
		return index;
	}

	public Object[] getValues(Object obj) {
		Object[] values = new Object[meta.getFields().size()];
		List<FieldMeta> fields = meta.getFields();
		for(int i=0;i<values.length;i++) {
			values[i] = getValue(obj, getFieldIndex(fields.get(i).getFieldName()));
		}
		return values;
	}

	public void setValues(Object obj, Object[] values) {
		for(int i=0;i<values.length;i++) {
			setValue(obj, i, values[i]);
		}
	}

	public boolean isSupportedDataType(Object obj, RawDataType type,
			Charset charset) {
		return false;
	}

	public Object getRawData(Object obj) {
		return null;
	}
}
