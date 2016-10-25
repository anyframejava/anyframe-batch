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

import com.sds.anyframe.batch.agent.model.Step;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public interface IResourceLockManager {
	/**
	 * request to the master agent to check if the resources was locked and lock the resources in step
	 * @param step
	 * @return false : resources is already locked, true : you can use the resources in the step 
	 * @throws Exception 
	 */
	boolean lockResources(Step step) throws Exception;

	/**
	 * request to the master agent to release resources in the step
	 * @param step
	 * @throws Exception 
	 */
	void releaseResources(Step step) throws Exception;
}
