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

package com.sds.anyframe.batch.manager.controller;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.manager.utils.ExcelWriter;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ExportToExcelAction extends Action {

	private List<Job> jobs;

	@Override
	public String getText() {
		return "Export to excel file";
	}

	public ExportToExcelAction(List<Job> list) {
		this.jobs = list;
	}

	@Override
	public void run() {
		String fileToWrite = getFileToWrite();
		
		if(fileToWrite == null)
			return;
		
		writeToExcel(fileToWrite);
	}

	private void writeToExcel(String fileToWrite) {
		ExcelWriter excelWriter = new ExcelWriter(fileToWrite);
		excelWriter.write(jobs);
	}

	private String getFileToWrite() {
		FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.xls" }); //$NON-NLS-1$ //$NON-NLS-2$
		dialog.setText("Select the Excel to be exported ");
		dialog.setFileName("jobs");
		String currentSourceString = ".xls";
		int lastSeparatorIndex = currentSourceString
				.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1)
			dialog.setFilterPath(currentSourceString.substring(0,
					lastSeparatorIndex));
		String fileName = dialog.open();

		if (fileName == null)
			return null;

		if (!(fileName.endsWith(".xls")))
			fileName = fileName + ".xls";

		File file = new File(fileName);

		if (file.exists()) {
			boolean flag = MessageDialog
					.openConfirm(new Shell(), "Confirm",
							"File already exist \n\n If you want overwrite, click ok !");
			if (!flag)
				return null;
		}
		
		return fileName;
	}
}
