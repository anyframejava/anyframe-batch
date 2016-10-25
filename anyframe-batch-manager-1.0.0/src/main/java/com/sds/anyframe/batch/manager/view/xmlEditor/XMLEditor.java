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

package com.sds.anyframe.batch.manager.view.xmlEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.service.EditorSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.controller.JobExecuteAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.JobExplorerView;
import com.sds.anyframe.batch.manager.view.ServerInfo;
import com.sds.anyframe.batch.manager.view.support.ExecState;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class XMLEditor extends TextEditor{

	public static final String ID = "com.sds.anyframe.batch.manager.view.xmlEditor.XMLEditor";
	private final ColorManager colorManager;
	private Button saveButton;
	private Button executeButton;
	Label jobFileNameLabel;
	private StringEditorInput stringEditorInput;
	private JobInfo jobInfo;
	private JobTreeNode jobTreeNode;
	private String jobName;
	private String jobFileName;

	public XMLEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
	    Composite baseComposite = new Composite(parent, SWT.FILL_WINDING);
	    baseComposite.setLayout(new GridLayout(1, false));
	    GridData layoutData = new GridData(GridData.FILL_BOTH);
	    baseComposite.setLayoutData(layoutData);
	  
	    Composite topControlComposite = new Composite(baseComposite, SWT.FILL_WINDING);
	    Composite editorComposite = new Composite(baseComposite, SWT.FILL_WINDING);
	          
	    topControlComposite.setLayout(new GridLayout(2, false));
	    GridData topControlLayoutData = new GridData(GridData.FILL_HORIZONTAL);
	    topControlLayoutData.horizontalAlignment = SWT.FILL;
	    topControlComposite.setLayoutData(topControlLayoutData);

	    GridData gData = new GridData();
	    gData.horizontalAlignment = SWT.LEFT;
	    
	    jobFileNameLabel = new Label(topControlComposite, SWT.NULL);
	    
		jobFileNameLabel.setText(jobInfo.getJobPath());
	    jobFileNameLabel.setLayoutData(gData);
	    
		Composite topButtonComposite = new Composite(topControlComposite, SWT.NULL); 
		topButtonComposite.setLayout(new GridLayout(2, false));
		topButtonComposite.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_END));
		
		GridData topButtonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		topButtonLayoutData.horizontalAlignment = SWT.RIGHT;
		topButtonComposite.setLayoutData(topButtonLayoutData);
	    
	    gData = new GridData();
	    gData.widthHint = 100;

		saveButton = new Button(topButtonComposite, SWT.PUSH);
	    saveButton.setText("Save As Temp");
	    saveButton.setLayoutData(gData);
	    saveButton.addSelectionListener(new SelectionAdapter() {
	        @Override
			public void widgetSelected(SelectionEvent e) {
	        	doSave(null);
	        }
	    });
	    
	    executeButton = new Button(topButtonComposite, SWT.PUSH);
	    executeButton.setText("Execute");
	    executeButton.setLayoutData(gData);
	    executeButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
			public void widgetSelected(SelectionEvent e) {
	    		String ip = stringEditorInput.getIp();
	    		String jobFileName = jobFileNameLabel.getText();
	    		String launchFileName = StringUtils.substringBetween(jobFileName, JobExplorerView.getPolicy().getFileSeparator()+JobExplorerView.getPolicy().getBuildPath()+JobExplorerView.getPolicy().getFileSeparator(), ".xml");
	    		new JobExecuteAction(new ServerInfo(ip), launchFileName, "", "", ExecState.Job, true).run();
	    	}
	    });
	    
	    if(!JobExplorerView.getPolicy().canRunJob()) {
	    	saveButton.setEnabled(false);
	    	executeButton.setEnabled(false);
	    }
	    
	    editorComposite.setLayout(new FillLayout());
	    editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
	    super.createPartControl(editorComposite);
	}
	
	
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		if(!saveAsTemp()) return;
		IDocumentProvider p = getDocumentProvider();
		if (p == null) {
			return;
		}
		updateState(getEditorInput());
		validateState(getEditorInput());
		performSave(false, progressMonitor);
		
		refreshJobTree();
	}
	
	private void refreshJobTree() {
		JobExplorerView jobExplorerView = null;
		try {
			jobExplorerView = (JobExplorerView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(JobExplorerView.ID);
			TreeViewer treeViewer = jobExplorerView.getTreeViewer();

	        JobTreeNode treeNode = jobTreeNode;
			JobTreeNode parentTreeNode = treeNode.getParent();
			int indexOfOriginItem = parentTreeNode.getChildren().indexOf(treeNode);
			
			JobInfo originJobInfo = treeNode.getJobInfo();
			if (originJobInfo.getJobPath().endsWith(BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX + ".xml")) {
				return;
			}
			JobInfo newJobInfo = JobInfo.newJob(StringUtils.substringBeforeLast(originJobInfo.getJobPath(), ".xml")
					+ BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX + ".xml", originJobInfo.getWorkName(), originJobInfo
					.getJobPackageName(), originJobInfo.getJobName() + BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX,
					originJobInfo.getJobId());

			JobTreeNode newTreeNode = new JobTreeNode(newJobInfo);
			List<JobTreeNode> children = parentTreeNode.getChildren();
			List<String> jobNameList = new ArrayList<String>();
			for (JobTreeNode node : children) {
				jobNameList.add(node.getJobInfo().getJobName());
			}
			if(!jobNameList.contains(newTreeNode.getJobInfo().getJobName())){
				parentTreeNode.getChildren().add(indexOfOriginItem+1, newTreeNode);
			}
			newTreeNode.setParent(parentTreeNode);
			treeViewer.refresh();
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	private boolean saveAsTemp() {
		if(!isDirty()) {
			MessageDialog.openInformation(new Shell(), "Batch Manager", "No changes in this page.");
			return false;
		}
		String contents = getDocumentProvider().getDocument(stringEditorInput).get();
		
		String ip = stringEditorInput.getIp();
		
		EditorSupport edit = null;
		try {
			edit = (EditorSupport) ProxyHelper.getProxyInterface(ip, "jobEdit", EditorSupport.class.getName());
		}
		catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		
		if(!jobFileName.endsWith(BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX + ".xml")) {
			jobFileName = jobInfo.getJobPath().replace(".xml", BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX + ".xml");
			jobName = jobName + BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX;
		}
		if(edit.isExist(jobFileName)) {
			if(!MessageDialog.openConfirm(new Shell(), "Batch Manager", jobName + " is already exist.\nWanna OVERWRITE it?")){
				return false;
			}
		}
		
		jobFileNameLabel.setText(jobFileName);
		jobFileNameLabel.pack();
		
		stringEditorInput.setName(jobName);
		setPartName(jobName);
		
		try {
			edit.saveAsTemp(jobFileName, Arrays.asList(contents.toString().split("\r\n")));
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean isSaveAsAllowed() {	// 여기서는 저장이 곧 Save As이다!
		return false;
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if(!(input instanceof StringEditorInput)) {
			throw new PartInitException("Invalid input: Do not support local file");
		}
		super.init(site, input);
		stringEditorInput = (StringEditorInput)input;
		jobName = stringEditorInput.getName();
		jobInfo = stringEditorInput.getJobInfo();
		jobTreeNode = stringEditorInput.getJobTreeNode();
		jobFileName = jobInfo.getJobPath();
	}

/* blocked by bonobono, 2009/07/24, 사용은 StringEditorInput으로 한번에 처리 
    public void setContent(String content) {
        if (getEditorInput() instanceof StringEditorInput) {   
            StringEditorInput editorInput = (StringEditorInput) getEditorInput();   
            editorInput.setContent(content);   
            setInput(editorInput);   
        }   
    }  
 */
	
}
