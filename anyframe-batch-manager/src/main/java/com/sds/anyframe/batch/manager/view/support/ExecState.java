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

package com.sds.anyframe.batch.manager.view.support;

import java.util.ArrayList;
import java.util.List;

import com.sds.anyframe.batch.manager.model.Parameter;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public enum ExecState {

	Job {
		public String getText(String stepName) {
			return "Launch a job";
		};
		
		public List<String> launchArgs(String jobPath, String stepId,
				List<Parameter> parameters) {
			List<String> list = new ArrayList<String>();
			list.add(jobPath);
			addParameters(list, parameters);
			return list;
		}
	},
	SingleStep {
		public String getText(String stepName) {
			return "Step - " + stepName;
		}

		public List<String> launchArgs(String jobPath, String stepId,
				List<Parameter> parameters) {
			List<String> list = new ArrayList<String>();
			list.add(jobPath);
			list.add("step=" + stepId);
			list.add("go_ahead=false");
			addParameters(list, parameters);
			return list;
		}
	},
	FromThisStep {
		public String getText(String stepName) {
			return "Step - " + stepName;
		}
		
		public List<String> launchArgs(String jobPath, String stepId,
				List<Parameter> parameters) {
			List<String> list = new ArrayList<String>();
			list.add(jobPath);
			list.add("step=" + stepId);
			list.add("go_ahead=true");
			addParameters(list, parameters);
			return list;
		}
	};

	public abstract String getText(String stepName);

	public abstract List<String> launchArgs(String jobPath, String stepId,
			List<Parameter> parameters);

	public void addParameters(List<String> list, List<Parameter> parameters) {
		for (Parameter param: parameters) {
			list.add(param.getName() + "=" + param.getValue());
		}
	}
}
