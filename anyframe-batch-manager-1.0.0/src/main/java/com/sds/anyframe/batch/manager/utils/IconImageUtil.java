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

package com.sds.anyframe.batch.manager.utils;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.core.AFImageCache;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class IconImageUtil {
	private static final String ICONS_LOCATION = "icons/";
	private static final AFImageCache afImageCache = new AFImageCache();

	public static Image getIconImage(String iconFileName) {
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(BatchActivator.PLUGIN_ID,
						ICONS_LOCATION + iconFileName);
		return afImageCache.getImage(imageDescriptor);
	}

	// step 상태 image를 위해서 추가
	public static Image getStepImage(Job job) {
		if (job.getStepStatus() == StepStatus.COMPLETED) {
			return IconImageUtil.getIconImage("complete_step.gif");
		} else if (job.getStepStatus() == StepStatus.READY) {
			return IconImageUtil.getIconImage("ready_step.gif");
		} else if (job.getStepStatus() == StepStatus.RUNNING) {
			boolean weired = BatchUtil.isWeiredJob(job.getLastUpdated());
			if (weired)
				return IconImageUtil.getIconImage("weiredRun_step.gif");
			else
				return IconImageUtil.getIconImage("run_step.gif");
		} else if (job.getStepStatus() == StepStatus.STOPPED) {
			return IconImageUtil.getIconImage("stop_step.gif");
		} else if (job.getStepStatus() == StepStatus.FAILED) {
			return IconImageUtil.getIconImage("fail_step.gif");
		} else if (job.getStepStatus() == StepStatus.GARBAGED) {
			return IconImageUtil.getIconImage("garbaged_step.gif");
		} else
			return null;

	}
}
