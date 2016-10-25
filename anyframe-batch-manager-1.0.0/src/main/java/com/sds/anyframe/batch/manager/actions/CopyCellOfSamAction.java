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

package com.sds.anyframe.batch.manager.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class CopyCellOfSamAction extends Action{

	private TableViewer tableViewer;

	public CopyCellOfSamAction(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public String getText() {
		return "Copy";
	}
	
	@Override
	public void run() {
		TableItem tableItem = tableViewer.getTable().getSelection()[0];
		
		String[] data = (String[])tableItem.getData();
		String result = arrayToString(data);
		System.out.println(result);
		
		TextTransfer transfer = TextTransfer.getInstance();
		Clipboard clip = new Clipboard(new Shell().getDisplay());

		clip.setContents(new Object[]{result}, new Transfer[] {transfer});
	}
	
	
	private static String arrayToString(String[] data){
		if(data==null)
			return null;
		 StringBuffer result = new StringBuffer();
		    if (data.length > 0) {
		        result.append(data[0]);
		        for (int i=1; i < data.length; i++) {
		            result.append(data[i]);
		        }
		    }
		    return result.toString();

		
	}
}
