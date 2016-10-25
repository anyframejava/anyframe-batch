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

import java.sql.SQLException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface ItemOperation {

	/**
	 * <p>itemReader인 경우  Resource로 부터 읽어들인 item 수를 반환한다.
	 * <p>itemWriter인 경우 Resource에 기록한 item 수를 반환한다.
	 * 
	 * @return
	 */
	long getItemCount();
	
	/**
	 * {@code this} itemReader 또는 itemWriter 에 바인딩된 Resource 의 URL
	 * 
	 * @return
	 */
	String getURL();

	/**
	 * {@code this} itemReader 또는 itemWriter 에 바인딩된 Resource 를 삭제한다
	 * 
	 * @return true: 정상적으로 삭제됨. false: 삭제 안됨
	 */
	boolean deleteResource();
	
	
	/**
	 * {@code queryID} 에 해당하는 SQL Query를 Query 로드한다.
	 * {@link #loadSQL(String)}이 성공적으로 수행된 이후에 실행되는 {@link AnyframeItemReader#read()} 는
	 * load된 Query를 기반으로 동작한다.
	 * 
	 * <p><i>※DB Reader, Writer만 사용 가능함</i>
	 * 
	 * @param queryID Query 파일(xml)에 정의한 query ID
	 * @see #setQueryPath(String)
	 */
	void loadSQL(String queryID);
	
	void setQuery(String query);
	/**
	 * {@link #loadSQL(String)}로 로딩된 SQL Query의 Parameter('?')에 {@code params}를 바인딩한다.
	 * 
	 * <p>※<i>{@code this} 메쏘드는 DB Reader, Writer만 사용 가능함</i>
	 * 
	 * <pre>
	 * 사용 예시)
	 * setSqlParameters(13, "홍길동");
	 * SELECT * FROM sample WHERE ID=? AND NAME=? 인 경우
	 * SELECT * FROM sample WHERE ID='13' AND NAME='홍길동'</pre>
	 * 
	 * @param params 바인딩할 SQL 파라메터 값들 (가변인자)
	 * @see #loadSQL(String)
	 * @throws SQLException 
	 */
	void setSqlParameters(Object... params) throws SQLException;
	
	/**
	 * {@link #loadSQL(String)} 시 사용할 쿼리 파일의 위치를 지정한다.
	 * 
	 * <p><i>※ DB Reader, Writer만 사용 가능함</i>
	 * 
	 * @param QueryFilePath 쿼리 파일의 경로. 절대경로 및 상대경로 모두 가능함
	 * @see #loadSQL(String)
	 */
	void setQueryPath(String QueryFilePath);
	
	/**
	 * Reader, Writer에 저장된 Query의 결과 및 파라메터를  초기화한다.
	 * {@code reset} 이후에 실행되는 {@code read(), write()}는 새로 지정된 파라메터를 기준으로 동작한다.
	 * 
	 * <p><i>※DB Reader, Writer만 사용 가능함</i>
	 * 
	 * <pre>
	 * reader = itemFactory.getItemReader("reader1", new SampleVO());
	 * reader.loadSQL("select");
	 * 
	 * reader.setSqlParameters(10, "홍길동");
	 * SampleVO vo1 = reader.read();
	 * 
	 * reader.reset();
	 * reader.setSqlParameters(20, "이순신");
	 * SampleVO vo2 = reader.read();
	 * 
	 * </pre>
	 */
	void reset();
	
	/**
	 * {@code Read(), Write()} 시 실행되는 Running Query를 Log로 기록할지 설정한다. (default: false)
	 * 
	 * <p><i>※DB Reader, Writer만 사용 가능함</i>
	 * 
	 * @param bShow 'true' 로그 기록, 'false' 기록 안함
	 */
	void showQueryLog(boolean bShow);
	
	/**
	 * Resource 의 컬럼  layout을 설정한다.
	 * {@code this} itemReader가 VO를 사용하지 않고 getter method 를 사용할 경우
	 * 사전에 필수적으로 {@link #setColumnSize(int[])}을 호출하여  컬럼 layout을 사전에 설정해야 한다. 
	 * 
	 * @param columnSize 컬럼 layout 정보를 담은 array 객체. array의 length는 컬럼의 개수를 의미하며 
	 *                   각 array component의 값은 해당 컬럼의 길이 정보를 담는다.
	 */
	void setColumnSize(int [] columnSize);
}
