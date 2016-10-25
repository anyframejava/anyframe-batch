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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.view.support.ErrorMessageDialog;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ExecutionErrorLogAction extends Action {
	public static final int BYTES_PER_PAGE = 1000000; //1mb

	private String message;


	@Override
	public String getText() {
		return "View error log";
	}

	public ExecutionErrorLogAction() {
	}

	public ExecutionErrorLogAction(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		try {
			ErrorMessageDialog emd = new ErrorMessageDialog(PlatformUI
					.getWorkbench().getDisplay().getActiveShell());
			emd.setMessage(message);
			emd.open();
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", AgentUtils
					.getStackTraceString(e), e);
		}
	}
}
