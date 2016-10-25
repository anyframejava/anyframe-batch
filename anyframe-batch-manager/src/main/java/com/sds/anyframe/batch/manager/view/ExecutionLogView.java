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


package com.sds.anyframe.batch.manager.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.ModelObject;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.view.support.ErrorMessageDialog;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ExecutionLogView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "com.sds.anyframe.batch.manager.executionlog";
	private ServerInfo serverInfo;
	private Text fileNameText;
	private Text executionLogTxt;
	private Text pageNoText;
	private Text totalPageText;
	private PageSupport logFile;
	private PageRequest pageRequest = new PageRequest();
	
	private final ErrorMessageDialog emd = new ErrorMessageDialog(new Shell());
	static class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            // do nothing
		}

		public void dispose() {
            // do nothing
		}

		public Object[] getElements(Object parent) {
			Object[] objList = ((ModelObject)parent).toArray();
			if(objList.length==0)
				return new Object[]{""};
			if(!(objList[0] instanceof List))
				return new Object[]{""};
			return ((List)objList[0]).toArray();
		}
	}

	static class ViewLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object obj) {
			return null;
		}
        @Override
		public String getText(Object element) {
            return (String)element;
        }
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		Composite composite  = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(6, false));
		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(buttonData);
		
		GridData gridData = new GridData(SWT.LEFT);
		gridData.widthHint = 50;
		Label fileNamelabel = new Label(composite, SWT.NONE);
		fileNamelabel.setText("Log file : ");
		fileNamelabel.setLayoutData(gridData);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileNameText = new Text(composite, SWT.NONE);
		fileNameText.setText("");
		fileNameText.setEditable(false);
		fileNameText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		fileNameText.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT);
		gridData.widthHint = 40;
		Button topButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		topButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		topButton.setText("Top");
		topButton.setLayoutData(gridData);
		topButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				executionLogTxt.setSelection(0);
			}
		});
		gridData.widthHint = 40;
		Button bottomButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		bottomButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		bottomButton.setText("Bottom");
		bottomButton.setLayoutData(gridData);
		bottomButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				executionLogTxt.setSelection(executionLogTxt.getCharCount());
			}
		});
		gridData.widthHint = 70;
		Button viewButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		viewButton.setFont(new Font(null, "", 8, SWT.BOLD));
		viewButton.setText(" Refresh ");
		viewButton.setLayoutData(gridData);
		
		viewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if(serverInfo == null || pageRequest.getParameter() == null){
					return;
				}
				try {
					pageRequest = logFile.getBottomPage(pageRequest);
					List<Object> bottomPage = (List<Object>) pageRequest.getResult();
					
					displayContents(bottomPage);
				}
				catch (Exception e) {
					emd.setMessage(AgentUtils.getStackTraceString(e));
					emd.open();
				}
			}
		});
        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 5;
		executionLogTxt = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		executionLogTxt.setEditable(false);
		executionLogTxt.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		executionLogTxt.setLayoutData(gridData);
		createPagingPanel(composite);
		BatchActivator.getDefault().getModelObject().addPropertyChangeListener(this);
	}
	
	private void createPagingPanel(Composite parent) {
		Composite pagingComposite = new Composite(parent, SWT.NULL);
		pagingComposite.setLayout(new GridLayout(7, false));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 9;
		pagingComposite.setLayoutData(gridData);
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button toStart = new Button(pagingComposite, SWT.PUSH | SWT.CENTER );
		toStart.setFont(new Font(null, "", 8, SWT.BOLD));
		toStart.setText("<<");
		toStart.setLayoutData(gridData);
		toStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(logFile == null) return;
				try {
					pageRequest = logFile.getTopPage(pageRequest);
					displayContents((List<Object>) pageRequest.getResult());
				}
				catch (Exception e1) {
					MessageUtil.showErrorMessage("Batch Manager",e1.getMessage(), e1);
				}
			}
		});	
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button prevButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER );
		prevButton.setFont(new Font(null, "", 8, SWT.BOLD));
		prevButton.setText("<");
		prevButton.setLayoutData(gridData);
		prevButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(logFile == null) return;
				try {
					pageRequest = logFile.previous(pageRequest);
					
					displayContents((List<Object>) pageRequest.getResult());
				}
				catch (Exception e1) {
					MessageUtil.showErrorMessage("Batch Manager",e1.getMessage(), e1);
				}
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
					if(logFile == null) return;
					if(BatchUtil.isNum(pageNoText.getText())){
						MessageUtil.showMessage("Illegal Number format!", "Batch Manager");
						return;
					}
					int pageNo = Integer.parseInt(pageNoText.getText());
					
					if(pageNo < 1 || pageNo > pageRequest.totalPageCount){
						MessageUtil.showMessage("Incorrect Page Number!", "Batch Manager");
						return;
					}
					try {
						pageRequest.pageNo = pageNo;
						pageRequest = logFile.pageByNo(pageRequest);
						
						displayContents((List<Object>) pageRequest.getResult());
					}
					catch (Exception e1) {
						MessageUtil.showErrorMessage("Batch Manager",e1.getMessage(), e1);
					}
				}
			}
		});
		
		Label lab = new Label(pagingComposite, SWT.NULL);
		lab.setText("/");
		lab.setFont(new Font(null, "", 10, SWT.NORMAL));
		totalPageText = new Text(pagingComposite, SWT.RIGHT | SWT.BORDER);
		totalPageText.setFont(new Font(null, "", 10, SWT.NORMAL));
		totalPageText.setText("        ");
		totalPageText.setEditable(false);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		Button nextButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		nextButton.setFont(new Font(null, "", 8, SWT.BOLD));
		nextButton.setText(">");
		nextButton.setLayoutData(gridData);
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if(logFile == null) return;
				try {
					pageRequest = logFile.next(pageRequest);
					
					displayContents((List<Object>) pageRequest.getResult());
				}
				catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager",AgentUtils.getStackTraceString(e), e);
				}
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		Button toEndButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER );
		toEndButton.setFont(new Font(null, "", 8, SWT.BOLD));
		toEndButton.setText(">>");
		toEndButton.setLayoutData(gridData);
		toEndButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if(logFile == null) return;
				try {
					pageRequest = logFile.getBottomPage(pageRequest);
					
					displayContents((List<Object>) pageRequest.getResult());
				}
				catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager",AgentUtils.getStackTraceString(e), e);
				}
			}
		});
		
	}
	
	public void displayContents(List<Object> resultList) {
		if(resultList == null)
			return;
		
		if(resultList == null || resultList.size() == 0) {
			MessageUtil.showMessage("Batch Manager", "Please, select log file.");
			return;
		}
		BatchActivator.getDefault().getModelObject().readExcutionLog(resultList);
	}
	
	@Override
	public void setFocus() {
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if("Excution_Log".equalsIgnoreCase(evt.getPropertyName())){
			ModelObject result = BatchActivator.getDefault().getModelObject();
	        if(result != null && result.toString().length() != 0){
		        List<String> objList =(List<String>) result.getObjectList().get(0);
		        totalPageText.setText(Integer.toString(pageRequest.totalPageCount));
		        pageNoText.setText(Integer.toString(pageRequest.pageNo));
		        StringBuilder resultStr = new StringBuilder();
		        if(objList != null && objList.size() > 0){
					for(String str : objList ){
						resultStr.append(str).append("\n");
					}
			        executionLogTxt.setText(resultStr.toString());
			        executionLogTxt.setSelection(executionLogTxt.getCharCount());
		        }
	        }
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		BatchActivator.getDefault().getModelObject().removePropertyChangeListener(this);
	}

	public void setConfiguration(ServerInfo serverInfo, PageRequest pageRequest, PageSupport logFile) {
		this.serverInfo = serverInfo;
		this.pageRequest = pageRequest;
		this.logFile = logFile;
		this.fileNameText.setText((String) pageRequest.getParameter());
	}

}
