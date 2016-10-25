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
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.properties.ClientConfigurations;
import com.sds.anyframe.batch.agent.service.HessianObjectAccessor;
import com.sds.anyframe.batch.agent.state.step.IStepStateClient;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.agent.util.TimeUnits;
import com.sds.anyframe.batch.util.FailOverUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepInterceptor extends RuntimeInterceptor implements IStepIntercetObservable {
	private static Logger log = Logger.getLogger(StepInterceptor.class);

	private static IStepStateClient stepStateClient;

	private Job job;

	/*
	 * steps를 ThreadLocal로 할경우, Timer에서 currentStep이 Null로 돌아오게 됨. 공유하도록 변경
	 */
	private Step currentStep;
	
//	private ThreadLocal<Step> steps = new ThreadLocal<Step>();

	private IStepInterceptObserver stepInterceptObserver;
	
	private Timer performanceWatcher;
	
	private PerformanceManager performanceManager;
	
	ClientConfigurations configurations = ClientConfigurations
			.getConfigurations();

	private boolean processShutingDown;
	
	private boolean waitingToLock;  // 스탭이 리소스 라킹을 수행중임을 선언하는 플래그.

	private final static Object timer = new Object();
	
	public final static Object deadlockMonitor = new Object();
	
	public StepInterceptor(Job job) throws Exception {
		this.job = job;
		JobInterceptor.addStepIntecetor(this);
		
		if (configurations.isBatchAgentOn()) {
			try {
				connectToHessianService();
			} catch (Exception e) {
				if (FailOverUtils.isFailOver(e)) {
					log.error("There is no live agent server neither Primary nor Secondary agent.");
					log.error(e.getMessage(), e);
				}
				throw e;
			}
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Step's shutdown listener starting...");
				shutdownUpdater();
			}
		});
	}

	public Step startStep(String stepId, List<Resource> resources, String logFiles) throws Exception {
		
		if(resources == null)
			resources = Collections.emptyList();
		
		if (!configurations.isBatchAgentOn())
			return new Step();

		log.info("Step is starting with step id " + stepId + " and resorces - " + resources.toString());

		// Start step
		Step step =null;
		try {
			step = new Step();
			
			step.setStepId(stepId);
			step.setJobSeq(job.getJobSeq());
			step.setJobId(job.getJobId());
			step.setPid(job.getPid());
			step.setResources(resources);

			step.setLogFiles(logFiles);
			
			step = stepStateClient.startStep(step);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					step = stepStateClient.startStep(step);
				} else
					throw new Exception("No agent available");
			} else {
				throw e;
			}
		} finally {
			step.setResources(resources);
			this.currentStep=step;
		}

		
		// Lock resources
		try {
			waitingToLock = true;
			lockResources();
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					lockResources();
				} else
					throw new Exception("No agent available");
			} else {
				throw e;
			}
		}  finally {
			waitingToLock = false;
			if(processShutingDown)
				return step;
		}
		
		//Step execute
		stepInterceptObserver.stepHasBeenStarted(step);
		
		try {
			log.info("Job Step Starting has been requested: "
					+ AgentUtils.toStepString(step));

			stepStateClient.stepExecuted(step);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					stepStateClient.stepExecuted(step);
				} else
					throw new Exception("No agent available");
			} else {
				log.error("Step running is  failed: " + e.getMessage());
				throw e;
			}
		}
			
		runUpdater();
		
		return step;
	}

	private void lockResources() throws Exception, InterruptedException,
			TimeoutException {
		
		Step step = this.currentStep;
		
		List<Resource> resources = step.getResources();
		
		if(resources == null || resources.size() == 0)
			return;
		
		
		long check_start = System.currentTimeMillis();

		int count = 0;
		
		if(processShutingDown)
			return;
	
		synchronized (timer) {
			while (!stepStateClient.lockResources(step)) {

				timer.wait(TimeUnits.SECOND
						* configurations.getResourceCheckInterval());

				if (processShutingDown)
					break;

				if ((count++ % 10) == 0)
					log.info("This step is trying to lock these resources["
							+ step.getResources() + "]");

				if (System.currentTimeMillis() - check_start > (configurations
						.getResourceQueuingTime())) {
					String msg = "Resource lock waiting timed out exception occured["
							+ (configurations.getResourceQueuingTime() / TimeUnits.HOURS)
							+ " hours]";
					throw new Exception(msg);
				}
			}
		}
	}

	public void completeStep(Step step) throws Exception {
		if (!configurations.isBatchAgentOn())
			return;
		
		shutdownUpdater();

		try {

			log.info("Job Step end requested to the Agent: "
					+ AgentUtils.toStepString(step));

			stepStateClient.completeStep(step);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					stepStateClient.completeStep(step);
				} else
					throw new Exception("No agent available");
			} else {
				throw e;
			}

			log.info("Job Step end has been requested: "
					+ AgentUtils.toStepString(step));
		}
		
	}
	
	public void failStep(Step step) throws Exception {
		if (!configurations.isBatchAgentOn())
			return;
		
		shutdownUpdater();

		try {

			log.info("Job Step fail requested to the Agent: "
					+ AgentUtils.toStepString(step));

			stepStateClient.failStep(step);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				if (reConnection()) {
					stepStateClient.failStep(step);
				} else
					throw new Exception("No agent available");
			} else {
				throw e;
			}

			log.info("Job Step fail has been requested: "
					+ AgentUtils.toStepString(step));
		}
		
	}

	@Override
	protected void connectToHessianService() throws IOException,
			ClassNotFoundException, Exception {
		
		String servers = configurations.getPrimaryBatchAgent();
		
		if(configurations.getSecondaryPort() != null)
			servers =  servers + "; " +configurations.getPrimaryBatchAgent().split(":")[0]+":" + configurations.getSecondaryPort();
		stepStateClient = (IStepStateClient) HessianObjectAccessor
				.getRemoteObject(servers, IStepStateClient.SERVICE_NAME, IStepStateClient.class.getName());
	}

	public void register(IStepInterceptObserver observer) {
		stepInterceptObserver = observer;
	}
	
	private void runUpdater() {
		if (!configurations.isBatchAgentOn())
			return;

		performanceManager = new PerformanceManager();

		performanceWatcher = new Timer(true);

		log.info("Step Performance Watcher executed");

		performanceWatcher.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				synchronized(deadlockMonitor) {
					updatePerformance(false);
				}
			}

		}, 1000, configurations.getUpdateInterval() * 1000);
	}
	
	private void updatePerformance(boolean bLast) {
		if(performanceManager == null)
			return;
		
		Performance performance = null;

		try {
			performance = performanceManager.getPerformance();
			if(bLast)
				performance = null;
			
			stepStateClient.updatePerformanceAndTransactedCount(performance,  this.currentStep);
		} catch (Exception e) {
			if (FailOverUtils.isFailOver(e)) {
				try {
					if (reConnection())
						stepStateClient.updatePerformanceAndTransactedCount(performance, this.currentStep);
				} catch (Exception e2) {
					log.error(e.getMessage(), e);
				}
			} else {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void shutdownUpdater() {
		if (!configurations.isBatchAgentOn())
			return;
		
		if (performanceWatcher != null) {
			performanceWatcher.cancel();
			performanceWatcher = null;
			
			log.info("Step's performance Watcher is shutting down...");
			
			//Update the final performance information
			updatePerformance(true);
		}
		
		performanceManager = null;
	}

	public boolean isWaitingToLock() {
		return waitingToLock;
	}

	public void setProcessShutingDown(boolean processShutingDown) {
		this.processShutingDown = processShutingDown;
		log.info("Resource waiting lock monitor will be awaken to stop.");
		synchronized(timer) {
			timer.notifyAll();
		}
	}
	
//	private Step getCurrentStep() {
//		Step currentStep = this.steps.get();
//		
//		return currentStep;
//	}
}
