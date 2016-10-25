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

package com.sds.anyframe.batch.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.log.LogManager;

/**
 * 배치 실행결과를 로그파일에 기록하기 위해 사용되는 Helper 클래스.
 * {@code this} 클래스를 통해 기록되는 배치 실행결과는 Log4j.xml에 설정된 
 * root 로거의 레벨에 무관하게 로그 파일에 기록됨.
 * 
 * @author hsoon.kim
 *
 */
public class MessageFormatter {
	
	private Log logger = LogFactory.getLog("MessageLogger");
	
	private boolean bInitialized = false;
	
	private StringBuilder sbBuffer = new StringBuilder();
	
	/**
	 * @deprecated 
	 */
	@Deprecated
	public void setLogger(Log srcLogger) {

		initLogger();
	}
	
	private void initLogger() {
		
		if(this.bInitialized)
			return;
		
		if(LogManager.isUseFileLog()) {
			long threadId = Thread.currentThread().getId();
			FileAppender appender = LogManager.getCurrentThreadAppender();
			
			String loggerName = "MessageLogger_" + threadId;
			
			Logger newLogger = Logger.getLogger(loggerName);
			//newLogger.removeAllAppenders();
			newLogger.addAppender(appender);
			newLogger.setAdditivity(false);
			newLogger.setLevel(Level.DEBUG);
			
			Logger messageLogger = Logger.getLogger("MessageLogger");
			Appender consoleAppender = messageLogger.getAppender("console");
			
			if(consoleAppender != null) {
				newLogger.addAppender(consoleAppender);
			}
			
			this.logger = new Log4JLogger(newLogger);
		}
		this.bInitialized = true;
	}
	
	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	public void addLineMessage(String message) {
		try {
			sbBuffer.append("\n").append(message);
		} catch (Throwable t) {
			throw new BatchRuntimeException("you may add too many messages without print. print messages out with debug() method.", t);
		}
	}

	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	public void debug(Object...objects) {
		if(sbBuffer.length() == 0)
			return;
		
		initLogger();
		
		logger.debug(String.format(sbBuffer.toString(), objects));
		sbBuffer = null;
		sbBuffer = new StringBuilder();
	}
	
	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	public void debug() {
		if(sbBuffer.length() == 0)
			return;
		
		initLogger();
		
		logger.debug(sbBuffer.toString());
		
		sbBuffer = null;
		sbBuffer = new StringBuilder();
	}
	
	
	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	@Deprecated
	public void debug(String message) {
		initLogger();
		
		logger.debug(message);
	}
	
	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	@Deprecated
	public void info(String message) {
		initLogger();
		
		logger.info(message);
	}
	
	/**
	 * @deprecated use {@link #print(String)} instead
	 * @param message
	 */
	@Deprecated
	public void error(String message) {
		initLogger();
		
		logger.error(message);
	}
	
	/**
	 * 로그에 {@code message}를 출력합니다. (로그 레벨에 무관하게 출력됨)
	 * 
	 * @param message 출력하고자 하는 문자열
	 */
	public void print(String message) {
		initLogger();
		
		logger.debug(message);
	}
	
}
