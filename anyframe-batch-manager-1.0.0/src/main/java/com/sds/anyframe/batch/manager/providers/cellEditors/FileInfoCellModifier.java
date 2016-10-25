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

package com.sds.anyframe.batch.manager.providers.cellEditors;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import com.sds.anyframe.batch.agent.model.FileInfoVO;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class FileInfoCellModifier implements ICellModifier {

	private TableViewer tableViewer;
	
	public FileInfoCellModifier(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public boolean canModify(Object element, String property) {
		FileInfoVO data = null;
		if (element instanceof FileInfoVO) {
			data = (FileInfoVO) element;
		}
		// "Path", "Name", "Size", "Mod Date", "VO Class"
		if (data != null && property.equalsIgnoreCase("VO Class")) {
			return true;
		}
		return false;
	}

	public Object getValue(Object element, String property) {
		FileInfoVO data = null;
		String result = "";
		if (element instanceof FileInfoVO) {
			data = (FileInfoVO) element;
		}

		// "Path", "Name", "Size", "Mod Date", "VO Class"
		if (property.equalsIgnoreCase("Path")) {
			if (data != null && data.getPath() != null) {
				result = data.getPath();
			}
		} else if (property.equalsIgnoreCase("Name")) {
			if (data != null && data.getName() != null) {
				result = data.getName();
			}
		} else if (property.equalsIgnoreCase("Size")) {
			if (data != null && Long.toString(data.getSize()) != null) {
				return Long.toString(data.getSize());
			}
			return 0;
		} else if (property.equalsIgnoreCase("Mod Date")) {
			if (data != null && data.getCreatedDate().toString() != null) {
				result = data.getCreatedDate().toString();
			}
		} else if (property.equalsIgnoreCase("VO Class")) {
			if (data != null && data.getVoClass() != null) {
				result = data.getVoClass();
			}
		}
		return (result == null) ? "" : result;
	}

	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		
		if (property.equalsIgnoreCase("VO Class")) {
			if (item.getData() instanceof FileInfoVO) {
				((FileInfoVO) item.getData()).setVoClass((String) value);
			}
			tableViewer.update(item.getData(), null);
		}
	}
}
