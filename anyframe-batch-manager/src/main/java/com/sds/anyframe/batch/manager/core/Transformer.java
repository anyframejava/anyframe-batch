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

/******************************************************************************
 * 나중에 Core와 통합을 용이하게 하기위해 Core 참조 부분을 본 Package에 옮겨둠! 
 * 본 Package의 Class들은 수정/개선 하지 말 것!
 * bonobono, 090706
 ******************************************************************************/

package com.sds.anyframe.batch.manager.core;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.sds.anyframe.batch.manager.BatchConstants;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class Transformer {

	public static CreateClass createClass = new CreateClass();
	public VelocityEngine velocityEngine = null;
	public InputStream classInputStream;
	public String path = null;
	public String fileName = null;
	public static final String TEMPLATE_FOLDER = "template/";

	public Transformer() {
		// TODO Auto-generated constructor stub
	}
	
	private Properties getClassResourceLoaderProperties(boolean useClassLoader, String characterSet) {

		Properties properties = new Properties();
		if (useClassLoader) {
			properties.setProperty("resource.loader", "class");
			properties
					.setProperty("class.resource.loader.class",
							"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		} else {
			properties.setProperty(VelocityEngine.RESOURCE_LOADER, "file");
			properties
					.setProperty("file.resource.loader.class",
							"org.apache.velocity.runtime.resource.loader.FileResourceLoader");

			properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_CACHE,
					"true");
			properties.setProperty(
					"file.resource.loader.modificationCheckInterval", "1");
		}
		
		if(characterSet == null)
			characterSet = "EUC-KR";
		
		properties.setProperty(VelocityEngine.INPUT_ENCODING, characterSet);
		properties.setProperty(VelocityEngine.OUTPUT_ENCODING, characterSet);
		properties.setProperty(VelocityEngine.ENCODING_DEFAULT, characterSet);
		
		return properties;

	}

	public String generateClassSource(Map inputMap,
			String templateName, boolean useClassLoader, String characterSet){

		Set keySet = inputMap.keySet();
		StringWriter writer = new StringWriter();
		try {

			// set the custom template loader
			velocityEngine = new VelocityEngine();

			// characterSet Default
			if(StringUtil.isEmptyOrNull(characterSet)) {
				characterSet = BatchConstants.BATCH_PROJECT_CHARACTERSET_DEFAULT;
			}
			
			// now initialize the engine
			Properties properties = getClassResourceLoaderProperties(useClassLoader, characterSet);
		

			if (!useClassLoader) {
				int tmpIdx = templateName.lastIndexOf('/');
				String templatePath = templateName.substring(0, tmpIdx);
				templateName = templateName.substring(tmpIdx + 1);
				properties.setProperty(
						VelocityEngine.FILE_RESOURCE_LOADER_PATH, templatePath);
			} else
				templateName = TEMPLATE_FOLDER + templateName;

			velocityEngine.init(properties);
			
			Template javaClassTemplate = null;
			try {
				javaClassTemplate = velocityEngine.getTemplate(templateName);

			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
				// couldn't find the template
				
				// LOGGER.error("couldn't find the template", rnfe);
			} catch (ParseErrorException pee) {
				// syntax error: problem parsing the template
				// LOGGER.error("syntax error: problem parsing the template",
				// pee);
			} catch (MethodInvocationException mie) {
				// something invoked in the template
				// threw an exception

				// LOGGER.error(
				// "something invoked in the template threw an exception",
				// mie);
			} catch (Exception exception) {

				// LOGGER.error("Some error occured", exception);
			}

			// Create a Context object
			VelocityContext context = new VelocityContext();

			Iterator iter = keySet.iterator();
			String key = null;
			String value = null;
			Vector v_value = null;

			// Add your data objects to the Context.
			while (iter.hasNext()) {

				key = (String) iter.next();
				if (key.equals("tableColumn") || key.equals("DescTextLines") || key.equals("importStatements")) {

					v_value = (Vector) inputMap.get(key);
					context.put(key, v_value);
					// 0827!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				} else if (inputMap.get(key) instanceof String) { // leeyh
					// modify
					// 2008.08.27
					value = (String) inputMap.get(key);
					context.put(key, value);
				} else if (inputMap.get(key) instanceof ArrayList) {
					ArrayList arrayList = (ArrayList) inputMap.get(key);
					context.put(key, arrayList);
				}
// ##### bonobono, 2009.05.01, need more test.
				else {
					context.put(key, inputMap.get(key));
				}
				
			}

			// added by bonobono, 10/01/14, 일단 임시로 이렇게...
			// bonobono TODO : 쓸만한 Date관련 사용자 정의 Util을 넘기거나, Velocity 1.5로 update 후 Velocity 내장 DateTool을 넘기자.
			context.put("DateUtil", new SimpleDateUtil());
			context.put("SystemUser", getSystemCurrentUser());
			context.put("SystemName", getSystemName());
			
			// Merge the template and your data to produce the ouput.
			if(javaClassTemplate != null)
				javaClassTemplate.merge(context, writer);

		} catch (Exception exception) {
			// Fail to transform template
//			PluginLogger.error(exception);
			exception.printStackTrace();
		}

	
		return writer.toString();
	}

	public String getAbsolutePathOfVoClass(String srcFolder,
			String packageName, String voClassName) {

		packageName = packageName.replaceAll("\\.", "/");

		if (packageName.trim().equals(""))
			packageName = "";
		else
			packageName = "/" + packageName;

		String absolutePathOfVoClass = null;

		absolutePathOfVoClass = srcFolder + packageName + "/" + voClassName;

		return absolutePathOfVoClass;
	}
	// ##### bonobono : Temporaty Date Util Class, 주의: Velocity 사용용이므로 접근자 private으로 바꾸지 말 것!
	public static final class SimpleDateUtil {
		private static final String defaultFormat = "yyyy.MM.dd";
		public static String getCurrentDate() {
			return new SimpleDateFormat(defaultFormat, Locale.KOREA).format(new Date());
		}
		public static String getCurrentDate(String format) {
			try {
				return new SimpleDateFormat(format, Locale.KOREA).format(new Date());
			} catch(IllegalArgumentException iae) {
				return new SimpleDateFormat(defaultFormat, Locale.KOREA).format(new Date());
			}
		}
	}
	
	/**
	 * retrieve current user of local system.
	 */
	public static String getSystemCurrentUser() {
		return System.getProperty("user.name");
	}
	
	/**
	 * retrieve host name of local system.
	 */
	public static String getSystemName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}
}
