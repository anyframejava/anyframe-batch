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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.anyframe.core.CoreContext;
import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.vo.charset.AbstractCharsetCoder;
import com.anyframe.core.vo.charset.EncodingException;
import com.anyframe.core.vo.charset.ICharsetDecoder;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.reflector.Reflector;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteDecoder {

	protected byte[] bytes;
	protected String voName;
	protected String charSet;
	protected AtomicInteger offset;
	protected ICharsetDecoder decoder;
	protected ConversionConfiguration conversionConfiguration;
	protected boolean trimSpace = true;

	public ByteDecoder(byte[] srcBytes, String voName, String charSet, boolean trimSpace,
			ConversionConfiguration conversionConfiguration) {
		this.bytes = srcBytes;
		this.voName = voName;
		this.charSet = charSet;
		this.trimSpace = trimSpace;
		if (conversionConfiguration == null)
			this.conversionConfiguration = new ConversionConfiguration();
		else
			this.conversionConfiguration = conversionConfiguration;
	}

	public Object decode() {
		offset = new AtomicInteger();
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(voName);
		decoder = AbstractCharsetCoder.newCharsetDecoder(charSet);
		return decode(voMeta);
	}

	protected Object decode(VoMeta voMeta) {
		Object obj = voMeta.newInstance();

		Reflector reflector = CoreContext.getMetaManager().getReflector(obj.getClass());

		List<FieldMeta> fields = voMeta.getFields();

		for (int index = 0; index < fields.size(); index++) {
			FieldMeta fieldMeta = fields.get(index);

			try {
				Object fieldValue = decodeField(fieldMeta);

				reflector.setValue(obj, index, fieldValue);
			} catch (Exception e) {
				throw new EncodingException("fail to decode " + fieldMeta.getFieldName() + " field.", e);
			}
		}

		return obj;
	}

	/**
	 * Decode field
	 * 
	 * @param fieldInfo FieldMeta to decode
	 * @return decoded object
	 */
	protected Object decodeField(FieldMeta fieldInfo) {
		FieldType fieldType = fieldInfo.getType();
		if (fieldInfo.isCollection()) {
			int sizeOfList = getSizeOfList(fieldInfo);

			if (fieldInfo.getType() == FieldType.VO) {
				return decodeVoOfList(fieldInfo, sizeOfList);
			} else {
				return decodePrimitiveTypeOfList(fieldInfo, sizeOfList);
			}

		} else if (fieldInfo.isArray()) {
			int sizeOfList = getSizeOfList(fieldInfo);

			if (fieldInfo.getType() == FieldType.VO) {
				Object obj = decodeVoOfList(fieldInfo, sizeOfList);
				Object newArray = Array.newInstance(fieldInfo.getFieldClass(), sizeOfList);
				return ((List<?>) obj).toArray((Object[]) newArray);

			} else {
				Object obj = decodePrimitiveTypeOfList(fieldInfo, sizeOfList);
				Object newArray = Array.newInstance(fieldInfo.getFieldClass(), sizeOfList);
				return ((List<?>) obj).toArray((Object[]) newArray);
			}
		} else if (fieldType == FieldType.VO) {
			return decode(fieldInfo.getVoMeta());
		} else {
			String decodeChar = decoder.decodeChar(bytes, offset.get(), fieldInfo.getFieldLength());
			if (trimSpace)
				decodeChar = decodeChar.trim();

			offset.addAndGet(fieldInfo.getLength());

			return fieldInfo.fromString(decodeChar);
		}
	}

	protected int getSizeOfList(FieldMeta fieldInfo) {
		ArraySize arraySize = fieldInfo.getAnnotation(ArraySize.class);

		if (arraySize.variable()) {
			int maxDigit = conversionConfiguration.getMaxDigit();
			String max = decoder.decodeChar(bytes, offset.get(), maxDigit);
			offset.addAndGet(maxDigit);
			return Integer.parseInt(max.trim());
		} else
			return fieldInfo.getArraySize();
	}

	protected Object decodePrimitiveTypeOfList(FieldMeta fieldInfo, int sizeOfList) {
		FieldType componentType = fieldInfo.getType();
		int length = fieldInfo.getFieldLength();
		List<Object> list = new ArrayList<Object>(sizeOfList);

		for (int i = 0; i < sizeOfList; i++) {
			String decodeChar = decoder.decodeChar(bytes, offset.get(), length);

			if (trimSpace)
				decodeChar = decodeChar.trim();

			Object childObject = componentType.fromString(decodeChar);
			list.add(childObject);

			offset.addAndGet(length);
		}
		return list;
	}

	protected Object decodeVoOfList(FieldMeta fieldInfo, int sizeOfList) {
		VoMeta childVoMeta = fieldInfo.getVoMeta();
		List<Object> list = new ArrayList<Object>(sizeOfList);

		for (int i = 0; i < sizeOfList; i++) {
			Object childObj = decode(childVoMeta);
			list.add(childObj);
		}
		return list;
	}
}
