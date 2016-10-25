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

import com.sds.anyframe.batch.agent.cluster.BatchAgent;
import com.sds.anyframe.batch.agent.cluster.MessageManager;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.state.step.IStepObserver;
import com.sds.anyframe.batch.agent.state.step.IStepState.STATES;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class ResourceLockClient  implements IStepObserver {

	private static ResourceLockClient client;
	private final MessageManager messageManager;
	
	private ResourceLockClient() throws Exception {
		messageManager = BatchAgent.createServer().getMessageManager();
	}

	public static ResourceLockClient getInstance() {
		if(client == null) {
			try {
				client = new ResourceLockClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	private boolean lockResources(Step step) throws Exception {
		return messageManager.lockResources(step);
	}
	
	private boolean releaseResources(Step step) throws Exception {
		if (step == null || step.getResources() == null || step.getResources().size() == 0)
			return false;
		return messageManager.releaseResources(step);
	}

	
	public Object stepChanged(Step step, STATES state) throws Exception {
		if (state == STATES.STOPPED || state == STATES.FAILED || state == STATES.COMPLETED)
			return releaseResources(step);
		else if (state == STATES.READY)
			return lockResources(step);
		else
			throw new IllegalStateException(
					"Step Observer received illegal state");
	}

}
