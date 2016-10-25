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

package com.anyframe.core.vo.proxy;

import java.nio.charset.Charset;

import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.transform.RawDataType;



/**
 * Access and modify the objects of fields for AbstractVo.
 * 
 * @author prever.kang
 *
 */
public interface VoProxy {
	<T> void setValue(int index, T value);
	<T> T getValue(int index);
	
	<T> void setValue(String name, T value);
	<T> T getValue(String name);
	
	public Object[] getValues();
	public void setValues(Object[] values);
	
	VoMeta getVoMeta();
	
	/**
	 * used by Batch
	 * 
	 * @param obj
	 * @param type
	 * @param charset
	 * @return
	 */
	boolean isSupportedDataType(RawDataType type, Charset charset);
	boolean isSupportedDataType(RawDataType type);
	Object getRawData();
}
