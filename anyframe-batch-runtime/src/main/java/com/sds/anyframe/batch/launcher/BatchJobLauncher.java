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

package com.sds.anyframe.batch.launcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.aop.intercept.JobInterceptor;
import com.sds.anyframe.batch.common.util.ParamUtil;
import com.sds.anyframe.batch.core.job.SequentialJob;
import com.sds.anyframe.batch.core.step.support.StepResourceHolder;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.log.LogManager;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class BatchJobLauncher implements JobLauncher{
	
	enum ENDSTATUS {
		SUCCESS, FAILED, ABNORMALLYFINISHED
	}
	private static final int _SUCCESS = 0;
	private static final int _FAIL = 1;
	private static final Log LOGGER = LogFactory.getLog(BatchJobLauncher.class);
	
	volatile static ENDSTATUS exitStatus = ENDSTATUS.ABNORMALLYFINISHED;
	
	private JobRepository jobRepository;
	
	private String stepFrom;
	private String stepTo;
	
	private ResourceLoader resourceLoader = new DefaultResourceLoader() {
		protected Resource getResourceByPath(String path) {
			
			if(BatchDefine.JOB_DEFAULT_LOCATION.compareToIgnoreCase("classpath") == 0)
				return super.getResourceByPath(path);
			else
				return new FileSystemResource(path);
		}
	};
	
	public static void main(String[] args) {
		
		
		BatchJobLauncher launcher = new BatchJobLauncher();
	
		int ret = launcher.execute(args);

		System.exit(ret);
	}
	
	/**
	 * 배치 실행 method 
	 * 
	 * @param args 사용자 입력 arguments
	 * @return 실행 성공  0, 실패  1
	 */
	public int execute(String[] args) {
		
		ResourceXmlApplicationContext context = null;
		Resource jobConfigResource;
		String jobConfigLocation;
		String jobName = FilenameUtils.getBaseName(args[0]);
		String jobLogFile = null;
		String jobSeq = null;
		
		try {
			//TODO 배치 내부로거 디자인 개선이 피요함.
			jobLogFile = LogManager.initializeJobLogger(args[0]);

			// register system environment values to ParamUtil
			Map<String, String> envMap = System.getenv();
			
			for(Entry<String, String> entry : envMap.entrySet()) {
				ParamUtil.addParameter(entry.getKey(), entry.getValue());
			}
			
			CommandLineOptions commandLine = CommandLineOptions.getInstance().parseArgurment(args);
			jobConfigLocation = commandLine.getJobConfigLocation();
			jobConfigResource = resourceLoader.getResource(jobConfigLocation);
			jobSeq = ParamUtil.getParameter("JOBSEQ");
			
			LogConfiguration(jobConfigResource);
			
			PreProcessor preprocessor = getPreprocessor();
			if(preprocessor != null)
				jobConfigResource = preprocessor.doPreProcess(jobConfigResource);
			
			stepFrom = commandLine.getStepFrom();
			stepTo   = commandLine.getStepTo();
			
			createJvmMainHooker();
			
			context = new ResourceXmlApplicationContext();
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(context);
			
			context.registerShutdownHook();
			
			String anyframeConfigLocation = BatchDefine.CONFIG_LOCATION;
			Resource[] anyframeConfigResources = resolver.getResources(anyframeConfigLocation);
			
			context.setConfigResources(Arrays.asList(anyframeConfigResources));
			context.addConfigResource(jobConfigResource);

			context.refresh();
			context.getAutowireCapableBeanFactory().autowireBeanProperties(this,
					AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
			
		} catch (Throwable t) {
			JobInterceptor interceptor = new JobInterceptor(false);
			com.sds.anyframe.batch.agent.model.Job job = 
				new com.sds.anyframe.batch.agent.model.Job(AgentUtils.getPid(), jobName);
			job.setLogFiles(jobLogFile);
			job.setAllowConcurrentRunning(true);
			if(jobSeq != null && (jobSeq.length() > 0) ){
				job.setJobSeq( Long.parseLong(jobSeq)) ;
			}
			
			try {
				interceptor.startJob(job);
				interceptor.failJob(AgentUtils.getStackTraceString(t));
				
			} catch (Exception e) {
				LOGGER.error("agent error", e);
			}
			
			LOGGER.error(t.getMessage(), t);
			
			exitStatus = ENDSTATUS.FAILED;
			return _FAIL;
		}
		
		try {

			SequentialJob jobBean = (SequentialJob)context.getBean("job");

			Map<String, String> stringMap = new HashMap<String, String>();
			stringMap.put("jobName", jobName);
			stringMap.put("logFile", jobLogFile);
			if(jobSeq != null && (jobSeq.length() > 0) ){
				stringMap.put("jobSeq", jobSeq);
			}
			stringMap.put("concurrent", String.valueOf(jobBean.isConcurrent()));
			
			JobParameters jobParameter = new JobParameters(stringMap, new HashMap<String, Long>(), new HashMap<String, Double>(), new HashMap<String, Date>());
			
			run(jobBean, jobParameter);
			
		} catch(Throwable t) {
			
			LOGGER.error(t.getMessage(), t);
			exitStatus = ENDSTATUS.FAILED;
			return _FAIL;
			
		} 
		
		exitStatus = ENDSTATUS.SUCCESS;
		
		return _SUCCESS;
	}

	public JobExecution run(final Job job, final JobParameters jobParameters)
	        throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

		Assert.notNull(job, "The Job must not be null.");
		Assert.notNull(jobParameters, "The JobParameters must not be null.");

		final JobExecution jobExecution = jobRepository.createJobExecution(job, jobParameters);

		jobExecution.getExecutionContext().putString("stepFrom", this.stepFrom);
		jobExecution.getExecutionContext().putString("stepTo", this.stepTo);
		
		try {
			job.execute(jobExecution);
			
		} catch (JobExecutionException e) {
			throw new RuntimeException(e);
		}
		
		return jobExecution;
	}

	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.state(jobRepository != null, "A JobRepository has not been set.");
	}
	

	private void LogConfiguration(Resource resource) throws Exception {
		
		String charset = BatchDefine.CONFIG_CHARSET;
		BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), charset));
		
		LOGGER.info("");
		LOGGER.info("##### Job Configuration");
		String line = null;
		while((line = reader.readLine()) != null) {
			LOGGER.info("## " + line);
		}
		
		LOGGER.info("#####");
	}
	
	private PreProcessor getPreprocessor() throws Exception {
		
		String preProcClass   = BatchDefine.CONFIG_PREPROCESSOR_CLASS;
		String styleSheetPath = BatchDefine.CONFIG_PREPROCESSOR_STYESHEET;
		
		if(StringUtils.isEmpty(preProcClass))
			return null;
		
		Class<?> preProcessClass = Class.forName(preProcClass);
		PreProcessor preProcessor = (PreProcessor) preProcessClass.newInstance();
		
		if(!StringUtils.isEmpty(styleSheetPath)) {
			Resource resource = resourceLoader.getResource(styleSheetPath);
			preProcessor.setStyleSheet(resource);
		}
		
		return preProcessor;
	}
	
	private static void createJvmMainHooker() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				
				if (exitStatus == ENDSTATUS.ABNORMALLYFINISHED) {
					LOGGER.info("batch process is shut down abnormally. probably process is killed by force.");

					if(BatchDefine.DELETE_FILE_ON_STOP)
						StepResourceHolder.deleteFiles();
					
				} else {
					LOGGER.info("batch process is shut down normally.");
				}

			}
		});
	}
}
