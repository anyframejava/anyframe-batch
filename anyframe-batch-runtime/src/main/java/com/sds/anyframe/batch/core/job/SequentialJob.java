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

package com.sds.anyframe.batch.core.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.ExitStatus;
import org.springframework.batch.repeat.RepeatException;

import com.sds.anyframe.batch.common.util.ParamUtil;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class SequentialJob extends SimpleJob {

	private static final Log LOGGER = LogFactory.getLog(SequentialJob.class);
	
	boolean concurrent = false;
	
	protected Map<String, String> parameters = new HashMap<String, String>();

	public boolean isConcurrent() {
		return concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecution execution) throws JobExecutionException {

		LOGGER.debug("Job execution starting: " + execution);
		
		Throwable jobFailure = null;
		
		try {
			
			// job instance id를 업무에서 사용할 수 있도록 ParamUtil에 등록
			ParamUtil.addParameter(BatchDefine.JOB_INSTANCE_ID, String.valueOf(execution.getJobId()));
			
			// job paramater를 ParamUtil에 등록
			for(Entry<String, String> entry : parameters.entrySet()) {
				
				String key = entry.getKey();
				if(ParamUtil.containsKey(key)) {
					String msg = String
							.format("Ignore job parameter[key=%s, value=%s] which conflict with [key=%s, value=%s].",
									key, ParamUtil.getParameter(key), key, entry.getValue());
					LOGGER.info(msg);
				} else {
					ParamUtil.addParameter(entry.getKey(), entry.getValue());
				}
			}
			
			String stepFrom = execution.getExecutionContext().getString("stepFrom");
			String stepTo   = execution.getExecutionContext().getString("stepTo");

			if (execution.getStatus() != BatchStatus.STOPPING) {

				execution.setStartTime(new Date());
				execution.setStatus(BatchStatus.STARTED);
				getJobRepository().saveOrUpdate(execution);
				
				getCompositeListener().beforeJob(execution);

				try {
					StepExecution currentStepExecution = null;
					
					boolean bSkip = stepFrom.compareToIgnoreCase("N/A") != 0;
					
					List<Step> steps = (List<Step>)getSteps();
					for (Step step : steps) {
						
						String stepName = step.getName();
						
						if(bSkip) {
							
							if(stepName.compareTo(stepFrom) == 0)
								bSkip = false;
							else
								continue;
							
						}
						
						currentStepExecution = execution.createStepExecution(step);
						currentStepExecution.setExecutionContext(new ExecutionContext());
						
						step.execute(currentStepExecution);
						
						if(!bSkip && stepName.compareTo(stepTo) == 0) {
							break;
						}
							
						if (currentStepExecution.getStatus() != BatchStatus.COMPLETED) {
							// Terminate the job if a step fails
							break;
						}
					}

					// Update the job status to be the same as the last step
					if (currentStepExecution != null) {
						LOGGER.debug("Upgrading JobExecution status: " + currentStepExecution);
						execution.setStatus(currentStepExecution.getStatus());
						execution.setExitStatus(currentStepExecution.getExitStatus());
						
					} else {	// no step executed
						throw new BatchRuntimeException("There is no step to execute");
					}
					LOGGER.debug("Job execution complete: " + execution);
				}
				catch (RepeatException e) {
					throw e.getCause();
				}
			}
			else {

				// The job was already stopped before we even got this far. Deal
				// with it in the same way as any other interruption.
				execution.setStatus(BatchStatus.STOPPED);
				execution.setExitStatus(ExitStatus.FINISHED);
				LOGGER.debug("Job execution was stopped: " + execution);

			}

		}
		catch (Throwable t) {

			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			execution.getExecutionContext().putString(BatchDefine.JOB_ERROR_MESSAGE, writer.toString());
			
			execution.setExitStatus(ExitStatus.FAILED);
			execution.setStatus(BatchStatus.FAILED);
			jobFailure = t;
			
			rethrow(t);
		}
		finally {

			BatchStatus jobStatus = execution.getStatus();
			if (jobStatus == BatchStatus.COMPLETED) {
				getCompositeListener().afterJob(execution);
			}
			else if (jobStatus == BatchStatus.FAILED) {
				getCompositeListener().onError(execution, jobFailure);
			}
			else if (jobStatus == BatchStatus.STOPPED) {
				getCompositeListener().onInterrupt(execution);
			}

			execution.setEndTime(new Date());
			getJobRepository().saveOrUpdate(execution);

		}
	}
	
	private static void rethrow(Throwable t) throws RuntimeException {
		if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		}
		else if (t instanceof Error) {
			throw (Error) t;
		}
		else {
			throw new UnexpectedJobExecutionException("Unexpected checked exception in job execution", t);
		}
	}

}
