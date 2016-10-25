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

package com.sds.anyframe.batch.aop.intercept;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.properties.ClientConfigurations;
import com.sds.anyframe.batch.agent.service.HessianObjectAccessor;
import com.sds.anyframe.batch.agent.state.job.IJobStateClient;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.util.FailOverUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JobInterceptor extends RuntimeInterceptor implements
		IStepInterceptObserver {

	private static Logger log = Logger.getLogger(JobInterceptor.class);

	private static List<StepInterceptor> stepInterceptors = new ArrayList<StepInterceptor>();

	private IJobStateClient jobStateClient;

	private ClientConfigurations configurations = ClientConfigurations
			.getConfigurations();

	private Job job;

	private Timer performanceWatcher;

	private boolean firstStepHasBeenStarted;

	PerformanceManager performanceManager;
	
	public JobInterceptor(boolean bHook) {
		if (configurations.isBatchAgentOn()) {
			try {
				connectToHessianService();
			} catch (Exception e) {
				if (FailOverUtils.isFailOver(e)) {
					log.error("There is no live agent server neither Primary nor Secondary agent.");
					log.error(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		}
		
		if(bHook) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					log.info("Job's shutdown listener starting...");
					shutdownUpdater();
				}
			});
		}
	}

	public JobInterceptor() {
		this(true);
	}

	public Job startJob(Job constJob) throws Exception {
		if (!configurations.isBatchAgentOn())
			return constJob;
		
		this.job = constJob;

		try {
			log.info("Job started : " + AgentUtils.toJobString(job));
			Job tJob = jobStateClient.startJob(job);
			
			job.setJobSeq(tJob.getJobSeq());
			job.setJobStatus(tJob.getJobStatus());
			
			jobStateClient.waitJobToExecute(job);
			runUpdater();
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					Job tJob = jobStateClient.startJob(job);
					job.setJobSeq(tJob.getJobSeq());
					job.setJobStatus(tJob.getJobStatus());

					jobStateClient.waitJobToExecute(job);
					runUpdater();
				} else
					throw new Exception("No agent available");
			} else {
				log.error("Job starting failed: " + e.getMessage());
				throw e;
			}
		}
		
		return job;
	}

	private void runUpdater() {
		if (!configurations.isBatchAgentOn())
			return;

		performanceManager = new PerformanceManager();
		
		performanceWatcher = new Timer(true);

		log.info("Performance Watcher executed");

		performanceWatcher.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updatePerformance();
			}

		}, 1000, configurations.getUpdateInterval() * 1000);
	}
	
	private void updatePerformance() {
		if(performanceManager == null)
			return;
		
		try {
			Performance performance = performanceManager.getPerformance();
			job.setPerformance(performance);

			jobStateClient.updatePerformance(job);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				try {
					if (reConnection())
						jobStateClient.updatePerformance(job);
				} catch (Exception e2) {
					log.error(e.getMessage(), e);
				}
			} else {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void completeJob() throws Exception {
		if (!configurations.isBatchAgentOn())
			return;

		shutdownUpdater();

		try {
			log.info("Job end requesting: " + AgentUtils.toJobString(job));

			jobStateClient.completedJob(job);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					jobStateClient.completedJob(job);
				} else
					throw new Exception("No agent available");
			} else {
				log.error("Job finishing failed: " + e.getMessage());
				throw e;
			}
		}

		log.info("Job end requested: " + AgentUtils.toJobString(job));
	}

	public synchronized void failJob(String errorMessage) throws Exception {
		if (!configurations.isBatchAgentOn())
			return;

		synchronized(StepInterceptor.deadlockMonitor) {
			/*
			 * Below method invoking is very important for deadlock in RDBMS.
			 * When the step throws an exception there is no chance to cancel the timer for step performance updater's timer task.
			 * Therefore JobInteceptor must call to cancel the step interceptor's performance updater.
			 */
			shutdownStepInterceptorPerformanceUpdater();
			
			shutdownUpdater();
			
			if(job.getJobSeq() <= 0)
				return;
				
			job.setExitMessage(errorMessage);
			
			try {
				log.info("Job fail requesting : " + AgentUtils.toJobString(job));
				jobStateClient.failJob(job);
			} catch (Exception e) {
				if (FailOverUtils.isFailOver(e)) {
					if (reConnection()) {
						jobStateClient.failJob(job);
					} else
						throw new Exception("No agent available");
				} else {
					log.error("Job failing failed: " + e.getMessage());
					throw e;
				}
			}
		}

		log.info("Job fail reuqesting completed : "
				+ AgentUtils.toJobString(job));
	}

	private synchronized void shutdownUpdater() {
		if (!configurations.isBatchAgentOn())
			return;
		
		if (performanceWatcher != null) {
			log.info("Job's performance is shutting down...");
			performanceWatcher.cancel();
		}
		
		performanceManager = null;
	}

	private void shutdownStepInterceptorPerformanceUpdater() {
		for(StepInterceptor stepInterceptor: stepInterceptors) {
			stepInterceptor.shutdownUpdater();
		}
	}

	@Override
	protected void connectToHessianService() throws IOException,
			ClassNotFoundException, Exception {
		String servers = configurations.getPrimaryBatchAgent();
		
		if(configurations.getSecondaryPort() != null)
			servers =  servers + "; " +configurations.getPrimaryBatchAgent().split(":")[0]+":" + configurations.getSecondaryPort();
		
		jobStateClient = (IJobStateClient) HessianObjectAccessor
				.getRemoteObject(servers, IJobStateClient.SERVICE_NAME, IJobStateClient.class.getName());
	}

	public void stepHasBeenStarted(Step step) throws Exception {
		if(firstStepHasBeenStarted)
			return;
		
		log.info("The fist step has been started: " + AgentUtils.toStepString(step));
		
		firstStepHasBeenStarted = true;
		
		try {
			jobStateClient.jobExecuted(job); // to RUNNING state
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					jobStateClient.jobExecuted(job); // to RUNNING state
				} else
					throw new Exception("No agent available");
			} else {
				log.error("Job running is failed: " + e.getMessage());
				throw e;
			}
		}
	}

	public void stopJob() throws Exception {
		if (!configurations.isBatchAgentOn())
			return;

		shutdownUpdater();
		
		if(job.getJobSeq() <= 0)
			return;
			
		try {
			log.info("Job stop requesting : " + AgentUtils.toJobString(job));
			jobStateClient.stopJob(job);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					jobStateClient.stopJob(job);
				} else
					throw new Exception("No agent available");
			} else {
				log.error("Job stop failed: " + e.getMessage());
				throw e;
			}
		}

		log.info("Job stop reuqesting completed : "
				+ AgentUtils.toJobString(job));
	}

	public static void addStepIntecetor(StepInterceptor stepInterceptor) {
		stepInterceptors .add(stepInterceptor);
	}
}
