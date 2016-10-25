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

package com.sds.anyframe.batch.vo.transform.bytes;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Align;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Padding;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.FieldMeta;
import com.sds.anyframe.batch.vo.meta.FieldMeta.FieldType;
import com.sds.anyframe.batch.vo.meta.VoMeta;
import com.sds.anyframe.batch.vo.reflector.Reflector;
import com.sds.anyframe.batch.vo.transform.Transform;
import com.sds.anyframe.batch.vo.transform.TransformationException;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */

public class TransformByte implements Transform {

	private boolean bTrim;

	protected ICharsetEncoder encoder;
	protected ICharsetDecoder decoder;

	private static final boolean writeNull = BatchDefine.WRITER_FILE_NULL_ALLOW;
	private static final String writeStringRepl = BatchDefine.WRITER_FILE_NULL_STRING_REPL;
	private static final BigDecimal writeDecimalRepl = BatchDefine.WRITER_FILE_NULL_DECIMAL_REPL;

	@Override
	public Object decodeVo(Object rawData, Class<?> voClass) {
		byte[] bytes = (byte[]) rawData;
		
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(voClass);
		Reflector reflector = CoreContext.getMetaManager()
				.getReflector(voClass);

		int fieldCount = voMeta.getFields().size();
		List<FieldMeta> fields = voMeta.getFields();

		Object[] values = new Object[fieldCount];

		for (int index = 0; index < fieldCount; index++) {
			FieldMeta fieldInfo = fields.get(index);
			
			try {
				values[index] = decodeField(bytes, fieldInfo);
				
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

		byte[] bytes = (byte[])rawData;
		FieldType fieldType = fieldInfo.getFieldType();
		
		Object result = null;

		if (fieldInfo.isCollection()) {
			result = decodeList(fieldInfo, bytes);
			
		} else if (fieldInfo.isArray()) {
			List<Object> list = decodeList(fieldInfo, bytes);
			Object newArray = Array.newInstance(fieldInfo.getFieldClass(),fieldInfo.getArraySize());
			result = list.toArray((Object[]) newArray);

		} else if (fieldType == FieldType.VO) {
			byte[] subBytes = new byte[fieldInfo.getLength()];
			System.arraycopy(bytes, fieldInfo.getByteOffset(), subBytes, 0,fieldInfo.getLength());

			Object childVo = decodeVo(subBytes, fieldInfo.getVoMeta().getVoClass());
			result = childVo;
			
		} else {
			String decodeChar = decoder.decodeChar(bytes,fieldInfo.getByteOffset(), fieldInfo.getLength());

			if (bTrim)
				decodeChar = decodeChar.trim();

			result = fieldInfo.fromString(decodeChar);
		}
		return result;

	}

	private List<Object> decodeList(FieldMeta fieldInfo, byte[] bytes) {

		int offset = fieldInfo.getByteOffset();
		int arrayCount = fieldInfo.getArraySize();
		int length = fieldInfo.getLength() / arrayCount;
		FieldType fieldType = fieldInfo.getFieldType();
		
		List<Object> list = new ArrayList<Object>(arrayCount);

		for (int i = 0; i < arrayCount; i++) {
			
			if (fieldType == FieldType.VO) {
				
				byte[] subBytes = new byte[length];
				System.arraycopy(bytes, offset, subBytes, 0, length);

				Object childVo = decodeVo(subBytes, fieldInfo.getVoMeta().getVoClass());
				list.add(childVo);
				
			} else {
				
				String decodeChar = decoder.decodeChar(bytes, offset, length);

				if (bTrim)
					decodeChar = decodeChar.trim();

				Object childObject = fieldType.fromString(decodeChar);
				list.add(childObject);
			}
			
			offset += length;
		}
		
		return list;
	}

	@Override
	public Object encodeVo(Object vo, Object target, List<String> parameterNames) {
		
		Reflector reflector = CoreContext.getMetaManager().getReflector(vo.getClass());
		byte[] bytes = null;

		if (reflector.isSupportedDataType(vo, "BYTE", encoder.getCharset())) {
			bytes = (byte[]) reflector.getRawData(vo);

		} else {
			VoMeta voMeta = CoreContext.getMetaManager().getMetadata(vo.getClass());
			Object[] values = reflector.getValues(vo);
			List<FieldMeta> fields = voMeta.getFields();
			int fieldCount = voMeta.getFields().size();

			bytes = new byte[voMeta.getLength()];

			for (int index = 0; index < fieldCount; index++) {
				FieldMeta fieldInfo = fields.get(index);
				
				try {
					encodeField(values[index], fieldInfo, bytes);
				} catch (Exception ex) {
					throw new EncodingException("fail to encode vo field. "
							+ fieldInfo.getFieldName() + " = " + values[index], ex);
				}
			}
		}

		return bytes;
	}

	@Override
	public void encodeField(Object value, FieldMeta fieldInfo, Object target) {

		byte[] bytes = (byte[])target;
		FieldType fieldType = fieldInfo.getFieldType();

		if (value == null) {
			
			if (fieldInfo.isCollection()) {
				value = new ArrayList<Object>();
				
			} else if (fieldInfo.isArray()) {
				value = new Object[fieldInfo.getArraySize()];
				
			} else {

				switch (fieldType) {
				case STRING: {
					if (writeNull)
						throw new NullPointerException(fieldInfo.getFieldName() + " is null");
					else
						value = writeStringRepl;
					break;
				}
				case BIGDECIMAL: {
					if (writeNull)
						throw new NullPointerException(fieldInfo.getFieldName() + " is null");
					else
						value = writeDecimalRepl;
					break;
				}
				case VO: {
					if (writeNull)
						throw new NullPointerException(fieldInfo.getFieldName() + " is null");
					else
						value = fieldInfo.getVoMeta().newInstance();
					break;
				}
				default: {
					value = fieldInfo.getInitValue();
				}
				}
			}
		}
		
		if (fieldInfo.isCollection()) {
			encodeList(value, fieldInfo, bytes);
			
		} else if (fieldInfo.isArray()) {
			List<Object> asList = Arrays.asList((Object[]) value);
			encodeList(asList, fieldInfo, bytes);
			
		} else if (fieldType == FieldType.VO) {
			byte[] subBytes = (byte[]) encodeVo(value, null, null);
			System.arraycopy(subBytes, 0, bytes, fieldInfo.getByteOffset(), fieldInfo.getLength());
			
		} else {
			
			char[] chars = fieldType.toString(value).toCharArray();
			if (fieldType.isNumeric())
				encoder.encodeNumber(chars, 0, chars.length, bytes,
						fieldInfo.getByteOffset(), fieldInfo.getLength(),
						Padding.ZERO, Align.RIGHT);
			else
				encoder.encodeChar(chars, 0, chars.length, bytes,
						fieldInfo.getByteOffset(), fieldInfo.getLength(),
						Padding.SPACE, Align.LEFT);
		}

	}

	@SuppressWarnings("unchecked")
	private void encodeList(Object value, FieldMeta fieldInfo, byte[] bytes) {

		List<Object> list = (List<Object>) value;
		int count = fieldInfo.getArraySize();

		if (list.size() != count) {
			throw new TransformationException(String.format(
					"size(%d) of %s is different from @ArraySize(%d)",
					list.size(), fieldInfo.getFieldName(), count));
		}
		
		int offset = fieldInfo.getByteOffset();
		int length = fieldInfo.getLength() / count;
		FieldType fieldType = fieldInfo.getFieldType();

		for (Object obj : list) {
			
			if(fieldType == FieldType.VO) {
				byte[] subBytes = (byte[]) encodeVo(obj, null, null);
				System.arraycopy(subBytes, 0, bytes, offset, length);
				
			} else {
				char[] chars = fieldType.toString(obj).toCharArray();
				if (fieldType.isNumeric())
					encoder.encodeNumber(chars, 0, chars.length, bytes, offset,
							length, Padding.ZERO, Align.RIGHT);
				else
					encoder.encodeChar(chars, 0, chars.length, bytes, offset,
							length, Padding.SPACE, Align.LEFT);
			}
			
			offset += length;
		}
	}

	@Override
	public void setTrim(boolean bTrim) {
		this.bTrim = bTrim;
	}

	@Override
	public void setEncoder(ICharsetEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public void setDecoder(ICharsetDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	public void clear() {
		// do nothing
	}

}
