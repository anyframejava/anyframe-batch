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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;

import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class IDGenerator {

    /**

     * 배치 JOB ID를 기반으로 Service Execution Id를 생성하여 리턴한다.
     * 
     * @param jobId 배치 JOB ID
     * @return Service Execution Id
     */

    public static String getServiceExecutionId(String jobId){

    	if(jobId == null || StringUtils.isEmpty(jobId))
    		throw new BatchRuntimeException("jobId shoud not be null or empty");
    	
        StringBuilder sb = new StringBuilder();

        String hostname;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
        	hostname = "UNKNOWN";
        }

        if(hostname == null || StringUtils.isEmpty(hostname))
        	hostname = "UNKNOWN";

        sb.append(hostname);
        sb.append("_");
        sb.append(jobId);
        sb.append("_");
        sb.append("T");

        // Thread ID는 총 6자리 : "T" + threadId(5)
        sb.append(String.format("%05d", Thread.currentThread().getId()));  
        sb.append("_");

        sb.append(System.currentTimeMillis());

        return sb.toString(); // ID 생성 규칙에 따른 서비스 실행 ID 생성.
    }

}
