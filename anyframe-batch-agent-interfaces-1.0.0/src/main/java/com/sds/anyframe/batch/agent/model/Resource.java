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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.util.AddableLong;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	private String resourceName;

	private String jobExecutionId;
	
	private long jobSeq;

	private String jobId;

	private String stepId;

	private ResourceIoType ioType;

	private ResourceType type = ResourceType.FILE;
	
	private ResourceStatus status;

	private Date createTime;

	private Date updateTime;

	private AddableLong transactedCount = new AddableLong(0);
	
	public Resource() {

	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getJobExecutionId() {
		return jobExecutionId;
	}

	public long getJobSeq() {
		return jobSeq;
	}

	public void setJobSeq(long jobSeq) {
		this.jobSeq = jobSeq;
	}

	public void setJobExecutionId(String jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public ResourceIoType getIoType() {
		return ioType;
	}

	public void setIoType(ResourceIoType status) {
		this.ioType = status;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
	@Override
	public String toString() {
		return String.format(
						"Resource [createTime=%s, jobExecutionId=%s, jobId=%s, status=%s, resourceName=%s, ioType=%s, stepId=%s, updateTime=%s]",
						createTime, jobExecutionId, jobId, status, resourceName, ioType, stepId, updateTime);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public AddableLong getTransactedCount() {
		return transactedCount;
	}

	public void setTransactedCount(AddableLong transactedCount) {
		this.transactedCount = transactedCount;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType resourceType) {
		this.type = resourceType;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus resourceStatus) {
		this.status = resourceStatus;
	}
}


