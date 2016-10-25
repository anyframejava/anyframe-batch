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

package com.sds.anyframe.batch.manager.property;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.core.AnyframePropertyHandler;
import com.sds.anyframe.batch.manager.core.ProjectElementTreeSelectionDialog;
import com.sds.anyframe.batch.manager.core.StringUtil;


/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchManagerPropertyPage extends PropertyPage {
	private Text templatePathText;

	private Combo pjtEncodingCombo;
	
	public BatchManagerPropertyPage() {
		noDefaultAndApplyButton();
	}

	@Override
	public boolean performOk() {
		saveProperties();
		return super.performOk();
	}

	private void saveProperties() {
		AnyframePropertyHandler prop = new AnyframePropertyHandler(getProjectPath());

		prop.put(BatchConstants.BATCH_TEMPLATE_PATH, templatePathText.getText());
		
		prop.put(BatchConstants.BATCH_PROJECT_CHARACTERSET, pjtEncodingCombo.getText());
		
		prop.saveProperties();
	}

	private String getProjectPath() {
		IProject selected = (IProject) getElement().getAdapter(IProject.class);
		return selected.getLocation().toString();
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
		AnyframePropertyHandler prop = new AnyframePropertyHandler(getProjectPath());

		templatePathText.setText(StringUtil.null2str(prop.getProperty(BatchConstants.BATCH_TEMPLATE_PATH)));
		
		pjtEncodingCombo.setText(StringUtil.null2str(prop.getProperty(BatchConstants.BATCH_PROJECT_CHARACTERSET)));
	}

	private void createTextField(Composite parent) {

		Composite tableComposite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		tableComposite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 530;
		tableComposite.setLayoutData(gridData);

		Group templatesGroup = new Group(tableComposite, SWT.NULL);
		templatesGroup.setText("Templates");
		
		GridLayout templatesLayout = new GridLayout(3, false);
		templatesLayout.marginHeight = 1;
		templatesLayout.marginWidth = 1;
		templatesGroup.setLayout(templatesLayout);
		
		GridData templatesData = new GridData(GridData.FILL_HORIZONTAL);
		templatesData.widthHint = 520;
		templatesGroup.setLayoutData(templatesData);
		
		Label templatesChooseLabel = new Label(templatesGroup, SWT.NULL);
		templatesChooseLabel.setText("Choose the template path for velocity templates: ");

		templatePathText = new Text(templatesGroup, SWT.LEFT | SWT.BORDER);
		templatePathText.setText("");

		GridData templatePathTextData = new GridData(SWT.NULL);
		templatePathTextData.widthHint = 150;
		templatePathText.setLayoutData(templatePathTextData);
		
		Button daoTemplateButton = new Button(templatesGroup, SWT.BUTTON1);
		daoTemplateButton.setText("Browse");

		daoTemplateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String voTemplatePath = openFolderDialog("");
				if(voTemplatePath == null)
					return;
				templatePathText.setText(StringUtil.null2str(voTemplatePath));

			}
		});
		
		// Text Group
		Group textGroup = new Group(tableComposite, SWT.NULL);
		textGroup.setText("Text Encoding for Source Generation");
		
		GridLayout textLayout = new GridLayout(2, false);
		textLayout.marginHeight = 1;
		textLayout.marginWidth = 1;
		textGroup.setLayout(textLayout);
		
		GridData textData = new GridData(SWT.NULL);
		textData.widthHint = 520;
		textGroup.setLayoutData(textData);

		Label textChooseLabel = new Label(textGroup, SWT.NULL);
		textChooseLabel.setText("Encoding Type : ");
		
		GridData  textLabelData = new GridData(SWT.NULL);
		textLabelData.widthHint = 100;
		textChooseLabel.setLayoutData(textLabelData);
		
		pjtEncodingCombo = new Combo(textGroup, SWT.SIMPLE | SWT.DROP_DOWN | SWT.READ_ONLY | SWT.CENTER);
		pjtEncodingCombo.setItems(new String[]{"UTF-8", "MS949", "ISO8859-1", "EUC-KR"});
		
	}
	
	public String openFolderDialog(String filtered) {
		IProject project = (IProject) getElement().getAdapter(IProject.class);
		ProjectElementTreeSelectionDialog dialog = new ProjectElementTreeSelectionDialog(project);
		return dialog.openFolderDialog(filtered);
	}

}
