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

package com.sds.anyframe.batch.manager.view.logEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.controller.ExecutionLogAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.utils.BatchUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ReadOnlyLogEditor extends TextEditor {

	private static final int _1MB = 1000000;

	public static final String ID = "com.sds.anyframe.batch.manager.view.logEditor.ReadOnlyLogEditor";

	private Text pageNoText;
	private Text totalPageText;
	private PageSupport logService;
	private PageRequest pageRequest;

	private LogEditorInput input;

	private Text fileNameText;

	private Text byteSizeText;

	public ReadOnlyLogEditor() {
		super();
		setDocumentProvider(new LogDocumentProvider());
	}

	public void dispose() {
		super.dispose();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.input = (LogEditorInput) input;
		logService = this.input.getLogService();
		pageRequest = this.input.getPageRequest();
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		Composite baseComposite = new Composite(parent, SWT.FILL_WINDING);
		baseComposite.setLayout(new GridLayout(1, false));
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		baseComposite.setLayoutData(layoutData);

		Composite topControlComposite = new Composite(baseComposite,
				SWT.FILL_WINDING);
		Composite editorComposite = new Composite(baseComposite,
				SWT.FILL_WINDING|SWT.BORDER);
		Composite bottomComposite = new Composite(baseComposite,
				SWT.FILL_WINDING);

		topControlComposite.setLayout(new GridLayout(7, false));
		GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
		topControlComposite.setLayoutData(buttonData);

		GridData gridData = new GridData(SWT.LEFT);
		gridData.widthHint = 50;

		gridData = new GridData(SWT.RIGHT);
		gridData.widthHint = 40;
		Button topButton = new Button(topControlComposite, SWT.PUSH
				| SWT.CENTER);
		topButton.setText("Top");
		topButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				ReadOnlyLogEditor.this.getSourceViewer().getTextWidget().invokeAction(17039367);
			}
		});
		gridData.widthHint = 40;
		Button bottomButton = new Button(topControlComposite, SWT.PUSH
				| SWT.CENTER);
		bottomButton.setText("Bottom");
		// bottomButton.setLayoutData(gridData);
		bottomButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				ReadOnlyLogEditor.this.getSourceViewer().getTextWidget().invokeAction(17039368);
			}
		});
		gridData.widthHint = 70;
		
		Button viewButton = new Button(topControlComposite, SWT.PUSH
				| SWT.CENTER);
		viewButton.setText("Refresh");
		//viewButton.setLayoutData(gridData);

		viewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				getBottom();
			}
		});
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileNameText = new Text(topControlComposite, SWT.BORDER);
		fileNameText.setText(input.getName());
		fileNameText.setEditable(false);
		fileNameText.setLayoutData(gridData);
		
		Label lab = new Label(topControlComposite, SWT.NULL);
		lab.setText("Bytes per page: ");
		
		gridData = new GridData(SWT.RIGHT);
		gridData.widthHint = 50;
		
		byteSizeText = new Text(topControlComposite, SWT.RIGHT|SWT.BORDER);
		byteSizeText.setText(""+ExecutionLogAction.BYTES_PER_PAGE);
		byteSizeText.setLayoutData(gridData);
		byteSizeText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent evt) {
				setBytesPerPage();
				if (evt.keyCode == 13) {
					try {
						getBottom();
					} catch (Exception e) {
						MessageUtil.showErrorMessage("Batch Manager",
								AgentUtils.getStackTraceString(e), e);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				setBytesPerPage();
			}
			
			private void setBytesPerPage() {
				try {
					int bytesPerPage = Integer.parseInt(byteSizeText.getText());
					
					if (bytesPerPage > _1MB) {//1mb
						byteSizeText.setText(""+_1MB);
						MessageUtil.showMessage("Bytes per page were exceed 1mb", "Batch Manager");
					}
					
					pageRequest.setPageSize(bytesPerPage);
				} catch (Exception e) {
					byteSizeText.setText(""+ExecutionLogAction.BYTES_PER_PAGE);
					MessageUtil.showErrorMessage("Batch Manager", e
							.getMessage(), e);
				}
			}
		});
		
		editorComposite.setLayout(new FillLayout());
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		bottomComposite.setLayout(new GridLayout(1, false));
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		bottomComposite.setLayoutData(gridData);
		
		createPagingPanel(bottomComposite);
		
		super.createPartControl(editorComposite);
	}

	private void createPagingPanel(Composite control) {
//		Composite pagingComposite = new Composite(parent, SWT.NULL);
//		pagingComposite.setLayout(new GridLayout(7, false));
//		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
//		gridData.horizontalSpan = 9;
//		pagingComposite.setLayoutData(gridData);

//		Group group = new Group(control, SWT.SHADOW_ETCHED_IN);
//		group.setLayout(new GridLayout(1, false));
////		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite pagingComposite = new Composite(control, SWT.NULL);
		pagingComposite.setLayout(new GridLayout(7, false));
		pagingComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
//		Composite pagingComposite = new Composite(group, SWT.NULL);
//		pagingComposite.setLayout(new GridLayout(7, false));
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
//		gridData.horizontalSpan = 9;
//		pagingComposite.setLayoutData(gridData);
		
		GridData gridData = new GridData(SWT.RIGHT);
		Button toStart = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		toStart.setText("<<");
		toStart.setLayoutData(gridData);
		toStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (logService == null)
					return;
				if(pageRequest.pageNo == 1)
					return;
				try {
					pageRequest = logService.getTopPage(pageRequest);
					displayContents((byte[]) pageRequest.getResult());
				} catch (Exception e1) {
					MessageUtil.showErrorMessage("Batch Manager", e1
							.getMessage(), e1);
				}
			}
		});
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button prevButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		prevButton.setText("<");
		prevButton.setLayoutData(gridData);
		prevButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (logService == null)
					return;
				if(pageRequest.pageNo == 1)
					return;
				
				try {
					pageRequest = logService.previous(pageRequest);

					displayContents((byte[]) pageRequest.getResult());
				} catch (Exception e1) {
					MessageUtil.showErrorMessage("Batch Manager", e1
							.getMessage(), e1);
				}
			}
		});
		gridData = new GridData(SWT.RIGHT);
		gridData.widthHint = 30;
		pageNoText = new Text(pagingComposite, SWT.RIGHT | SWT.BORDER);
		pageNoText.setLayoutData(gridData);
		pageNoText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					if (logService == null)
						return;
					if (BatchUtil.isNum(pageNoText.getText())) {
						MessageUtil.showMessage("Illegal Number format!",
								"Batch Manager");
						return;
					}
					int pageNo = Integer.parseInt(pageNoText.getText());

					if (pageNo < 1 || pageNo > pageRequest.totalPageCount) {
						MessageUtil.showMessage("Incorrect Page Number!",
								"Batch Manager");
						return;
					}
					try {
						pageRequest.pageNo = pageNo;
						pageRequest = logService.pageByNo(pageRequest);

						displayContents((byte[]) pageRequest.getResult());
					} catch (Exception e1) {
						MessageUtil.showErrorMessage("Batch Manager", e1
								.getMessage(), e1);
					}
				}
			}
		});

		Label lab = new Label(pagingComposite, SWT.NULL);
		lab.setText("/");
		totalPageText = new Text(pagingComposite, SWT.RIGHT | SWT.BORDER);
		totalPageText.setText("        ");
		totalPageText.setEditable(false);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		Button nextButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		nextButton.setText(">");
		nextButton.setLayoutData(gridData);
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if (logService == null)
					return;
				if(pageRequest.pageNo == pageRequest.totalPageCount)
					return;
				
				try {
					pageRequest = logService.next(pageRequest);

					displayContents((byte[]) pageRequest.getResult());
				} catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager", AgentUtils
							.getStackTraceString(e), e);
				}
			}
		});
		gridData = new GridData(SWT.RIGHT);
		Button toEndButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		toEndButton.setText(">>");
		toEndButton.setLayoutData(gridData);
		toEndButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				if (logService == null)
					return;
				if(pageRequest.pageNo == pageRequest.totalPageCount)
					return;
				try {
					pageRequest = logService.getBottomPage(pageRequest);

					displayContents((byte[]) pageRequest.getResult());
				} catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager", AgentUtils
							.getStackTraceString(e), e);
				}
			}
		});

	}

	public void displayContents(byte[] lines) {

		if (lines == null || lines.length == 0) {
			MessageUtil
					.showMessage("Batch Manager", "Please, select log file.");
			return;
		}
		
		input.setLog(lines);
		
		setInput(input);
		showNumbers();
	}

	public void showNumbers() {
		totalPageText.setText(Integer.toString(pageRequest.totalPageCount));
	    pageNoText.setText(Integer.toString(pageRequest.pageNo));
	    
	    this.getSourceViewer().getTextWidget().invokeAction(17039368);
	    
	}
	
	private void getBottom() {
		if (pageRequest.getParameter() == null) {
			return;
		}
		try {
			pageRequest = logService.getBottomPage(pageRequest);
			byte[] bottomPage = (byte[]) pageRequest
			.getResult();
			
			displayContents(bottomPage);
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", e
					.getMessage(), e);
		}
	}
	
}
