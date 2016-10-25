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

package com.sds.anyframe.batch.manager;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchConstants {

	public static final String BATCH_WIZARD_MESSAGE_TITLE = "Batch Wizard";
	
// START : Constants for Batch Program Wizard
	public static final String BATCH_TEMPLATE_PATH = "BATCH_TEMPLATE_PATH";
	public static final String BATCH_PATTERN_CONFIG_FILENAME = "BatchPatternConfig.xml";
	public static final String BATCH_PATTERN_CONFIG_ROOT = "/batch/pattern";
	
	// for Batch Item Handler's IO Type
	public static final String ITEM_HANDLER_IO_TYPE_READER = "reader";
	public static final String ITEM_HANDLER_IO_TYPE_WRITER = "writer";
	
	// for Batch Item Handler Type, TO DO : Consider make type
	public static final String TYPE_SAM = "SAM";
	public static final String TYPE_DB = "DB";
	public static final String TYPE_EACHQUERY = "EachQuery";
	
	public static final String TYPE_DEFAULT = TYPE_SAM;
	
	// for Batch Pattern XML Tags
	public static final String XML_TAG_ID = "id";
	public static final String XML_TAG_NAME = "name";
	public static final String XML_TAG_SERVICE_NAME = "serviceName";
	public static final String XML_TAG_QUERY_NAME = "queryName";

	public static final String XML_TAG_READER_NUMBER = "readerNumber";
	public static final String XML_TAG_WRITER_NUMBER = "writerNumber";

	// for Range of num. of Batch Item Handler 
	public static final int MININUM_ITEM_HANDLER_DEFAULT = 1;
	public static final int MAXIMUM_ITEM_HANDLER_DEFAULT = 99999;

	// for file name
	public static final String SUFFIX_QUERY_FILE = "_SQL";
// END : Constants for Batch Program Wizard
	

	public static final String BATCH_PROJECT_CHARACTERSET = "BATCH_PROJECT_CHARACTERSET";
	public static final String BATCH_PROJECT_CHARACTERSET_DEFAULT = "UTF-8";

	public static final String BATCH_MANAGER_PREFERENCE = "batchManager.server.path";
	
	// for Job Config Edit
	public static final String JOB_CONFIG_TEMP_FILE_SUFFIX = ".tmp";
	
	public static final String PREF_SECURITY_USERID = "batch.userid";
	public static final String PREF_USER_PARAMETER = "batch.user.parameter";
	
}
