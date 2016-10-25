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

import java.util.ArrayList;
import java.util.HashMap;
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

public class MapEncoder {
	
	public static Map<String, Object> toMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		Reflector reflector = CoreContext.getMetaManager().getReflector(obj.getClass());
		VoMeta info = CoreContext.getMetaManager().getMetadata(obj.getClass());
		
		List<FieldMeta> fields = info.getFields();
		
		for(int i=0; i<fields.size(); i++) {
			FieldMeta field = fields.get(i);
			
			Object data = reflector.getValue(obj, i);
			
			if(data != null) {
				if(field.isCollection()) {
					if(field.getType() == FieldType.VO)
						data = toListOfMap(data);
				} else if(field.isArray()) {
					if(field.getType() == FieldType.VO)
						data = toArrayOfMap((Object[])data);
				} else if(field.getType() == FieldType.VO) {
					data = toMap(data);
				}
			}
			
			map.put(field.getFieldName(), data);
		}
		
		return map;
	}
//
//	private static Object toListOfMap(Object data) {
//		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		@SuppressWarnings("unchecked")
//		List<AbstractVo> lst = (List<AbstractVo>)data;
//		
//		for(AbstractVo vo : lst)
//			list.add(toMap(vo));
//		
//		data = list;
//		return data;
//	}
//	
//	private static Object toArrayOfMap(Object[] data) {
//		
//		@SuppressWarnings("unchecked")
//		Map<String, Object>[] array = new HashMap[data.length];
//		
//		for(int i=0;i<data.length;i++)
//			array[i] = toMap((AbstractVo)data[i]);
//		
//		return array;
//	}
//	==> Changed to the followings... by jbjang

	private static Object toListOfMap(Object data) {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<Object> lst = (List<Object>)data;
		
		for(Object vo : lst)
			list.add(toMap(vo));
		
		data = list;
		return data;
	}
	
	private static Object toArrayOfMap(Object[] data) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object>[] array = new HashMap[data.length];
		
		for(int i=0;i<data.length;i++)
			array[i] = toMap((Object)data[i]);
		
		return array;
	}

}
