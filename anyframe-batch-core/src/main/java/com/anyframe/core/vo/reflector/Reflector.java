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

package com.anyframe.core.vo.reflector;

import java.nio.charset.Charset;

import com.anyframe.core.vo.meta.MetadataManager;
import com.anyframe.core.vo.transform.RawDataType;

/**
 * Reflector is a support class to access the attribute of a class without casting. 
 *  
 * provide set/get functionality via reflection ( POJO ) 
 * provide set/get functionality via Metadata ( AEJO : Anyframe Enterprise Java Object )
 * {@link MetadataManager}.
 * 
 * AEJO is a VO class which inherits AbstractVO class. 
 *  
 * @author prever.kang
 *
 */
public interface Reflector {
	<T> void setValue(Object obj, int index, T value);
	<T> T getValue(Object obj, int index);
	<T> void setValue(Object obj, String name, T value);
	<T> T getValue(Object obj, String name);
	Object[] getValues(Object obj);
	void setValues(Object obj, Object[] values);
	/**
	 * used by Batch
	 * 
	 * @param obj
	 * @param type
	 * @param charset
	 * @return
	 */
	boolean isSupportedDataType(Object obj, RawDataType type, Charset charset);
	Object getRawData(Object obj);
}
