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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.preferences.DebugPreferencesMessages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.icu.text.MessageFormat;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.BatchPreferenceMultipleInputDialog;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class EnvironmentVariablesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage{

	private TableViewer variableTable;
	protected Button envAddButton;
	protected Button envEditButton;
	protected Button envRemoveButton;
	private List<VariableWrapper> fWorkingList = new ArrayList<VariableWrapper>();
	protected SimpleVariableContentProvider variableContentProvider;
	protected static final String BATCH_VARIABLE_LABEL = "Variable";
	protected static final String BATCH_VALUE_LABEL = "Value";
	private static final String DIALOG_NAME_FIELD = "Name:";
	private static final String DIALOG_VALUE_FIELD = "Value:";
	protected static final String batch_preference_key = "BatchPreferencePage:";
	protected static String tableColumnPropertiesBatch[] = { BATCH_VARIABLE_LABEL, BATCH_VALUE_LABEL};
	protected String tableColumnHeaders[];
	protected ColumnLayoutData tableColumnLayouts[] = { new ColumnWeightData(30), new ColumnWeightData(40) };
	protected IPreferenceStore doGetPreferenceStore(){
		return BatchActivator.getDefault().getPreferenceStore();
	}
	private class SimpleVariableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return fWorkingList.toArray();
		}

		public void addVariable(VariableWrapper variable) {
			fWorkingList.add(variable);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public List<VariableWrapper> getWorkingSetVariables() {
			return fWorkingList;
		}

		private SimpleVariableContentProvider() {
			fWorkingList = new ArrayList();
		}

		SimpleVariableContentProvider(SimpleVariableContentProvider simplevariablecontentprovider) {
			this();
		}
	}

	private class SimpleVariableLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof VariableWrapper) {
				VariableWrapper variable = (VariableWrapper) element;
					switch (columnIndex) {
					case 0: // '\0'
						StringBuffer name = new StringBuffer();
						name.append(variable.getName());
						if (variable.isReadOnly())
							name.append(DebugPreferencesMessages.StringVariablePreferencePage_26);
						return name.toString();
	
					case 1: // '\001'
						String value = variable.getValue();
						if (value == null)
							value = "";
						return value;
					}
			}
			return null;
		}

		public Color getForeground(Object element) {
			return null;
		}


		private SimpleVariableLabelProvider() {
		}

		public Color getBackground(Object arg0) {
			return null;
		}
	}

	class VariableFilter extends ViewerFilter {

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return !((VariableWrapper) element).isRemoved();
		}

		VariableFilter() {
		}
	}

	public class VariableWrapper {

		protected IValueVariable fVariable;
		protected String fNewName;
		protected String fNewValue;
		boolean fRemoved;
		boolean fAdded;

		public boolean isAdded() {
			return fAdded;
		}

		public String getName() {
			if (fNewName == null)
				return fVariable.getName();
			else
				return fNewName;
		}

		public void setName(String name) {
			fNewName = name;
		}

		public String getValue() {
			if (fNewValue == null)
				return fVariable.getValue();
			else
				return fNewValue;
		}

		public void setValue(String value) {
			fNewValue = value;
		}

		public boolean isChanged() {
			return !fAdded && !fRemoved	&& (fNewValue != null );
		}

		public boolean isReadOnly() {
			if (fVariable == null)
				return false;
			else
				return fVariable.isReadOnly();
		}

		public boolean isContributed() {
			if (fVariable == null)
				return false;
			else
				return fVariable.isContributed();
		}

		public IValueVariable getUnderlyingVariable() {
			return fVariable;
		}

		public boolean isRemoved() {
			return fRemoved;
		}

		public void setRemoved(boolean removed) {
			fRemoved = removed;
		}

		public VariableWrapper(String name, String value) {
			fNewName = null;
			fNewValue = null;
			fRemoved = false;
			fAdded = false;
			fNewName = name;
			fNewValue = value;
			fAdded = true;
		}
	}

	public EnvironmentVariablesPreferencePage() {
		variableContentProvider = new SimpleVariableContentProvider(null);
		tableColumnHeaders = (new String[] {"Variable",	"Value"});
		setDescription("Configure Environment Variables");
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		
	}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		org.eclipse.swt.graphics.Font font = parent.getFont();
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setFont(font);
		createTable(composite);
		createButtons(composite);
		return composite;
	}

	private void createTable(Composite parent) {
		org.eclipse.swt.graphics.Font font = parent.getFont();
		Composite tableComposite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		GridData gridData = new GridData(1808);
		gridData.heightHint = 150;
		gridData.widthHint = 400;
		tableComposite.setLayout(layout);
		tableComposite.setLayoutData(gridData);
		tableComposite.setFont(font);
		variableTable = new TableViewer(tableComposite, 68354);
		Table table = variableTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(font);
		gridData = new GridData(1808);
		variableTable.getControl().setLayoutData(gridData);
		variableTable.setContentProvider(variableContentProvider);
		variableTable.setColumnProperties(tableColumnPropertiesBatch);
		variableTable.addFilter(new VariableFilter());
		variableTable.setComparator(new ViewerComparator() {

			public int compare(Viewer iViewer, Object e1, Object e2) {
				if (e1 == null)
					return -1;
				if (e2 == null)
					return 1;
				else
					return ((VariableWrapper) e1).getName().compareToIgnoreCase(((VariableWrapper) e2).getName());
			}

		});
		variableTable.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						handleTableSelectionChanged(event);
					}

				});
		variableTable.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (!variableTable.getSelection().isEmpty())
					handleEditButtonPressed();
			}

		});
		variableTable.getTable().addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent event) {
				if (event.character == '\177' && event.stateMask == 0)
					handleRemoveButtonPressed();
			}

		});
		for (int i = 0; i < tableColumnHeaders.length; i++) {
			TableColumn tc = new TableColumn(table, 0, i);
			tc.setResizable(tableColumnLayouts[i].resizable);
			tc.setText(tableColumnHeaders[i]);
		}

		if (!restoreColumnWidths())
			restoreDefaultColumnWidths();
		variableTable.setInput(fWorkingList);
		variableTable.setLabelProvider(new SimpleVariableLabelProvider());
	}

	private void createButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, 0);
		GridLayout glayout = new GridLayout();
		glayout.marginHeight = 0;
		glayout.marginWidth = 0;
		glayout.numColumns = 1;
		GridData gdata = new GridData(2);
		buttonComposite.setLayout(glayout);
		buttonComposite.setLayoutData(gdata);
		buttonComposite.setFont(parent.getFont());
		envAddButton = SWTFactory.createPushButton(buttonComposite,	DebugPreferencesMessages.SimpleVariablePreferencePage_7, null);
		envAddButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				handleAddButtonPressed();
			}

		});
		envEditButton = SWTFactory.createPushButton(buttonComposite, DebugPreferencesMessages.SimpleVariablePreferencePage_8, null);
		envEditButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				handleEditButtonPressed();
			}

		});
		envEditButton.setEnabled(false);
		envRemoveButton = SWTFactory.createPushButton(buttonComposite, DebugPreferencesMessages.SimpleVariablePreferencePage_9, null);
		envRemoveButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				handleRemoveButtonPressed();
			}

		});
		envRemoveButton.setEnabled(false);
	}

	private void handleAddButtonPressed() {
		boolean done = false;
		String name = null;
		String value = null;
		while (!done) {
			BatchPreferenceMultipleInputDialog dialog = new BatchPreferenceMultipleInputDialog(
					getShell(),
					"New Environment Variable");
			dialog.addTextField(DIALOG_NAME_FIELD, name, false);
			dialog.addBrowseField(DIALOG_VALUE_FIELD, value, true);
			if (dialog.open() != 0) {
				done = true;
			} else {
				name = dialog.getStringValue(DIALOG_NAME_FIELD).trim();
				value = dialog.getStringValue(DIALOG_VALUE_FIELD);
				done = addVariable(name, value);
			}
		}
	}

	private boolean addVariable(String name, String value) {
		VariableWrapper newVariable = new VariableWrapper(name, value);
		for (VariableWrapper var : fWorkingList) {
			if(var.getName().equals(name)){
				MessageUtil.showMessage("This variable is already set.", "Batch Manager");
				return false;
			}
		}
		variableContentProvider.addVariable(newVariable);
		variableTable.refresh();
		return true;
	}
	private void handleEditButtonPressed() {
		IStructuredSelection selection = (IStructuredSelection) variableTable.getSelection();
		VariableWrapper variable = (VariableWrapper) selection.getFirstElement();
		if (variable == null || variable.isReadOnly())
			return;
		String value = variable.getValue();
		String name = variable.getName();
		BatchPreferenceMultipleInputDialog dialog = new BatchPreferenceMultipleInputDialog(
				getShell(),
				MessageFormat.format(DebugPreferencesMessages.SimpleVariablePreferencePage_14,	new String[] { name }));
		dialog.addBrowseField(DIALOG_VALUE_FIELD, value, true);
		if (dialog.open() == 0) {
			value = dialog.getStringValue(DIALOG_VALUE_FIELD);
			if (value != null)
				variable.setValue(value);
			variableTable.update(variable, null);
		}
	}

	@SuppressWarnings("restriction")
	private void handleRemoveButtonPressed() {
		IStructuredSelection selection = (IStructuredSelection) variableTable.getSelection();
		List variablesToRemove = selection.toList();
		StringBuffer contributedVariablesToRemove = new StringBuffer();
		for (Iterator iter = variablesToRemove.iterator(); iter.hasNext();) {
			VariableWrapper variable = (VariableWrapper) iter.next();
			if (variable.isContributed())
				contributedVariablesToRemove.append('\t').append(variable.getName()).append('\n');
		}

		if (contributedVariablesToRemove.length() > 0) {
			boolean remove = MessageDialog.openQuestion(
							getShell(),
							DebugPreferencesMessages.SimpleLaunchVariablePreferencePage_21,
							MessageFormat.format(
											DebugPreferencesMessages.SimpleLaunchVariablePreferencePage_22,
											new String[] { contributedVariablesToRemove
													.toString() }));
			if (!remove)
				return;
		}
		
		VariableWrapper variables[] = (VariableWrapper[]) variablesToRemove.toArray(new VariableWrapper[0]);
		for (int i = 0; i < variables.length; i++){
			variables[i].setRemoved(true);
		    variableTable.refresh();
		}
	}

	protected void handleTableSelectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		VariableWrapper variable = (VariableWrapper) selection.getFirstElement();
		if (variable == null || variable.isReadOnly()) {
			envEditButton.setEnabled(false);
			envRemoveButton.setEnabled(false);
		} else {
			envEditButton.setEnabled(selection.size() == 1);
			envRemoveButton.setEnabled(selection.size() > 0);
		}
	}

	protected void performDefaults() {
		variableTable.refresh();
		super.performDefaults();
	}

	public boolean performOk() {
		List<VariableWrapper> enteredValues = variableContentProvider.getWorkingSetVariables();
		
		for (VariableWrapper variable : enteredValues) {
			if(variable.isRemoved()){
				variable.setValue("");
				getPreferenceStore().setValue(variable.getName() , variable.getValue());
				continue;
			}
			getPreferenceStore().setValue(variable.getName() , variable.getValue());
		}

		saveColumnWidths();
		return super.performOk();

	}
	
	public void saveColumnWidths() {
		StringBuffer widthPreference = new StringBuffer();
		for (int i = 0; i < variableTable.getTable().getColumnCount(); i++) {
			widthPreference.append(variableTable.getTable().getColumn(i).getWidth());
			widthPreference.append(',');
		}

		if (widthPreference.length() > 0)
			DebugUIPlugin.getDefault().getPreferenceStore().setValue(batch_preference_key, widthPreference.toString());
	}

	private boolean restoreColumnWidths() {
		String columnWidthStrings[] = DebugUIPlugin.getDefault().getPreferenceStore().getString(batch_preference_key).split(",");
		int columnCount = variableTable.getTable().getColumnCount();
		if (columnWidthStrings.length != columnCount)
			return false;
		for (int i = 0; i < columnCount; i++)
			try {
				int columnWidth = Integer.parseInt(columnWidthStrings[i]);
				variableTable.getTable().getColumn(i).setWidth(columnWidth);
			} catch (NumberFormatException e) {
				DebugUIPlugin.log(new Throwable("Problem loading persisted column sizes for StringVariablePreferencesPage",	e));
			}
		return true;
	}

	private void restoreDefaultColumnWidths() {
		TableLayout layout = new TableLayout();
		for (int i = 0; i < tableColumnLayouts.length; i++)
			layout.addColumnData(tableColumnLayouts[i]);
		variableTable.getTable().setLayout(layout);
	}

	@Override
	public void init(IWorkbench workbench) {
		fWorkingList.clear();
		String[] names = BatchActivator.getDefault().getPluginPreferences().propertyNames();
		for (String name : names) {
			String value = getPreferenceStore().getString(name);
			VariableWrapper variable = new VariableWrapper(name, value);
			fWorkingList.add(variable);
		} 
	}
}
