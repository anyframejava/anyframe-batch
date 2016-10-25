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

package com.sds.anyframe.batch.agent.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PropertiesUtil {
	private static Logger log = Logger.getLogger(PropertiesUtil.class);
	public static Map<String, Properties> properties = new HashMap<String, Properties>();

	private PropertiesUtil() {
		new AssertionError("This class is an utility class.");
	}

	public synchronized static Properties getProperties(String fileName) {
		Properties tmp = properties.get(fileName);

		if (tmp != null)
			return tmp;

		Properties prop = new Properties();
		InputStream stream = null;

		try {
			stream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(fileName);
			if(stream == null)
				return prop;
			prop.load(stream);
		} catch (Exception e) {
			log.error("There is no properties file in classpath. [" + fileName + "]", e);
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}
		}

		properties.put(fileName, prop);

		return prop;
	}
}
