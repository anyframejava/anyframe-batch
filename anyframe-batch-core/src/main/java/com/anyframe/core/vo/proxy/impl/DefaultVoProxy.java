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

package com.anyframe.core.vo.proxy.impl;

import java.nio.charset.Charset;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.proxy.NoIndexOfFieldNameException;
import com.anyframe.core.vo.proxy.VoProxy;
import com.anyframe.core.vo.transform.RawDataType;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class DefaultVoProxy implements VoProxy{

	Object[] values;

	private VoMeta meta;

	public DefaultVoProxy(Class<? extends AbstractVo> cls) {
		this.meta = CoreContext.getMetaManager().getMetadata(cls);
		this.values = meta.getInitialValues();
	}

	public <T> void setValue(int index, T value) {
		values[index] = value;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(int index) {
		return (T) values[index];
	}

	public <T> void setValue(String name, T value) {
		int index = getFieldIndex(name);

		setValue(index, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String name) {
		Integer index = getFieldIndex(name);
		return (T) getValue(index);
	}

	public VoMeta getVoMeta() {
		return meta;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		int size = getVoMeta().getFields().size();

		if (values.length != size)
			throw new IllegalArgumentException("the size of array should be "+ size);

		this.values = values;
		this.values = values;
	}

	protected int getFieldIndex(String name) {
		Integer index = getVoMeta().getNamedMap().get(name);

		if(index == null) //Throw an exception for unmatched filed name
			throw new NoIndexOfFieldNameException(name);
		return index;
	}


	public boolean isSupportedDataType(RawDataType type, Charset charset) {
		return false;
	}

	public boolean isSupportedDataType(RawDataType type) {
		return false;
	}

	public Object getRawData() {
		return null;
	}
}
