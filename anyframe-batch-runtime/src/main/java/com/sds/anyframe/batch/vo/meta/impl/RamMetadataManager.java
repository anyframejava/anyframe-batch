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

package com.sds.anyframe.batch.vo.meta.impl;


import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.vo.meta.MetadataManager;
import com.sds.anyframe.batch.vo.meta.MetadataResolver;
import com.sds.anyframe.batch.vo.meta.StackItem;
import com.sds.anyframe.batch.vo.meta.VoMeta;
import com.sds.anyframe.batch.vo.reflector.Reflector;
import com.sds.anyframe.batch.vo.reflector.ReflectorResolver;
import com.sds.anyframe.batch.vo.reflector.impl.PojoReflectorResolver;

public class RamMetadataManager implements MetadataManager {
	
	private static final Logger logger = Logger.getLogger(RamMetadataManager.class);

	private Map<Class<?>, VoMeta> metas = new ConcurrentHashMap<Class<?>, VoMeta>();
	private Map<Class<?>, Reflector> reflectors = new ConcurrentHashMap<Class<?>, Reflector>();

	private boolean throwExceptionWhenTheSameFieldnameExist;

	protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	private MetadataResolver metadataResolver = new AnnotationMetadataResolver();
	
	private ReflectorResolver reflectorResolver = new PojoReflectorResolver();

	public VoMeta getMetadata(Class<?> voClass) {
		return getMetadata(voClass, 0, new Stack<StackItem>());
	}

	public VoMeta getMetadata(Class<?> voClass, int depth, Stack<StackItem> parents) {
		VoMeta metadata = metas.get(voClass);

		if (metadata == null) {
			metadata = metadataResolver.getMetaData(voClass, depth, parents);
			if (metadata != null) {
				metas.put(voClass, metadata);
			}
		}

		return metadata;
	}
	
	public VoMeta getMetadata(String voClassName) {
		Class<?> voClass = null;
		
		try {
			voClass = classLoader.loadClass(voClassName);
			
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(voClassName + " does not exist", e);
		}

		return getMetadata(voClass);
	}

	public Reflector getReflector(Class<?> voClass) {

		VoMeta voMeta = getMetadata(voClass);
		Reflector reflector = reflectors.get(voClass);

		if (reflector == null) {
			reflector = reflectorResolver.getReflector(voMeta);
			if (reflector != null) {
				reflectors.put(voClass, reflector);
			}
		}

		return reflector;
		
	}

	public Reflector getReflector(String voClassName) {
		Class<?> voClass = null;
		
		try {
			voClass = classLoader.loadClass(voClassName);
			
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(voClassName + " does not exist", e);
		}
		
		return getReflector(voClass);
	}

	public boolean isThrowExceptionWhenTheSameFieldnameExist() {
		return throwExceptionWhenTheSameFieldnameExist;
	}

	public void setThrowExceptionWhenTheSameFieldnameExist(boolean throwExceptionWhenTheSameFieldnameExist) {
		this.throwExceptionWhenTheSameFieldnameExist = throwExceptionWhenTheSameFieldnameExist;
	}

	public void setMetadataResolver(MetadataResolver metaResolver) {
		this.metadataResolver = metaResolver;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		logger.info("Hot Class Loader has been installed.");
	}

	public void setReflectorResolver(ReflectorResolver reflectorResolver) {
		this.reflectorResolver = reflectorResolver;
	}

}
