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

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.sds.anyframe.batch.agent.util.AgentUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class Job implements Serializable, Comparable<Job> {
	
	private static final long serialVersionUID = 1L;

	private boolean lockingByJob;
	
	private String ip;

	private long pid;

	private String jobId;

	private long jobSeq = -1;

	private JobStatus jobStatus;

	private String currentStepId;

	private List<Resource> resources;

	private Date createdDate;

	private Date lastUpdated;

	private String exitMessage;

	private boolean checked;

	private StepStatus stepStatus;

	private String logFiles;

	private Job parent;

	private List<Job> childrens;
	
	private boolean allowConcurrentRunning;
	
	private Performance performance = new Performance();

	public Performance getPerformance() {
		return performance;
	}

	public void setPerformance(Performance performance) {
		this.performance = performance;
	}

	public Job(long pid, String jobId) {
		this.ip = AgentUtils.getIp();
		this.pid = pid;
		this.jobId = jobId;
		
		Date current = new Date();
		
		this.createdDate = current;
		this.lastUpdated = current;
	}
	
	public Job() {
		this(0l, null);
	}

	public void setChildren(List<Job> jobs) {
		this.childrens = jobs;
	}

	public List<Job> getChildrens() {
		return childrens;
	}

	public Job getParent() {
		return parent;
	}

	public void setParent(Job parent) {
		this.parent = parent;
	}

	public boolean isChecked() {
		return checked;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getJobSeq() {
		return jobSeq;
	}

	public void setJobSeq(long jobSeq) {
		this.jobSeq = jobSeq;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
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

	public String getCurrentStepId() {
		return currentStepId;
	}

	public void setCurrentStepId(String currentStepId) {
		this.currentStepId = currentStepId;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return String
				.format(
						"Job [ip=%s, pid=%s, jobId=%s, jobSeq=%s, currentStepId=%s, jobStatus=%s, stepStatus=%s, createdDate=%s, lastUpdated=%s, exitMessage=%s, resources=%s, logFiles=%s, performance(%s)]",
						ip, pid, jobId, jobSeq, currentStepId,
						jobStatus, stepStatus, createdDate, lastUpdated,
						exitMessage, resources, logFiles, performance.toString());
	}

	public int compareTo(Job o) {
		return this.createdDate.compareTo(o.createdDate);
	}

	public StepStatus getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(StepStatus stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getLogFiles() {
		return logFiles;
	}

	public void setLogFiles(String logFiles) {
		this.logFiles = logFiles;
	}

	public boolean isLockingByJob() {
		return lockingByJob;
	}

	public void setLockingByJob(boolean lockingByJob) {
		this.lockingByJob = lockingByJob;
	}

	public boolean isAllowConcurrentRunning() {
		return allowConcurrentRunning;
	}

	public void setAllowConcurrentRunning(boolean allowConcurrentRunning) {
		this.allowConcurrentRunning = allowConcurrentRunning;
	}
}
