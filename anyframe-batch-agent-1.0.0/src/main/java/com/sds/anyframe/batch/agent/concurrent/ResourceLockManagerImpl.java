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
package com.sds.anyframe.batch.agent.concurrent;

import java.util.List;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.cluster.BatchAgent;
import com.sds.anyframe.batch.agent.dao.IResourceDao;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class ResourceLockManagerImpl implements IResourceLockManager {
	private final Logger log = Logger.getLogger(ResourceLockManagerImpl.class);

	private final Logger resourceAgentLogger = Logger.getLogger("ResourceAgentLogger");

	private IResourceDao resourceDao;

	private final BatchAgent agent;
	
	public void setResourceDao(IResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	public ResourceLockManagerImpl() throws Exception {
		agent = BatchAgent.createServer();
		agent.setResourceLockManager(this);
	}

	
	public void releaseResources(Step step) throws Exception {
		if (step == null || step.getResources() == null || step.getResources().size() == 0)
			return;
		
		resourceDao.updateResourcesAsReleased(step);
	}

	public synchronized boolean lockResources(Step step) throws Exception, InterruptedException {
		boolean available = checkResources(step);
		
		if (available) {
			log.info("The step locked resources:  " + AgentUtils.toStepString(step));
			resourceDao.updateResourcesAsLocked(step);
		}
		return available;
	}

	/**
	 * check if it is locked
	 * 
	 * @param step
	 * @return true : it is not yet locked, false : it is already locked
	 */
	private boolean checkResources(Step step) {
		if (step.getResources() == null || step.getResources().size() == 0)
			return true;
		
		
		// check if the resources was locked
		for (Resource resource : step.getResources()) {
			ResourceIoType ioType = resource.getIoType();
			String resourceName = resource.getResourceName();

			if(resource.getType() != null && resource.getType() == ResourceType.DATABASE)
				continue;
			
			//throwExceptionIfPreviousJobHasBeenFailed(ioType, resourceName);
			
			List<Resource> resourcesNotReleased = resourceDao
					.getPreLockedResources(resourceName, step.getJobSeq());
			
			if (ioType == ResourceIoType.READ) {
				
				for (Resource resourceInDb : resourcesNotReleased) {
					
					if (resourceInDb.getIoType() == ResourceIoType.WRITE || 
						resourceInDb.getIoType() == ResourceIoType.REWRITE ||
						resourceInDb.getIoType() == ResourceIoType.DELETE) {

						logLockingInformation(step, resourceName, resourceInDb);
						return false;
					}
				}
			} else if (ioType == ResourceIoType.WRITE
					|| ioType == ResourceIoType.REWRITE
					|| ioType == ResourceIoType.DELETE) {
				if (resourcesNotReleased.size() > 0) {
					logLockingInformation(step, resourceName, resourcesNotReleased.get(0));
					return false;
				}
			}
		}
		return true;
	}

	private void logLockingInformation(Step step, String resourceName,
			Resource resourceInDb) {
		String logMsg = "Resource File ["
				+ resourceName
				+ "] is occupied by [Job : "
				+ resourceInDb.getJobId()
				+ ", Step : "
				+ resourceInDb.getStepId()
				+ ", Job Seq: "
				+ resourceInDb.getJobSeq()
				+ "], attempts to get this resource's lock failed [Job : "
				+ step.getJobId() + ", Step :"
				+ step.getStepId() + ", Job Seq : "
				+ step.getJobSeq() + "]";
		resourceAgentLogger.info(logMsg);
	}
}
