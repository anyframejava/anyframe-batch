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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;
import com.sds.anyframe.batch.manager.view.JobMonitorView.COLUMNS;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class TableTreeVieverLabelProvider extends TableViewLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		Job job = (Job) element;
		switch (COLUMNS.values()[columnIndex]) {
		case JOB_ID:
			if (job != null) {
				if (job.getJobStatus() == JobStatus.COMPLETED
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("complete.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.READY
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
							return IconImageUtil.getIconImage("ready.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.RUNNING
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						boolean weired = BatchUtil.isWeiredJob(job
								.getLastUpdated());
						if (weired)
							return IconImageUtil.getIconImage("weiredRun.gif");
						else
							return IconImageUtil.getIconImage("run.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.STOPPED
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("stop.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.FAILED
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("fail.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.WAITING
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("waiting.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.BLOCKING
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("blocking.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				} else if (job.getJobStatus() == JobStatus.GARBAGED
						|| job.getStepStatus() != null) {
					if (job.getParent() == null) {
						return IconImageUtil.getIconImage("garbaged.gif");
					} else {
						return IconImageUtil.getStepImage(job);
					}
				}
			}
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String result = null;
		if (element != null && element instanceof Job) {
			Job job = (Job) element;
			switch (COLUMNS.values()[columnIndex]) {
			case PID:
				result = job.getPid() == 0 ? "" : String.valueOf(job.getPid());
				break;
			case JOB_ID:
				if (job.getParent() == null) // Job
					result = StringUtils.defaultIfEmpty(job.getJobId(), "");
				else
					// Step
					result = StringUtils.defaultIfEmpty(job.getCurrentStepId(),
							"");
				break;
			case CREATED_TIME:
				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
						.getCreatedDate());
				break;
			case LAST_UPDATED_TIME:
				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
						.getLastUpdated());
				break;
			case AVERAGE_CPU_USAGE:
				result = Integer.toString((int) (job.getPerformance()
						.getTotalCpuUsage() * 100.0));
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
			case ELAPSED_TIME:
				return BatchUtil.getElapsedTime(job.getCreatedDate(),
						job.getLastUpdated());

			case RESOURCE_FILES:
				if (job.getResources() != null && job.getResources().size() > 0) {
					StringBuilder files = new StringBuilder();

					for (Resource resource : job.getResources()) {
						files.append(resource.getIoType().name()).append("=")
								.append(resource.getResourceName()).append(" ");
					}

					result = files.toString();
				}
				break;
			case LOG_FILES:
				if (job.getLogFiles() != null)
					result = job.getLogFiles();
				break;

			case SERVER_IP:
				if (job.getParent() == null)
					result = StringUtils.defaultIfEmpty(job.getIp(), "");
				break;
			case JOB_SEQ:
				if (job.getParent() == null)
					result = String.valueOf(job.getJobSeq());
				break;
			default:
				break;
			}
		}
		return result == null ? "" : result;
	}
}
