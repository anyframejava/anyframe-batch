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
package com.sds.anyframe.batch.agent.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.ProcessStreamReader;
import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class BatchUtils {
	private static final Logger killedJobLogger = Logger
			.getLogger("KilledJobLogger");

	private static final Logger log = Logger.getLogger(BatchUtils.class);

	public static void killProcess(Job job, String clientIp) {

		killedJobLogger.info("Job has been killed by " + clientIp + ": "
				+ AgentUtils.toJobString(job));

		List<String> list = new ArrayList<String>();

		list.add(PropertyProvider.shell);
		if (PropertyProvider.shellOption != null)
			list.add(PropertyProvider.shellOption);
		list.add(PropertyProvider.killShell);
		list.add(Long.toString(job.getPid()));

		log.info("Kill Command:" + StringUtils.join(list, " "));

		Process process = null;

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(list);
			processBuilder.directory(new File(PropertyProvider.runtimeBasePath
					+ PropertyProvider.shellPath));

			process = processBuilder.start();
			ProcessStreamReader error = new ProcessStreamReader(process.getErrorStream(),
					"Error");
			ProcessStreamReader output = new ProcessStreamReader(process.getInputStream(),
					"Output");
			error.start();
			output.start();

			TimeUnit.SECONDS.sleep(3);

			if (error.getErrorBuffer().length() > 0) {
				throw new RuntimeException("Job killing failed : "
						+ error.getErrorBuffer());
			}

			int exitValue = process.waitFor();
			log.info("Job has been killed by external shell, return code is "
					+ exitValue);
		} catch (Exception e) {
			log.error("Error occured to kill job using external shell", e);
		}
	}
}
