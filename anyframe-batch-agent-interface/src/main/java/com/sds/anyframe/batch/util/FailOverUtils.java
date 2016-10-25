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

package com.sds.anyframe.batch.util;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;

import com.caucho.hessian.client.HessianConnectionException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class FailOverUtils {
	private static final Logger logger = Logger.getLogger(FailOverUtils.class);
	
	private static final Random random = new Random();
	
	public static boolean isFailOver(Exception e) {
		return e instanceof RemoteAccessException
				|| e instanceof SocketException
				|| e instanceof HessianConnectionException;
	}
	
	public static String getAvailableServerUrl(String servers,
			String context_service, int timeout, boolean applyRandom)
			throws Exception, ConnectException, SocketTimeoutException,
			SocketException {
		List<String> lst = new ArrayList<String>();
		
		createServerList(lst, servers);
		
		String url = null;
		
		if(lst.size() == 0)
			throw new Exception("No server list found.");
		
		for (;0 < lst.size();) {
			
			int index = 0;
			
			if(applyRandom)
				index = getRandomServer(lst);
					
			String server = lst.get(index);

			lst.remove(index);
			
			url = "http://" + server + "/" + context_service;
			try {
				canConnect(url, timeout);
				logger.info("The client connected to " + server);
				break;
			} catch (ConnectException e) {
				if(0 == lst.size()) {
					logger.error("ConnectException", e);
					throw e;
				}
			} catch (SocketTimeoutException e) {
				if(0 == lst.size()) {
					logger.error("SocketTimeoutException", e);
					throw e;
				}
			} catch (SocketException e) {
				if(0 == lst.size()) {
					logger.error("SocketException", e);
					throw e;
				}
			} catch (Exception e) {
				logger.error("Unexpected error occured", e);
				throw e;
			}
		}
		return url;
	}

	private static int getRandomServer(List<String> lst) {
		return random.nextInt(lst.size());
	}
	
	private static void createServerList(List<String> servers,
			String failoverServers) {
		if(failoverServers == null)
			return;
		
		String[] split = failoverServers.split(";");
		
		for(String server: split) {
			servers.add(server.trim());
		}
	}
	
	private static void canConnect(String urlString, int timeout) throws Exception {
		URL url = null;
		try {
			url = new URL(urlString);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			URLConnection connection = url.openConnection();

			if (timeout > 0)
				connection.setConnectTimeout(timeout);
			connection.connect();
		} catch (Exception e) {
			logger.info("Can not connect to " + urlString);
			throw e;
		}
	}
}
