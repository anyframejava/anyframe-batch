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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
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

import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.aop.intercept.JobInterceptor;
import com.sds.anyframe.batch.aop.intercept.StepInterceptor;
import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.config.BatchResource.Type;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.AgentInvocationException;
import com.sds.anyframe.batch.util.AddableLong;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AgentJobRepository implements JobRepository {

	static final Log LOGGER = LogFactory.getLog(AgentJobRepository.class);
	
	JobInterceptor jobInterceptor = new JobInterceptor();
	ThreadLocal<StepInterceptor> stepInterceptors = new ThreadLocal<StepInterceptor>();

	com.sds.anyframe.batch.agent.model.Job  agentJob = null;
	
	ThreadLocal<com.sds.anyframe.batch.agent.model.Step> agentSteps = 
		new ThreadLocal<com.sds.anyframe.batch.agent.model.Step>();
	
	private List<BatchResource.Type> skipResourceTypeList = null;
	
	public AgentJobRepository() {
		skipResourceTypeList = new ArrayList<BatchResource.Type>();
	}


	public void setSkipResourceTypeList(
			List<BatchResource.Type> skipResourceTypeList) {
		this.skipResourceTypeList = skipResourceTypeList;
	}


	public JobExecution createJobExecution(Job job, JobParameters jobParameters)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

		String jobName = jobParameters.getString("jobName");
		String logFile = jobParameters.getString("logFile");
		String concurrent = jobParameters.getString("concurrent");
		String jobSeq = jobParameters.getString("jobSeq");
		
		boolean bConcurrent = true;
		
		if(concurrent != null)
			bConcurrent = Boolean.parseBoolean(concurrent);
		
		this.agentJob = new com.sds.anyframe.batch.agent.model.Job(AgentUtils.getPid(), jobName);
		this.agentJob.setAllowConcurrentRunning(bConcurrent);
		this.agentJob.setLogFiles(logFile);
		if(jobSeq != null && (jobSeq.length() > 0) ){
			this.agentJob.setJobSeq(Long.parseLong(jobSeq));
		}
		
		try {
			jobInterceptor.startJob(agentJob);
			
			
		} catch(Throwable t1) {
			String msg = AgentUtils.getStackTraceString(t1);
			if(t1 instanceof IllegalStateException){
				msg = t1.getMessage();
			}
			try {
				jobInterceptor.failJob(msg);
			} catch (Throwable t2) {
				throw new AgentInvocationException(t2.getMessage(), t2);
			}
			throw new JobExecutionAlreadyRunningException(t1.getMessage(),t1);
		}
		
		long jobSeqId = agentJob.getJobSeq();
		
		JobInstance jobInstance = new JobInstance(jobSeqId, jobParameters, jobName);
		
		ExecutionContext executionContext = new ExecutionContext();
		
		JobExecution jobExecution = new JobExecution(jobInstance, jobSeqId);
		jobExecution.setExecutionContext(executionContext);
		
		return jobExecution;
	}

	public void saveOrUpdate(JobExecution jobExecution) {
		
		BatchStatus status = jobExecution.getStatus();
		
		if (status == BatchStatus.STARTING) {
			// DO Nothing (createJobExecution() 시 start 됨)
		} else if (status == BatchStatus.STARTED) {
			// DO Nothing (createJobExecution() 시 start 됨)
		} else if (status == BatchStatus.COMPLETED) {
			try {
				jobInterceptor.completeJob();
			} catch (Exception e) {
				throw new AgentInvocationException("fail to complete job", e);
			}

		} else if (status == BatchStatus.FAILED) {
			String errorMessage = jobExecution.getExecutionContext().getString(BatchDefine.JOB_ERROR_MESSAGE);
			try {
				jobInterceptor.failJob(errorMessage);
			} catch (Exception e) {
				throw new AgentInvocationException("fail to fail job", e);
			}

		} else if (status == BatchStatus.STOPPING) {
			throw new IllegalStateException();

		} else if (status == BatchStatus.STOPPED) {
			throw new IllegalStateException();

		} else if (status == BatchStatus.UNKNOWN) {
			throw new IllegalStateException();
		}
			
	}

	@SuppressWarnings("unchecked")
	public void saveOrUpdate(StepExecution stepExecution) {
		
		BatchStatus status = stepExecution.getStatus();
		ExecutionContext stepContext = stepExecution.getExecutionContext();
		
		if (status == BatchStatus.STARTING) {
			throw new IllegalStateException();

		} else if (status == BatchStatus.STARTED) {
			try {
				/*
				 * StepInterceptor 생성타이밍변경 : 스탭이 시작할때로, ThreadLocal로 생성되는 Step별로 생성되도록 함.(Parallel고려)
				 */
				StepInterceptor stepInterceptor = new StepInterceptor(agentJob);
				stepInterceptor.register(jobInterceptor);
				stepInterceptors.set(stepInterceptor);
				
				List<BatchResource> resources = (List<BatchResource>) stepContext.get(BatchDefine.STEP_RESOURCE_LIST);
				List<Resource> agentResource = this.convertResources(resources);
				String stepName = stepExecution.getStepName();
				String stepLogFilePath = stepContext.getString(BatchDefine.STEP_LOG_FILE_PATH);
				
				com.sds.anyframe.batch.agent.model.Step currentAgentStep = 
					stepInterceptor.startStep(stepName, agentResource, stepLogFilePath);
				
				agentSteps.set(currentAgentStep);
				
			} catch (Exception e) {
				LOGGER.info("fail to start step", e);
				throw new AgentInvocationException("fail to start step", e);
			}

		} else if (status == BatchStatus.COMPLETED) {
			try {
				stepInterceptors.get().completeStep(getCurrentStep());
			} catch (Exception e) {
				throw new AgentInvocationException("fail to complete step", e);
			}

		} else if (status == BatchStatus.FAILED) {
			try {
				com.sds.anyframe.batch.agent.model.Step currentStep = getCurrentStep();
				if(currentStep == null) {
					LOGGER.info("step did not start");
				} else {
					stepInterceptors.get().failStep(currentStep);
				}
			} catch (Exception e) {
				throw new AgentInvocationException("fail to fail step", e);
			}

		} else if (status == BatchStatus.STOPPING) {
			throw new IllegalStateException();

		} else if (status == BatchStatus.STOPPED) {
			throw new IllegalStateException();

		} else if (status == BatchStatus.UNKNOWN) {
			throw new IllegalStateException();

		}
			
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
	
	private com.sds.anyframe.batch.agent.model.Step getCurrentStep() {
		return agentSteps.get();
	}
	
	private List<Resource> convertResources(List<BatchResource> batchResources) {

		List<Resource> agentResources = new ArrayList<Resource>();
		
		for(BatchResource batchResource : batchResources) {
			
			if(skipResourceTypeList.contains(batchResource.getType())){
				continue;
			}
			
			if(batchResource.hasChildren()) {
				agentResources.addAll(convertResources(batchResource.getChildResource()));
				
			}else {
				agentResources.add(convertResource(batchResource));
			}
		}
		
		return agentResources;
	}

	private Resource convertResource(BatchResource batchResource) {
		Resource agentResource = new Resource();

		Type type = batchResource.getType();
		String resourceName = null;
		
		switch(type) {
		case FILE:
			agentResource.setType(ResourceType.FILE);
			resourceName = batchResource.getUrl();
			resourceName = FilenameUtils.normalize(resourceName);
			resourceName = FilenameUtils.separatorsToSystem(resourceName); 
			break;
		case DB:
			agentResource.setType(ResourceType.DATABASE);
			resourceName = batchResource.getId() + "(" + batchResource.getUrl() + ")";
			break;
		default:
			agentResource.setType(ResourceType.DATABASE);
			resourceName = batchResource.getId() + "(" + batchResource.getUrl() + ")";
		}
		agentResource.setResourceName(resourceName);
		
		Mode mode = batchResource.getMode();
		switch(mode) {
		case WRITE:
			agentResource.setIoType(ResourceIoType.WRITE);
			break;
		case UPDATE:
			agentResource.setIoType(ResourceIoType.REWRITE);
			break;
		case READ:
			agentResource.setIoType(ResourceIoType.READ);
			break;
		case DELETE:
			agentResource.setIoType(ResourceIoType.DELETE);
			break;
		default:
			agentResource.setIoType(ResourceIoType.NONE);
		}
		
		AddableLong countRef = batchResource.getCountReference();
		agentResource.setTransactedCount(countRef);
		
		return agentResource;
	}
}
