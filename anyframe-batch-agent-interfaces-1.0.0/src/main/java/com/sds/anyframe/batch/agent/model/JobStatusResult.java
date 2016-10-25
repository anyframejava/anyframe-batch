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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JobStatusResult extends PageResult {
	
	private static final long serialVersionUID = 1L;
	
	String sysDate,  jobInstanceId, version, jobName, jobKey, 
	jobExecutionId, createTime, startTime, endTime, status, continuable, exitCode, exitMessage,
	stepExecutionId , itemName,stepName, commitCount, itemCount, readSkipCount, writeSkipCount, rollbackCount, elapsedTime;

	public String getSysDate() {
		return sysDate;
	}

	public void setSysDate(String sysDate) {
		this.sysDate = sysDate;
	}

	public String getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(String jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public String getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(String jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContinuable() {
		return continuable;
	}

	public void setContinuable(String continuable) {
		this.continuable = continuable;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public String getStepExecutionId() {
		return stepExecutionId;
	}

	public void setStepExecutionId(String stepExecutionId) {
		this.stepExecutionId = stepExecutionId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getCommitCount() {
		return commitCount;
	}

	public void setCommitCount(String commitCount) {
		this.commitCount = commitCount;
	}

	public String getItemCount() {
		return itemCount;
	}

	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	public String getReadSkipCount() {
		return readSkipCount;
	}

	public void setReadSkipCount(String readSkipCount) {
		this.readSkipCount = readSkipCount;
	}

	public String getWriteSkipCount() {
		return writeSkipCount;
	}

	public void setWriteSkipCount(String writeSkipCount) {
		this.writeSkipCount = writeSkipCount;
	}

	public String getRollbackCount() {
		return rollbackCount;
	}

	public void setRollbackCount(String rollbackCount) {
		this.rollbackCount = rollbackCount;
	};
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String getFieldValue(int columnIndex) {
		String returnValue = null;
		switch (columnIndex) {
		case 0:
			returnValue = getSysDate();
			break;
		case 1:
			returnValue = getJobInstanceId();
			break;
		case 2:
			returnValue = getJobName();
			break;
		case 5:
			returnValue = getStepName();
			break;
		case 4:
			returnValue = getStepExecutionId();
			break;
		case 6:
			returnValue = getStartTime();
			break;
		case 7:
			returnValue = getEndTime();
			break;
		case 8:
			returnValue = getStatus();
			break;
		case 9:
			returnValue = getItemCount();
			break;
		case 10:
			returnValue = getExitCode();
			break;
		case 11:
			returnValue = getExitMessage();
			break;
		default:
			break;
		}
		return returnValue;
	}
	
	@Override
	public int compareTo(Object object, int columnIndex) {
		int returnValue = 0;
		if (columnIndex == 0) {
			returnValue = getSysDate().compareTo(((JobStatusResult) object).getSysDate());
		}
		if (columnIndex == 1) {
			returnValue = new Integer(getJobInstanceId()).intValue() - new Integer(((JobStatusResult) object).getJobInstanceId()).intValue();
		}
		if (columnIndex == 2) {
			returnValue = getJobName().compareTo(((JobStatusResult) object).getJobName());
		}
		if (columnIndex == 3) {
			returnValue = getJobName().compareTo(((JobStatusResult) object).getJobName());
		}
		if (columnIndex == 4) {
			returnValue = new Integer(getStepExecutionId()).intValue() -  new Integer(((JobStatusResult) object).getStepExecutionId()).intValue();
		}
		if (columnIndex == 5) {
			returnValue = getStepName().compareTo(((JobStatusResult) object).getStepName());
		}
		if (columnIndex == 6) {
			returnValue = getStartTime().compareTo(((JobStatusResult) object).getStartTime());
		}
		if (columnIndex == 7) {
			returnValue = getEndTime().compareTo(((JobStatusResult) object).getEndTime());
		}
		if (columnIndex == 8) {
			returnValue = getStatus().compareTo(((JobStatusResult) object).getStatus());
		}
		if (columnIndex == 9) {
			returnValue = new Integer(getItemCount()).intValue() - new Integer(((JobStatusResult) object).getItemCount()).intValue();
		}
		if (columnIndex == 10) {
			returnValue = getExitCode().compareTo(((JobStatusResult) object).getExitCode());
		}
		if (columnIndex == 11) {
			returnValue = getExitMessage().compareTo(((JobStatusResult) object).getExitMessage());
		}
		return returnValue;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
}
