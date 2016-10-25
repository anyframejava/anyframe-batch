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


/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public interface AnyframeItemReadWriterFactory {

	/**
	 * Job Configration 파일(CFG.xml)에 정의된 {@code id}에 해당하는 {@link AnyframeItemReadWriter}를 반환한다.
	 * 반환되는 itemReadWriter는 vo를 사용하지 않으며 getter/setter method를 이용하여 item을 읽고 쓸 수 있다.
	 * 
	 * @param id itemReader의 고유한 ID
	 * @return {@code id}에 대한  {@link AnyframeItemReadWriter}
	 * 
	 * @see AnyframeItemReader#getInt(int)
	 * @see AnyframeItemReader#getString(int)
	 * @see AnyframeItemReader#getBigDecimal(int)
	 * @see AnyframeItemWriter#setInt(int, int)
	 * @see AnyframeItemWriter#setString(int, String)
	 * @see AnyframeItemWriter#setBigDecimal(int, java.math.BigDecimal)
	 */
	public AnyframeItemReadWriter getItemReadWriter(String id);

	/**
	 * Job Configration 파일(CFG.xml)에 정의된 {@code id}에 해당하는 {@link AnyframeItemReadWriter}를 반환한다.
	 * 반환되는 itemReadWriter는 인자로 전달된 {@code vo}를 기준으로 read/write를 수행하게 된다.
	 * 
	 * @param id itemReadWriter는 고유한 ID
	 * @param vo itemReadWriter가 사용할 VO 객체
	 * @return {@code id}에 대한 {@link AnyframeItemReadWriter}
	 */
	public AnyframeItemReadWriter getItemReadWriter(String id, Object vo);

}
