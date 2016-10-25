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

package com.anyframe.core.vo.transform.json;

import java.io.InputStream;
import java.io.OutputStream;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JsonConverter {
	/**
	 * JSON String을 VO 객체로 변환
	 * 
	 * @param clazz 리턴받고자 하는 클래스 타입
	 * @param jsonString String 형태의 JSON 데이터
	 * @return converted value object
	 * @throws Exception
	 */
	public Object fromJson(Class<?> clazz, String jsonString) throws Exception {
		ObjectMapper objectMapper = getJsonObjectMapper();

		return objectMapper.readValue(jsonString, clazz);
	}

	/**
	 * InputStream형태의 JSON 데이터를 VO 객체로 변환
	 * 
	 * @param clazz 리턴받고자 하는 클래스 타입
	 * @param is InputStream 형태의 JSON 데이터
	 * @return converted value object
	 * @throws Exception
	 */
	public Object fromJson(Class<?> clazz, InputStream is) throws Exception {
		ObjectMapper objectMapper = getJsonObjectMapper();

		return objectMapper.readValue(is, clazz);
	}

	/**
	 * VO객체를 JSON String으로 변환
	 * 
	 * @param object JSON으로 변환하고자 하는 VO객체
	 * @return converted JSON String
	 * @throws Exception
	 */
	public String toJson(Object object) throws Exception {
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
	public String toJson(Object object, boolean failOnEmptyBeans) throws Exception {
		ObjectMapper objectMapper = getJsonObjectMapper();
		objectMapper.configure(Feature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);
		return objectMapper.writeValueAsString(object);
	}

	/**
	 * VO객체를 JSON으로 변환하여 OutputStream으로 Write.
	 * 
	 * @param object JSON으로 변환하고자 하는 VO객체
	 * @param out Write하고자 하는 OuputStream
	 * @throws Exception
	 */
	public void toJson(Object object, OutputStream out) throws Exception {
		JsonEncoding encoding = JsonEncoding.UTF8;
		ObjectMapper objectMapper = getJsonObjectMapper();

		JsonGenerator jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(out, encoding);

		objectMapper.writeValue(jsonGenerator, object);
	}

	/**
	 * JSON serialize/deserialize 시에 Json Annotation과 JAXB Annotation을 모두 인식하도록 설정한 ObejctMapper 생성
	 * 
	 * @return ObjectMapper
	 */
	private ObjectMapper getJsonObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		AnnotationIntrospector primary = new JaxbAnnotationIntrospector();
		AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
		// make deserializer, serializer use JAXB, Jackson annotations
		objectMapper.setAnnotationIntrospector(pair);

		return objectMapper;
	}
}
