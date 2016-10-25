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

package com.anyframe.core.vo.meta;


import java.util.Stack;

import com.anyframe.core.lifecycle.LifeCycle;
import com.anyframe.core.vo.reflector.Reflector;

/**
 * MetadataManager creates and caches meta information of VO classes.
 * 
 * Also this class provides Reflector to class of Value Object to handle it's fields.
 * 
 * @author prever.kang
 *
 */
public interface MetadataManager extends LifeCycle {

	/**
	 * Extract Metadata for a VO class. 
	 * 
	 * @param voClass
	 * @return MetaData 
	 */
	public VoMeta getMetadata(Class<?> voClass);

	public VoMeta getMetadata(Class<?> voClass, int depth, Stack<StackItem> parents);
	
	/**
	 * Extract Metadata for a VO class by class name 
	 * 
	 * @param voClassName
	 * @return MetaData 
	 */
	public VoMeta getMetadata(String voClassName);

	public void setClassLoader(ClassLoader classLoader);

	public Reflector getReflector(String voClassName);

	public Reflector getReflector(Class<?> cls, boolean usingByte);

	public Reflector getReflector(Class<?> cls);

	public boolean isThrowExceptionWhenTheSameFieldnameExist();

}
