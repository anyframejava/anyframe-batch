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

package com.anyframe.core.vo.transform.map;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.reflector.Reflector;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class MapDecoder {

	public static Object fromMap(Map<String, Object> map, Class<?> voClass) {
		return fromMap(map, voClass.getName());
	}

	public static Object fromMap(Map<String, Object> map, String voClassName) {
		VoMeta info = CoreContext.getMetaManager().getMetadata(voClassName);

		Object obj = info.newInstance();
		fromMap(map, obj);

		return obj;
	}

	@SuppressWarnings("unchecked")
	private static void fromMap(Map<String, Object> sourceMap, Object obj) {
		VoMeta info = CoreContext.getMetaManager().getMetadata(obj.getClass());
		Reflector reflector = CoreContext.getMetaManager().getReflector(obj.getClass());

		List<FieldMeta> fields = info.getFields();

		for (int i = 0; i < fields.size(); i++) {
			FieldMeta field = fields.get(i);

			Object data = sourceMap.get(field.getFieldName());
			if (data == null)
				continue;

			if (field.isCollection()) {
				if (field.getType() == FieldType.VO) {
					List<Object> list = toListOfVo(field, data);
					reflector.setValue(obj, i, list);
				}
			} else if (field.isArray()) {
				if (field.getType() == FieldType.VO)
					reflector.setValue(obj, i, toArrayOfVo(field, data));
				else
					reflector.setValue(obj, i, data);
			} else if (field.getType() == FieldType.VO) {
				Object childObj = fromMap((Map<String, Object>) data, field.getFieldClass().getName());
				reflector.setValue(obj, i, childObj);
			} else {
				reflector.setValue(obj, i, data);
			}
		}

	}

	private static List<Object> toListOfVo(FieldMeta field, Object data) {
		List<Object> list = new ArrayList<Object>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> maps = (List<Map<String, Object>>) data;

		for (Map<String, Object> aMap : maps) {
			Object obj = fromMap(aMap, field.getFieldClass().getName());
			list.add(obj);
		}
		return list;
	}

	private static Object[] toArrayOfVo(FieldMeta field, Object data) {
		@SuppressWarnings("unchecked")
		Map<String, Object>[] array = (Map<String, Object>[]) data;

		Object[] voArray = (Object[]) Array.newInstance(field.getFieldClass(), array.length);

		for (int i = 0; i < voArray.length; i++) {
			Object obj = fromMap(array[i], field.getFieldClass().getName());
			voArray[i] = obj;
		}
		return voArray;
	}
}
