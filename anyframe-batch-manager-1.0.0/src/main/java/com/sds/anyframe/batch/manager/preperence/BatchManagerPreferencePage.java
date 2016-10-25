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

package com.sds.anyframe.batch.manager.preperence;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.manager.BatchConstants;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchManagerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Text serverTemplatePath;

	public BatchManagerPreferencePage() {
		noDefaultAndApplyButton();
	}

	@Override
	public boolean performOk() {
		saveProperties();
		return super.performOk();
	}

	private void saveProperties() {
		PlatformUI.getPreferenceStore().setValue(BatchConstants.BATCH_MANAGER_PREFERENCE, serverTemplatePath.getText());
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		createTextField(composite);

		loadProperties();

		return composite;
	}

	private void loadProperties() {
		serverTemplatePath.setText(PlatformUI.getPreferenceStore().getString(BatchConstants.BATCH_MANAGER_PREFERENCE));
	}

	private void createTextField(Composite parent) {
		Composite tableComposite = new Composite(parent, SWT.NULL);

		// GridLayout : 여러 열과 행으로 구성된 격자 배치
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		tableComposite.setLayout(layout);

		Group connectionInfoGroup = new Group(tableComposite, SWT.NULL);
		connectionInfoGroup.setText("Batch Server configurations");

		GridLayout connectionInfoLayout = new GridLayout(3, false);
		connectionInfoLayout.marginHeight = 1;
		connectionInfoLayout.marginWidth = 1;
		connectionInfoGroup.setLayout(connectionInfoLayout);

		GridData connectionInfoData = new GridData(SWT.NULL);
		connectionInfoData.widthHint = 550;
		connectionInfoGroup.setLayoutData(connectionInfoData);

		Label driverLabel = new Label(connectionInfoGroup, SWT.NULL);
		driverLabel.setText("Server configuration file path : ");
		serverTemplatePath = new Text(connectionInfoGroup, SWT.LEFT | SWT.BORDER);
		serverTemplatePath.setText("");
		GridData driverTextData = new GridData(SWT.NULL);
		driverTextData.widthHint = 300;
		serverTemplatePath.setLayoutData(driverTextData);

		GridData driverSearchButtonData = new GridData(SWT.NULL);
		driverSearchButtonData.widthHint = 60;
		serverTemplatePath.setLayoutData(driverTextData);
		Button driverSearchButton = new Button(connectionInfoGroup, SWT.BUTTON1);
		driverSearchButton.setText("Browse");
		driverSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = openFileDialog("");
				if (path == null)
					return;
				serverTemplatePath.setText(path);

			}
		});
		driverSearchButton.setLayoutData(driverSearchButtonData);

	}

	public String openFileDialog(String filtered) {
		FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.xml" });
		dialog.setText("Select the template path");
		
		String selectedFileName = dialog.open();
		if (selectedFileName == null || selectedFileName.length() == 0)
			return null;
		
		return selectedFileName;
	}

	public void init(IWorkbench iworkbench) {
		
	}

}
