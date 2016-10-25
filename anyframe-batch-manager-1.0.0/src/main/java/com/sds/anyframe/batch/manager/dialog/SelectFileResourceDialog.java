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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
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
import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.service.FileList;
import com.sds.anyframe.batch.manager.controller.FileServiceAction;
import com.sds.anyframe.batch.manager.controller.GetResourcesAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.sorter.SelectFileResourceSorter;
import com.sds.anyframe.batch.manager.providers.FileSelectDialogTableViewerContentProider;
import com.sds.anyframe.batch.manager.providers.FileSelectDialogTableViewerLabelProvider;
import com.sds.anyframe.batch.manager.providers.cellEditors.FileInfoCellModifier;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SelectFileResourceDialog extends Dialog{
	private Composite dialogArea;
	public static Table table;
	private TableViewer tableViewer = null;
	private static final String shelName = "Select Resource";
	private String baseDirectoryStr;
	private String firstFilename;
	private Text baseDirectoryText;
	private Text fileNameFilterText;
	private Combo serverListCombo;
	private Combo resourceTypeCombo;
	private Text fromDate;
	private Text toDate;
	private ServerInfo serverInfo;
	private final String fromDateText = BatchUtil.monthBeforeDate();
	private final String toDateText = BatchUtil.todayDate();
	private FileList samFiles;
	private List<FileInfoVO> fileList = new ArrayList<FileInfoVO>();
	private JobInfo jobInfo;
	private List<String> samFileList;
	public static final String[] columnHeaders = { "Path", "Name",	"Size", "Mod Date", "VO Class", "Column Seperator", "Line Seperator"};
	private TableColumn colSepColumn, lineSepColumn;
	private Composite leftBottomPanel;
	private Text colSepText, lineSepText;
	private Policy policy;
	public static final int ShowTop = 1;
	public static final int ShowBottom = 2;
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		leftPanelForBottomBar(parent);
		
		Composite rightPanel = new Composite(parent, SWT.RIGHT);
		rightPanel.setLayout(new GridLayout(3, false));
		createButton(rightPanel, ShowTop, "Show Top", false);
		createButton(rightPanel, ShowBottom, "Show Bottom",	false);
		createButton(rightPanel, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, false);
	}

	private Composite leftPanelForBottomBar(Composite parent) {
		leftBottomPanel = new Composite(parent, SWT.NULL);
		leftBottomPanel.setLayout(new GridLayout(6, false));
		Label lab = new Label(leftBottomPanel, SWT.NULL);
		lab.setText("Column Seperator : ");
		GridData gridData = new GridData(SWT.NULL);
		gridData.widthHint = 30;
		gridData.horizontalSpan = 1;
		colSepText = new Text(leftBottomPanel, SWT.CENTER | SWT.BORDER);
		colSepText.setLayoutData(gridData);
		colSepText.setText(",");
		
		Label lab2 = new Label(leftBottomPanel, SWT.NULL);
		lab2.setText("   ");
		leftBottomPanel.setVisible(false);
		return leftBottomPanel;
	}
	
	public SelectFileResourceDialog(Shell activeShell, ServerInfo serverInfo, JobInfo jobInfo, final Policy policy) {
		super(activeShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
		this.jobInfo = jobInfo;
		this.policy = policy;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(shelName);
		shell.setBounds(200, 200, 800, 440);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		dialogArea.setLayout(layout);
		
		createHead(dialogArea);
		createTable(dialogArea);
		return dialogArea;
	}
	
	private void createHead(final Composite composite) {
		GridData gridData = new GridData(GridData.CENTER);
		Composite buttonComposite = new Composite(composite, SWT.NULL);
		buttonComposite.setLayout(new GridLayout(9, false));
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
		lab.setText("Resource Type : ");
		gridData = new GridData(SWT.NULL);
		resourceTypeCombo = new Combo(buttonComposite, SWT.READ_ONLY);
		resourceTypeCombo.setLayoutData(gridData);
		resourceTypeCombo.add(ResourceType.SAM.name(), 0);
		resourceTypeCombo.add(ResourceType.VSAM.name(), 1);
		resourceTypeCombo.select(0);
		resourceTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = resourceTypeCombo.getSelectionIndex();
				if(index == 1){
					showSeperatorsColumns();
				} else{
					hideSeperatorsColumns();
				}
			}
		});
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("     ");
		
		lab = new Label(buttonComposite, SWT.NULL);
		lab.setText("Mod Date : ");
		Composite dateComposite = new Composite(buttonComposite, SWT.NULL);
		dateComposite.setLayout(new GridLayout(5, false));
		gridData = new GridData(SWT.NULL);
		gridData.horizontalSpan = 2;
		dateComposite.setLayoutData(gridData);
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 80;
		fromDate = new Text(dateComposite, SWT.BORDER);
		fromDate.setText(fromDateText);
		fromDate.setLayoutData(gridData);
		fromDate.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					if(getResourceList())
						displayResourceList(true);
				}
			}
		});
		gridData = new GridData(SWT.NULL);
		Button fromDateButton = new Button(dateComposite, SWT.NONE);
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
		lab = new Label(dateComposite, SWT.NULL);
		lab.setText("~");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 80;
		toDate = new Text(dateComposite, SWT.BORDER);
		toDate.setText(toDateText);
		toDate.setLayoutData(gridData);
		toDate.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					if(getResourceList())
						displayResourceList(true);
				}
			}
		});
		gridData = new GridData(SWT.NULL);
		Button toDateButton = new Button(dateComposite, SWT.NONE);
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

		lab = new Label(buttonComposite, SWT.RIGHT);
		lab.setText("Directory : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 200;
		gridData.horizontalSpan = 5;
		baseDirectoryText = new Text(buttonComposite, SWT.LEFT | SWT.BORDER);
		baseDirectoryText.setLayoutData(gridData);
		baseDirectoryText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					if(getResourceList())
						displayResourceList(true);
				}
			}
		});
		
		lab = new Label(buttonComposite, SWT.RIGHT);
		lab.setText("File Name : ");
		gridData = new GridData(SWT.NULL);
		gridData.widthHint = 200;
		fileNameFilterText = new Text(buttonComposite, SWT.LEFT | SWT.BORDER);
		fileNameFilterText.setLayoutData(gridData);
		fileNameFilterText.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 13){
					if(getResourceList())
						displayResourceList(true);
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
				if(getResourceList())
					displayResourceList(true);
			}
		});
		
	}
	private void showSeperatorsColumns() {
		leftBottomPanel.setVisible(true);
	}
	private void hideSeperatorsColumns() {
		leftBottomPanel.setVisible(false);
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
				tableViewer.setSorter(new SelectFileResourceSorter(currentColumn.getText(), direction));
			}
		};
		
		TableColumn pathColumn = new TableColumn(table, SWT.None );
		pathColumn.setText("Path");
		pathColumn.setWidth(180);
		pathColumn.addListener(SWT.Selection, sortListener);
		pathColumn.setMoveable(true);

		TableColumn NameColumn = new TableColumn(table, SWT.None);
		NameColumn.setText("Name");
		NameColumn.setWidth(180);
		NameColumn.addListener(SWT.Selection, sortListener);
		NameColumn.setMoveable(true);
		
		TableColumn sizeColumn = new TableColumn(table, SWT.None);
		sizeColumn.setText("Size");
		sizeColumn.setWidth(60);
		sizeColumn.addListener(SWT.Selection, sortListener);
		sizeColumn.setMoveable(true);
		
		TableColumn modDateColumn = new TableColumn(table, SWT.None);
		modDateColumn.setText("Mod Date");
		modDateColumn.setWidth(140);
		modDateColumn.addListener(SWT.Selection, sortListener);
		modDateColumn.setMoveable(true);
		
		TableColumn voClassColumn = new TableColumn(table, SWT.None);
		voClassColumn.setText("VO Class");
		voClassColumn.setWidth(200);
		voClassColumn.addListener(SWT.Selection, sortListener);
		voClassColumn.setMoveable(true);

		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new FileSelectDialogTableViewerContentProider());
		tableViewer.setColumnProperties(new String[] { "Path", "Name", "Size", "Mod Date", "VO Class" });
		tableViewer.setLabelProvider(new FileSelectDialogTableViewerLabelProvider());
		
		FileInfoCellModifier modifier = new FileInfoCellModifier(tableViewer);
		tableViewer.setCellModifier(modifier);
		
		tableViewer.setCellEditors(new CellEditor[] {null, null, null,	null, new VOSelectionDialog(tableViewer.getTable(), VOSelectionDialog.FOR_FILEINFOVO_ACTION) });
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				showSelectedResource(ShowTop, ResourceType.valueOf(resourceTypeCombo.getItem(resourceTypeCombo.getSelectionIndex())));
			}
		});
		initDailog();
	}
	
	private void initDailog() {
		if(serverInfo == null)
			return;
		initResourceList();

		serverListCombo.add(serverInfo.getServerName());
		serverListCombo.select(0);
		serverListCombo.setEnabled(false);
		
		if(baseDirectoryStr != null){
			baseDirectoryText.setText(baseDirectoryStr);
		}else{
			baseDirectoryText.setText("");
		}
		
		if(firstFilename != null){
			String filename = firstFilename.contains(".GDG")?StringUtils.substringBefore(firstFilename, ".GDG"):firstFilename;
			fileNameFilterText.setText(filename);
		}else{
			fileNameFilterText.setText("");
		}
		
		displayResourceList(false);
	}
	
	private void initResourceList(){
		GetResourcesAction selectResourceDialogAction = new GetResourcesAction(serverInfo, jobInfo, policy);
		samFileList = selectResourceDialogAction.resourcesInJob(); 
		
		FileServiceAction fileService = new FileServiceAction(serverInfo);
		
		boolean isFieldSetting = false;
		for(String samfile : samFileList){

			if(samfile == null)
				continue;
			int startIndex = samfile.lastIndexOf("/");

			String samFilePath = samfile.substring(0, startIndex+1);
			String samFileName = samfile.substring(startIndex+1, samfile.length());
			
			List<FileInfoVO> list = fileService.searchFiles(samFilePath, samFileName, BatchUtil.convertDateFormatWithoutHyphen(toDateText), BatchUtil.convertDateFormatWithoutHyphen(toDateText));
			if(list != null) fileList.addAll(list);				
		
			if(!isFieldSetting && samFilePath != null && samFileName != null){
				baseDirectoryStr = samFilePath;
				if(baseDirectoryStr.endsWith("//") || baseDirectoryStr.endsWith("\\")) {
					baseDirectoryStr = baseDirectoryStr.substring(0, baseDirectoryStr.length() -1);
				}
				firstFilename = samFileName;
				isFieldSetting = true;
			}
		}
	}
	
	private boolean getResourceList() {
		if(serverInfo == null || baseDirectoryText.getText().trim().length() == 0)
			return false;
		
		if(!isAvailablePath())
			return false;
		
		FileServiceAction selectResourceDialogAction = new FileServiceAction(serverInfo);
		fileList = selectResourceDialogAction.searchFiles(baseDirectoryText.getText(), fileNameFilterText.getText(), BatchUtil.convertDateFormatWithoutHyphen(fromDate.getText()), BatchUtil.convertDateFormatWithoutHyphen(toDate.getText()));
		return true;
	}

	private boolean isAvailablePath() {
		if(policy.getSamRootPath() == null) {
			MessageUtil.showMessage("Base sam path must not be null in the configuration file 'runtime.properties'", "Batch Manager");
			return false;
		}
		if(!baseDirectoryText.getText().startsWith(policy.getSamRootPath())) {
			MessageUtil.showMessage("Configured root folder or it's sub folder can be searched.\n Root path is "+policy.getSamRootPath(), "Batch Manager");
			return false;
		}
		return true;
	}
	
	private void displayResourceList(boolean withMessages) {
		if(fileList == null || fileList.size() == 0){
			tableViewer.refresh();
			tableViewer.setInput(fileList);
			if(withMessages)
				MessageUtil.showMessage("No files", "Batch Manager");
		}else if (fileList.size() > 0) {
			tableViewer.refresh();
			tableViewer.setInput(fileList);
		}
	}
	
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID){
			cancelPressed();
		} else if (buttonId == ShowTop){
			showSelectedResource(ShowTop, ResourceType.valueOf(resourceTypeCombo.getItem(resourceTypeCombo.getSelectionIndex())));
		} else if (buttonId == ShowBottom){
			showSelectedResource(ShowBottom, ResourceType.valueOf(resourceTypeCombo.getItem(resourceTypeCombo.getSelectionIndex())));
		}
	}

	private boolean showSelectedResource(int topOrBottom, ResourceType resourceType) {
		ISelection selection = tableViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj == null){
			MessageUtil.showMessage("No Selected Item", "Batch Manager");
			return false;
		}
		else{
			displayShowResource(topOrBottom, resourceType);
			return true;
		}
	}

	private void displayShowResource(int topOrBottom, ResourceType resourceType) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		Iterator iterator= selection.iterator();
		
		String filePath = null;
		String fileName = null;
		
		FileInfoVO fileInfoVO = new FileInfoVO(); 
		while(iterator.hasNext()){
			fileInfoVO = (FileInfoVO)iterator.next();
			
			filePath = fileInfoVO.getPath();
			fileName = fileInfoVO.getName();
		}
		if(serverInfo == null || filePath == null || fileName == null){
			MessageDialog.openWarning(new Shell(), "Batch Manager", "Do not Show Resource file! \n ");
			return;
		}
		
		if(filePath.charAt(filePath.length()-1) != '/' )
			filePath = filePath + "/";
		
		fileInfoVO.setFullPathName(filePath + fileName);
		FileResourceResultDialog resultListDialog = new FileResourceResultDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), serverInfo, fileInfoVO, topOrBottom, resourceType, colSepText.getText());
		resultListDialog.open();
	}
	
	public static String getColumnTextFromTable(FileInfoVO result, int columnIndex){
		
		//"Path", "Name", "Size", "Mod Date", "VO Class"
		switch(columnIndex){
		case 0:
			return result.getPath(); 
		case 1:
			return result.getName();
		case 2:
			return Long.toString(result.getSize());
		case 3:
			return result.getCreatedDate().toString();
		case 4:
			return result.getVoClass();
		default:
			return "";
		}
	}
	public enum ResourceType{
		SAM, VSAM;
	}
}
