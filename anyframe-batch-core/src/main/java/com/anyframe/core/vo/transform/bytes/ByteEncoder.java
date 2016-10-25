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

package com.anyframe.core.vo.transform.bytes;

import java.util.Arrays;
import java.util.List;

import com.anyframe.core.CoreContext;
import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.vo.charset.AbstractCharsetCoder;
import com.anyframe.core.vo.charset.EncodingException;
import com.anyframe.core.vo.charset.ICharsetEncoder;
import com.anyframe.core.vo.charset.ICharsetEncoder.Align;
import com.anyframe.core.vo.charset.ICharsetEncoder.Padding;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.transform.RawDataType;
import com.anyframe.core.vo.transform.TransformationException;
import com.anyframe.core.vo.transform.bytes.io.ExpandableByteArrayOutputStream;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteEncoder {
	private Object object;
	private String charSet;
	private ConversionConfiguration conversionConfiguration;
	private ICharsetEncoder encoder;
	private ExpandableByteArrayOutputStream arrayOutputStream;

	public ByteEncoder(Object obj, String charSet, ConversionConfiguration conversionConfiguration) {
		this.object = obj;
		this.charSet = charSet;
		if (conversionConfiguration != null)
			this.conversionConfiguration = conversionConfiguration;
		else
			this.conversionConfiguration = new ConversionConfiguration();
	}

	public byte[] encode() {
		encoder = AbstractCharsetCoder.newCharsetEncoder(charSet);
		Reflector reflector = CoreContext.getMetaManager().getReflector(object.getClass());

		if (reflector.isSupportedDataType(object, RawDataType.BYTE, encoder.getCharset()))
			return (byte[]) reflector.getRawData(object);

		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(object.getClass());
		arrayOutputStream = new ExpandableByteArrayOutputStream(voMeta.getLength());

		encodeVo(object, 0);
		return arrayOutputStream.getBuf();
	}

	protected int encodeVo(Object object, int offset) {

		VoMeta voInfo = CoreContext.getMetaManager().getMetadata(object.getClass());
		Reflector reflector = CoreContext.getMetaManager().getReflector(object.getClass());

		Object[] values = reflector.getValues(object);

		List<FieldMeta> fields = voInfo.getFields();
		int fieldCount = fields.size();

		for (int index = 0; index < fieldCount; index++) {
			FieldMeta fieldInfo = fields.get(index);
			Object value = values[index];

			try {
				offset = encodeField(value, fieldInfo, offset);
			} catch (Exception ex) {
				throw new EncodingException("fail to encode vo field. " + fieldInfo.getFieldName() + " = " + value, ex);
			}
		}

		return offset;
	}

	/**
	 * Encode field
	 * 
	 * @param fieldValue field value to encode
	 * @param fieldInfo FieldMeta to encode
	 * @param offset The offset of the subarray to be used
	 * @return
	 */
	public int encodeField(Object fieldValue, FieldMeta fieldInfo, int offset) {
		if (fieldInfo.getLength() == -1)
			throw new ZeroLengthException("Length of " + fieldInfo.getFieldName() + " could not be zero. Must be greater than 0");

		if (fieldValue == null)
			return writeNullValue(fieldInfo, offset);

		FieldType fieldType = fieldInfo.getType();

		if (fieldInfo.isCollection()) {
			List<Object> list = (List<Object>) fieldValue;
			ArraySize arraySize = fieldInfo.getAnnotation(ArraySize.class);

			if (arraySize.variable())
				offset = expandSizeAndWriteSizeOfList(list, fieldInfo, offset);

			if (fieldInfo.getType() == FieldType.VO)
				return encodeListOfVo(list, fieldInfo, offset, arraySize);
			else
				return encodeListOfPrimitiveObject(list, fieldInfo, offset, arraySize);

		} else if (fieldInfo.isArray()) {
			List<Object> asList = Arrays.asList((Object[]) fieldValue);
			ArraySize arraySize = fieldInfo.getAnnotation(ArraySize.class);
			if (arraySize.variable())
				offset = expandSizeAndWriteSizeOfList(asList, fieldInfo, offset);

			if (fieldInfo.getType() == FieldType.VO)
				return encodeListOfVo(asList, fieldInfo, offset, arraySize);
			else
				return encodeListOfPrimitiveObject(asList, fieldInfo, offset, arraySize);

		} else if (fieldType == FieldType.VO) {
			return encodeVo(fieldValue, offset);

		} else {
			char[] chars = fieldInfo.toString(fieldValue).toCharArray();
			byte[] bytes = arrayOutputStream.getBuf();

			if (fieldType.isNumeric()) {
				encoder.encodeNumber(chars, 0, chars.length, bytes, offset, fieldInfo.getLength(), Padding.ZERO,
						Align.RIGHT);
			} else {
				encoder.encodeChar(chars, 0, chars.length, bytes, offset, fieldInfo.getLength(), Padding.SPACE,
						Align.LEFT);
			}

			offset += fieldInfo.getLength();

			return offset;
		}
	}

	protected int writeNullValue(FieldMeta fieldInfo, int offset) {
		byte[] bytes = arrayOutputStream.getBuf();
		if (fieldInfo.isCollection() || fieldInfo.isArray()) {
			ArraySize arraySize = fieldInfo.getAnnotation(ArraySize.class);

			if (arraySize.variable())
				return expandSizeAndWriteSizeOfList(null, fieldInfo, offset);
			else
				encoder.encodeChar(new char[] { ' ' }, 0, 1, bytes, offset, fieldInfo.getLength(), Padding.SPACE,
						Align.LEFT);
		} else
			encoder.encodeChar(new char[] { ' ' }, 0, 1, bytes, offset, fieldInfo.getLength(), Padding.SPACE,
					Align.LEFT);

		return offset + fieldInfo.getLength();
	}

	protected int expandSizeAndWriteSizeOfList(List<Object> list, FieldMeta fieldInfo, int offset) {
		int length = fieldInfo.getFieldLength();

		int size = (list == null ? 0 : list.size());
		int expand = size * length;

		int maxDigit = conversionConfiguration.getMaxDigit();
		Padding padding = conversionConfiguration.getPadding();
		arrayOutputStream.expand(expand + maxDigit);

		byte[] bytes = arrayOutputStream.getBuf();
		char[] chars = new Integer(size).toString().toCharArray();

		if (padding == Padding.ZERO)
			encoder.encodeNumber(chars, 0, chars.length, bytes, offset, maxDigit, Padding.ZERO, Align.RIGHT);
		else
			encoder.encodeChar(chars, 0, chars.length, bytes, offset, maxDigit, Padding.SPACE, Align.LEFT);

		return offset + maxDigit;
	}

	protected int encodeListOfVo(List<Object> list, FieldMeta fieldInfo, int offset, ArraySize arraySize) {

		if (!arraySize.variable() && list.size() != fieldInfo.getArraySize()) {
			throw new TransformationException(String.format("size(%d) of %s is different from @ArraySize(%d)", list
					.size(), fieldInfo.getFieldName(), arraySize.value()));
		}

		for (Object obj : list) {
			offset = encodeVo(obj, offset);
		}

		return offset;
	}

	protected int encodeListOfPrimitiveObject(List<Object> list, FieldMeta fieldInfo, int offset, ArraySize arraySize) {
		if (!arraySize.variable() && list.size() != fieldInfo.getArraySize()) {
			throw new TransformationException(String.format("size(%d) of %s is defferent from @ArraySize(%d)", list
					.size(), fieldInfo.getFieldName(), arraySize.value()));
		}

		int length = fieldInfo.getFieldLength();
		FieldType componentType = fieldInfo.getType();

		byte[] bytes = arrayOutputStream.getBuf();

		for (Object obj : list) {
			char[] chars = obj.toString().toCharArray();
			if (componentType.isNumeric())
				encoder.encodeNumber(chars, 0, chars.length, bytes, offset, length, Padding.ZERO, Align.RIGHT);
			else
				encoder.encodeChar(chars, 0, chars.length, bytes, offset, length, Padding.SPACE, Align.LEFT);

			offset += length;
		}

		return offset;
	}

}
