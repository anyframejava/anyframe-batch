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

package com.anyframe.core.vo.transform.vo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.VoUtil;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.reflector.Reflector;

/**
 * Help utility to copy VO. 
 * 
 * copy VO to another VO via field name mapping.  
 * if field name is not met, then it simply ignored. 
 * 
 * @author Hyoungsoon Kim
 *
 */
public class VoCopier {
	public static void copy(Object source, Object target, boolean typeCheck) {
		if (target == null)
			throw new NullPointerException();

		if (source == null)
			return;

		VoMeta sourceVoMeta = CoreContext.getMetaManager().getMetadata(source.getClass());
		VoMeta targetVoMeta = CoreContext.getMetaManager().getMetadata(target.getClass());

		Reflector sourcReflector = CoreContext.getMetaManager().getReflector(source.getClass());
		Reflector targetReflector = CoreContext.getMetaManager().getReflector(target.getClass());

		Map<String, Integer> sourceNamedMap = sourceVoMeta.getNamedMap();
		Map<String, Integer> targetNamedMap = targetVoMeta.getNamedMap();

		for (Entry<String, Integer> entry : targetNamedMap.entrySet()) {
			String fieldName = entry.getKey();

			Integer targetIndex = entry.getValue();
			Integer sourceIndex = sourceNamedMap.get(fieldName);

			// 서로 같은 필드명이 있는지 확인
			if (sourceIndex != null) {
				FieldMeta sourceField = sourceVoMeta.getFields().get(sourceIndex);
				FieldMeta targetField = targetVoMeta.getFields().get(targetIndex);

				// typeCheck가 true인 경우 데이터 타입이 서로 다르면 해당 field는 copy하지 않고 pass
				if (typeCheck && (sourceField.getFieldClass() != targetField.getFieldClass()))
					continue;

				Object sourceValue = sourcReflector.getValue(source, sourceIndex);

				if (sourceValue == null) {
					targetReflector.setValue(target, targetIndex, null);
				} else {
					if (sourceField.isCollection()) {
						// "simple" property인지 확인
						if (BeanUtils.isSimpleProperty(sourceField.getFieldClass())) {
							@SuppressWarnings("unchecked")
							List<Object> sourceList = (List<Object>) sourceValue;
							List<Object> targetList = new ArrayList<Object>(sourceList);
							targetReflector.setValue(target, targetIndex, targetList);
						} else {
							VoMeta childVoMeta = sourceField.getVoMeta();
							@SuppressWarnings("unchecked")
							List<Object> sourceList = (List<Object>) sourceValue;
							List<Object> targetList = new ArrayList<Object>();

							for (Object childVo : sourceList) {
								Object newVo = childVoMeta.newInstance();
								VoUtil.copy(childVo, newVo);
								targetList.add(newVo);
							}
							targetReflector.setValue(target, targetIndex, targetList);
						}
					} else if (sourceField.isArray()) {
						// "simple" property인지 확인
						if (BeanUtils.isSimpleProperty(sourceField.getFieldClass())) {
							Object[] sourceArray = (Object[]) sourceValue;
							Object newArray = Array.newInstance(sourceField.getFieldClass(), sourceArray.length);

							System.arraycopy(sourceArray, 0, (Object[]) newArray, 0, sourceArray.length);

							targetReflector.setValue(target, targetIndex, newArray);
						} else {
							VoMeta childVoMeta = sourceField.getVoMeta();

							Object[] sourceArray = (Object[]) sourceValue;
							Object[] newArray = (Object[]) Array.newInstance(sourceField.getFieldClass(),
									sourceArray.length);

							for (int i = 0; i < sourceArray.length; i++) {
								Object newVo = childVoMeta.newInstance();
								VoUtil.copy(sourceArray[i], newVo);
								newArray[i] = newVo;
							}

							targetReflector.setValue(target, targetIndex, newArray);
						}
					} else if (BeanUtils.isSimpleValueType(sourceField.getFieldClass())) {
						// case of simple type
						if (sourceField.getFieldClass().equals(String.class)) {
							targetReflector.setValue(target, targetIndex, targetField.fromString((String) sourceValue));
						} else {
							targetReflector.setValue(target, targetIndex, sourceValue);
						}
					} else {
						// case of complex type (custom vo)
						Object newVo = sourceField.newInstance();
						VoUtil.copy(sourceValue, newVo);
						targetReflector.setValue(target, targetIndex, newVo);
					}
				}
			}
		}
	}
}
