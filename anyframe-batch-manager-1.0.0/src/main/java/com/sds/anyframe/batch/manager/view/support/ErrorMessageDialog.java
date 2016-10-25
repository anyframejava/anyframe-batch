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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.sds.anyframe.batch.manager.utils.IconImageUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ErrorMessageDialog extends Dialog {
	private String message;

	private String jobExecutionId;

	public ErrorMessageDialog(Shell parent) {
		super(parent);
	}

	public void setJobExecutionId(String jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void open() {
		Shell parent = getParent();
		Shell dialog = new Shell(parent, SWT.RESIZE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(800, 400);
		if (jobExecutionId != null) {
			dialog.setText("Error Message [Job Execution Id :" + jobExecutionId + "]");
		}
		else {
			dialog.setText("Error Message");
		}
		dialog.setImage(IconImageUtil.getIconImage("error.gif"));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 400;
		dialog.setLayout(gridLayout);
		Display display = parent.getDisplay();
		Text errorText = new Text(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		errorText.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		errorText.setText(message);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		errorText.setLayoutData(gridData);
		errorText.setEditable(false);
		message = "";
		dialog.open();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
