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

package com.anyframe.sample.Parallel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sds.anyframe.batch.common.util.MessageFormatter;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReaderFactory;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriter;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriterFactory;
import com.sds.anyframe.batch.core.step.tasklet.AnyframeAbstractTasklet;

/**
 * 샘플용 Employee 데이터를 100개 생성하여 파일에 기록하는 배치 작업 
 * 
 * @author Hyoungsoon Kim 
 */

public class CreateDataWithoutVo extends AnyframeAbstractTasklet {
	
	private static final Log LOGGER = LogFactory.getLog(CreateDataWithoutVo.class);
	private MessageFormatter message = new MessageFormatter();
	
	@Override
	public void execute(AnyframeItemReaderFactory readerFactory,
                        AnyframeItemWriterFactory writerFactory) throws Exception {

		// CFG.xml 파일에 정의된 writer의 ID를 기반으로 writerFactory로부터 writer 획득.  
 		AnyframeItemWriter writer = writerFactory.getItemWriter("writer");

 		writer.setColumnSize(new int[4]);
 		
 		for(int i=0; i<100; i++) {
 			writer.setInt(0, i);
 			writer.setString(1,"이름_"+i);
 			writer.setString(2,"주소_"+i);
 			writer.setString(3,"설명_"+i);
 			Thread.sleep(2000);
 			writer.write();
 			
 		}
 		
 		// LOGGER를 사용하는 것과 달리 MessageFormatter를 사용할 경우 Log 레벨과 상관 없이 결과를 로그에 기록함.
 		// 따라서 개발/운영계에 로그레벨이 달라지는 경우에도 출력을 보장함
 		message.print("########## 배치 실행 결과 ##########");
		message.print("## WRITE 횟수 : " + writer.getItemCount());
		message.print("########################################");
	}
	
}
