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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class AnyframePropertyHandler {
	public static final String FILENAME = ".anyframe";
	
	Properties prop = new Properties();
	String path;
	
	public AnyframePropertyHandler(String path) {
		this.path = path;
		loadProperties(prop);
	}
	
	public void put(String key, String value) {
		prop.setProperty(key, value);
	}
	
	public void saveProperties() {
		
		path = path+"/"+FILENAME;
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path);
			prop.store(out, "AnyFrame IDE Framework properties");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadProperties(Properties properties) {
		InputStream in = null;
		try {
			in = new FileInputStream(path+"/"+FILENAME);
			
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
						in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getProperty(String key) {
		if(prop.isEmpty()) {
			loadProperties(prop);
		}
		
		return prop.getProperty(key);
	}
	
	public static String getProperty(String path, String key) {
		
		AnyframePropertyHandler handler = new AnyframePropertyHandler(path);

		return  handler.getProperty(key);
	}
	
	public Properties getProperty() {
		return prop;
	}
}
