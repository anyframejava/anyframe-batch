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

package com.sds.anyframe.batch.core.step;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.ExitStatus;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.log.LogManager;


/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AnyframeAbstractStep implements Step, InitializingBean, BeanNameAware {

	private static final Log LOGGER = LogFactory.getLog(AnyframeAbstractStep.class);
	
	private String name;

	private CompositeStepExecutionListener listener = new CompositeStepExecutionListener();

	private PlatformTransactionManager transactionManager;
	
	private JobRepository jobRepository;
	
	protected Map<String, String> parameters = new HashMap<String, String>();
	
	private List<BatchResource> resources = new ArrayList<BatchResource>();

	
	protected abstract ExitStatus doExecute(StepExecution stepExecution) throws Exception;

	protected void open(ExecutionContext ctx) throws Exception {
	}

	protected void close(ExecutionContext ctx) throws Exception {
	}

	public final void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
		
		stepExecution.setStartTime(new Date());
		stepExecution.setStatus(BatchStatus.STARTED);
		
		ExitStatus exitStatus = ExitStatus.FAILED;
		
		try {
			beforeExeute(stepExecution);

			getCompositeListener().beforeStep(stepExecution);
			
			getJobRepository().saveOrUpdate(stepExecution);
			
			open(stepExecution.getExecutionContext());
			
			exitStatus = doExecute(stepExecution);

			stepExecution.setStatus(BatchStatus.COMPLETED);
			
			exitStatus = exitStatus.and(getCompositeListener().afterStep(stepExecution));
			
		} catch (Throwable e) {
			
			stepExecution.setStatus(BatchStatus.FAILED);
			try {
				exitStatus = ExitStatus.FAILED.and(getCompositeListener().onErrorInStep(stepExecution, e));
			} catch (Exception ex) {
				LOGGER.error("Encountered an error on listener error callback.", ex);
			}
			
			rethrow(e);
			
		} finally {

			stepExecution.setExitStatus(exitStatus);
			stepExecution.setEndTime(new Date());

			Exception commitException = null;
			try {
				getJobRepository().saveOrUpdate(stepExecution);
			} catch (Exception e) {
				commitException = e;
			}

			try {
				close(stepExecution.getExecutionContext());
			} catch (Exception e) {
				LOGGER.error("Exception while closing step resources", e);
				throw new UnexpectedJobExecutionException("Exception while closing step resources", e);
			}
			
			if (commitException != null) {
				stepExecution.setStatus(BatchStatus.UNKNOWN);
				LOGGER.error("Encountered an error saving batch meta data."
						+ "This job is now in an unknown state and should not be restarted.", commitException);
				throw new UnexpectedJobExecutionException("Encountered an error saving batch meta data.",
						commitException);
			}

		}
	}

	public void beforeExeute(StepExecution stepExecution) {
		
		stepExecution.getExecutionContext().putString(BatchDefine.STEP_LOG_FILE_PATH, createStepLogFile());
		stepExecution.getExecutionContext().put(BatchDefine.STEP_RESOURCE_LIST, getResources());
		
	}
	
	public String createStepLogFile() {
		
		String stepLogFileName = LogManager.initializeStepLogger(this.name);
		
		return stepLogFileName;
	}
	
	private static void rethrow(Throwable e) throws JobInterruptedException {
		if (e instanceof Error) {
			throw (Error) e;
		}
		if (e instanceof JobInterruptedException) {
			throw (JobInterruptedException) e;
		}
		else if (e.getCause() instanceof JobInterruptedException) {
			throw (JobInterruptedException) e.getCause();
		}
		else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new UnexpectedJobExecutionException("Unexpected checked exception in step execution", e);
	}

	/**
	 * Register a step listener for callbacks at the appropriate stages in a
	 * step execution.
	 * 
	 * @param listener a {@link StepExecutionListener}
	 */
	public void registerStepExecutionListener(StepExecutionListener listener) {
		this.listener.register(listener);
	}

	/**
	 * Register each of the objects as listeners.
	 * 
	 * @param listeners an array of listener objects of known types.
	 */
	public void setStepExecutionListeners(StepExecutionListener[] listeners) {
		for (int i = 0; i < listeners.length; i++) {
			registerStepExecutionListener(listeners[i]);
		}
	}

	/**
	 * @return composite listener that delegates to all registered listeners.
	 */
	protected StepExecutionListener getCompositeListener() {
		return listener;
	}

	/**
	 * Public setter for {@link JobRepository}.
	 * 
	 * @param jobRepository is a mandatory dependence (no default).
	 */
	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	protected JobRepository getJobRepository() {
		return jobRepository;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jobRepository, "JobRepository is mandatory");
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBeanName(String name) {
		if (this.name == null) {
			this.name = name;
		}
	}

	public int getStartLimit() {
		return Integer.MAX_VALUE;
	}

	public boolean isAllowStartIfComplete() {
		return false;
	}
	
	public List<BatchResource> getResources() {
		return this.resources;
	}

	public void setResources(List<BatchResource> resources) {
		this.resources = resources;
	}
	public void addResource(BatchResource resource) {
		this.resources.add(resource);
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
}
