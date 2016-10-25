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
import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ibm.icu.text.DecimalFormat;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.manager.UIConstants;
import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog;
import com.sds.anyframe.batch.manager.dialog.ResourceAndFile;
import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog.ResourceTableCOLUMN;
import com.sds.anyframe.batch.manager.utils.BatchUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ResourceTableViewerLabelProvider extends LabelProvider
		implements ITableLabelProvider {

	
	private JobDetailInfoDialog jobDetailInfoDialog;

	public ResourceTableViewerLabelProvider(
			JobDetailInfoDialog jobDetailInfoDialog) {
		this.jobDetailInfoDialog = jobDetailInfoDialog;
	}

	public Image getColumnImage(Object element, int columnIndex) {

		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = null;
		if (element != null && element instanceof ResourceAndFile) {
			ResourceAndFile randF = (ResourceAndFile) element;
			switch (ResourceTableCOLUMN.values()[columnIndex]) {
			
			case RESOURCE_TYPE:
				result = randF.getResource().getType().toString();
				break;
			case IO_TYPE:
				result = randF.getResource().getIoType().toString();
				break;
			case RESOURCE_NAME:
				String resourceName = randF.getResource().getResourceName();
				if(randF.getResource().getType() == ResourceType.DATABASE)
					return resourceName;
				
				if(randF.getResource().getType() == ResourceType.FILE && randF.getResource().getIoType() == ResourceIoType.WRITE && jobDetailInfoDialog.getLastStep().getStepStatus() != StepStatus.COMPLETED) {
					resourceName += UIConstants.TEMP_EXT;
				}
				result = resourceName;
				break;
			case STATUS:
				if(randF.getResource().getStatus() != null)
					result = randF.getResource().getStatus().toString();
				break;
			case SIZE:
			{
				DecimalFormat df = new DecimalFormat("#,###");
				result = df.format(BatchUtil.byteToKillo(randF.getFile().getSize()));
				break;
			}
			case MOD_DATE:
				if(randF.getResource().getType()==ResourceType.DATABASE){
					Date date = randF.getResource().getUpdateTime();
					if(date!=null)
					result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				}else{
				result = randF.getFile().getCreatedDate();
				}
				break;
			case TRANSACTION_COUNT:
				DecimalFormat df = new DecimalFormat("#,###");
				result = df.format(randF.getResource().getTransactedCount().get());
				break;	
				
			default:
				break;
			}
		}
		return result == null ? "" : result;
	}

}
