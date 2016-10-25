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

package com.sds.anyframe.batch.core.step.support;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.repeat.ExitStatus;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.define.BatchDefine;


/**
 * 배치 작업이 비정상적으로 종료할 때(kill -3), 사용하던 Resource 를 반환하기 위한 Listener
 * 
 * @author Hyoungsoon Kim
 *
 */
public class StepResourceListener implements StepExecutionListener{

	/**
	 * 스탭 시작 시 스탭에서 사용하는 Resource 를 등록
	 */
	public void beforeStep(StepExecution stepExecution) {
		@SuppressWarnings("unchecked")
		List<BatchResource> resources = (List<BatchResource>) stepExecution
				.getExecutionContext().get(BatchDefine.STEP_RESOURCE_LIST);
		
		StepResourceHolder.setResource(resources);
	}
	
	
	/**
	 * 스텝이 정상적으로 종료 시 resource 해제
	 */
	public ExitStatus afterStep(StepExecution stepExecution) {
		StepResourceHolder.clear();
		return null;
	}


	/**
	 * 스텝에서 에러가 발생한 경우 스텝에서 사용하는 Resource 를 삭제한다.
	 */
	public ExitStatus onErrorInStep(StepExecution stepExecution, Throwable e) {
		if(BatchDefine.DELETE_FILE_ON_ERROR) {
			StepResourceHolder.deleteFiles();
		}
		return null;
	}

}
