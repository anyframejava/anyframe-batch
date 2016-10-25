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

import org.apache.commons.lang.builder.ToStringBuilder;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JobInfo implements Comparable<JobInfo>, Serializable{
	private static final long serialVersionUID = 1L;
	
	public final static String TYPE_CATEGORY = "CATEGORY";
	public final static String TYPE_PACKAGE = "PACKAGE";
	public final static String TYPE_JOB = "JOB";
	
	private String type;
	private String jobPath;
	private String workName;
	private String jobPackageName;
	private String jobName;
	private String jobId;
	
	public static JobInfo newCategory(String workName) {
		return new JobInfo( TYPE_CATEGORY,  null,  workName,  null,  null,  null);
	}

	public static JobInfo newPackage(String jobPath, String workName, String jobPackageName) {
		return new JobInfo( TYPE_PACKAGE,  jobPath,  workName,  jobPackageName,  null,  null);
	}
	
	public static JobInfo newJob(String jobPath, String workName, String jobPackageName, String jobName, String jobId) {
		return new JobInfo( TYPE_JOB,  jobPath,  workName,  jobPackageName,  jobName,  jobId);
	}
	
	private JobInfo(String type, String jobPath, String workName, String jobPackageName, String jobName, String jobId) {
		this.type = type;
		this.jobPath = jobPath;
		this.workName = workName;
		this.jobPackageName = jobPackageName;
		this.jobName = jobName;
		this.jobId = jobId;
	}
	
	public String getWorkName() {
		return workName;
	}

	public String getJobName() {
		return jobName;
	}

	public String getJobPath() {
		return jobPath;
	}

	public String getJobPackageName() {
		return jobPackageName;
	}
	
	public String getType() {
		return type;
	}
	
	public String getJobId() {
		return jobId;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 *  주의 : 본 method는 x.compareTo(y)==0) == (x.equals(y))를 만족하지 않는다.
	 */
	public int compareTo(JobInfo o) {
		if (this.type.equals(TYPE_JOB)) {
			if (!this.workName.equals(o.workName))
				return this.workName.compareTo(o.workName);
			if (!this.jobPackageName.equals(o.jobPackageName))
				return this.jobPackageName.compareTo(o.jobPackageName);
			return this.jobName.compareTo(o.jobName);
		} else if (this.type.equals(TYPE_PACKAGE)) {
			if (!this.workName.equals(o.workName))
				return this.workName.compareTo(o.workName);
			return this.jobPackageName.compareTo(o.jobPackageName);
		} else if (this.type.equals(TYPE_CATEGORY)) {
			return this.workName.compareTo(o.workName);
		}
		return this.jobName.compareTo(o.jobName);
	}
}
