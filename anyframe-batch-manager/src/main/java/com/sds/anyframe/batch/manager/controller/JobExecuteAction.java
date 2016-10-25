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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.service.JobLauncher;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.ParameterDialog;
import com.sds.anyframe.batch.manager.model.Parameter;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.JobExplorerView;
import com.sds.anyframe.batch.manager.view.ServerInfo;
import com.sds.anyframe.batch.manager.view.support.ExecState;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobExecuteAction extends Action {
	Log log = LogFactory.getLog(JobExecuteAction.class);

	private final String ip;
	private final String jobPath;
	private final String stepId;
	private final String stepName;
	private final ExecState execState;

	private boolean withParams;
	
	@Override
	public String getText() {
		String stepText = "id:" + stepId + ", name:" +stepName;
		if(withParams)
			return execState.getText(stepText) + " with Dialog";
		return execState.getText(stepText);
	}
	
	public JobExecuteAction(ServerInfo serverInfo, String jobPath, String stepId, String stepName, ExecState state) {
		this(serverInfo, jobPath, stepId, stepName, state, false);
	}
	
	public JobExecuteAction(ServerInfo serverInfo, String jobPath, String stepId, String stepName, ExecState state, boolean withParams) {
		this.ip = serverInfo.getAddress();
		this.jobPath = jobPath;
		this.stepId = stepId;
		this.stepName = stepName;
		this.execState = state;
		this.withParams = withParams;
	}
	
	@Override
	public void run() {
		
		List<Parameter> parameters = null;
	
		if (withParams) {
			ParameterDialog parameterDialog = new ParameterDialog(PlatformUI
					.getWorkbench().getDisplay().getActiveShell());
			if (parameterDialog.open() == IDialogConstants.CANCEL_ID)
				return;

		}
		
		parameters = BatchUtil.loadParameters();
		
		JobLauncher jobLauncher = null;
		try {
			jobLauncher = (JobLauncher)ProxyHelper.getProxyInterface(ip, "jobLauncher", JobLauncher.class.getName());
		}
		catch (Exception e1) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e1), "Batch Manager");
		}
		
		List<String> args = execState.launchArgs(jobPath, stepId, parameters);
				
		String executeCommand = JobExplorerView.getPolicy().getCommand() + " " + StringUtils.join(args, " ");
		try {
			//check if this command is right with parameters(environment variables)
			if(!MessageDialog.openConfirm(new Shell(), "Batch Manager", "Do you want to execute this command? \n "+executeCommand)){
				return;
			}

			String[] cmds = args.toArray(new String[0]);
			jobLauncher.launch(cmds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			MessageDialog.openInformation(new Shell(), "Batch Manager", "Failed to launch job : "+e.getMessage());
			return;
		}
		//show status bar message
		IViewPart viewPart = BatchActivator.getDefault().getActiveWorkbenchPage().findView(JobExplorerView.ID);
		viewPart.setFocus();
		IStatusLineManager statusLineManager = viewPart.getViewSite().getActionBars().getStatusLineManager();
		statusLineManager.setMessage(IconImageUtil.getIconImage("status_yellow.gif"), "Execution command : "+ executeCommand);
	}
}
