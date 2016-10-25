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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.sds.anyframe.batch.agent.model.ColumnInfo;
import com.sds.anyframe.batch.agent.model.TableInfo;
import com.sds.anyframe.batch.manager.providers.DBColumnInfoLabelProvider;
import com.sds.anyframe.batch.manager.providers.DBSelectDialogTableViewerContentProider;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ColumnListPanel {

	private Table table;
	private TableViewer tableViewer;
	private final List<ColumnInfo> selectedColumnList = new ArrayList<ColumnInfo>();
	private Button descButton, ascButton;
	
	public Button getDescButton() {
		return descButton;
	}

	public ColumnListPanel() {
	}
	
	public void createTable(Composite parent) {
		
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridLayout layout = new GridLayout(1, false);
		layout.numColumns = 2;
		layout.verticalSpacing = 2;
		layout.marginWidth = 1;
		layout.marginHeight = 1;

		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lab = new Label(group, SWT.NULL);
		lab.setText("Column Information");
		Label blank = new Label(group, SWT.NULL);
		blank.setText("");
		
		table = new Table(group, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setFont(new Font(null, "", 9, SWT.NORMAL));

		TableColumn chkBox = new TableColumn(table, SWT.None);
		chkBox.setText("");
		chkBox.setWidth(30);

		TableColumn name = new TableColumn(table, SWT.CENTER);
		name.setText("Column Name");
		name.setWidth(150);
		
		TableColumn type = new TableColumn(table, SWT.CENTER);
		type.setText("Type");
		type.setWidth(80);

		TableColumn length = new TableColumn(table, SWT.CENTER);
		length.setText("Length");
		length.setWidth(60);

		TableColumn primaryKey = new TableColumn(table, SWT.CENTER);
		primaryKey.setText("PK");
		primaryKey.setWidth(60);
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new DBSelectDialogTableViewerContentProider());
		
		TableViewerCellModifier modifier = new TableViewerCellModifier();
		tableViewer.setCellModifier(modifier);
		tableViewer.setColumnProperties(new String[] { "", "Column Name", "Type", "Length", "PK" });
		tableViewer.setCellEditors(new CellEditor[] { null, null, null, null, null });
		
		tableViewer.setLabelProvider(new DBColumnInfoLabelProvider(false));
		Label warning = new Label(group, SWT.NULL);
		warning.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		warning.setText("\nSelect the column(s) to sort. \n\nIt takes several seconds \nduring load all the data in this table.");
		
		Group subGroup = new Group(group, SWT.RIGHT);
		GridLayout sublayout = new GridLayout(1, false);
		sublayout.numColumns = 3;
		sublayout.verticalSpacing = 1;
		sublayout.marginWidth = 1;
		sublayout.marginHeight = 1;

		subGroup.setLayout(sublayout);
		subGroup.setLayoutData(new GridData(GridData.FILL));
		
		Label label = new Label(subGroup, SWT.NULL);
		label.setText("Sort : ");
		
		descButton = new Button(subGroup, SWT.RADIO);
		descButton.setText("desc");
		descButton.setSelection(true);
		
		ascButton = new Button(subGroup, SWT.RADIO);
		ascButton.setText("asc");
		
	}

	public TableInfo displayColumnList(TableInfo tableInfo) {
		tableViewer.refresh();
		tableViewer.setInput(tableInfo.getColumns());
		return tableInfo;
	}

	public List<ColumnInfo> getSelectedColumnList() {
		//init selectedColumnList
		for (ColumnInfo col : selectedColumnList) {
			col.setSelected(false);
		}
		selectedColumnList.clear();
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		Iterator iterator=selection.iterator();
		while(iterator.hasNext()){
			ColumnInfo column = (ColumnInfo) iterator.next();
			column.setSelected(true);
			selectedColumnList.add(column);
		}
		return selectedColumnList;
	}
	
	private class TableViewerCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {

			if (property.equalsIgnoreCase("")) {
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			ColumnInfo tableInfo = null;
			if (element instanceof ColumnInfo)
				tableInfo = (ColumnInfo) element;

			if (property.equalsIgnoreCase("")) {
				if (tableInfo.isSelected()) {
					return true;
				} else {
					return false;
				}
			}
			return "";
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			ColumnInfo tableData = (ColumnInfo) element;
			if (property.equalsIgnoreCase("")) {
				tableData.setSelected(((Boolean) value).booleanValue());
			} 
			tableViewer.update(tableData, null);
			tableViewer.refresh();
		}
	}

}
