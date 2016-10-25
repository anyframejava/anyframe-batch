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

package com.sds.anyframe.batch.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ProcessStreamReader extends Thread {
	private static Logger log = Logger.getLogger(ProcessStreamReader.class);

	private int lineCount;
	
	public static final String ERROR_STREAM = "ErrorStream";
	public static final String OUTPUT_STREAM = "OutputStream";
	
	private final StringBuilder errorBuffer = new StringBuilder();

	private final InputStream is;
	private final String type;

	public StringBuilder getErrorBuffer() {
		return errorBuffer;
	}

	public ProcessStreamReader(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				log.debug(type + "> " + line);
				if (type.equals(ERROR_STREAM)) {
					if(lineCount == 1000)
						continue;
					errorBuffer.append(line);
					lineCount++;
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
