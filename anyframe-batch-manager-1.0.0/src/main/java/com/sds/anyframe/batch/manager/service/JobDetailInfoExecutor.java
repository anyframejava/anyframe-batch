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

package com.sds.anyframe.batch.manager.service;

import org.eclipse.swt.widgets.Display;

import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobDetailInfoExecutor implements Runnable {

	private JobDetailInfoDialog jobDetailInfoDialog;
	private Display display;

	public JobDetailInfoExecutor(Display display,
			JobDetailInfoDialog jobDetailInfoDialog) {
		this.display = display;
		this.jobDetailInfoDialog = jobDetailInfoDialog;
	}

	@Override
	public void run() {
		display.asyncExec(new Runnable() {
			public void run() {
				jobDetailInfoDialog.refresh();
			}
		});
	}
}
