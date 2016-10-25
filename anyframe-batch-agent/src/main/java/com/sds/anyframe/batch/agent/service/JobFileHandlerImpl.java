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
package com.sds.anyframe.batch.agent.service;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobFileHandlerImpl implements JobFileHandler {
	private static final Logger logger = Logger
			.getLogger(JobFileHandlerImpl.class);

	public boolean delete(String filepath) throws Exception {
		if (!filepath.endsWith(".tmp.xml"))
			return false; // 안전빵~
		File file = new File(filepath);
		if (file.delete()) {
			logger.debug(filepath + " was successfully deleted.");
			return true;
		}
		return false;
	}
}
