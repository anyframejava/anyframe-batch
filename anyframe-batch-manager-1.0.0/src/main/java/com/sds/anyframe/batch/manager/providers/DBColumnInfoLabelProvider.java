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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.anyframe.batch.agent.model.ColumnInfo;


/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class DBColumnInfoLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE = "unchecked";
	private static ImageRegistry imageRegistry = new ImageRegistry();
	private boolean isSelectedColumn;
	static {
		String iconPath = "icons/";
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(DBColumnInfoLabelProvider.class, iconPath + CHECKED_IMAGE + ".gif"));
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(DBColumnInfoLabelProvider.class, iconPath + UNCHECKED_IMAGE + ".gif"));
	}

	public DBColumnInfoLabelProvider(boolean isSelectedColumn) {
		this.isSelectedColumn = isSelectedColumn;
	}
	
	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		return imageRegistry.get(key);
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		ColumnInfo data = (ColumnInfo) element;
		switch (columnIndex) {
		case 0:
			if(isSelectedColumn)
				return getMappingImage(data.isSelected());
			else 
				return null;
		default:
			break;
		}
		return null;
	}
	
	public Image getMappingImage(boolean data){

		return getImage(data);
	}
	
	public String getColumnText(Object element, int columnIndex) {
		String result = "";

		ColumnInfo columnInfo = (ColumnInfo) element;
		switch (columnIndex) {
		case 0:
			break;
		case 1:
			if (columnInfo.getName() != null) {
				result = columnInfo.getName();
			}
			break;
		case 2:
			if (columnInfo.getTypeName() != null) {
				result = columnInfo.getTypeName();
			}
			break;
		case 3:
			result = Integer.toString(columnInfo.getDisplaySize());
			break;
		case 4:
			 if (columnInfo.isPk()) {
				 result = "true";
			 }
			break;
			
		default:
			break;
		}
		return result;
	}
}

