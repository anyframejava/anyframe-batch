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

package com.sds.anyframe.batch.core.step.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


/**
 * 생성자로 전달된 InputStream을 끝까지 읽어 로그로 출력하는 쓰레드.
 * InputStream 에서 1,000줄까지는 내부 msg 버퍼에 캐싱한다.
 * child process의 stdout, stderr 스트림을 소진하기 위해서 사용함
 * @author Hyoungsoon Kim
 *
 */

public class StreamConsumerThread extends Thread {
	private static Logger logger = Logger.getLogger("ShellScriptLogger");
	
	public final static String STDERR = "stderr";
	public final static String STDOUT = "stdout";
	
	private final InputStream inputStream;
	private final String prefix;
	private final boolean bBuffer;
	
	private final StringBuilder msgBuffer = new StringBuilder(1024);
	
	public StreamConsumerThread(InputStream stream, String prefix, boolean bBuffer) {
		this.inputStream = stream;
		this.prefix = prefix;
		this.bBuffer = bBuffer;
	}

	@Override
	public void run() {
		if(inputStream == null)
			return;
		
		BufferedReader bis = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		int readCount = 0;

		try {
			while ((line = bis.readLine()) != null) {
				logger.info(prefix + "> " + line);
				
				if(this.bBuffer && readCount++ < 1000)
					msgBuffer.append(prefix + "> " + line+"\n");
				
			}
		} catch (IOException e) {
			logger.error("error occur while reading inputstream", e);
			
		} finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("error occur while closing inputstream", e);
				}
			}
		}
	}

	public String getMessage() {
		if(msgBuffer == null)
			return null;
			
		return msgBuffer.toString();
	}
}
