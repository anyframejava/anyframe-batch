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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.anyframe.batch.agent.model.FileInfoVO;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class FileSelectDialogTableViewerLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	FileInfoVO data;
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		if(element instanceof FileInfoVO)
		     data = (FileInfoVO) element;
		switch (columnIndex) {
		case 0:
			result = data.getPath();
			break;
		case 1:
			result = data.getName();
			break;
		case 2:
			result = Long.toString(data.getSize());
			break;
		case 3:
			result = data.getCreatedDate().toString();
			break;
		case 4:
			result = data.getVoClass();
			break;
		default:
			break;
		}
		return result;
	}
	}


