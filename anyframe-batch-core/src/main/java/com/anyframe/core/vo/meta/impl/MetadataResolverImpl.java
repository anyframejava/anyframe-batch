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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.anyframe.core.CoreContext;
import com.anyframe.core.annotation.DefaultValue;
import com.anyframe.core.util.ClassUtil;
import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.meta.StackItem;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.meta.MetadataResolver;
import com.anyframe.core.vo.meta.MetadataResolverListener;
import com.anyframe.core.vo.meta.MethodHolder;
import com.anyframe.core.vo.meta.TheSameFieldNameExistRuntimeException;
import com.anyframe.core.vo.meta.VoMeta;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class MetadataResolverImpl implements MetadataResolver {

	private static MetadataResolver metadataResolver;

	private List<MetadataResolverListener> listeners = new ArrayList<MetadataResolverListener>();

	private MetadataResolverImpl() {
	}

	public static MetadataResolver getInstance() {
		if (metadataResolver == null)
			metadataResolver = new MetadataResolverImpl();

		return metadataResolver;
	}

	public void setMetadataResolverListeners(List<MetadataResolverListener> metadataResolverListeners) {
		listeners = metadataResolverListeners;
	}

	/**
	 * resolve meta data of vo class<p>
	 * circular dependency를 감지하기 위해 vo class와 depth를 담을 Stack을 준비한다. 
	 * @param voClass class to resolve meta data
	 */
	public VoMeta getMetaData(Class<?> voClass) {
		return getMetaData(voClass, 0, new Stack<StackItem>());
	}
	
	/**
	 * resolve meta data of vo class<p>
	 * depth 가 다른 vo class가 stack 에 있을 경우, circular dependency 로 판단한다.<p>
	 * circular dependency 가 감지될 경우, null을 리턴한다.
	 * @param voClass class to resolve meta data
	 * @param depth call depth (starts with 0, depth of child field is added by 1) 
	 * @param parents Call stack (VO Meta 정보를 구성하는 동안 Circular dependency 를 감지하기 위한 stack) 
	 */
	public VoMeta getMetaData(Class<?> voClass, int depth, Stack<StackItem> parents) {
		// check circular dependency in resolve meta data of this vo class
		if(!List.class.isAssignableFrom(voClass)){
			for (StackItem item : parents) {
				if(depth != item.getDepth() && item.getClazz().equals(voClass)){
					return null;
				}
			}
			parents.add(new StackItem(depth, voClass));
		}
		VoMeta voMeta = new VoMeta();
		int index = 0;

		List<Field> fields = ClassUtil.getAllFields(voClass);

		List<FieldMeta> fieldMetas = new ArrayList<FieldMeta>();
		Map<String, Integer> namedMap = new HashMap<String, Integer>();

		// TODO by jr : AEJO인 경우 method를 따로 정리할 필요가 없으므로 pojo check해서 사용한다.
		Map<String, MethodHolder> methods = new HashMap<String, MethodHolder>();

		boolean pojo = false;
		if (!AbstractVo.class.isAssignableFrom(voClass))
			pojo = true;

		int offsetField = 0;
		int length = 0;

		List<Object> defaultValues = new ArrayList<Object>();

		for (Field field : fields) {
			int modifier = field.getModifiers();

			MethodHolder methodHolder = ClassUtil.getMethodHolder(field);
			if (methodHolder == null)
				continue;

			if (pojo) {
				methods.put(field.getName(), methodHolder);
			}

			// skip private static final field (META_INFO)
			if ((modifier & 0x08) == 0x08) // static
				continue;

			// If there is already the same field name then there are two
			// options(First ignore it, otherwise throw an exception)
			if (namedMap.containsKey(field.getName())) {
				if (!CoreContext.getMetaManager().isThrowExceptionWhenTheSameFieldnameExist())
					continue;
				else
					throw new TheSameFieldNameExistRuntimeException("The name of the '" + field.getName() + "' in "
							+ field.getDeclaringClass().getCanonicalName() + " is already exist at "
							+ fieldMetas.get(namedMap.get(field.getName())).getField().getDeclaringClass());
			}
			// FieldMeta 를 읽어올 때, depth를 1단계 올린다. (depth++ 하지 않는다.)
			FieldMeta fieldInfo = FieldMeta.valueOf(field, depth + 1, parents);
			// TODO by jr : fieldCount is used by Anyframe Batch. 몇번째 필드인지를 나타내는 값 - 고윤원
			fieldInfo.setByteOffset(length);
			fieldInfo.setFieldOffset(offsetField);

			offsetField += fieldInfo.getFieldCount();
			length += fieldInfo.getLength();

			// default field meta resolution이 아닌 경우 customizing point 제공
			fieldMetaResolved(fieldInfo);

			fieldMetas.add(fieldInfo);

			namedMap.put(fieldInfo.getFieldName(), index++);

			Object initValue = getInitValue(fieldInfo);

			fieldInfo.setInitValue(initValue);

			defaultValues.add(initValue);
		}

		if (pojo)
			voMeta.setMethods(methods);

		voMeta.setVoClass(voClass);
		voMeta.setLength(length);
		voMeta.setFields(fieldMetas);
		voMeta.setNamedMap(namedMap);
		voMeta.setFieldCount(offsetField);
		voMeta.setDefaultValues(defaultValues.toArray());
		vometaResolved(voMeta);

		return voMeta;
	}

	private Object getInitValue(FieldMeta fieldInfo) {
		DefaultValue annotation = fieldInfo.getField().getAnnotation(DefaultValue.class);

		FieldType type = fieldInfo.getType();

		Object defaultValue = null;

		if (annotation != null && (type != FieldType.VO || type != FieldType.UNKNOWN)) {
			String value = annotation.value();
			defaultValue = type.fromString(value);
		}
		return defaultValue != null ? defaultValue : type.getInitValue(fieldInfo.isArray());
	}

	private void vometaResolved(VoMeta voMeta) {
		for (MetadataResolverListener metadataResolverListener : listeners)
			metadataResolverListener.valueObjectResolved(voMeta);
	}

	private void fieldMetaResolved(FieldMeta fieldInfo) {
		for (MetadataResolverListener metadataResolverListener : listeners)
			metadataResolverListener.fieldMetaResolved(fieldInfo);
	}

	public void addMetadataResolverListener(MetadataResolverListener metadataResolverListener) {
		listeners.add(metadataResolverListener);
	}

}
