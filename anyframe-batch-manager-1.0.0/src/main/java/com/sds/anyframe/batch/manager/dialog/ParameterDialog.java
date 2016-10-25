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

package com.sds.anyframe.batch.manager.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.variables.ValueVariable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.preferences.DebugPreferencesMessages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.model.Parameter;
import com.sds.anyframe.batch.manager.utils.BatchUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ParameterDialog extends Dialog {
	private Composite dialogArea;
	private static final String shelName = "Input your parameters";
	private Table table;
	private TableViewer tableViewer;
	public List<Parameter> parameters = new ArrayList<Parameter>();
	public List<Parameter> previousParams = new ArrayList<Parameter>();
	private Button addButton;
	private Button deleteButton;
		
	public enum ENUM_PARAMETER {
		NAME("Name"), VALUE("Value");
		
		private String title;
		
		ENUM_PARAMETER(String title) {
			this.title = title;
		}
		public String getTitle() {
			return title;
		}
	}
	
	public ParameterDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | getShellStyle());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		Composite rightPanel = new Composite(parent, SWT.CENTER);
		rightPanel.setLayout(new GridLayout(1, false));
		createButton(rightPanel, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		createButton(rightPanel, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setSize(450, 300);
		super.configureShell(shell);
		shell.setText(shelName);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		dialogArea.setLayout(layout);

		Composite composite = new Composite(dialogArea, 0);
		layout = new GridLayout();
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		createTable(composite);
		createButtons(composite);
		
		return dialogArea;
	}

	
	private void createButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.RIGHT);
		GridLayout glayout = new GridLayout();
		glayout.marginHeight = 0;
		glayout.marginWidth = 0;
		glayout.numColumns = 1;
		GridData gdata = new GridData(2);
		buttonComposite.setLayout(glayout);
		buttonComposite.setLayoutData(gdata);

		addButton = SWTFactory.createPushButton(buttonComposite, "Add", null);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
				parameters.add(new Parameter("key", "value"));
				tableViewer.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		
		
		deleteButton = SWTFactory.createPushButton(buttonComposite, DebugPreferencesMessages.SimpleVariablePreferencePage_9, null);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				if(selection == null || selection.length == 0)
					return;
				
				parameters.remove(selection[0].getData());
				tableViewer.refresh();
			}

		});

	
	}

	private void createTable(final Composite parent) {
		Composite tableComposite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 150;
		gridData.widthHint = 320;
		
		tableComposite.setLayout(layout);
		tableComposite.setLayoutData(gridData);

		table = new Table(tableComposite, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER
				| SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 150;
		gridData.widthHint = 320;
		table.setLayoutData(gridData);

		try {
			// first column that display line numbers
			TableColumn keyColumn = new TableColumn(table, SWT.NONE);
			keyColumn.setText(ENUM_PARAMETER.NAME.getTitle());
			keyColumn.setWidth(100);
			keyColumn.setAlignment(SWT.LEFT);
	
			TableColumn valueColumn = new TableColumn(table, SWT.NONE);
			valueColumn.setText(ENUM_PARAMETER.VALUE.getTitle());
			valueColumn.setWidth(200);
			valueColumn.setAlignment(SWT.LEFT);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tableViewer = new TableViewer(table);
		tableViewer.setColumnProperties(new String[] {ENUM_PARAMETER.NAME.getTitle(), ENUM_PARAMETER.VALUE.getTitle()});

		tableViewer.setCellModifier(new TableCellModifier());
		tableViewer.setCellEditors(new CellEditor[] { 			
				new TextCellEditor(tableViewer.getTable()), 
				new TextCellEditor(tableViewer.getTable())
		});

		
		tableViewer.setContentProvider(new SimpleVariableContentProvider());
		tableViewer.setLabelProvider(new SimpleVariableLabelProvider());
		loadParameters();
		tableViewer.setInput(parameters);
	}

	private void loadParameters() {
		parameters = BatchUtil.loadParameters();
		
		for(Parameter param: parameters) {
			previousParams.add(new Parameter(param.getName(), param.getValue()));
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			cancelPressed();
		} else {
			
			saveParameters();

			okPressed();
		}
	}

	private void saveParameters() {
		List<IValueVariable> removed = new ArrayList<IValueVariable>();
		List<IValueVariable> added = new ArrayList<IValueVariable>();
		
		IStringVariableManager manager = getVariableManager();
		
		for (Parameter parameter : previousParams) {
			
			removed.add(new ValueVariable(BatchConstants.PREF_USER_PARAMETER+"."+ parameter.getName(), "", false, parameter.getValue()));
		}
	
		manager.removeVariables((IValueVariable[]) removed
				.toArray(new IValueVariable[removed.size()]));
	
		for (Parameter parameter : parameters) {
			IValueVariable vv = manager.newValueVariable(
					BatchConstants.PREF_USER_PARAMETER + "."
							+ parameter.getName(), "");
			vv.setValue(parameter.getValue());
			added.add(vv);
		}

		try {
			manager.addVariables((IValueVariable[]) added
					.toArray(new IValueVariable[added.size()]));
		} catch (CoreException e) {
			DebugUIPlugin
					.errorDialog(
							new Shell(),
							DebugPreferencesMessages.StringVariablePreferencePage_24,
							DebugPreferencesMessages.StringVariablePreferencePage_25,
							e.getStatus());
		}
	}

	private class SimpleVariableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return parameters.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class SimpleVariableLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Parameter) {
				Parameter parameter = (Parameter) element;
				
					switch (ENUM_PARAMETER.values()[columnIndex]) {
					case NAME:
						return parameter.getName();
					case VALUE:
						return parameter.getValue();
					}
			}
			return null;
		}
	}
	
		
	private class TableCellModifier implements ICellModifier {


			@Override
			public boolean canModify(Object element, String property) {
				return true;
			}

			@Override
			public Object getValue(Object element, String property) {
				Parameter parameter = (Parameter)element;
				
				if(property.equals(ENUM_PARAMETER.NAME.getTitle())) {
					return parameter.getName();
				}
				else
					return parameter.getValue();
			}

			@Override
			public void modify(Object element, String property, Object value) {
					Parameter parameter = (Parameter) ((TableItem)element).getData();
					
					if(property.equals(ENUM_PARAMETER.NAME.getTitle())) {
						parameter.setName(value.toString());
					}
					else
						parameter.setValue(value.toString());
					
					tableViewer.update(parameter, null);
					tableViewer.refresh();
			}
	}

	private IStringVariableManager getVariableManager() {
		return VariablesPlugin.getDefault().getStringVariableManager();
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
}
