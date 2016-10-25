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

package com.sds.anyframe.batch.core.item.support;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.batch.item.ItemReader;

/** 
 * Resource(파일, 데이터베이스 등)로부터 처리할 데이터를 읽어들이기 위한 추상화된 API를 제공한다.
 * 
 * @author Hyoungsoon Kim 
 *
 */

public interface AnyframeItemReader extends ItemReader, ItemOperation {
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 bytes 값을 반환한다
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 byte[] 값
	 * @throws Exception
	 */
	byte[] getBytes(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Integer 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Integer 값
	 * @throws Exception
	 */
	int getInt(int index) throws Exception;

	/**
	 * 현재 row 에서 {@code index} column 위치의 String 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 String 값
	 * @throws Exception
	 */
	String getString(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 BigDecimal 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 BigDecimal 값
	 * @throws Exception
	 */
	BigDecimal getBigDecimal(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Boolean 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Boolean 값
	 * @throws Exception
	 */
	Boolean getBoolean(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Timestamp 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Timestamp 값
	 * @throws Exception
	 */
	Timestamp getTimestamp(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Time 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Time 값
	 * @throws Exception
	 */
	Time getTime(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Character 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Character 값
	 * @throws Exception
	 */
	Character getCharacter(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Byte 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Byte 값
	 * @throws Exception
	 */
	Byte getByte(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Date 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Date 값
	 * @throws Exception
	 */
	Date getDate(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Double 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Double 값
	 * @throws Exception
	 */
	Double getDouble(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Float 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Float 값
	 * @throws Exception
	 */
	Float getFloat(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Long 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Long 값
	 * @throws Exception
	 */
	Long getLong(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 BigInteger 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 BigInteger 값
	 * @throws Exception
	 */
	BigInteger getBigInteger(int index) throws Exception;
	
	/**
	 * 현재 row 에서 {@code index} column 위치의 Short 값을 반환한다.
	 * 
	 * @param index 컬럼 인덱스
	 * @return {@code index}위치의 Short 값
	 * @throws Exception
	 */
	Short getShort(int index) throws Exception;
	
    /**
     * {@code this} itemReader가 사용하는 Resource(File)가 가용한지 확인
     *
     * @return Resource(File)이 있는 경우 true, 아닌 경우 false
     */
	boolean exists();
	
	/**
	 * {@code this} itemReader가 사용할 VO 객체를 등록한다.
	 * VO 객체를 등록한 경우 이후 {@link ItemReader#read()} 메써드를 사용하여 데이터를 읽어올 때
	 * 데이터가 자동적으로 VO로 변환되어 리턴된다. 
	 * 
	 * @param vo {@link AbstractVo}를 상속한 VO 객체
	 */
	void setVo(Object vo);
	
	/**
	 * {@code this} ItemReader가 처리하는 Resource 에 대한 전체 item(row) 수를 반환한다.
	 * 
	 * @return
	 */
	long getTotalLineCount() throws IOException;
	
	/**
	 * Database Reader인 경우 ResultSet의 Fetch 사이즈를 설정한다.
	 * 
	 * <p><i>※DB Reader만 사용 가능함</i>
	 * 
	 * @deprecated
	 * @param fetchSize
	 */
	@Deprecated
	void setFetchSize(int fetchSize);
	
}
