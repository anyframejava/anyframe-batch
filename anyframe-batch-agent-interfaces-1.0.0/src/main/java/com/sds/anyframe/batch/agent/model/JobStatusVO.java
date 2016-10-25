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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */

public class JobStatusVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String jobId;
	private String jobName;
	private String ownerId;
	private String odate;
	private String description;
	private List<StepStatusVO> stepStatusList;
	private String jobStatus;
	private String exitCode;

	public JobStatusVO() {
		stepStatusList = new ArrayList<StepStatusVO>();
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOdate() {
		return odate;
	}

	public void setOdate(String odate) {
		this.odate = odate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<StepStatusVO> getStepStatusList() {
		return stepStatusList;
	}

	public void addStepStatus(StepStatusVO vo) {
		this.stepStatusList.add(vo);
	}

	public void setStepStatusList(List<StepStatusVO> stepStatusList) {
		this.stepStatusList = stepStatusList;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
