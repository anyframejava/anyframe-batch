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

package com.sds.anyframe.batch.define;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sds.anyframe.batch.agent.PropertyConstants;
import com.sds.anyframe.batch.agent.util.PropertiesUtil;
import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class BatchDefine {
	private static Log logger = LogFactory.getLog(BatchDefine.class);
	
	public static final String JOB_INSTANCE_ID = "JOB_INSTANCE_ID";
	public static final String JOB_ID = "JOB_ID";
	public static final String USER_ID = "USER_ID";
	
	public static final String BEAN_NAME_JOB_REPOSITORY = "jobRepository";
	public static final String TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";
	
	public static final String STEP_LOG_FILE_PATH = "anyframe.step.log.file.path";
	public static final String STEP_RESOURCE_LIST = "anyframe.step.resource.list";
	public static final String JOB_ERROR_MESSAGE = "anyframe.job.error.message";
	
	public static String CONFIG_CHARSET = Charset.defaultCharset().toString();
	public static String CONFIG_LOCATION = "classpath:spring/batch/*.xml";
	public static String CONFIG_PREPROCESSOR_CLASS = null;
	public static String CONFIG_PREPROCESSOR_STYESHEET = null;
	public static String JOB_DEFAULT_LOCATION = "classpath";

	public static String BASE_LOG_DIR = "./log/";
	public static String BASE_LOG_DIR_DATEFORMAT = null;
	
	public static int PARALLEL_MAX_THREAD = -1; 
	
	public static boolean IS_AUTHENTICATION = false;
	public static boolean IS_CUSTOM_SQL_ALLOWED = false;
	
	public static String DEFAULT_ENCODING = "UTF-8";
	
	// default behavior of encoder/decoder on coding error 
	public static CodingErrorAction CHARSET_OVERFLOW   = CodingErrorAction.REPORT;
	public static CodingErrorAction CHARSET_MALFORMED  = CodingErrorAction.REPORT;
	public static CodingErrorAction CHARSET_UNMAPPABLE = CodingErrorAction.REPLACE;

	// default properties for item reader
	public static int 	  READER_BUFFER_SIZE_DEFAULT_KB = 64;	// 64Kbyte
	public static boolean READER_DB_SHARE_CONNECTION = true;

	public static boolean READER_SAM_FIXED_ROW = true;
	public static boolean READER_SAM_TRIM  = true;
	public static boolean READER_VSAM_TRIM = true;
	public static boolean READER_VSAM_ESCAPE = false;
	public static boolean WRITER_VSAM_ESCAPE = false;
	
	public static boolean READER_DB_NULL_ALLOW = false;
	public static String  READER_DB_NULL_STRING_REPL = " ";
	public static BigDecimal READER_DB_NULL_DECIMAL_REPL = BigDecimal.ZERO;
		
	// default properties for item writer
	public static int 	  WRITER_BUFFER_SIZE_DEFAULT_KB = 64;
	public static int     WRITER_COMMIT_INTERVAL = 10000;
	public static boolean WRITER_DELETE_EMPTY = false;
	
	public static boolean WRITER_DB_NULL_ALLOW = false;
	public static String  WRITER_DB_NULL_STRING_REPL = " ";
	public static BigDecimal WRITER_DB_NULL_DECIMAL_REPL = BigDecimal.ZERO;

	public static boolean WRITER_FILE_NULL_ALLOW = false;
	public static String  WRITER_FILE_NULL_STRING_REPL = " ";
	public static BigDecimal WRITER_FILE_NULL_DECIMAL_REPL = BigDecimal.ZERO;
	
	public static boolean WRITER_FILE_MAX_SIZE_ON = false;
	public static int     WRITER_FILE_MAX_SIZE_GB = -1;
	public static boolean WRITER_FILE_ERROR_ON_EXIST = false;
	public static boolean WRITER_FILE_USE_TMP = false;
	public static String  WRITER_FILE_TMP_SUFFIX = "._BATCH_TMP_";
	
	public static boolean DELETE_FILE_ON_ERROR = false;
	public static boolean DELETE_FILE_ON_STOP = false;

	public static String SHELL_COMMENT="#";
	public static boolean IS_SHELL_AUTHORIZED = false;
	
	static {
		
		String key = System.getProperty("BATCH_KEY");
		if(key == null) {
			System.setProperty("BATCH_KEY", "%BATCH_!@#$_AGENT_PBEWithMD5AndDES#%");
		}
		
		Properties prop = PropertiesUtil.getProperties(PropertyConstants.BATCH_PROPERTIES_FILE);
		
		if (prop != null) {
			BASE_LOG_DIR                  = readString(prop, "batch.log.directory", BASE_LOG_DIR);
			BASE_LOG_DIR_DATEFORMAT       = readString(prop, "batch.log.directory.dateformat", BASE_LOG_DIR_DATEFORMAT);
			JOB_DEFAULT_LOCATION          = readString(prop, "batch.job.default.location", JOB_DEFAULT_LOCATION);
			CONFIG_CHARSET	     		  = readString(prop, "batch.config.charset", CONFIG_CHARSET);
			CONFIG_LOCATION     		  = readString(prop, "batch.config.location", CONFIG_LOCATION);
			CONFIG_PREPROCESSOR_CLASS     = readString(prop, "batch.config.preprocessor", CONFIG_PREPROCESSOR_CLASS);
			CONFIG_PREPROCESSOR_STYESHEET = readString(prop, "batch.config.preprocessor.stylesheet", CONFIG_PREPROCESSOR_STYESHEET);
			
			//override default value from property file
			IS_AUTHENTICATION             = readBoolean(prop, "batch.security.on", IS_AUTHENTICATION);
			IS_CUSTOM_SQL_ALLOWED         = readBoolean(prop, "batch.custom.sql", IS_CUSTOM_SQL_ALLOWED);
			PARALLEL_MAX_THREAD			  = readInt(prop, "batch.parallel.maxthread", PARALLEL_MAX_THREAD);
			
			SHELL_COMMENT        		  = readString(prop, "batch.shell.comment", SHELL_COMMENT);
			IS_SHELL_AUTHORIZED		  	  = readBoolean(prop, "batch.shell.authorize", IS_SHELL_AUTHORIZED);
			DELETE_FILE_ON_ERROR		  = readBoolean(prop, "batch.delete.file.on.error", DELETE_FILE_ON_ERROR);
			DELETE_FILE_ON_STOP			  = readBoolean(prop, "batch.delete.file.on.stop", DELETE_FILE_ON_STOP);
			
			CHARSET_OVERFLOW              = readCodingAction(prop, "batch.charset.overflow", CHARSET_OVERFLOW);
			CHARSET_MALFORMED             = readCodingAction(prop, "batch.charset.malformed", CHARSET_MALFORMED);
			CHARSET_UNMAPPABLE            = readCodingAction(prop, "batch.charset.unmappable", CHARSET_UNMAPPABLE);

			READER_BUFFER_SIZE_DEFAULT_KB = readInt(prop, "batch.reader.buffersize.kb", READER_BUFFER_SIZE_DEFAULT_KB);
			READER_DB_SHARE_CONNECTION    = readBoolean(prop, "batch.reader.db.share.connection", READER_DB_SHARE_CONNECTION);
			READER_SAM_FIXED_ROW          = readBoolean(prop, "batch.reader.sam.fixed", READER_SAM_FIXED_ROW);
			READER_SAM_TRIM               = readBoolean(prop, "batch.reader.sam.trim", READER_SAM_TRIM);
			READER_VSAM_TRIM              = readBoolean(prop, "batch.reader.vsam.trim", READER_VSAM_TRIM);
			READER_VSAM_ESCAPE            = readBoolean(prop, "batch.reader.vsam.escape", READER_VSAM_ESCAPE);
			WRITER_VSAM_ESCAPE            = readBoolean(prop, "batch.writer.vsam.escape", WRITER_VSAM_ESCAPE);
			
			READER_DB_NULL_ALLOW          = readBoolean(prop, "batch.reader.db.null.allow", READER_DB_NULL_ALLOW);
			READER_DB_NULL_STRING_REPL    = readString(prop, "batch.reader.db.null.replace.string", READER_DB_NULL_STRING_REPL);
			READER_DB_NULL_DECIMAL_REPL   = readBigDecimal(prop, "batch.reader.db.null.replace.bigdecimal", READER_DB_NULL_DECIMAL_REPL);
			
			WRITER_BUFFER_SIZE_DEFAULT_KB = readInt(prop, "batch.writer.buffersize.kb", WRITER_BUFFER_SIZE_DEFAULT_KB);
			WRITER_COMMIT_INTERVAL        = readInt(prop, "batch.writer.commitInterval", WRITER_COMMIT_INTERVAL);
			WRITER_DELETE_EMPTY           = readBoolean(prop, "batch.writer.deleteEmptyFile", WRITER_DELETE_EMPTY);
			
			WRITER_DB_NULL_ALLOW          = readBoolean(prop, "batch.writer.db.null.allow", WRITER_DB_NULL_ALLOW);
			WRITER_DB_NULL_STRING_REPL    = readString(prop, "batch.writer.db.null.replace.string", WRITER_DB_NULL_STRING_REPL);
			WRITER_DB_NULL_DECIMAL_REPL   = readBigDecimal(prop, "batch.writer.db.null.replace.bigdecimal", WRITER_DB_NULL_DECIMAL_REPL);
			
			WRITER_FILE_NULL_ALLOW        = readBoolean(prop, "batch.writer.file.null.allow", WRITER_FILE_NULL_ALLOW);
			WRITER_FILE_NULL_STRING_REPL  = readString(prop, "batch.writer.file.null.replace.string", WRITER_FILE_NULL_STRING_REPL);
			WRITER_FILE_NULL_DECIMAL_REPL = readBigDecimal(prop, "batch.writer.file.null.replace.bigdecimal", WRITER_FILE_NULL_DECIMAL_REPL);

			WRITER_FILE_MAX_SIZE_ON       = readBoolean(prop, "batch.writer.file.max.size.on", WRITER_FILE_MAX_SIZE_ON);
			WRITER_FILE_MAX_SIZE_GB 	  = readInt(prop, "batch.writer.file.max.size", WRITER_FILE_MAX_SIZE_GB);
			WRITER_FILE_ERROR_ON_EXIST    = readBoolean(prop, "batch.writer.file.error.on.exist", WRITER_FILE_ERROR_ON_EXIST);
			WRITER_FILE_USE_TMP			  = readBoolean(prop, "batch.writer.file.use.tmp", WRITER_FILE_USE_TMP);
			WRITER_FILE_TMP_SUFFIX		  = readString(prop, "batch.writer.file.tmp.suffix", WRITER_FILE_TMP_SUFFIX);
			
		} else {	// property file does not exist
			logger.info("There is no property file. default configuration will be set");
		}
	}
	
	private static String readString(Properties prop, String key, String defaultValue) {
		String value = prop.getProperty(key);
		if(value == null)
			return defaultValue;
		else 
			return value;
	}
	
	private static int readInt(Properties prop, String key, int defaultValue) {
		String value = prop.getProperty(key);
		if(value == null)
			return defaultValue;
		else 
			return Integer.parseInt(value);
	}
	
	private static BigDecimal readBigDecimal(Properties prop, String key, BigDecimal defaultValue) {
		String value = prop.getProperty(key);
		if(value == null)
			return defaultValue;
		else 
			return new BigDecimal(value);
	}
	
	private static boolean readBoolean(Properties prop, String key, boolean defaultValue) {
		String value = prop.getProperty(key);
		if(value == null)
			return defaultValue;
		else 
			return Boolean.parseBoolean(value);
	}
	
	private static CodingErrorAction readCodingAction(Properties prop, String key, CodingErrorAction defaultValue) {
		String value = prop.getProperty(key);
		
		if(value != null) {
			if(value.equalsIgnoreCase("report")) {
				return CodingErrorAction.REPORT;
			} else if (value.equalsIgnoreCase("ignore")) {
				return CodingErrorAction.IGNORE;
			} else if (value.equalsIgnoreCase("replace")) {
				return CodingErrorAction.REPLACE;
			} else {
				throw new BatchRuntimeException(key + " should be one of 'report', 'replace' and 'ignore'");
			}
		}
		
		return defaultValue;
	}
	
}
