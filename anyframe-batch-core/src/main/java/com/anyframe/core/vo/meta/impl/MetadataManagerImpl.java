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

package com.anyframe.core.vo.meta.impl;


import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.meta.StackItem;
import com.anyframe.core.vo.meta.MetadataManager;
import com.anyframe.core.vo.meta.MetadataResolver;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.reflector.impl.AejoReflector;
import com.anyframe.core.vo.reflector.impl.BciReflectorByField;
import com.anyframe.core.vo.reflector.impl.PojoReflector;

/**
 * VO의 Field나 Method 등의 meta data를 분석하는 역할을 담당하는 클래스
 * 
 * MetadataManagerImpl을 Bean으로 정의 시에 useBci를 true로 설정할 경우 reflection api가 아닌 asm 기반으로 field나 method를 access 하게 된다.
 * 
 * TODO by jr : 지금 현재는 Field를 직접 접근하도록 되어 있지만 setter메소드의 logic을 정의한 경우 method접근 방식으로 변경해야만 한다.
 * 				nestedVO나 Collection Array 등에 대해서도 테스트가 추가적으로 필요하다. 
 * @author prever.kang
 * @author Jeryeon Kim
 *
 */
public class MetadataManagerImpl implements MetadataManager {
	private static final Logger logger = Logger.getLogger(MetadataManagerImpl.class);

	private Map<Class<?>, VoMeta> metas = new ConcurrentHashMap<Class<?>, VoMeta>();
	private Map<Class<?>, Reflector> pojoReflectors = new ConcurrentHashMap<Class<?>, Reflector>();
	private Map<Class<?>, Reflector> asmReflectors = new ConcurrentHashMap<Class<?>, Reflector>();
	private boolean useBci = false;

	private boolean throwExceptionWhenTheSameFieldnameExist;

	protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	private static MetadataManager metadataManager;

	private MetadataResolver metadataResolver = MetadataResolverImpl.getInstance();

	protected MetadataManagerImpl() {
	}

	public static MetadataManager getInstance() {
		if (metadataManager == null)
			metadataManager = new MetadataManagerImpl();

		return metadataManager;
	}

	/**
	 * Factory Method for creating MetadataManager Directly with Current Class Loader. 
	 * 
	 * @return MetaManager Object
	 */
	public static MetadataManager getInstanceWithCurrentClassLoader() {
		if (metadataManager == null)
			metadataManager = new MetadataManagerImpl();
		metadataManager.setClassLoader(Thread.currentThread().getContextClassLoader());

		return metadataManager;
	}
	
	/**
	 * VO Metadata를 만들기 위해 호출한 Class들을 담을 Set을 준비한다.
	 */
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
//			voClass = Class.forName(voClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(voClassName + " does not exist", e);
		}

		return getMetadata(voClass);
	}

	public void destroy() {
		metas.clear();
		pojoReflectors.clear();
		asmReflectors.clear();
		logger.info("Value Object Meta Manager has been removed.");
	}

	public Reflector getReflector(Class<?> cls) {
		return getReflector(cls, this.useBci);
	}

	public Reflector getReflector(Class<?> cls, boolean useBci) {

		// AEJO
		if (AbstractVo.class.isAssignableFrom(cls))
			return AejoReflector.getInstance();

		if (useBci) { // POJO BCI
			Reflector reflector = asmReflectors.get(cls);
			if (reflector == null) {
				reflector = new BciReflectorByField(cls);
				asmReflectors.put(cls, reflector);
			}
			return reflector;
		} else { // POJO
			Reflector reflector = pojoReflectors.get(cls);

			if (reflector == null) {
				VoMeta metadata = getMetadata(cls);
				reflector = new PojoReflector(metadata);
				pojoReflectors.put(cls, reflector);
			}
			return reflector;
		}
	}

	public Reflector getReflector(String voClassName) {
		try {
			Class<?> cls = classLoader.loadClass(voClassName);
//			Class<?> cls = Class.forName(voClassName);
			return getReflector(cls);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(voClassName + " does not exist", e);
		}
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

	public void setUseBci(boolean useBci) {
		this.useBci = useBci;
	}
}
