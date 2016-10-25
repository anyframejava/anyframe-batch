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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.ProcessStreamReader;
import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.command.UserCommander;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobLauncherImpl implements JobLauncher {
	private static final int _SUCCESS = 0;
	private static final Logger logger = Logger.getLogger(JobLauncherImpl.class);
	private String workingDirectory = null;
	private static UserCommander userCommander;
	
	public JobLauncherImpl() {
		userCommander = loadUserCommander();
	}
	
	@SuppressWarnings("unchecked")
	private UserCommander loadUserCommander() {
		if(PropertyProvider.userCommandClass != null && PropertyProvider.userCommandUrl != null) {
			try {
				
				URL systemResource = ClassLoader.getSystemResource(PropertyProvider.userCommandUrl.trim() );
				if(systemResource == null)
					throw new Exception("Can not load the jar file " + PropertyProvider.userCommandUrl );
				ClassLoader loader = URLClassLoader.newInstance(
					    new URL[] { systemResource},
					    getClass().getClassLoader()
					);
//				
				
//				URLClassLoader cl = URLClassLoader.newInstance(new URL[] {systemResource});
//				URLClassLoader cl = URLClassLoader.newInstance(new URL[] {new URL("jar", "", "file:"+PropertyProvider.userCommandUrl) });
//				Class loadedClass = cl.loadClass(PropertyProvider.userCommandClass);
//				UserCommander newInstance = (UserCommander) loadedClass.newInstance();
				
				Class<UserCommander> userCommander = (Class<UserCommander>) Class.forName(PropertyProvider.userCommandClass.trim(), true, loader);
				UserCommander newInstance = userCommander.newInstance();
				return newInstance;
			} catch (Throwable e) {
				logger.error("Error occured to load the user commander implementation class", e);
			}
		}
		return null;
	}

	public void setWorkingDirectory(String directory) {
		workingDirectory = directory;
	}

	public int launchOnSynchronization(String... args) throws Exception {
		return launchInternal(true, args);
	}
	
	public void launch(String... args) throws Exception {
		launchInternal(false, args);
	}
	
	private int launchInternal(boolean synchronization, String... args) throws Exception {
		
		try {
			List<String> list = getCommand(args);
	
			ProcessBuilder builder = new ProcessBuilder(list);
			workingDirectory = PropertyProvider.runtimeBasePath
					+ PropertyProvider.shellPath;
			builder.directory(new File(workingDirectory));
			logger.debug("Working Directory : " + workingDirectory);
			logger.debug("Executed as : " + StringUtils.join(list, " "));
			logger.info("Job Command:" + StringUtils.join(list, " "));
	
			int exitCode = _SUCCESS;
			
			Process process = null;
			process = builder.start();

			ProcessStreamReader error = new ProcessStreamReader(process.getErrorStream(),
					ProcessStreamReader.ERROR_STREAM);
			ProcessStreamReader output = new ProcessStreamReader(process.getInputStream(),
					ProcessStreamReader.OUTPUT_STREAM);
			error.start();
			output.start();

			TimeUnit.SECONDS.sleep(2);

			if (error.getErrorBuffer().length() > 0) {
				throw new RuntimeException("Job Execution Failed : "
						+ error.getErrorBuffer());
			}
			
			if(synchronization){
				exitCode = process.waitFor();

				error.join();
				output.join();
				return exitCode;
			}
			
			return exitCode;
			
		} catch (Throwable e) { // This exception is very starnage thing, therefore attach our message.
			logger.error(e);
			throw new RuntimeException(AgentUtils.getStackTraceString(e));
		}
		
	}

	private List<String> getCommand(String... args) {
		List<String> list = new ArrayList<String>();
		
		if(PropertyProvider.shell != null)
			list.add(PropertyProvider.shell);
		
		if (PropertyProvider.shellOption != null)
			list.add(PropertyProvider.shellOption);
		
		list.add(PropertyProvider.executeShell);

		if (userCommander != null) {
			List<String> cmds = userCommander.generateCommandLine(args);
			list.addAll(cmds);
		} else {
			for (String string : args) {
				list.add(string);
			}
		}
		return list;
	}
}
