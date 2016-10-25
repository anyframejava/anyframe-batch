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

package com.sds.anyframe.batch.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.sds.anyframe.batch.define.BatchDefine;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class LogManager {
	private static Log log = LogFactory.getLog(LogManager.class);
	
	private static final String ANYFRAME_JOB_LOGGER = "com.sds.anyframe.batch";
	private static final String ANYFRAME_JOB_APPENDER = "anyframeAppender";
	private static final String ANYFRAME_STEP_APPENDER = "stepAppender";
	
	private static boolean useFileLog = false;
	
	private static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy.MM.dd.HH.mm.ss.S");
	
	private static final Map<Long, Log> loggers = new ConcurrentHashMap<Long, Log>();
	private static final Map<Long, FileAppender> appenders = new ConcurrentHashMap<Long, FileAppender>();
	private static String baseLogPath;
	
	public static String initializeJobLogger(String jobConfigPath) {
		
		int prefixLength = jobConfigPath.indexOf(":");
		if(prefixLength != -1) {
			jobConfigPath = jobConfigPath.substring(prefixLength+1);
		}
		
		jobConfigPath = FilenameUtils.separatorsToSystem(jobConfigPath);
		
		if(jobConfigPath.startsWith(File.separator)) {
			jobConfigPath = jobConfigPath.substring(File.separator.length());
		}
		
		String baseLogFilePath = "";
		
		if( !StringUtils.isEmpty(BatchDefine.BASE_LOG_DIR_DATEFORMAT) ){
			baseLogFilePath = FilenameUtils.concat(BatchDefine.BASE_LOG_DIR, new SimpleDateFormat(BatchDefine.BASE_LOG_DIR_DATEFORMAT).format(new Date()));
			baseLogFilePath = FilenameUtils.concat(baseLogFilePath            , FilenameUtils.getFullPath(jobConfigPath));
		}else{
			baseLogFilePath = FilenameUtils.concat(BatchDefine.BASE_LOG_DIR, FilenameUtils.getFullPath(jobConfigPath));
		}
		
		String baseLogFileName = FilenameUtils.getBaseName(jobConfigPath) + "_" + format.format(new Date());
		
		baseLogPath = FilenameUtils.concat(baseLogFilePath, baseLogFileName);
		
		String jobLogFileName = baseLogPath + ".log";

		// init job logger
		Logger logger = Logger.getLogger(ANYFRAME_JOB_LOGGER);
		
		RollingFileAppender appender = (RollingFileAppender) logger
				.getAppender(ANYFRAME_JOB_APPENDER);
		
		if(appender == null) {
			log.warn("Can not find the Anyframe job appender[" + ANYFRAME_JOB_APPENDER + "]");
		} else {
			appender.setFile(jobLogFileName);
			appender.activateOptions();
		}
		
		
		// init default step logger
		Logger rootLogger = Logger.getRootLogger();

		RollingFileAppender stepAppender = (RollingFileAppender) rootLogger
				.getAppender(ANYFRAME_STEP_APPENDER);

		if (stepAppender == null) {
			useFileLog = false;
			log.warn("Can not find the Anyframe job appender[" + ANYFRAME_STEP_APPENDER + "]");
			
		} else {
			useFileLog = true;
			stepAppender.setFile(jobLogFileName);
			stepAppender.activateOptions();
			long mainThreadId = Thread.currentThread().getId();
			appenders.put(mainThreadId, stepAppender);
		}

		
		return jobLogFileName;
	}

	public static String initializeStepLogger(String stepId) {
		
		String stepLogFileName = baseLogPath + "_" + stepId + ".log";
		
		if( useFileLog) {
			long threadId = Thread.currentThread().getId();
			FileAppender stepAppender = appenders.get(threadId);
			
			if(stepAppender == null) {   // in parallel step (multi thread)
				createStepLogger(threadId, stepId, stepLogFileName);
				
			} else {	// in single step
				stepAppender.setFile(stepLogFileName);
				stepAppender.activateOptions();
			}
	
			log.info("Step appender's file has been changed[" + stepLogFileName + "]");
			return stepLogFileName;
			
		} else {
			return "console";
		}
		
	}
	
	private static Log createStepLogger(long threadId, String stepId, String logFileName) {
		Logger newLogger = Logger.getLogger(stepId);
		Log logWrapper = new Log4JLogger(newLogger);

		Logger rootLogger = Logger.getRootLogger();

		newLogger.setLevel(rootLogger.getLevel());

		Appender stepAppender = rootLogger.getAppender(ANYFRAME_STEP_APPENDER);
		PatternLayout layout = null;
		
		if (stepAppender != null) {
			layout = new PatternLayout(((PatternLayout)stepAppender.getLayout()).getConversionPattern());
		}
		else
			layout = new PatternLayout("%-5p [%d{MM/dd HH:mm:ss,SSS}] %3x:%m%n;");

		try {
			FileAppender fileAppender = new RollingFileAppender(
					layout, logFileName, false);
			fileAppender.setName(stepId + "_Appender");
			newLogger.addAppender(fileAppender);
			newLogger.setAdditivity(false);
			
			loggers.put(threadId, logWrapper);
			appenders.put(threadId, fileAppender);

		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
		return logWrapper;
	}
	
	//TODO @deprecated use {@link #getLogger()} instead
	public static Log getLogger(int seq) {
		return getLogger();
	}
	
	public static Log getLogger() {
		long threadId = Thread.currentThread().getId();
		
		Log logger = loggers.get(threadId);
		
		if (logger == null)
			return new Log4JLogger(Logger.getRootLogger());
		
		return logger;
	}
	
	public static FileAppender getCurrentThreadAppender() {
		long threadId = Thread.currentThread().getId();
		return appenders.get(threadId);
	}

	public static boolean isUseFileLog() {
		return useFileLog;
	}

	public static void setUseFileLog(boolean useFileLog) {
		LogManager.useFileLog = useFileLog;
	}
}
