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

package com.sds.anyframe.batch.manager.controller;

import java.util.Collections;
import java.util.List;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.manager.core.StringUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobTreeMaker {
	
	public static JobTreeNode convertJobTreeNode(List<JobInfo> jobInfoList) {
		
		JobTreeNode rootNode = new JobTreeNode(JobInfo.newCategory("root"));
		
		
		JobTreeNode node2 = null;

//		
		String jobPackageName = "";
		String jobName = "";
		
		if (jobInfoList == null || jobInfoList.size() == 0) {
			rootNode.addChild(new JobTreeNode(JobInfo.newCategory("No Job")));
			return rootNode;
		} else {
			
			Collections.sort(jobInfoList);
		}

		for (int i = 0; i < jobInfoList.size(); i++) {
			JobInfo jobInfo = jobInfoList.get(i);
			

			// PackageName
			if (!jobInfo.getJobPackageName().equals(jobPackageName)) {
				jobName = "";
				jobPackageName = jobInfo.getJobPackageName();
				node2 = new JobTreeNode(JobInfo.newPackage(jobInfo.getJobPath(), jobInfo.getWorkName(), jobInfo.getJobPackageName()));
				rootNode.addChild(node2);
			}

			// jobName
			if (!StringUtil.isEmptyOrNull(jobInfo.getJobName()) && !jobInfo.getJobName().equals(jobName)) {
				jobName = jobInfo.getJobName();
				if(node2 == null)
					rootNode.addChild(new JobTreeNode(jobInfo));
				else
					node2.addChild(new JobTreeNode(jobInfo));
			}
		}

		return rootNode;

	}
	
}
