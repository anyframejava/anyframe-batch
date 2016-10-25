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

package com.sds.anyframe.batch.vo.transform.string;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.FieldMeta;
import com.sds.anyframe.batch.vo.meta.FieldMeta.FieldType;
import com.sds.anyframe.batch.vo.meta.VoMeta;
import com.sds.anyframe.batch.vo.reflector.Reflector;
import com.sds.anyframe.batch.vo.transform.Transform;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class TransformString implements Transform {
	
	private static final boolean    writeNull        = BatchDefine.WRITER_FILE_NULL_ALLOW;
	private static final String 	writeStringRepl  = BatchDefine.WRITER_FILE_NULL_STRING_REPL;
	private static final BigDecimal writeDecimalRepl = BatchDefine.WRITER_FILE_NULL_DECIMAL_REPL;

	private boolean bTrim = false;
	
	@Override
	public Object decodeVo(Object rawData, Class<?> voClass) {
		String[] tokens = (String[])rawData;
		
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(voClass);
		Reflector reflector = CoreContext.getMetaManager().getReflector(voClass);
		
		int fieldCount = voMeta.getFields().size();
		List<FieldMeta> fields = voMeta.getFields();
		
		Object[] values = new Object[fieldCount];

		for(int index=0; index < fieldCount; index++) {
			FieldMeta fieldInfo = fields.get(index);
			
			try {
				values[index] = decodeField(tokens, fieldInfo);
				
			} catch (Exception ex) {
				throw new EncodingException("fail to decode vo field. "
						+ fieldInfo.getFieldName() + " = " + values[index], ex);
			}
			
		}
		
		Object vo = voMeta.newInstance();
		reflector.setValues(vo, values);
		return vo;
		
	}
	
	@Override
	public Object decodeField(Object rawData, FieldMeta fieldInfo) {
		
		String[] tokens = (String[])rawData;
		FieldType fieldType = fieldInfo.getFieldType();
		
		Object result = null;
		
		if(fieldInfo.isCollection()) {
			result = decodeList(tokens, fieldInfo);
			
		} else if(fieldInfo.isArray()) {
			List<Object> list = decodeList(tokens, fieldInfo);
			Object newArray = Array.newInstance(fieldInfo.getFieldClass(), fieldInfo.getArraySize());
			result = list.toArray((Object[])newArray);
			
		}else if(fieldType == FieldType.VO){
			
			VoMeta voMeta = fieldInfo.getVoMeta();
			int field_count = voMeta.getFieldCount();
			String[] subTokens = new String[field_count];
			System.arraycopy(tokens, fieldInfo.getFieldOffset(), subTokens, 0, field_count);
			
			result = decodeVo(subTokens, voMeta.getVoClass());
			
		}else {
			String decodeChar = tokens[fieldInfo.getFieldOffset()];
			
			if(bTrim)
				decodeChar = decodeChar.trim();

			result = fieldInfo.fromString(decodeChar);
		}
		
		return result;
		
	}

	private List<Object> decodeList(String tokens[], FieldMeta fieldInfo){

		int arrayCount = fieldInfo.getArraySize();
		int componentCount = fieldInfo.getFieldCount() / arrayCount;
		int offset = fieldInfo.getFieldOffset();
		
		FieldType fieldType = fieldInfo.getFieldType();
		VoMeta voMeta = fieldInfo.getVoMeta();
	
		List<Object> list = new ArrayList<Object>(arrayCount);
	
		for (int i = 0; i < arrayCount; i++) {
			
			if(fieldType == FieldType.VO) {
				String[] subTokens = new String[componentCount];
				System.arraycopy(tokens, offset, subTokens, 0, componentCount);
		
				Object childVo = decodeVo(subTokens, voMeta.getVoClass());
				list.add(childVo);
				
			} else {
				Object child = fieldInfo.fromString(tokens[offset]);
				list.add(child);
			}
			
			offset += componentCount;
		}
	
		return list;
	}
	
	@Override
	public Object encodeVo(Object vo, Object target, List<String> parameterNames) {
		
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(vo.getClass());
		Reflector reflector = CoreContext.getMetaManager().getReflector(vo.getClass());
		
		String[] tokens = new String[voMeta.getFieldCount()];
		Object[] values = reflector.getValues(vo);
		
		int fieldCount = voMeta.getFields().size();
		List<FieldMeta> fields = voMeta.getFields();
		
		for (int index = 0; index < fieldCount; index++) {
			FieldMeta fieldInfo = fields.get(index);

			try {
				encodeField(values[index], fieldInfo, tokens);
			} catch (Exception ex) {
				throw new EncodingException("fail to encode vo field. "
						+ fieldInfo.getFieldName() + " = " + values[index], ex);
			}
		}
		
		return tokens;
		
	}
	
	@Override
	public void encodeField(Object value, FieldMeta fieldInfo, Object target) {
		
		String[] tokens = (String[])target;
		FieldType fieldType = fieldInfo.getFieldType();
		
		if(value == null) {
			
			if(fieldInfo.isCollection()){
				value = new ArrayList<Object>();
				
			}else if(fieldInfo.isArray()){
				value = new Object[fieldInfo.getArraySize()];
				
			}else{
				switch (fieldType){
					case STRING:{
						if(writeNull)
							throw new NullPointerException(fieldInfo.getFieldName() + " is null");
						else
							value = writeStringRepl;
						break;
					}
					case BIGDECIMAL:{
						if(writeNull)
							throw new NullPointerException(fieldInfo.getFieldName() + " is null");
						else
							value = writeDecimalRepl;
						break;
					}
					case VO:{
						if(writeNull)
							throw new NullPointerException(fieldInfo.getFieldName() + " is null");
						else
							value = fieldInfo.getVoMeta().newInstance();
						break;
					}default:{
						value = fieldInfo.getInitValue();
					}
				}
			}
		}
		
		if(fieldInfo.isCollection()) {
			encodeList(value, fieldInfo, tokens);
			
		} else if(fieldInfo.isArray()) {
			List<Object> asList = Arrays.asList((Object[]) value);
			encodeList(asList, fieldInfo, tokens);
			
		}else if(fieldType == FieldType.VO){
			String[] subToken = (String[]) encodeVo(value, null, null);
			System.arraycopy(subToken, 0, tokens, fieldInfo.getFieldOffset(), fieldInfo.getFieldCount());
			
		}else {
			tokens[fieldInfo.getFieldOffset()] = fieldType.toString(value);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void encodeList(Object value, FieldMeta fieldInfo, String tokens[]){
	
		List<Object> list = (List<Object>)value;
		
		int arrayCount = fieldInfo.getArraySize();
		int componentCount = fieldInfo.getFieldCount() / arrayCount;
		int offset = fieldInfo.getFieldOffset();
		
		FieldType fieldType = fieldInfo.getFieldType();
		
		if(list.size() != arrayCount) {
			throw new BatchRuntimeException(
					String.format("list size(%d) of %s is defferent from @array_count(%d)",
							list.size(), fieldInfo.getFieldName(), fieldInfo.getArraySize()));
		}
		
		for(int i=0 ; i<list.size() ; i++) {
			
			if(fieldType == FieldType.VO) {
				String[] subToken = (String[]) encodeVo(list.get(i), null, null);
				System.arraycopy(subToken, 0, tokens, offset, componentCount);
				
			} else {
				tokens[offset] = fieldType.toString(list.get(i));
			}
			
			offset += componentCount;
		}
	}	
	
	@Override
	public void setTrim(boolean bTrim) {
		this.bTrim = bTrim;
	}
	
	@Override
	public void setEncoder(ICharsetEncoder encoder) {
		//do nothing
	}

	@Override
	public void setDecoder(ICharsetDecoder decoder) {
		//do nothing
	}

	@Override
	public void clear() {
		//do nothing
	}	

}
