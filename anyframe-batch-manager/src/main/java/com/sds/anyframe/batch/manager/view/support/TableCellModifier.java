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

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Item;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.manager.view.JobMonitorView.COLUMNS;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class TableCellModifier implements ICellModifier {

	private final TreeViewer treeViewerForJobDetail;

	public TableCellModifier(TreeViewer treeViewerForJobDetail) {
		this.treeViewerForJobDetail = treeViewerForJobDetail;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		Job job = (Job)element;
		
		if(property.equals(COLUMNS.PID.getTitle())) {
			if(job.getParent() == null)
				return Long.toString(job.getPid());
			else
				return "";
		}
		else if(property.equals(COLUMNS.JOB_ID.getTitle())) {
			if(job.getParent() == null)
				return job.getJobId();
			else
				return job.getCurrentStepId();
		}
		else if(property.equals(COLUMNS.CREATED_TIME.getTitle())) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getCreatedDate());
		}
		else if(property.equals(COLUMNS.LAST_UPDATED_TIME.getTitle())) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getLastUpdated());
		}
		else if(property.equals(COLUMNS.RESOURCE_FILES.getTitle())) {
			if(job.getResources() != null)
				return job.getResources().toString();
			else
				return "";
		}
		else if(property.equals(COLUMNS.LOG_FILES.getTitle()))
			return StringUtils.defaultIfEmpty(job.getLogFiles(),"");
		else if(property.equals(COLUMNS.JOB_SEQ.getTitle()))
			return Long.toString(job.getJobSeq());
		else if(property.equals(COLUMNS.SERVER_IP.getTitle()))
			return job.getIp();
		return "";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		Job job = (Job) element;
		treeViewerForJobDetail.update(job, null);
		treeViewerForJobDetail.refresh();
	}

}
