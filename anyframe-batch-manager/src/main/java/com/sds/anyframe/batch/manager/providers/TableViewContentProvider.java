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
import java.util.List;

import org.eclipse.jface.viewers.Viewer;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.service.IJobMonitor;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.view.JobMonitorView.COLUMNS;
import com.sds.anyframe.batch.manager.view.support.ITreeTableContentProvider;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class TableViewContentProvider implements ITreeTableContentProvider {

	private IJobMonitor jobMonitor;

	public TableViewContentProvider() {
	}

	public TableViewContentProvider(IJobMonitor jobMonitor) {
		this.jobMonitor = jobMonitor;
	}

	public Object[] getElements(Object obj) {
		return ((List<Job>) obj).toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object obj, Object obj1) {

	}

	public Object[] getChildren(Object arg0) {
		Job parent = (Job) arg0;

		List<Job> childrens = parent.getChildrens();
		if(childrens != null && childrens.size() > 0)
			return childrens.toArray();
		
		try {
			List<Job> steps = jobMonitor.getStepsInSelectedJob(parent);
			parent.setChildren(steps);
			return steps.toArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public Object getParent(Object arg0) {
		return null;
	}

	public boolean hasChildren(Object arg0) {
		Job job = (Job) arg0;
		if (job.getPid() == 0)
			return false;
		else
			return true;
	}

	@Override
	public Object getColumnValue(Object element, int columnIndex) {
		Job job = (Job) element;
		if (job == null)
			return null;

		switch (COLUMNS.values()[columnIndex]) {
		case SHOW_STEPS:
			return "";
		case PID:
			return job.getPid();
		case JOB_ID:
			return job.getJobId();
		case RESOURCE_FILES:
			if (job.getResources() != null && job.getResources().size() > 0) {
				StringBuilder files = new StringBuilder();
				
				for(Resource resource: job.getResources()) {
					files.append(resource.getIoType().name()).append("=").append(resource.getResourceName()).append(" ");
				}
				
				return files.toString();
			}
			break;
		case LOG_FILES:
			if (job.getLogFiles() != null)
				return job.getLogFiles();
			break;
		case CREATED_TIME:
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getCreatedDate());
		case LAST_UPDATED_TIME:
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getLastUpdated());
		case SERVER_IP:
			return job.getIp();
		case JOB_SEQ:
			return String.valueOf(job.getJobSeq());
		case ELAPSED_TIME:
			return BatchUtil.getElapsedTime(job.getCreatedDate(), job
					.getLastUpdated());
		case AVERAGE_CPU_USAGE:
			return Integer.toString((int) (job.getPerformance()
					.getTotalCpuUsage() * 100.0));
		case ACTIVE_THREAD_COUNT:
			return Integer.toString(job.getPerformance()
					.getActiveThreadCount());
		case FREE_MEMORY:
			return Long
			.toString(BatchUtil.byteToMega(job.getPerformance().getFreeMemory()));
		case TOTAL_MEMORY:
			return Long
			.toString(BatchUtil.byteToMega(job.getPerformance().getTotalMemory()));
		default:
			break;
		}
		return null;
	}

}
