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

package com.sds.anyframe.batch.manager.model;

import java.util.ArrayList;
import java.util.List;

import com.sds.anyframe.batch.agent.model.JobInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobTreeNode{
	private JobInfo jobInfo;
	private List<JobTreeNode> children = new ArrayList<JobTreeNode>();
	private JobTreeNode parent;
	
	public JobTreeNode(JobInfo info){
		jobInfo = info;
	}
	public JobInfo getJobInfo() {
		return jobInfo;
	}
	public JobTreeNode getParent(){
		return parent;
	}
	public void setParent(JobTreeNode parent) {
		this.parent = parent;
	}
	public JobTreeNode addChild(JobTreeNode child){
		children.add(child);
		child.parent = this;
		return this;
	}
	public List<JobTreeNode> getChildren(){
		return children;
	}
	public boolean hasChildren(){
		return !children.isEmpty();
	}
	@Override
	public String toString(){
		return jobInfo.getJobName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jobInfo == null) ? 0 : jobInfo.hashCode());
		return result;
	}
	
}
