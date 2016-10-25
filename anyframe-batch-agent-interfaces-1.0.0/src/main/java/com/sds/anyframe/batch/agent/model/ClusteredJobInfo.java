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

package com.sds.anyframe.batch.agent.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ClusteredJobInfo implements Serializable, Comparable<ClusteredJobInfo>{
	private static final long serialVersionUID = 1L;
	
	private final String ip;
	{
		InetAddress addr = null;
		try {
			addr  = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}finally{
			ip = addr!=null?addr.getHostAddress():"Unknown Host";
		}
	}
	
	private final long pid;
	private final String jobId;
	private String jobExecutionId;
	private String currentStepId;
	private JobStatus jobStatus;
	private Date updatedDate;
	private Map<ResourceIoType, Set<String>> resourceList;
	private boolean checkBoxSelected;
	
	public ClusteredJobInfo(long pid, String jobId) {
		this.pid = pid;
		this.jobId = jobId;
		this.updatedDate = new Date();
	}
	
	public ClusteredJobInfo(long pid, String jobId, String jobExecutionId) {
		this.pid = pid;
		this.jobId = jobId;
		this.jobExecutionId = jobExecutionId;
		this.updatedDate = new Date();
	}

	public ClusteredJobInfo(long pid, String jobId, String jobExecutionId, String currentStepId, Map<ResourceIoType, Set<String>> resourceList) {
		this.pid = pid;
		this.jobId = jobId;
		this.jobExecutionId = jobExecutionId;
		this.currentStepId = currentStepId;
		this.resourceList = resourceList;
		this.updatedDate = new Date();
	}

	public boolean isCheckBoxSelected() {
		return checkBoxSelected;
	}

	public void setCheckBoxSelected(boolean checkBoxSelected) {
		this.checkBoxSelected = checkBoxSelected;
	}
	
	public String getCurrentStepId() {
		return currentStepId;
	}

	public void setCurrentStepId(String currentStepId) {
		this.currentStepId = currentStepId;
	}

	public Map<ResourceIoType, Set<String>> getResourceList() {
		return resourceList;
	}

	public void setResourceList(Map<ResourceIoType, Set<String>> resourceList) {
		this.resourceList = resourceList;
	}

	public String getIp() {
		return ip;
	}

	public long getPid() {
		return pid;
	}

	public String getJobId() {
		return jobId;
	}
	
	public String getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(String jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}
	
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int compareTo(ClusteredJobInfo o) {
		return this.updatedDate.compareTo(o.updatedDate); 
	}
}
