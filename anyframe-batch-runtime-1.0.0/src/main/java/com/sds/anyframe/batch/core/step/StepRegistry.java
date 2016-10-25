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

package com.sds.anyframe.batch.core.step;

import java.util.HashMap;
import java.util.Map;

import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepRegistry {

	protected static StepRegistry REGISTRY = null;

	// Step Type
	private static final String DELETE = "delete";
	private static final String PARALLEL = "parallel";
	private static final String SHELL = "shell";
	private static final String JAVA = "java";

	private static final Map<String, Class<? extends AnyframeAbstractStep>> map = new HashMap<String, Class<? extends AnyframeAbstractStep>>();

	private StepRegistry() {
		map.put(JAVA, JavaStep.class);
		map.put(SHELL, ShellStep.class);
		map.put(PARALLEL, ParallelStep.class);
		map.put(DELETE, DeleteStep.class);
	}

	public static synchronized StepRegistry getInstance() {

		if (REGISTRY == null) {
			REGISTRY = new StepRegistry();
		}
		return REGISTRY;
	}

	public AnyframeAbstractStep newInstance(String stepType) throws Exception {
		Class<? extends AnyframeAbstractStep> stepClass = map.get(stepType);

		if (stepClass == null) {
			throw new BatchRuntimeException("Unkown step type : " + stepType);
		}

		return stepClass.newInstance();
	}
}
