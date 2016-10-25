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

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.manager.actions.CopyCellOfSamAction;
import com.sds.anyframe.batch.manager.controller.GetResourceAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog.ResourceType;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class FileResourceResultDialog extends Dialog {
	private static final double ROUGH_PIXEL_SIZE = 10;
	private Composite dialogArea;
	public static Table table;
	private TableViewer tableViewer;
	private Label voClassLabel;
	private Label voClass;
	private FileInfoVO fileInfoVO;
	private String[][] lines;
	public int endIndex;
	public int startIndex;
	private Text pageNoText;
	private int topOrBottom; // 1 == top, 2== bottom
	private PageSupport fileResource;
	private ServerInfo serverInfo;
	private Label totalLab;
	private ResourceType resourceType;
	GetResourceAction resourceDialogAction = new GetResourceAction();
	private String colSeperator;

	private PageRequest request = new PageRequest();
	private int index = 0;
	private Text fileNameText;

	private static final String shellName = "File Result List";

	public static final int ShowTop = 1;
	public static final int ShowBottom = 2;

	public FileResourceResultDialog(Shell parentShell, ServerInfo serverInfo,
			FileInfoVO fileInfoVO, int topOrBottom, ResourceType resourceType,
			String colSeperator) {
		super(parentShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
		this.fileInfoVO = fileInfoVO;
		this.topOrBottom = topOrBottom;
		this.resourceType = resourceType;
		this.colSeperator = colSeperator;
		request.setParameter(fileInfoVO.getFullPathName());
		resourceDialogAction.setPageRequest(request);
	}

	public PageSupport getFileResource() {
		return fileResource;
	}

	public void setFileResource(PageSupport resource) {
		this.fileResource = resource;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ShowTop, "Show Top", false);
		createButton(parent, ShowBottom, "Show Bottom", false);
		createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(shellName);
		shell.setBounds(100, 50, 1200, 600);
	}

	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		dialogArea.setLayout(layout);

		showInitPageSupport();

		showInitResourceList();

		createLabel(dialogArea);
		createTable(dialogArea);
		createHookContextMenu();
		createPaging(dialogArea);
		displayLabelValues();
		displayContents();
		return dialogArea;
	}

	private void createLabel(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);

		Button refreshButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		gridData = new GridData(SWT.LEFT);
		refreshButton.setText("Refresh");
		refreshButton.setLayoutData(gridData);
		refreshButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showInitResourceList();
				index = 0;
				request.pageNo = 1;
				displayLabelValues();
				displayContents();
			}
		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileNameText = new Text(composite, SWT.BORDER);
		fileNameText.setEditable(false);
		fileNameText.setLayoutData(gridData);

		gridData = new GridData(SWT.LEFT);
		voClassLabel = new Label(composite, SWT.NONE);
		voClassLabel.setText("VO Class : ");
		voClassLabel.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		voClass = new Label(composite, SWT.NONE);
		voClass.setText("");
		voClass.setLayoutData(gridData);
	}

	private void createTable(final Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);

		table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER
				| SWT.V_SCROLL);
		table.setLayoutData(data);

		table.setFont(new Font(Display.getCurrent(), "굴림체", 10, SWT.NORMAL));

		// first column that display line numbers
		TableColumn rowNumberColumn = new TableColumn(table, SWT.NONE);

		rowNumberColumn.setWidth(30);
		rowNumberColumn.setAlignment(SWT.LEFT);

		tableViewer = new TableViewer(table);

		if (lines != null && lines[0].length > 1) { // Multi Column
			if (resourceType == ResourceType.VSAM) {
				for (int i = 0, length = lines[0].length; i < length; i++) {
					TableColumn column = new TableColumn(table, SWT.LEFT);
					column.setText("");
					column
							.setWidth((int) (lines[0][i].length() * ROUGH_PIXEL_SIZE));
				}
			} else {
				Map<String, Integer> columns = fileInfoVO.getVoFields();

				Set<Entry<String, Integer>> entrySet = columns.entrySet();
				for (Entry<String, Integer> entry : entrySet) {
					TableColumn column = new TableColumn(table, SWT.LEFT);
					column.setText(entry.getKey());
					column
							.setWidth((int) (entry.getValue() * ROUGH_PIXEL_SIZE)); // length
																					// of
																					// column
				}
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			tableViewer
					.setContentProvider(new FileResourceResultViewContentProvider());
			tableViewer
					.setLabelProvider(new FileResourceResultViewLabelProvider());

		} else { // One column
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			TableColumn column = new TableColumn(table, SWT.FILL);
			column.setResizable(true);

			if (resourceType == ResourceType.SAM)
				column.setWidth((int) (request.rowLength * ROUGH_PIXEL_SIZE));
			else if (resourceType == ResourceType.VSAM)
				column.setWidth(800);

			tableViewer
					.setContentProvider(new FileResourceResultViewContentProviderWithoutVO());
			tableViewer
					.setLabelProvider(new FileResourceResultViewLabelProviderWithoutVO());
		}

	}


	private void createHookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				createHookContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
	}

	private void createHookContextMenu(IMenuManager manager) {
		 ISelection selection = tableViewer.getSelection();

		if (selection.isEmpty())
			return;
		manager.add(new CopyCellOfSamAction(tableViewer));
	}

	private void createPaging(Composite parent) {
		Composite pagingComposite = new Composite(parent, SWT.NULL);
		pagingComposite.setLayout(new GridLayout(5, false));
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 9;
		pagingComposite.setLayoutData(gridData);

		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		Button prevButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		prevButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		prevButton.setText("Prev");
		prevButton.setLayoutData(gridData);
		prevButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (request.pageNo == 1)
					return;
				showPreviousPage();
				displayContents();
			}
		});
		if (resourceType == ResourceType.SAM) {
			gridData = new GridData(SWT.NULL);
			gridData.widthHint = 30;
			pageNoText = new Text(pagingComposite, SWT.RIGHT | SWT.BORDER);
			pageNoText.setLayoutData(gridData);
			pageNoText.setFont(new Font(null, "", 10, SWT.NORMAL));
			pageNoText.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 13) {
						if (BatchUtil.isNum(pageNoText.getText())) {
							MessageUtil.showMessage("Illegal Number format!",
									"Batch Manager");
							return;
						}
						int pageNo = Integer.parseInt(pageNoText.getText());

						if (pageNo < 1 || pageNo > request.totalPageCount) {
							MessageUtil.showMessage("Incorrect Page Number!",
									"Batch Manager");
							return;
						}
						showPageByNo(pageNo);
						displayContents();
					}
				}
			});

			Label lab = new Label(pagingComposite, SWT.NULL);
			lab.setText("/");
			lab.setFont(new Font(null, "", 10, SWT.NORMAL));
			totalLab = new Label(pagingComposite, SWT.NULL);
			totalLab.setFont(new Font(null, "", 10, SWT.NORMAL));
			totalLab.setText("");
		}
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		Button nextButton = new Button(pagingComposite, SWT.PUSH | SWT.CENTER);
		nextButton.setFont(new Font(null, "", 8, SWT.NORMAL));
		nextButton.setText("Next");
		nextButton.setLayoutData(gridData);
		nextButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (request.totalPageCount == request.pageNo)
					return;
				showNextPage();
				displayContents();
			}
		});

	}

	protected boolean showNextPage() {
		request = resourceDialogAction.getNextPage(fileResource);
		byte[] buffer = (byte[]) request.getResult();
		if (buffer == null)
			return false;

		try {
			convertBufferToLines(buffer);
			index = (request.pageNo - 1) * (request.pageSize);
		} catch (UnmatchedLineLengthExceptionForSAM e) {
			MessageUtil
					.showMessage(
							"You opened a VSAM file with SAM type. Each length of line is different.",
							"Batch Manager");
			return false;
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Error Occured", e);
			return false;
		}
		return true;
	}

	protected boolean showPreviousPage() {
		request = resourceDialogAction.getPreviousPage(fileResource);
		byte[] buffer = (byte[]) request.getResult();
		if (buffer == null)
			return false;

		try {
			convertBufferToLines(buffer);
			index = (request.pageNo - 1) * (request.pageSize);
		} catch (UnmatchedLineLengthExceptionForSAM e) {
			MessageUtil
					.showMessage(
							"You opened a VSAM file with SAM type. Each length of line is different.",
							"Batch Manager");
			return false;
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Error Occured", e);
			return false;
		}
		return true;
	}

	protected boolean showPageByNo(int pageNo) {
		request = resourceDialogAction.getPageByNo(fileResource, pageNo);
		byte[] buffer = (byte[]) request.getResult();
		if (buffer == null)
			return false;
		try {
			convertBufferToLines(buffer);
			index = (request.pageNo - 1) * request.pageSize;
		} catch (UnmatchedLineLengthExceptionForSAM e) {
			MessageUtil
					.showMessage(
							"You opened a VSAM file with SAM type. Each length of line is different.",
							"Batch Manager");
			return false;
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Error Occured", e);
			return false;
		}
		return true;
	}

	private void displayContents() {
		if (resourceType == ResourceType.SAM && getFileResource() != null) {
			totalLab.setText(Integer.toString(request.totalPageCount));
			pageNoText.setText(Integer.toString(request.pageNo));
		}
		try {
			if (lines != null)
				tableViewer.setInput(lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showInitPageSupport() {
		setFileResource(resourceDialogAction.getPageSupport(serverInfo,
				fileInfoVO.getFullPathName(), resourceType));
	}

	private void displayLabelValues() {
		if (fileInfoVO != null) {
			if (fileInfoVO.getFullPathName() != null)
				fileNameText.setText(fileInfoVO.getFullPathName());
			if (fileInfoVO.getVoClass() != null)
				voClass.setText(fileInfoVO.getVoClass());
		}
	}

	private void convertBufferToLines(byte[] buffer) {
		try {
			if (resourceType == ResourceType.SAM) {

				int lineLength = 1;

				for (int i = 0; i < buffer.length; i++) {
					if (buffer[i] != '\n')
						lineLength++;
					else
						break;
				}

				int columns = 1;
				if (fileInfoVO != null && fileInfoVO.getVoFields() != null)
					columns = fileInfoVO.getVoFields().size();

				this.lines = new String[Math.round((buffer.length + 0f)
						/ (lineLength + 0f))][columns];

				if (fileInfoVO != null && fileInfoVO.getVoFields() != null) {
					Map<String, Integer> fields = fileInfoVO.getVoFields();
					Set<Entry<String, Integer>> entrySet = fields.entrySet();

					for (int i = 0; (i * lineLength) < buffer.length; i++) {
						this.lines[i] = new String[fields.size()];

						int j = 0;
						int sum = 0;
						for (Entry<String, Integer> entry : entrySet) {
							int size = fields.get(entry.getKey());

							this.lines[i][j++] = new String(buffer,
									(i * lineLength) + sum, size,
									ResourcesPlugin.getEncoding());
							sum += size;
						}
					}
				} else {
					try {
						for (int i = 0; (i * lineLength) < buffer.length; i++) {
							int realLineLength = lineLength;
							if (buffer.length < ((i + 1) * lineLength))
								realLineLength = lineLength
										- (((i + 1) * lineLength) - buffer.length);

							this.lines[i] = new String[] { new String(buffer, i
									* lineLength, realLineLength,
									ResourcesPlugin.getEncoding()) };
						}
					} catch (Exception e) {
						throw new UnmatchedLineLengthExceptionForSAM();
					}
				}

			} else {
				String[] lines = StringUtils.split(new String(buffer,
						ResourcesPlugin.getEncoding()), IOUtils.LINE_SEPARATOR);

				String[] split = StringUtils.split(lines[0], colSeperator);

				this.lines = new String[lines.length][split.length];
				for (int i = 0; i < lines.length; i++) {
					split = StringUtils.split(lines[i], colSeperator);
					this.lines[i] = split;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void showInitResourceList() {
		try {
			request = resourceDialogAction.getFileResource(getFileResource(),
					topOrBottom);
			if (request != null && request.getResult() != null) {
				convertBufferToLines((byte[]) request.getResult());
				index = (request.pageNo - 1) * (request.pageSize);
			}
		} catch (UnmatchedLineLengthExceptionForSAM e) {
			MessageUtil
					.showMessage(
							"You opened a VSAM file with SAM type. Each length of line is different.",
							"Batch Manager");
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Error Occured", e);
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			cancelPressed();
		} else if (buttonId == ShowTop) {
			topOrBottom = ShowTop;
			showInitResourceList();
			displayLabelValues();
			displayContents();
		} else if (buttonId == ShowBottom) {
			topOrBottom = ShowBottom;
			showInitResourceList();
			displayLabelValues();
			displayContents();
		}
	}

	private class FileResourceResultViewLabelProvider extends LabelProvider
			implements ITableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			if (columnIndex == 0)
				result = Integer.toString(++index);
			else {
				String[] colums = (String[]) element;
				if (colums.length >= (columnIndex))
					result = colums[columnIndex - 1];
			}
			return result;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	private static class FileResourceResultViewContentProvider implements
			IStructuredContentProvider {
		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object obj, Object obj1) {
		}

		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}
	}

	private class FileResourceResultViewLabelProviderWithoutVO extends
			LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			String result = "";
			if (columnIndex == 0) {
				result = Integer.toString(++index);
				return result;
			} else {
				result = ((String[]) element)[0];
			}
			return result;
		}
	}

	private static class FileResourceResultViewContentProviderWithoutVO
			implements IStructuredContentProvider {
		private FileResourceResultViewContentProviderWithoutVO() {
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object obj, Object obj1) {
		}

		public Object[] getElements(Object parent) {
			String[][] lines = (String[][]) parent;

			return lines;
		}
	}
}
