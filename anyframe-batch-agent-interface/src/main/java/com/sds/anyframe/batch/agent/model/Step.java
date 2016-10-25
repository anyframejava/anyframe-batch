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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.sds.anyframe.batch.agent.util.AgentUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class Step implements Serializable, Comparable<Step> {
	private static final long serialVersionUID = 1L;

	private String ip = AgentUtils.getIp();

	private long pid;

	private String jobId;

	private String jobExecutionId;
	
	private long jobSeq;

	private String stepId;

	private StepStatus stepStatus;
	
	private Date createdDate;

	private Date lastUpdated;

	private List<Resource> resources;

	private String exitMessage;

	private boolean checked;
	
	private String logFiles;
	
	
	public Step() {
		this(0l, null, 0l, null, null);
	}

	public Step(long pid, String jobId, long jobSeq, String jobExecutionId, String stepId) {
		this.pid = pid;
		this.jobId = jobId;
		this.stepId = stepId;
		this.jobSeq = jobSeq;
		this.jobExecutionId = jobExecutionId;
		
		Date current = new Date();
		this.createdDate = current;
		this.lastUpdated = current;
	}

	public boolean isChecked() {
		return checked;
	}

	public long getJobSeq() {
		return jobSeq;
	}

	public void setJobSeq(long jobSeq) {
		this.jobSeq = jobSeq;
	}

	public String getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(String jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public StepStatus getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(StepStatus status) {
		this.stepStatus = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date updateDate) {
		this.lastUpdated = updateDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public String getIp() {
		return ip;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}


	@Override
	public String toString() {
		return String.format(
						"StepInfo [ip=%s, pid=%s, stepId=%s, resources=%s, jobId=%s, stepStatus=%s, jobSeq=%s, jobExecutionId=%s, createdDate=%s, lastUpdated=%s, exitMessage=%s]",
						ip, pid, stepId, resources, jobId, stepStatus, jobSeq, jobExecutionId, createdDate, lastUpdated, exitMessage);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int compareTo(Step o) {
		return this.createdDate.compareTo(o.createdDate);
	}

	public String getLogFiles() {
		return logFiles;
	}

	public void setLogFiles(String logFiles) {
		this.logFiles = logFiles;
	}
}
