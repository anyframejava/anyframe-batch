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

package com.anyframe.core.vo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.anyframe.core.vo.transform.bytes.ByteDecoder;
import com.anyframe.core.vo.transform.bytes.ByteEncoder;
import com.anyframe.core.vo.transform.bytes.ConversionConfiguration;
import com.anyframe.core.vo.transform.json.JsonConverter;
import com.anyframe.core.vo.transform.map.MapDecoder;
import com.anyframe.core.vo.transform.map.MapEncoder;
import com.anyframe.core.vo.transform.vo.VoCopier;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class VoUtil {
	private static JsonConverter jsonConverter;

	static {
		boolean jacksonPresent = ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper",
				VoUtil.class.getClassLoader())
				&& ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", VoUtil.class.getClassLoader());
		if (jacksonPresent)
			jsonConverter = new JsonConverter();
	}

	/**
	 * VO 객체를 Map으로 변환.
	 * 
	 * @param obj
	 * @return 변환된 Map 객체
	 */
	public static Map<String, Object> toMap(Object obj) {
		return MapEncoder.toMap(obj);
	}

	/**
	 * Map으로부터 VO객체를 생성.
	 * 
	 * @param sourceMap VO객체로 생성될 Map 객체. Map 안에 포함된 데이터는 일반 Primitive타입의 데이터와 List, Map 타입등이 있음. 
	 * @param voClass 생성될 VO Class.
	 * @return 변환된 VO객체
	 */
	public static <T> T fromMap(Map<String, Object> sourceMap, Class<T> voClass) {
		return (T) MapDecoder.fromMap(sourceMap, voClass);
	}

	/**
	 * Map으로부터 VO객체를 생성.
	 * 
	 * @param sourceMap VO객체로 생성될 Map 객체. Map 안에 포함된 데이터는 일반 Primitive타입의 데이터와 List, Map 타입등이 있음. 
	 * @param voClass 생성될 VO Class 이름.
	 * @return 변환된 VO객체
	 */
	public static Object fromMap(Map<String, Object> sourceMap, String voName) {
		return MapDecoder.fromMap(sourceMap, voName);
	}

	/**
	 * 대외 전문 전송이나 연계를 위한 변환 API. VO 객체를 byte[]로 생성.
	 * 
	 * @param obj byte[] 로 변환될 VO 객체.
	 * @param charSet byte 변환시 사용된 Encoding Character Set.
	 * 
	 * @return VO 객체에서 변환 된 byte[]
	 */
	public static byte[] toBytes(Object obj, String charSet) {
		return toBytes(obj, charSet, null);
	}

	/**
	 * byte[] 전문 생성시 ConversionConfiguration 정보를 통해 가변길이 처리를 추가적으로 수행하여 처리 합니다.
	 * ConversionConfiguration이 제공하는 추가 속성은 VO에서 List 나 [] 에 사용하는 ArraySize 어노테이션에서 variable=true
	 * 속성이 정의된 필드에 대해서 maxDigit과 Padding 속성을 지정할 수 있습니다.
	 * 
	 * @param obj byte[] 로 변환될 VO 객체.
	 * @param charSet byte 변환시 사용될 Encoding Character Set.
	 * @param conversionConfiguration 가변길이 List 또는 []처리시 가변 길이 정보 기록을 위한 최대 자리수(maxDigit)와 Padding(0, ' ')을 지정합니다.
	 * @return VO 객체에서 변환 된 byte[]
	 */
	public static byte[] toBytes(Object obj, String charSet, ConversionConfiguration conversionConfiguration) {
		ByteEncoder byteEncoder = new ByteEncoder(obj, charSet, conversionConfiguration);
		return byteEncoder.encode();
	}

	/**
	 * encode object using Customizing ByteEncoder
	 * 
	 * @param encoder customized ByteEncoder subclass
	 * @return encoded byte array from VO
	 */
	public static byte[] toBytes(ByteEncoder encoder) {
		Assert.notNull(encoder, "ByteEncoder must be not null");
		if (encoder != null)
			return encoder.encode();
		return null;
	}

	/**
	 * decode byte array using customizing ByteDecoder
	 * 
	 * @param decoder customized ByteDecoder subclass
	 * @return decoded object from byte array
	 */
	public static Object fromByte(ByteDecoder decoder) {
		return decoder.decode();
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성.
	 * 
	 * @param srcBytes 전달된 전문 데이터. VO에 정의된 필드별 길이의 총 합산이 실제 byte[]크기와 같아야 함.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @return converted value object
	 */
	public static <T> T fromByte(byte[] srcBytes, Class<T> voClass, String charSet) {
		return (T) fromByte(srcBytes, voClass.getName(), charSet, true, null);
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성. VO생성시 가변길이가 사용된 경우 List, [] 필드 타입에 대하여 가변길이 최대 자리수 정보와 Padding 정보 제공.
	 * 
	 * @param srcBytes 전달된 전문 데이터. 가변길이의 경우 실제 VO에 정의한 길이 정보와 다를 수 있음.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @param conversionConfiguration 가변길이 List 또는 []처리시 가변 길이 정보 기록을 위한 최대 자리수(maxDigit)와 Padding(0, ' ')을 지정합니다.
	 * @return converted value object
	 */
	public static <T> T fromByte(byte[] srcBytes, Class<T> voClass, String charSet,
			ConversionConfiguration conversionConfiguration) {
		return (T) fromByte(srcBytes, voClass.getName(), charSet, true, conversionConfiguration);
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성. VO생성시 가변길이가 사용된 경우 List, [] 필드 타입에 대하여 가변길이 최대 자리수 정보와 Padding 정보 제공.
	 * 
	 * @param srcBytes 전달된 전문 데이터. 가변길이의 경우 실제 VO에 정의한 길이 정보와 다를 수 있음.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @param trimSpace byte[] 전문에 고정길이 처리를 위한 공백 문자가 있는 경우 이를 Trim할지 여부 결정.
	 * @return converted value object
	 */
	public static <T> T fromByte(byte[] srcBytes, Class<T> voClass, String charSet, boolean trimSpace) {
		return (T) fromByte(srcBytes, voClass.getName(), charSet, trimSpace, null);
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성. VO생성시 가변길이가 사용된 경우 List, [] 필드 타입에 대하여 가변길이 최대 자리수 정보와 Padding 정보 제공.
	 * 
	 * @param srcBytes 전달된 전문 데이터. 가변길이의 경우 실제 VO에 정의한 길이 정보와 다를 수 있음.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @param trimSpace byte[] 전문에 고정길이 처리를 위한 공백 문자가 있는 경우 이를 Trim할지 여부 결정.
	 * @param conversionConfiguration 가변길이 List 또는 []처리시 가변 길이 정보 기록을 위한 최대 자리수(maxDigit)와 Padding(0, ' ')을 지정합니다.
	 * @return converted value object
	 */
	public static <T> T fromByte(byte[] srcBytes, Class<T> voClass, String charSet, boolean trimSpace,
			ConversionConfiguration conversionConfiguration) {
		return (T) fromByte(srcBytes, voClass.getName(), charSet, trimSpace, conversionConfiguration);
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성. VO생성시 가변길이가 사용된 경우 List, [] 필드 타입에 대하여 가변길이 최대 자리수 정보와 Padding 정보 제공.
	 * 
	 * @param srcBytes 전달된 전문 데이터. 가변길이의 경우 실제 VO에 정의한 길이 정보와 다를 수 있음.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @param trimSpace byte[] 전문에 고정길이 처리를 위한 공백 문자가 있는 경우 이를 Trim할지 여부 결정.
	 * @return converted value object
	 */
	public static Object fromByte(byte[] srcBytes, String voName, String charSet, boolean trimSpace) {
		return fromByte(srcBytes, voName, charSet, trimSpace, null);
	}

	/**
	 * byte[] 로 부터 VO 객체를 생성. VO생성시 가변길이가 사용된 경우 List, [] 필드 타입에 대하여 가변길이 최대 자리수 정보와 Padding 정보 제공.
	 * 
	 * @param srcBytes 전달된 전문 데이터. 가변길이의 경우 실제 VO에 정의한 길이 정보와 다를 수 있음.
	 * @param voClass 생성될 VO 클래스.
	 * @param charSet byte 변환시 사용될 Decoding Character Set.
	 * @param trimSpace byte[] 전문에 고정길이 처리를 위한 공백 문자가 있는 경우 이를 Trim할지 여부 결정.
	 * @param conversionConfiguration 가변길이 List 또는 []처리시 가변 길이 정보 기록을 위한 최대 자리수(maxDigit)와 Padding(0, ' ')을 지정합니다.
	 * @return converted value object
	 */
	public static Object fromByte(byte[] srcBytes, String voName, String charSet, boolean trimSpace,
			ConversionConfiguration conversionConfiguration) {
		ByteDecoder decoder = new ByteDecoder(srcBytes, voName, charSet, trimSpace, conversionConfiguration);
		return decoder.decode();
	}

	/**
	 * JSON String을 VO 객체로 변환
	 * 
	 * @param clazz 리턴받고자 하는 클래스 타입
	 * @param jsonString String 형태의 JSON 데이터
	 * @return converted value object
	 * @throws Exception
	 */
	public static <T> T fromJson(Class<T> clazz, String jsonString) throws Exception {
		return (T) jsonConverter().fromJson(clazz, jsonString);
	}

	/**
	 * InputStream형태의 JSON 데이터를 VO 객체로 변환
	 * 
	 * @param clazz 리턴받고자 하는 클래스 타입
	 * @param is InputStream 형태의 JSON 데이터
	 * @return converted value object
	 * @throws Exception
	 */
	public static <T> T fromJson(Class<T> clazz, InputStream is) throws Exception {
		return (T) jsonConverter().fromJson(clazz, is);
	}

	/**
	 * VO객체를 JSON String으로 변환
	 * 
	 * @param object JSON으로 변환하고자 하는 VO객체
	 * @return converted JSON String
	 * @throws Exception
	 */
	public static String toJson(Object object) throws Exception {
		return toJson(object, true);
	}

	/**
	 * VO객체를 JSON String으로 변환
	 * 
	 * @param object JSON으로 변환하고자 하는 VO객체
	 * @param failOnEmptyBeans 객체의 Property가 accessor가 없는 경우, 
	 * 			failOnEmptyBeans 값을 true로 설정하면 {@link org.codehaus.jackson.map.JsonMappingException}을 던지고 
	 * 			failOnEmptyBeans 값을 false로 설정하면 empty object로 처리한다.
	 * @return 변환된 JSON String
	 * @throws Exception
	 * @see org.codehaus.jackson.map.SerializationConfig.Feature
	 */
	public static String toJson(Object object, boolean failOnEmptyBeans) throws Exception {
		return jsonConverter().toJson(object, failOnEmptyBeans);
	}

	/**
	 * VO객체를 JSON으로 변환하여 OutputStream으로 Write.
	 * 
	 * @param object JSON으로 변환하고자 하는 VO객체
	 * @param out Write하고자 하는 OuputStream
	 * @throws Exception
	 */
	public static void toJson(Object object, OutputStream out) throws Exception {
		jsonConverter().toJson(object, out);
	}

	private static JsonConverter jsonConverter() throws Exception {
		if (jsonConverter == null)
			throw new Exception("Can't find jackson-json library.");
		return jsonConverter;
	}

	/**
	 * VO 객체를 복사.
	 * @param source 소스 객체
	 * @param target 대상 객체
	 */
	public static void copy(Object source, Object target) {
		VoCopier.copy(source, target, true);
	}

	/**
	 * VO 객체를 복사.
	 * @param source 소스 객체
	 * @param target 대상 객체
	 */
	public static void copy(Object source, Object target, boolean typeCheck) {
		VoCopier.copy(source, target, typeCheck);
	}

}
