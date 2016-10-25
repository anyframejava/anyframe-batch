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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.sds.anyframe.batch.agent.model.ColumnInfo;
import com.sds.anyframe.batch.agent.model.TableInfo;
import com.sds.anyframe.batch.agent.service.DBHandleSupport;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class DatabaseResourceResultDialog extends Dialog {
	private Composite dialogArea;
	public static Table table;
	private TableViewer tableViewer;
	private Text pageNoText;
	private final ServerInfo serverInfo;
	private Label totalLab;
	private List<String> resultList = new ArrayList<String>();
	private final TableInfo tableInfo;
	private DBHandleSupport dbHandler;
	private final int type;
	private static final String shelName = "Database Result List";
	Map<String, Object> paramMap = new HashMap<String, Object>();

	PageRequest pageRequest = new PageRequest();
	
	public DatabaseResourceResultDialog(Shell parentShell, ServerInfo serverInfo, boolean isDesc, TableInfo tableInfo, List<ColumnInfo> sortingColumnList, int type) {
		super(parentShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
		this.tableInfo = tableInfo;
		this.type = type;
		paramMap.put("table", tableInfo.getName());
		paramMap.put("dataSource", tableInfo.getDataSource());
		paramMap.put("sortingColumnList", sortingColumnList);
		paramMap.put("order", isDesc?"DESC":"ASC");
		pageRequest.setParameter(paramMap);
		setDbHandler();
	}
	
	private void setDbHandler(){
		try {
			dbHandler = (DBHandleSupport) ProxyHelper.getProxyInterface(serverInfo.getAddress(), "dbHandle", DBHandleSupport.class.getName());
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID,	IDialogConstants.CLOSE_LABEL, false);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(shelName);
		shell.setBounds(100, 100, 1200, 700);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		dialogArea.setLayout(layout);

		createTable(dialogArea);
		createPaging(dialogArea);
		initSettingValuse();
		return dialogArea;

	}
	
	private void createTable(final Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setText(" ");
		column1.setWidth(50);
		for (ColumnInfo col : tableInfo.getColumns()) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(col.getName());
			column.setWidth(100);
		}
		
		tableViewer = new TableViewer(table);
		ResultViewLabelProvider sqlResultTableLabelProvider = new ResultViewLabelProvider();
		tableViewer.setLabelProvider(sqlResultTableLabelProvider);
		ResultViewContentProvider sqlResultContentProvider = new ResultViewContentProvider();
		tableViewer.setContentProvider(sqlResultContentProvider);
	}

	private void createPaging(Composite parent) {
		Composite pagingComposite = new Composite(parent, SWT.NULL);
		pagingComposite.setLayout(new GridLayout(5, false));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 9;
		pagingComposite.setLayoutData(gridData);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button prevButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER );
		prevButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		prevButton.setText("Prev");
		prevButton.setLayoutData(gridData);
		prevButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(pageRequest.pageNo == 1){
					MessageUtil.showMessage("No more previous! ", "Batch Manager");
					return;
				}
				showPreviousPage();
				displayResourceList();
			}
		});	
		
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 30;
		pageNoText = new Text(pagingComposite, SWT.RIGHT | SWT.BORDER);
		pageNoText.setLayoutData(gridData);
		pageNoText.setFont(new Font(null, "", 10, SWT.NORMAL));
		pageNoText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					// 숫자만 가능
					if(BatchUtil.isNum(pageNoText.getText())){
						MessageUtil.showMessage("Illegal Number format!", "Batch Manager");
						return;
					}
					int pageNo = Integer.parseInt(pageNoText.getText());
					
					// 페이지는 최소 1에서 최대 totalpage까지
					if(pageNo < 1 || pageNo > pageRequest.totalPageCount){
						MessageUtil.showMessage("Incorrect Page Number!", "Batch Manager");
						return;
					}
					showPageByNo(pageNo);
					
					displayResourceList();
				}
			}
		});
		
		Label lab = new Label(pagingComposite, SWT.NULL);
		lab.setText("/");
		lab.setFont(new Font(null, "", 10, SWT.NORMAL));
		totalLab = new Label(pagingComposite, SWT.NULL);
		totalLab.setFont(new Font(null, "", 10, SWT.NORMAL));
		totalLab.setText("");
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		Button nextButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER );
		nextButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		nextButton.setText("Next");
		nextButton.setLayoutData(gridData);
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(pageRequest.pageNo == pageRequest.totalPageCount){
					MessageUtil.showMessage("No more Last!", "Batch Manager");
					return;
				}
				showNextPage();
				displayResourceList();
			}
		});
	}	
	
	private void initSettingValuse() {
		showInitResourceList();
		displayResourceList();
	}
	
	private void showInitResourceList() {
		try {
			if(type == SelectDatabaseResourceDialog.ShowTop) {
				pageRequest = dbHandler.getTopPage(pageRequest);
				resultList = (List<String>) pageRequest.getResult();
			}
			else if(type == SelectDatabaseResourceDialog.ShowBottom) {
				pageRequest = dbHandler.getBottomPage(pageRequest);
				resultList = (List<String>) pageRequest.getResult();
			}
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		BatchActivator.getDefault().getModelObject().readResource(resultList);
	}

	private void displayResourceList() {
		if (resultList == null || resultList.size() == 0){
			this.close();
			MessageUtil.showMessage("No Data", "Batch Manager");
		} else{
			tableViewer.refresh();
			tableViewer.setInput(resultList);
			totalLab.setText(Integer.toString(pageRequest.totalPageCount));
			pageNoText.setText(Integer.toString(pageRequest.pageNo));
		}
	}
	
	protected void showNextPage() {
		try {
			pageRequest = dbHandler.next(pageRequest);
			resultList = (List<String>) pageRequest.getResult();
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		BatchActivator.getDefault().getModelObject().readResource(resultList);
	}

	protected void showPreviousPage() {
		try {
			pageRequest = dbHandler.previous(pageRequest);
			resultList = (List<String>) pageRequest.getResult();
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		BatchActivator.getDefault().getModelObject().readResource(resultList);
	}

	protected void showPageByNo(int pageNo) {
		try {
			pageRequest.pageNo = pageNo;
			pageRequest = dbHandler.pageByNo(pageRequest);
			resultList = (List<String>) pageRequest.getResult();
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
		}
		BatchActivator.getDefault().getModelObject().readResource(resultList);
	}
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			cancelPressed();
		}
	}

	private static class ResultViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			ListOrderedMap map = (ListOrderedMap) element;
			Object x = map.get(columnIndex);
			Object r = map.get(x);
			if (r == null) {
				result = "";
			} else {
				result = r.toString();
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	static class ResultViewContentProvider implements IStructuredContentProvider {
		public ResultViewContentProvider() {
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object obj, Object obj1) {
		}

		public Object[] getElements(Object inputElement) {
			return ((List) inputElement).toArray();
		}

	}
}
