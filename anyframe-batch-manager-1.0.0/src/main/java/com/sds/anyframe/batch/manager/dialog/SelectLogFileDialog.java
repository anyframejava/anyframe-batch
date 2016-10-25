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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.service.FileList;
import com.sds.anyframe.batch.manager.controller.ExecutionLogAction;
import com.sds.anyframe.batch.manager.controller.SelectLogFileDialogAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.sorter.SelectLogFileSorter;
import com.sds.anyframe.batch.manager.providers.FileSelectDialogTableViewerContentProider;
import com.sds.anyframe.batch.manager.providers.FileSelectDialogTableViewerLabelProvider;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.view.JobExplorerView;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SelectLogFileDialog extends Dialog{
	private Composite dialogArea;
	public static Table table;
	private TableViewer tableViewer = null;
	private static final String shelName = "Select Log File";
	private Text baseDirectoryText;
	private Text fileNameText;
	private Combo serverListCombo;
	private Text fromDate;
	private Text toDate;
	private String jobFilePath;
	private ServerInfo serverInfo;
	private final String toDayDate = BatchUtil.todayDate();
	private FileList logFileList;
	private List<FileInfoVO> fileList;
	private JobInfo jobInfo;
	public static final String[] columnHeaders = { "Path", "Name",	"Size", "Mod Date"};
	
	public SelectLogFileDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, " Show Log ",	false);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL,	false);
	}
	
	public SelectLogFileDialog(Shell activeShell, ServerInfo serverInfo, JobInfo jobInfo) {
		super(activeShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
		this.jobFilePath = jobInfo.getJobPath();
		this.jobInfo = jobInfo;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);

		shell.setText(shelName);
		shell.setBounds(500, 200, 700, 440);

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		dialogArea.setLayout(layout);
		
		createButton(dialogArea);
		createTable(dialogArea);
		return dialogArea;

	}

	private void createButton(final Composite composite) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Composite serverComposite = new Composite(composite, SWT.NULL);
		serverComposite.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.NULL);
		serverComposite.setLayoutData(gridData);
		Label lab = new Label(serverComposite, SWT.NULL);
		lab.setText("Target Server : ");
		gridData = new GridData(SWT.NULL);
		serverListCombo = new Combo(serverComposite, SWT.READ_ONLY);
		serverListCombo.setLayoutData(gridData);
		
		Composite executionDateComposite = new Composite(composite, SWT.NULL);
		executionDateComposite.setLayout(new GridLayout(6, false));
		gridData = new GridData(SWT.NULL);
		gridData.horizontalSpan = 2;
		executionDateComposite.setLayoutData(gridData);
		lab = new Label(executionDateComposite, SWT.NULL);
		lab.setText("Execution Date : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 80;
		fromDate = new Text(executionDateComposite, SWT.BORDER);
		fromDate.setText(toDayDate);
		fromDate.setLayoutData(gridData);
		fromDate.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					displayLogList();
				}
			}
		});
		gridData = new GridData(SWT.NULL);
		Button fromDateButton = new Button(executionDateComposite, SWT.NONE);
		fromDateButton.setText("...");
		fromDateButton.setLayoutData(gridData);
		fromDateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				CalendarDialog cd = new CalendarDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
				String date = (String)cd.open();
				if(date!= null)
					fromDate.setText(date);
			}
		});
		lab = new Label(executionDateComposite, SWT.NULL);
		lab.setText("~");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 80;
		toDate = new Text(executionDateComposite, SWT.BORDER);
		toDate.setText(toDayDate);
		toDate.setLayoutData(gridData);	
		toDate.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					displayLogList();
				}
			}
		});
		gridData = new GridData(SWT.NULL);
		Button toDateButton = new Button(executionDateComposite, SWT.NONE);
		toDateButton.setText("...");
		toDateButton.setLayoutData(gridData);
		toDateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				CalendarDialog cd = new CalendarDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
				String date = (String)cd.open();
				if(date!= null)
					toDate.setText(date);				
			}
		});
		
		Composite directoryComposite = new Composite(composite, SWT.NULL);
		directoryComposite.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.NULL);
		directoryComposite.setLayoutData(gridData);	
		lab = new Label(directoryComposite, SWT.RIGHT);
		lab.setText("Directory : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 150;
		baseDirectoryText = new Text(directoryComposite, SWT.LEFT | SWT.BORDER);
		baseDirectoryText.setLayoutData(gridData);
		baseDirectoryText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					displayLogList();
				}
			}
		});
		
		
		Composite fileNameComposite = new Composite(composite, SWT.NULL);
		fileNameComposite.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.NULL);
		fileNameComposite.setLayoutData(gridData);	
		lab = new Label(fileNameComposite, SWT.RIGHT);
		lab.setText("File Name : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 150;
		fileNameText = new Text(fileNameComposite, SWT.LEFT | SWT.BORDER);
		fileNameText.setLayoutData(gridData);
		fileNameText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					displayLogList();
				}
			}
		});
		
		Composite buttonComposite = new Composite(composite, SWT.NULL);
		buttonComposite.setLayout(new GridLayout(1, false));
		gridData = new GridData(SWT.NULL);
		buttonComposite.setLayoutData(gridData);
		gridData = new GridData(SWT.RIGHT);
		Button searchButton = new Button(buttonComposite, SWT.PUSH | SWT.CENTER );
		searchButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		searchButton.setText("Search");
		searchButton.setLayoutData(gridData);
		searchButton.setFocus();
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				displayLogList();
			}
		});
	}

	private void createTable(final Composite composite) {
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
				tableViewer.setSorter(new SelectLogFileSorter(currentColumn.getText(), direction));
			}
		};
		
		TableColumn pathColumn = new TableColumn(table, SWT.None );
		pathColumn.setText("Path");
		pathColumn.setWidth(150);
		pathColumn.addListener(SWT.Selection, sortListener);
		pathColumn.setMoveable(true);

		TableColumn NameColumn = new TableColumn(table, SWT.None);
		NameColumn.setText("Name");
		NameColumn.setWidth(250);
		NameColumn.addListener(SWT.Selection, sortListener);
		NameColumn.setMoveable(true);
		
		TableColumn sizeColumn = new TableColumn(table, SWT.None);
		sizeColumn.setText("Size");
		sizeColumn.setWidth(60);
//		sizeColumn.setAlignment(SWT.CENTER);
		sizeColumn.addListener(SWT.Selection, sortListener);
		sizeColumn.setMoveable(true);
		
		TableColumn modDateColumn = new TableColumn(table, SWT.None);
		modDateColumn.setText("Mod Date");
		modDateColumn.setWidth(200);
		modDateColumn.addListener(SWT.Selection, sortListener);
		modDateColumn.setMoveable(true);
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new FileSelectDialogTableViewerContentProider());
		tableViewer.setLabelProvider(new FileSelectDialogTableViewerLabelProvider());
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				displayExecutionLog();
				close();
			}
		});
		
		initLogList();
	}
	
	private void initLogList() {
		if(serverInfo == null || jobFilePath == null)
			return;
		SelectLogFileDialogAction logFileSelectDialogAction = new SelectLogFileDialogAction(serverInfo, jobInfo);
		logFileList = logFileSelectDialogAction.getLogFileList(serverInfo);
		fileList = logFileSelectDialogAction.getFileList(logFileList, jobFilePath, BatchUtil.convertDateFormatWithoutHyphen(toDayDate), BatchUtil.convertDateFormatWithoutHyphen(toDayDate));
		
		serverListCombo.add(serverInfo.getServerName());
		serverListCombo.select(0);
		serverListCombo.setEnabled(false);
		if(jobInfo != null){
			baseDirectoryText.setText(StringUtils.substringBetween(jobFilePath, "build"+JobExplorerView.getPolicy().getFileSeparator(), jobInfo.getJobName()));
			fileNameText.setText(jobInfo.getJobName());
		}
		else{
			baseDirectoryText.setText("");
			fileNameText.setText("");
		}
		if(fileList == null || fileList.size() == 0){
			tableViewer.refresh();
			tableViewer.setInput(fileList);

		}else if (fileList.size() > 0) {
			tableViewer.refresh();
			tableViewer.setInput(fileList);
		}
	}
	

	protected void displayExecutionLog() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		Iterator iterator=selection.iterator();
		String filePath = null;
		String fileName = null;
		while(iterator.hasNext()){
			FileInfoVO logFileInfoVO = (FileInfoVO)iterator.next();
			
			filePath = logFileList.getBaseDir() + baseDirectoryText.getText();
			fileName = logFileInfoVO.getName();
		}
		if(serverInfo == null || filePath == null || fileName == null){
			MessageDialog.openWarning(new Shell(), "Batch Manager", "Do not Show Excution Log! \n ");
			return;
		}
		if(!filePath.endsWith(JobExplorerView.getPolicy().getFileSeparator()))
			filePath = filePath + JobExplorerView.getPolicy().getFileSeparator();
		ExecutionLogAction executionLogAction = new ExecutionLogAction(serverInfo, filePath+fileName);
		executionLogAction.run();
	}
	
	protected void displayLogList() {
		if(serverInfo == null)
			return;
		SelectLogFileDialogAction logFileSelectDialogAction = new SelectLogFileDialogAction(serverInfo, jobInfo);
		logFileList = logFileSelectDialogAction.getLogFileList(serverInfo);
		fileList = logFileSelectDialogAction.getFileListToDialog(logFileList, baseDirectoryText.getText(), fileNameText.getText(), BatchUtil.convertDateFormatWithoutHyphen(fromDate.getText()), BatchUtil.convertDateFormatWithoutHyphen(toDate.getText()));
		
		if(fileList == null || fileList.size() == 0){
			tableViewer.refresh();
			tableViewer.setInput(fileList);
			MessageUtil.showMessage("No log files", "Batch Manager");
		}else if (fileList.size() > 0) {
			tableViewer.refresh();
			tableViewer.setInput(fileList);
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if(showSelectedLog()){
				close();
			}
		} else if (buttonId == IDialogConstants.CANCEL_ID){
			cancelPressed();
		} else if (buttonId == IDialogConstants.CLOSE_ID){
			cancelPressed();
		}
	}

	private boolean showSelectedLog() {
		ISelection selection = tableViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj == null){
			MessageUtil.showMessage("No Selected Item", "Batch Manager");
			return false;
		}
		else{
			displayExecutionLog();
			return true;
		}
	}

	public static String getColumnTextFromTable(FileInfoVO result, int columnIndex){
		
		//"Path", "Name", "Size", "Mod Date"
		
		switch(columnIndex){
		case 0:
			return result.getPath(); 
		case 1:
			return result.getName();
		case 2:
			return Long.toString(result.getSize());
		case 3:
			return result.getCreatedDate().toString();
		default:
			return "";
		}
	}
}
