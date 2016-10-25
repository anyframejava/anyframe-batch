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

package com.sds.anyframe.batch.vo.meta;

import java.util.List;
import java.util.Map;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class VoMeta {

	private Class<?> voClass;
	/** dictionary for field names; key(field name), value(field index) */
	private Map<String, Integer> namedMap;
	/** vo 의 field 정보 */
	private List<FieldMeta> fields;
	/** vo 전체 byte 길이 */
	private int length;
	/** vo 및 자손의 field 수 누계 */
	private int fieldCount;

	private Object[] defaultValues;

	public void setDefaultValues(Object[] defaultValues) {
		this.defaultValues = defaultValues;
	}

	public int getFieldCount() {
		return fieldCount;
	}

	public void setFieldCount(int fieldLength) {
		this.fieldCount = fieldLength;
	}

	private Map<String, MethodHolder> methods;

	public VoMeta() {

	}

	public Class<?> getVoClass() {
		return voClass;
	}

	public void setVoClass(Class<?> voClass) {
		this.voClass = voClass;
	}

	public void setNamedMap(Map<String, Integer> namedMap) {
		this.namedMap = namedMap;
	}

	public Map<String, Integer> getNamedMap() {
		return namedMap;
	}

	public void setFields(List<FieldMeta> fields) {
		this.fields = fields;
	}

	public List<FieldMeta> getFields() {
		return fields;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public FieldMeta getFieldMeta(String colName) {
		Integer index = namedMap.get(colName);
		if (index == null)
			return null;

		FieldMeta fieldMeta = fields.get(index);
		return fieldMeta;
	}

	public Object[] getInitialValues() {
		int fildCnt = fields.size();
		Object[] initValues = new Object[fildCnt];

		System.arraycopy(defaultValues, 0, initValues, 0, initValues.length);

		return initValues;
	}

	public Object newInstance() {
		try {
			return voClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		for (FieldMeta field : fields) {
			sb.append(field.toString()).append(",");
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");

		return sb.toString();
	}

	public Map<String, MethodHolder> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, MethodHolder> methods) {
		this.methods = methods;
	}
}
