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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.batch.item.ItemWriter;

/** 
 * 배치 업무에서 처리된 데이터를 Resource(파일, 데이터베이스 등)에 기록하기 위한 추상화된 API를 제공한다.
 * 
 * @author Hyoungsoon Kim
 *
 */
public interface AnyframeItemWriter extends ItemWriter, ItemOperation {
	
	/**
	 * {@code index} column 위치에 byte[] 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setBytes(int index, byte[] bytes) throws Exception;
	
	/**
	 * {@code index} column 위치에 Integer 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setInt(int index, int value) throws Exception;
	
	/**
	 * {@code index} column 위치에 String 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setString(int index, String value) throws Exception;
	
	/**
	 * {@code index} column 위치에 BigDecimal 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setBigDecimal(int index, BigDecimal value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Boolean 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setBoolean(int index, Boolean value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Short 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setShort(int index, Short value) throws Exception;
	
	/**
	 * {@code index} column 위치에 BigInteger 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setBigInteger(int index, BigInteger value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Long 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setLong(int index, Long value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Float 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setFloat(int index, Float value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Double 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setDouble(int index, Double value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Date 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setDate(int index, Date value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Byte 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setByte(int index, Byte value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Character 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setCharacter(int index, Character value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Time 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setTime(int index, Time value) throws Exception;
	
	/**
	 * {@code index} column 위치에 Timestamp 데이터를 기록한다.
	 * 
	 * @param index 컬럼 위치
	 * @throws Exception
	 */
	void setTimestamp(int index, Timestamp value) throws Exception;
	
	/**
	 * {@code index} preparedStatement.executeUpdate 수행후 결과값을 가져온다.
	 * 
	 * @param 
	 * @throws Exception
	 */
	int getSqlRowCount() throws Exception;
	
	/**
	 * setter method를 통해 설정된 값을 바인딩된 Resource에 기록한다.
	 * 
	 * @throws Exception
	 * 
	 * @see #setInt(int, int)
	 * @see #setString(int, String)
	 * @see #setBigDecimal(int, BigDecimal)
	 * @see #setBytes(int, byte[])
	 */
	void write() throws Exception;
	
}
