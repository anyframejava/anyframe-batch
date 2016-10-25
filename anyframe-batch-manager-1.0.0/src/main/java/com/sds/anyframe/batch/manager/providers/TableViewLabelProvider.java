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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;



/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class TableViewLabelProvider  extends LabelProvider implements ITableLabelProvider {

	public TableViewLabelProvider(Object element) {
		fields = element.getClass().getDeclaredFields();
	}
	public TableViewLabelProvider() {
	}
	
	private Field[] fields;
	
	
	private int getColumnIndex(int columnIndex){
		return fields.length>columnIndex?columnIndex:fields.length;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		int idx = getColumnIndex(columnIndex);

		String fieldName = fields[idx].getName().substring(0, 1).toUpperCase()
				+ fields[idx].getName().substring(1,
						fields[idx].getName().length());
		try {
			Method method = element.getClass().getMethod("get" + fieldName, new Class[] {});
			return String.valueOf(method.invoke(element, new Object[] {}));
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
		return null;
	}
}
