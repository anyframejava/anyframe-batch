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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sds.anyframe.batch.common.util.ParamUtil;
import com.sds.anyframe.batch.define.BatchDefine;

/**
 * Store Command Line Arguments with this class 
 *
 * @author Hyoungsoon Kim
 */

public class CommandLineOptions {
	
	private static Log logger = LogFactory.getLog(CommandLineOptions.class);
	private static final CommandLineOptions INSTANCE = new CommandLineOptions();
	private static final String POSTFIX = "_CFG";

	private String jobConfigFilePath = null;
	private String jobName = null;
	private String userId = null;
	
	private String stepFrom = "N/A";
	private String stepTo = "N/A";
	
	private CommandLineOptions() {}
	
	public CommandLineOptions parseArgurment(String [] args) {

		if(args.length < 1)
			throw new IllegalArgumentException("Batch requires job xml file path in the first argument");
		
		if(BatchDefine.IS_AUTHENTICATION  && args.length < 2)
			throw new IllegalArgumentException("In case of authentication, Batch requires user id in the second argument");
		
		int index = 0;
		
		// the first argument must be a job configuration location.
		// it should be relative file path or classpath
		// example) args[0] = build/sli/co/tp/tpsmp/PTPSMP3000_CFG.xml
		//					  filepath:sli/co/tp/tpsmp/PTPSMP3000_CFG.xml
		//                    classpath:sli/co/tp/tpsmp/PTPSMP3000_CFG.xml
		String normalizedPath = FilenameUtils.normalize(args[index]);
		
		// baseName is filename without file extension
		String baseName = FilenameUtils.getBaseName(normalizedPath);	// PTPSMP3000_CFG
		
		// jobName comes from baseName without postfix; '_CFG'
		if(baseName.endsWith(POSTFIX)) {
			jobName = baseName.substring(0, baseName.length() - POSTFIX.length());	// PTPSMP3000
		} else {
			jobName = baseName;
		}
		
		ParamUtil.addParameter(BatchDefine.JOB_ID, jobName);
		System.setProperty(BatchDefine.JOB_ID, jobName);

		// Do not remove file extension because arg[0] could be 'job_cfg.tmp'
		// which comes from Batch Manager.
		// jobXmlFilePath = FilenameUtils.removeExtension(args[index]) + ".xml";
		
		// insure that job XML file extension should be ".xml"
		if(normalizedPath.endsWith(".xml"))
			jobConfigFilePath = normalizedPath;
		else
			jobConfigFilePath  = normalizedPath + ".xml";

		index++;
		
		if(BatchDefine.IS_AUTHENTICATION) {
			// Set up user id. the second argument must be a user ID
			userId = args[index];
			ParamUtil.addParameter(BatchDefine.USER_ID, userId);					
			index++;
		}
		
		String name = null;
		String value = null;
		Map<String, String> params = new HashMap<String, String>();
		
		if(index < args.length) {
			// the rest are parameters and properties.
			for (; index < args.length; index++) {
				String argument = args[index];
				int firstEqual = argument.indexOf("=");
				if (firstEqual == -1) {
					throw new IllegalArgumentException("error in arguments[" + argument +
							"] Additional parameters must be name/value pair. ex) [name=value]");
				}

				name = argument.substring(0, firstEqual);
				value = argument.substring(firstEqual + 1, argument.length());

				if (name.equals("step")) {
					stepFrom = value;
					
				} else if (name.equals("go_ahead")) {
					if(!Boolean.valueOf(value)) // 특정 step 하나만 실행함
						stepTo = stepFrom; 
					
				} else {
					ParamUtil.addParameter(name, value);
					System.setProperty(name, value);
				}
				
				params.put(name, value);
			}
		}
		
		// log input parameters
		logger.info("###### Batch Input Parameters");
		logger.info("## Command Line : " + StringUtils.join(args, ' '));	
		logger.info("## Job XML File : " + jobConfigFilePath);
		logger.info("## Job Name     : " + jobName);
		if(BatchDefine.IS_AUTHENTICATION) {
			logger.info("## User ID      : " + userId);
		}
		
		index = 0;
		for(Entry<String, String> entry : params.entrySet()) {			
			logger.info("## Param[" + index++ + "]     : " + entry.getKey() + " = " + entry.getValue());
		}
		logger.info("######");
		
		return this;
	}
	
	public static CommandLineOptions getInstance() {
		return INSTANCE;
	}
	
	public boolean isAuthentication() {
		return BatchDefine.IS_AUTHENTICATION;
	}
	
	public String getJobConfigLocation() {
		return jobConfigFilePath;
	}

	public String getJobName() {
		return jobName;
	}
	
	public String getStepFrom() {
		return stepFrom;
	}

	public String getStepTo() {
		return stepTo;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
