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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.sds.anyframe.batch.annotation.DefaultValue;
import com.sds.anyframe.batch.util.ClassUtil;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.FieldMeta;
import com.sds.anyframe.batch.vo.meta.FieldMeta.FieldType;
import com.sds.anyframe.batch.vo.meta.MetadataResolver;
import com.sds.anyframe.batch.vo.meta.MetadataResolverListener;
import com.sds.anyframe.batch.vo.meta.MethodHolder;
import com.sds.anyframe.batch.vo.meta.StackItem;
import com.sds.anyframe.batch.vo.meta.TheSameFieldNameExistRuntimeException;
import com.sds.anyframe.batch.vo.meta.VoMeta;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AnnotationMetadataResolver implements MetadataResolver {

	private List<MetadataResolverListener> listeners = new ArrayList<MetadataResolverListener>();

	public void setMetadataResolverListeners(List<MetadataResolverListener> metadataResolverListeners) {
		listeners = metadataResolverListeners;
	}

	public VoMeta getMetaData(Class<?> voClass) {
		return getMetaData(voClass, 0, new Stack<StackItem>());
	}
	
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
		Map<String, Integer> namedMap = new LinkedHashMap<String, Integer>();
		Map<String, MethodHolder> methods = new HashMap<String, MethodHolder>();

		int offsetField = 0;
		int offsetByte = 0;

		List<Object> defaultValues = new ArrayList<Object>();

		for (Field field : fields) {
			int modifier = field.getModifiers();

			MethodHolder methodHolder = ClassUtil.getMethodHolder(field);
			if (methodHolder == null)
				continue;

			methods.put(field.getName(), methodHolder);

			// skip private static final field
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

			FieldMeta fieldInfo = FieldMeta.valueOf(field, depth + 1, parents);

			fieldInfo.setByteOffset(offsetByte);
			fieldInfo.setFieldOffset(offsetField);

			offsetField += fieldInfo.getFieldCount();
			offsetByte += fieldInfo.getLength();

			fieldMetaResolved(fieldInfo);

			fieldMetas.add(fieldInfo);

			namedMap.put(fieldInfo.getFieldName(), index++);

			Object initValue = getInitValue(fieldInfo);

			fieldInfo.setInitValue(initValue);

			defaultValues.add(initValue);
		}

		voMeta.setMethods(methods);
		voMeta.setVoClass(voClass);
		voMeta.setLength(offsetByte);
		voMeta.setFields(fieldMetas);
		voMeta.setNamedMap(namedMap);
		voMeta.setFieldCount(offsetField);
		voMeta.setDefaultValues(defaultValues.toArray());
		vometaResolved(voMeta);

		return voMeta;
	}

	private Object getInitValue(FieldMeta fieldInfo) {
		DefaultValue annotation = fieldInfo.getField().getAnnotation(DefaultValue.class);

		FieldType type = fieldInfo.getFieldType();

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
