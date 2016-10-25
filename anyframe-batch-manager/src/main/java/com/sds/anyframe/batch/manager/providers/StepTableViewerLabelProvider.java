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

package com.sds.anyframe.batch.manager.providers;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog.StepTableCOLUMN;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class StepTableViewerLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		Job job = (Job) element;
		switch (StepTableCOLUMN.values()[columnIndex]) {
		case STEP_STATE:

			if (job.getStepStatus() == StepStatus.COMPLETED) {
				return IconImageUtil.getIconImage("complete_step.gif");
			} else if (job.getStepStatus() == StepStatus.READY) {
				boolean weired = BatchUtil.isWeiredJob(job.getLastUpdated());
				if (weired)
					return IconImageUtil.getIconImage("weiredRun_step.gif");
				else
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
			} else {
				return null;
			}
		default:
			break;
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = null;
		if (element != null && element instanceof Job) {
			Job job = (Job) element;
			switch (StepTableCOLUMN.values()[columnIndex]) {
			case STEP_ID:
				result = job.getCurrentStepId();
				break;
			case STEP_STATE:
				result = job.getStepStatus().toString();
				break;
			case CREATED_TIME:
				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
						.getCreatedDate());
				break;
			case LAST_UPDATED_TIME:
				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
						.getLastUpdated());
				break;
			case ELAPSED_TIME:
				return BatchUtil.getElapsedTime(job.getCreatedDate(),
						job.getLastUpdated());
			case AVERAGE_CPU_USAGE:
				result = Integer.toString((int) (job.getPerformance()
						.getTotalCpuUsage() * 100.0));
				break;
			case CURRENT_CPU_USAGE:
				result = Integer.toString((int) (job.getPerformance()
						.getCurrentCpuUsage() * 100.0));
				break;
			case ACTIVE_THREAD_COUNT:
				result = Integer.toString(job.getPerformance()
						.getActiveThreadCount());
				break;
			case FREE_MEMORY:
				result = Long.toString(BatchUtil.byteToMega(job
						.getPerformance().getFreeMemory()));
				break;
			case TOTAL_MEMORY:
				result = Long.toString(BatchUtil.byteToMega(job
						.getPerformance().getTotalMemory()));
				break;
			
			default:
				break;
			}
		}
		return result == null ? "" : result;
	}

}
