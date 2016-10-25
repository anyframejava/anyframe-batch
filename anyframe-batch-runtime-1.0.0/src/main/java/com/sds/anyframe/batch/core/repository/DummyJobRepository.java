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

package com.sds.anyframe.batch.core.repository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class DummyJobRepository implements JobRepository {


	public DummyJobRepository() {
	}


	public JobExecution createJobExecution(Job job, JobParameters jobParameters)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

		JobInstance jobInstance = new JobInstance(new Long(0), jobParameters, job.getName());
		ExecutionContext executionContext = new ExecutionContext();
		
		JobExecution jobExecution = new JobExecution(jobInstance);
		jobExecution.setExecutionContext(executionContext);
		
		return jobExecution;
	}

	public void saveOrUpdate(JobExecution jobExecution) {
		return;
	}

	public void saveOrUpdate(StepExecution stepExecution) {
		return;
	}

	public void saveOrUpdateExecutionContext(StepExecution stepExecution) {
		return;
	}

	public StepExecution getLastStepExecution(JobInstance jobInstance, Step step) {
		return null;

	}

	public int getStepExecutionCount(JobInstance jobInstance, Step step) {	
		return -1;
	}

}
