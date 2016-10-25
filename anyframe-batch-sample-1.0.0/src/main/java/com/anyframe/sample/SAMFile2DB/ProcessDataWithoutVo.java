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

package com.anyframe.sample.SAMFile2DB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.anyframe.sample.CreateData.Vo.EmployeeVo;
import com.sds.anyframe.batch.common.util.MessageFormatter;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReader;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReaderFactory;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriter;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriterFactory;
import com.sds.anyframe.batch.core.step.tasklet.AnyframeAbstractTasklet;

/**
 * 입력 데이터로부터 Employee 데이터를 획득하여 Employee의 사번(no)이 50보다 큰 경우만
 * 결과 파일에 기록하는 샘플용 배치 작업
 * 
 * <p><i>모든 배치작업용 클래스는 AnyframeAbstractTasklet를 상속해야 하며 
 * abstract 메써드로 정의된 execute() 메써드를 오버라이딩 해야 함. </i>
 * 
 * @author Hyoungsoon Kim 
 */

public class ProcessDataWithoutVo extends AnyframeAbstractTasklet {
	
	private static final Log LOGGER = LogFactory.getLog(ProcessDataWithoutVo.class);
	private MessageFormatter message = new MessageFormatter();
	
	@Override
	public void execute(AnyframeItemReaderFactory readerFactory,
                        AnyframeItemWriterFactory writerFactory) throws Exception {

		// reader/writer Factory로부터 ID에 해당하는 reader/writer 획득
		AnyframeItemWriter writer = writerFactory.getItemWriter("writer");
		AnyframeItemReader reader = readerFactory.getItemReader("reader");
		
		reader.setColumnSize(new int[]{20,20,30,30,30,30,30,30});
		// writer가 실행할 SQL 로딩
		writer.loadSQL("insert");
		
		// reader로부터 순차적으로 읽어들임(끝에 도달한 경우 null을 반환함) 
		while( reader.read() != null ) {
			
			// 첫번째 컬럼(사번)이 50보다 큰 경우에만 결과에 기록하는 로직
			if(reader.getInt(0) >= 50) {
				int index =0;
				writer.setInt(0,  reader.getInt(index++));
	 			writer.setBigDecimal(1, reader.getBigDecimal(index++));
	 			writer.setBigInteger(2, reader.getBigInteger(index++));
	 			writer.setDate(3, reader.getDate(index++));
	 			writer.setTimestamp(4, reader.getTimestamp(index++));
	 			
	 			writer.setString(5,reader.getString(index++));
	 			writer.setString(6,reader.getString(index++));
	 			writer.setString(7,reader.getString(index++));
				
				writer.write();
			}
		}
		
 		// LOGGER를 사용하는 것과 달리 MessageFormatter를 사용할 경우 Log 레벨과 상관 없이 결과를 로그에 기록함.
 		// 따라서 개발/운영계에 로그레벨이 달라지는 경우에도 출력을 보장함
 		message.print("########## 배치 실행 결과 ##########");
 		message.print("## READ  횟수 : " + reader.getItemCount());
		message.print("## WRITE 횟수 : " + writer.getItemCount());
		message.print("########################################");
	}
	
}
