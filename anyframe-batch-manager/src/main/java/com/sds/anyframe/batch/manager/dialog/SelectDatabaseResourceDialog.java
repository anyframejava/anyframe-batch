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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.ColumnInfo;
import com.sds.anyframe.batch.agent.model.TableInfo;
import com.sds.anyframe.batch.agent.service.DBHandleSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.controller.SelectDatabaseResourceDialogAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.sorter.SelectDatabaseResourceSorter;
import com.sds.anyframe.batch.manager.providers.DBSelectDialogTableViewerContentProider;
import com.sds.anyframe.batch.manager.providers.DBSelectDialogTableViewerLabelProvider;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SelectDatabaseResourceDialog extends Dialog{
	private Composite dialogArea;
	public static Table table;
	private TableViewer tableViewer;
	private static final String shelName = "Select Resource";
	private Text tableNameText;
	private Combo serverListCombo, typeCombo, dsCombo;
	private ServerInfo serverInfo;
	public static final String[] columnHeaders = {"Table Name", "Type", "Table Description", "Data Source" };
	private final String[] types = new String[] {"ALL", "TABLE", "VIEW", "ALIAS", "SYNONYM" };
	private DBHandleSupport dBHandleSupport;
	private SelectDatabaseResourceDialogAction selectResourceDialogAction;
	private List<String> dataSourceList;
	private final List<TableInfo> tableInfoList = new ArrayList<TableInfo>();
	private final ColumnListPanel columnListPanel = new ColumnListPanel();
	public static final int ShowTop = 1;
	public static final int ShowBottom = 2;
	
	public SelectDatabaseResourceDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ShowTop, "Show Top",	false);
		createButton(parent, ShowBottom, "Show Bottom",	false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL,	false);
	}
	
	public SelectDatabaseResourceDialog(Shell activeShell, ServerInfo serverInfo) {
		super(activeShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(shelName);
		shell.setBounds(500, 200, 620, 600);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		dialogArea.setLayout(layout);
		
		createButton(dialogArea);
		createTableListPanel(dialogArea);
		columnListPanel.createTable(dialogArea);
		
		return dialogArea;
	}

	private void createButton(final Composite composite) {
		GridData gridData = new GridData(GridData.CENTER);
		Composite buttonComposite = new Composite(composite, SWT.NULL);
		buttonComposite.setLayout(new GridLayout(10, false));
		gridData = new GridData(SWT.NULL);
		buttonComposite.setLayoutData(gridData);
		
		Label lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("Target Server : ");
		gridData = new GridData(SWT.NULL);
		serverListCombo = new Combo(buttonComposite, SWT.READ_ONLY);
		serverListCombo.setLayoutData(gridData);

		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("     ");
		
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("Type : ");
		gridData = new GridData(SWT.NULL);
		gridData.horizontalSpan = 2;
		typeCombo = new Combo(buttonComposite, SWT.READ_ONLY);
		typeCombo.setLayoutData(gridData);
		
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("     ");
		
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("DataSource : ");
		gridData = new GridData(SWT.NULL);
		gridData.horizontalSpan = 2;
		dsCombo = new Combo(buttonComposite, SWT.READ_ONLY);
		dsCombo.setLayoutData(gridData);
		
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("Table name : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 200;
		gridData.horizontalSpan = 4;
		tableNameText = new Text(buttonComposite, SWT.LEFT | SWT.BORDER);
		tableNameText.setLayoutData(gridData);
		tableNameText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					showDataBaseTableList();
					displayDBTableList(true);
				}
			}
		});
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button searchButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER );
		searchButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		searchButton.setText("Search");
		searchButton.setLayoutData(gridData);
		searchButton.setFocus();
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showDataBaseTableList();
				displayDBTableList(true);
			}
		});
	}

	private void createTableListPanel(final Composite composite) {
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true, 8, 10);

		table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION	| SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(data);

		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				// determine new sort column and direction
				TableColumn sortColumn = tableViewer.getTable().getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				int direction = tableViewer.getTable().getSortDirection();
				if (sortColumn == currentColumn) {
					direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					tableViewer.getTable().setSortColumn(currentColumn);
					direction = SWT.UP;
				}
				// sort the data based on column and direction
				tableViewer.getTable().setSortDirection(direction);
				tableViewer.setSorter(new SelectDatabaseResourceSorter(currentColumn.getText(), direction));
			}
		};
		
		TableColumn nameColumn = new TableColumn(table, SWT.CENTER );
		nameColumn.setText("Table Name");
		nameColumn.setWidth(200);
		nameColumn.addListener(SWT.Selection, sortListener);
		nameColumn.setMoveable(true);

		TableColumn typeColumn = new TableColumn(table, SWT.CENTER);
		typeColumn.setText("Type");
		typeColumn.setWidth(60);
		typeColumn.addListener(SWT.Selection, sortListener);
		typeColumn.setMoveable(true);
		
		TableColumn descColumn = new TableColumn(table, SWT.CENTER);
		descColumn.setText("Table Description");
		descColumn.setWidth(180);
		descColumn.addListener(SWT.Selection, sortListener);
		descColumn.setMoveable(true);
		
		TableColumn dataSourceColumn = new TableColumn(table, SWT.CENTER );
		dataSourceColumn.setText("Data Source");
		dataSourceColumn.setWidth(150);
		dataSourceColumn.addListener(SWT.Selection, sortListener);
		dataSourceColumn.setMoveable(true);
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new DBSelectDialogTableViewerContentProider());
		tableViewer.setLabelProvider(new DBSelectDialogTableViewerLabelProvider());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Iterator iterator=selection.iterator();
				while(iterator.hasNext()){
					columnListPanel.displayColumnList((TableInfo)iterator.next());
				}
			}
		});
		
		initDialog();
	}
	
	// 다이얼로그 열림과 동시에 데이터값 설정
	private void initDialog() {
		if(serverInfo == null)
			return;
		serverListCombo.add(serverInfo.getServerName());
		serverListCombo.select(0);
		serverListCombo.setEnabled(false);
		
		for (int i = 0; i < types.length; i++) {
			typeCombo.add(types[i], i);
		}
		typeCombo.select(1);
		
		getDBHandleSupport();
		try {
			dataSourceList = selectResourceDialogAction.getDataSourceList(dBHandleSupport);
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Error", AgentUtils.getStackTraceString(e), e);
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < dataSourceList.size(); i++) {
			dsCombo.add(dataSourceList.get(i), i);
		}
		dsCombo.select(0);
		tableNameText.setText("");
		
//		showDataBaseTableList();
//		displayDBTableList(false);
	}

	private void getDBHandleSupport() {
		selectResourceDialogAction = new SelectDatabaseResourceDialogAction(serverInfo);
		dBHandleSupport = selectResourceDialogAction.getDBHandleSupport(serverInfo); 
	}
	
	private void showDataBaseTableList() {
		String searchType = null;
		String tableName = null;
		tableInfoList.clear();
		
		if(dBHandleSupport == null){
			getDBHandleSupport();
		}
		if(typeCombo.getText() == null || typeCombo.getText().length() == 0){
			searchType = "";
		}else{
			searchType = typeCombo.getText();
		}
		
		if(tableNameText.getText() == null || tableNameText.getText().length() == 0){
			MessageUtil.showMessage("Please, type a table name to search.", "Batch Manager");
//			tableName = "";
			return;
		}else {
			tableName = tableNameText.getText();
		}
		
		List<TableInfo> dbMetaInfo =  selectResourceDialogAction.getDBMetaInfo(dBHandleSupport, dsCombo.getText(), tableName, searchType);
		if(dbMetaInfo!=null) tableInfoList.addAll(dbMetaInfo);
	}
	
	//  Table List 다이얼로그에 뿌려주기
	private void displayDBTableList(boolean withMessages) {
		if(tableInfoList == null || tableInfoList.size() == 0){
			tableViewer.refresh();
			tableViewer.setInput(tableInfoList);
			if(withMessages)
				MessageUtil.showMessage("No Informations", "Batch Manager");
		}else if (tableInfoList.size() > 0) {
			tableViewer.refresh();
			tableViewer.setInput(tableInfoList);
		}
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID){
			cancelPressed();
		} else if (buttonId == ShowTop){
			showSelectedResource(ShowTop);
		} else if (buttonId == ShowBottom){
			showSelectedResource(ShowBottom);
		}
	}

	// column Result List 다이얼로그로 결과 뿌리기
	private void showSelectedResource(int type) {
		ISelection selection = tableViewer.getSelection();
		TableInfo tableInfo = (TableInfo)((IStructuredSelection) selection).getFirstElement();
		List<ColumnInfo> selectedColumnList = columnListPanel.getSelectedColumnList();
		// sorting order : desc - true, asc - false
		boolean isDesc = false;
		if(columnListPanel.getDescButton().getSelection()) isDesc = true;
		else isDesc = false;
		if (tableInfo == null){
			MessageUtil.showMessage("No Selected Item", "Batch Manager");
		}else{
			displayShowColumnResultList(isDesc, tableInfo, selectedColumnList, type);
		}
	}

	// column Result dialog에 찍어주기
	private void displayShowColumnResultList(boolean isDesc, TableInfo tableInfo, List<ColumnInfo> sortingColumnList, int type) {
		DatabaseResourceResultDialog resultListDialog = new DatabaseResourceResultDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo, isDesc, tableInfo, sortingColumnList, type);
		resultListDialog.open();
	}
	
	// Sort시 사용
	public static String getColumnTextFromTable(TableInfo e1, int columnIndex){
		//"Table Name", "Table Description", "Type"
		switch(columnIndex){
		case 0:
			return e1.getName(); 
		case 1:
			return e1.getType();
		case 2:
			return e1.getRemarks();
		case 3:
			return e1.getDataSource();
		default:
			return "";
		}
	}
}
