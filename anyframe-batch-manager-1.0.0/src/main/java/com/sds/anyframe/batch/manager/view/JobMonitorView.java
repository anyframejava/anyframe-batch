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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.sds.anyframe.batch.agent.exception.NoAvailableDestinationAgentException;
import com.sds.anyframe.batch.agent.management.AgentCondition;
import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.service.IJobMonitor;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.state.job.IJobStateClient;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.UIConstants;
import com.sds.anyframe.batch.manager.controller.ExecutionErrorLogAction;
import com.sds.anyframe.batch.manager.controller.ExecutionLogAction;
import com.sds.anyframe.batch.manager.controller.ExportToExcelAction;
import com.sds.anyframe.batch.manager.controller.JobDetailInfoAction;
import com.sds.anyframe.batch.manager.controller.ShowSamAction;
import com.sds.anyframe.batch.manager.controller.UnblockJobAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.dialog.CalendarDialog;
import com.sds.anyframe.batch.manager.dialog.JobDetailInfoDialog;
import com.sds.anyframe.batch.manager.dialog.SelectFileResourceDialog;
import com.sds.anyframe.batch.manager.providers.TableTreeVieverLabelProvider;
import com.sds.anyframe.batch.manager.providers.TableViewContentProvider;
import com.sds.anyframe.batch.manager.service.AgentController;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.utils.ServerListUtil;
import com.sds.anyframe.batch.manager.view.support.TableCellModifier;
import com.sds.anyframe.batch.manager.view.support.TreeViewerSorter;
import com.sds.anyframe.batch.manager.view.support.TreeViewerSorterHandler;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobMonitorView extends EditorPart implements IJobChangedObserver {
	public static final String ID = "com.sds.anyframe.batch.manager.jobmonitor";

	private final String[] statusItems = { "ALL", "BLOCKING", "WAITING", "READY",
			"RUNNING", "COMPLETED", "FAILED", "STOPPED"};

	private Combo serverListCombo;

	private TreeViewer treeViewer;

	private ServerInfo currentServer;

	private static List<Job> jobList = new ArrayList<Job>();

	private Label labelWatingCount;
	private Label labelReadyCount;
	private Label labelRunCount;
	private Label labelCompleteCount;
	private Label labelStopCount;
	private Label labelFailCount;

	private Map<String, ServerInfo> serverMap;

	private IJobMonitor jobMonitor;

	private Text jobId;

	private Text fatchCount;

	private Text fromDate;

	private Text toDate;

	private Combo statusCombo;

	private Policy policy;

	private int pageIndex = 1;

	private int pageSize = 20;

	private IJobStateClient jobStateClient;

	private Label labelBlockingCount;

	private Button jobBlocking;

	private AgentCondition agentCondition;

	private Button systemAgent;

	private Label rows;

	public JobMonitorView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		try {

			Composite group = new Composite(parent, SWT.NONE); 
			group.setLayout(new GridLayout(1, false));
			
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.widthHint = 0;
			gridData.horizontalSpan = 0;
			gridData.verticalSpan = 0;
			
			group.setLayoutData(gridData);

			createServerPanel(group);
			
			createControlsPanel(group);
			
			createJobSeriesPanel(group);

		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}

		createTreeTable(parent);
		createHookContextMenu();

	}

	private void createControlsPanel(Composite panel) throws Exception {
		Group container = new Group(panel, SWT.NONE);
		container.setText("Controls");

		createToolButtons(container);
		
		GridData gridData = new GridData(SWT.LEFT);

		container.setLayoutData(gridData);
		container.setLayout(new GridLayout(15, false));


		gridData = new GridData(SWT.LEFT);
		gridData.widthHint = 100;

		Label fromLabel = new Label(container, SWT.NONE);
		fromLabel.setText("Start date: ");
		gridData = new GridData(SWT.CENTER);
		gridData.widthHint = 65;

		fromDate = new Text(container, SWT.BORDER);
		fromDate.setLayoutData(gridData);
		fromDate.setText(BatchUtil.todayDate());
		fromDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.keyCode == 13) {
					try {
						displayTreeView(true);
					} catch (Exception e) {
						MessageUtil.showErrorMessage("Batch Manager",
								AgentUtils.getStackTraceString(e), e);
					}
				}
			}
		});
		Button fromDateButton = new Button(container, SWT.NONE);
		fromDateButton.setText("..");
		fromDateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				CalendarDialog cd = new CalendarDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				String date = (String) cd.open();
				if (date != null)
					fromDate.setText(date);
			}
		});
		Label endLabel = new Label(container, SWT.NONE);
		endLabel.setText("End date :");

		gridData = new GridData(SWT.CENTER);
		gridData.widthHint = 65;

		toDate = new Text(container, SWT.BORDER);
		toDate.setLayoutData(gridData);
		toDate.setText(BatchUtil.todayDate());
		toDate.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.keyCode == 13) {
					try {
						displayTreeView(true);
					} catch (Exception e) {
						MessageUtil.showErrorMessage("Batch Manager",
								AgentUtils.getStackTraceString(e), e);
					}
				}
			}
		});
		Button toDateButton = new Button(container, SWT.NONE);
		toDateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				CalendarDialog cd = new CalendarDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				String date = (String) cd.open();
				if (date != null)
					toDate.setText(date);
			}
		});
		toDateButton.setText("..");
		Label statusLabel = new Label(container, SWT.NONE);
		statusLabel.setText("Job status :");
		statusCombo = new Combo(container, SWT.READ_ONLY);
		statusCombo.setLayoutData(BatchUtil.getWidthSizedGridData(72));
		for (int index = 0; index < statusItems.length; index++) {
			statusCombo.add(statusItems[index], index);
		}
		statusCombo.select(0);
		Label jobLabel = new Label(container, SWT.NONE);
		jobLabel.setText("Job Id :");

		gridData = new GridData(SWT.LEFT);
		gridData.widthHint = 80;

		jobId = new Text(container, SWT.BORDER);
		jobId.setLayoutData(gridData);
		jobId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.keyCode == 13) {
					try {
						displayTreeView(true);
					} catch (Exception e) {
						MessageUtil.showErrorMessage("Batch Manager",
								AgentUtils.getStackTraceString(e), e);
					}
				}
			}
		});

		Label fatchCountLabel = new Label(container, SWT.NONE);
		fatchCountLabel.setText("Fetch Count :");
		fatchCount = new Text(container, SWT.BORDER | SWT.RIGHT);
		fatchCount.setLayoutData(BatchUtil.getWidthSizedHorizontalSpanGridData(
				30, 1));
		fatchCount.setText("20");
		fatchCount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.keyCode == 13) {
					try {
						displayTreeView(true);
					} catch (Exception e) {
						MessageUtil.showErrorMessage("Batch Manager",
								AgentUtils.getStackTraceString(e), e);
					}
				}
			}
		});

	}

	private void createJobSeriesPanel(Composite container) {
		
		Group group = new Group(container, SWT.NONE);
		group.setText("Job Status");
		group.setLayout(new GridLayout(14, false));
		
		GridData gridData = new GridData(SWT.LEFT);
		gridData.widthHint=105;
		
		GridData gridDataComplete = new GridData(SWT.LEFT);
		gridDataComplete.widthHint=125;
		
		Font font = new Font(null, "", 8, SWT.NORMAL);
	
		Label labelBlocking = new Label(group, SWT.NONE);
		labelBlocking.setImage(IconImageUtil.getIconImage("blocking.gif"));

		labelBlockingCount = new Label(group, SWT.NONE);
		labelBlockingCount.setText(JobStatus.BLOCKING + "(0)");
		labelBlockingCount.setFont(font);
		labelBlockingCount.setLayoutData(gridData);
		
		
		Label labelWait = new Label(group, SWT.NONE);
		labelWait.setImage(IconImageUtil.getIconImage("waiting.gif"));

		
		labelWatingCount = new Label(group, SWT.NONE);
		labelWatingCount.setText(JobStatus.WAITING + "(0)");
		labelWatingCount.setFont(font);
		labelWatingCount.setLayoutData(gridData);

		Label labelReady = new Label(group, SWT.NONE);
		labelReady.setImage(IconImageUtil.getIconImage("ready.gif"));

		labelReadyCount = new Label(group, SWT.NONE);
		labelReadyCount.setText(JobStatus.READY + "(0)");
		labelReadyCount.setFont(font);
		labelReadyCount.setLayoutData(gridData);

		Label labelRun = new Label(group, SWT.NONE);
		labelRun.setImage(IconImageUtil.getIconImage("run.gif"));

		labelRunCount = new Label(group, SWT.NONE);
		labelRunCount.setText(JobStatus.RUNNING + "(0)");
		labelRunCount.setFont(font);
		labelRunCount.setLayoutData(gridData);

		Label labelComplete = new Label(group, SWT.NONE);
		labelComplete.setImage(IconImageUtil.getIconImage("complete.gif"));

		labelCompleteCount = new Label(group, SWT.NONE);
		labelCompleteCount.setText(JobStatus.COMPLETED + "(0)");
		labelCompleteCount.setFont(font);
		labelCompleteCount.setLayoutData(gridDataComplete);

		Label labelStop = new Label(group, SWT.NONE);
		labelStop.setImage(IconImageUtil.getIconImage("stop.gif"));

		labelStopCount = new Label(group, SWT.NONE);
		labelStopCount.setText(JobStatus.STOPPED + "(0)");
		labelStopCount.setFont(font);
		labelStopCount.setLayoutData(gridData);

		Label labelFail = new Label(group, SWT.NONE);
		labelFail.setImage(IconImageUtil.getIconImage("fail.gif"));

		labelFailCount = new Label(group, SWT.NONE);
		labelFailCount.setText(JobStatus.FAILED + "(0)");
		labelFailCount.setFont(font);
		labelFailCount.setLayoutData(gridData);

//		Label labelGarbage = new Label(group, SWT.NONE);
//		labelGarbage.setImage(IconImageUtil.getIconImage("garbaged.gif"));
//
//		labelGabageCount = new Label(group, SWT.NONE);
//		labelGabageCount.setText(JobStatus.GARBAGED + "(  0  )");
//		labelGabageCount.setFont(font);
	}
	

	private void createServerPanel(Composite group) {
		
		Group groupAgentStatus = new Group(group, SWT.NONE);
		groupAgentStatus.setText("Servers");
		groupAgentStatus.setLayout(new GridLayout(5, false));

		Font font = new Font(null, "", 9, SWT.NORMAL);
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 200;
		
		Label labelServerIP = new Label(groupAgentStatus, SWT.NONE);
		labelServerIP.setText("Server : ");
		
		serverListCombo = new Combo(groupAgentStatus, SWT.READ_ONLY);
		try {
		serverMap = ServerListUtil.fillServerListCombo(serverListCombo);
		if(serverMap != null)
			serverListCombo.setVisibleItemCount(serverMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		serverListCombo.setLayoutData(gridData);

		serverListCombo.select(0);
		serverListCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				currentServer = serverMap.get(serverListCombo.getText());
				if(serverListCombo.getSelectionIndex() == 0)
					return;
				getPolicy();
			}
		});
		
		jobBlocking = new Button(groupAgentStatus, SWT.CHECK);
		jobBlocking.setText(" Block all jobs ");
		jobBlocking.setFont(font);
		jobBlocking.setSelection(false);
		jobBlocking.setEnabled(false);
		
		systemAgent = new Button(groupAgentStatus, SWT.CHECK);
		systemAgent.setText(" System agent mode ?");
		systemAgent.setFont(font);
		systemAgent.setSelection(false);
		systemAgent.setEnabled(false);
	}

	private void createToolButtons(Composite panel) {
		ToolBar stateToolBar = new ToolBar(panel, SWT.NONE);
		stateToolBar.setLayout(new GridLayout(3, false));

		ToolItem searchButtonItem = new ToolItem(stateToolBar, SWT.BUTTON1);
		searchButtonItem.setImage(IconImageUtil.getIconImage("find.gif"));
		searchButtonItem.setText("Search");
		searchButtonItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					pageIndex = 1;
					displayTreeView(true);
				} catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager", AgentUtils
							.getStackTraceString(e), e);
				}
			}
		});

		ToolItem nextButtonItem = new ToolItem(stateToolBar, SWT.BUTTON1);
		nextButtonItem.setImage(IconImageUtil.getIconImage("next.gif"));
		nextButtonItem.setText("Next");
		nextButtonItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				try {
					pageIndex++;
					displayTreeView(false);
				} catch (Exception e) {
					MessageUtil.showErrorMessage("Batch Manager", AgentUtils
							.getStackTraceString(e), e);
				}
			}
		});

		new ToolItem(stateToolBar, SWT.SEPARATOR);

		ToolBar buttonToolBar = new ToolBar(panel, SWT.NONE);
		ToolItem stopButtonItem = new ToolItem(buttonToolBar, SWT.BUTTON1);

		stopButtonItem.setImage(IconImageUtil.getIconImage("stop.gif"));
		stopButtonItem.setText("Stop");
		stopButtonItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selectedItems = treeViewer.getTree().getSelection();

				List<Job> jobs = new ArrayList<Job>();

				for (TreeItem item : selectedItems) {
					Job job = (Job) item.getData();
					jobs.add(job);
				}

				if (jobs.size() == 0)
					return;

				List<Job> killprocessList = new ArrayList<Job>();

				if (jobs != null && jobs.size() > 0) {
					List<Long> pidList = new ArrayList<Long>();
					for (Job element : jobs) {
						if (element.getPid() > 0
								&& (element.getJobStatus() == JobStatus.READY || element.getJobStatus() == JobStatus.BLOCKING
										|| element.getJobStatus() == JobStatus.WAITING || element
										.getJobStatus() == JobStatus.RUNNING)) {
							killprocessList.add(element);
							pidList.add(element.getPid());
						} else {
							MessageUtil.showMessage(
									"You can only stop the valid job["
											+ element.toString() + "]",
									"Batch Manager");
						}
					}

					if (killprocessList != null && killprocessList.size() > 0) {
						MessageBox messageBox = new MessageBox(new Shell(),
								SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
						messageBox.setText("Confirm");
						messageBox
								.setMessage("kill the selected process? process id : "
										+ pidList);
						if (messageBox.open() == SWT.OK) {
							for (Job job : killprocessList)
								try {
									jobStateClient.killAndStop(job, AgentUtils.getIp());
								} catch (Exception e1) {
									if(e1 instanceof NoAvailableDestinationAgentException) {
										messageBox = new MessageBox(new Shell(),
												SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
										messageBox.setText("Warning to kill the process");
										messageBox.setMessage(e1.getMessage() + ", do you only want to stop the job.");
										if (messageBox.open() == SWT.OK) {
											try {
												jobStateClient.stopJob(job);
											} catch (Exception e2) {
												MessageUtil.showMessage(e2.getMessage(), "Batch Manager");
											}
										}
									} else {
										MessageUtil.showMessage(e1.getMessage(), "Batch Manager");
									}
								}
							try {
								displayTreeView(true);
							} catch (Exception e1) {
								MessageUtil.showErrorMessage("Batch Manager",
										e1.getMessage(), e1);
							}
						}
					} else {
						MessageUtil.showMessage("No checked items",
								"Batch Manager");
					}
				} else {
					MessageUtil
							.showMessage("No checked items", "Batch Manager");
				}
			}
		});
	}

	public enum COLUMNS {
		SHOW_STEPS("Show Steps"), JOB_ID("Job ID"), CREATED_TIME("Created time"), LAST_UPDATED_TIME(
				"Last updated time"), ELAPSED_TIME("Elapsed time"), AVERAGE_CPU_USAGE("Cpu Usage(Avg)"), FREE_MEMORY(
				"Free Memory"), TOTAL_MEMORY("Total Memory"), ACTIVE_THREAD_COUNT(
				"Active Threads"), PID("PID"), JOB_SEQ(
				"Job Seq"), SERVER_IP("Server IP"), LOG_FILES("Log file"), RESOURCE_FILES(
				"Resource files");

		private final String title;

		public String getTitle() {
			return title;
		}

		COLUMNS(String title) {
			this.title = title;
		}
	}

	private void createTreeTable(Composite panel) {

		Group tmpGroup = new Group(panel, SWT.SHADOW_ETCHED_IN);

		tmpGroup.setLayout(new GridLayout(1, false));
		tmpGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridData gridData = new GridData(SWT.CENTER);
		gridData.widthHint = 200;
		
		rows = new Label(tmpGroup, SWT.NONE);
		rows.setText("0 row");
		rows.setLayoutData(gridData);
		
		treeViewer = new TreeViewer(tmpGroup, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);

		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.getTree().setLinesVisible(true);
		treeViewer.getTree().setHeaderVisible(true);

		TreeColumn plusColumn = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		plusColumn.setText(COLUMNS.SHOW_STEPS.getTitle());
		plusColumn.setWidth(30);
		plusColumn.setMoveable(true);

		TreeColumn jobIdColumn = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		jobIdColumn.setText(COLUMNS.JOB_ID.getTitle());
		jobIdColumn.setWidth(120);
		jobIdColumn.setMoveable(true);

		TreeColumn createDateColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		createDateColumn.setText(COLUMNS.CREATED_TIME.getTitle());
		createDateColumn.setWidth(120);
		createDateColumn.setMoveable(true);

		TreeColumn updateDateColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		updateDateColumn.setText(COLUMNS.LAST_UPDATED_TIME.getTitle());
		updateDateColumn.setWidth(120);
		updateDateColumn.setMoveable(true);

		TreeColumn elapsedTimeColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		elapsedTimeColumn.setText(COLUMNS.ELAPSED_TIME.getTitle());
		elapsedTimeColumn.setWidth(70);
		elapsedTimeColumn.setMoveable(true);
		
		TreeColumn averageCpuUsageColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		averageCpuUsageColumn.setText(COLUMNS.AVERAGE_CPU_USAGE.getTitle());
		averageCpuUsageColumn.setWidth(40);
		averageCpuUsageColumn.setMoveable(true);

		TreeColumn freeMemoryColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		freeMemoryColumn.setText(COLUMNS.FREE_MEMORY.getTitle());
		freeMemoryColumn.setWidth(40);
		freeMemoryColumn.setMoveable(true);

		TreeColumn totalMemoryColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		totalMemoryColumn.setText(COLUMNS.TOTAL_MEMORY.getTitle());
		totalMemoryColumn.setWidth(40);
		totalMemoryColumn.setMoveable(true);
		
		TreeColumn activeThreadsColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		activeThreadsColumn.setText(COLUMNS.ACTIVE_THREAD_COUNT.getTitle());
		activeThreadsColumn.setWidth(30);
		activeThreadsColumn.setMoveable(true);
		

		TreeColumn processIdColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		processIdColumn.setText(COLUMNS.PID.getTitle());
		processIdColumn.setWidth(40);
		processIdColumn.setMoveable(true);

		TreeColumn jobSeqColumn = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		jobSeqColumn.setText(COLUMNS.JOB_SEQ.getTitle());
		jobSeqColumn.setWidth(40);
		jobSeqColumn.setMoveable(true);

		TreeColumn serverIpColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		serverIpColumn.setText(COLUMNS.SERVER_IP.getTitle());
		serverIpColumn.setWidth(70);
		serverIpColumn.setMoveable(true);

		TreeColumn logFilesColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		logFilesColumn.setText(COLUMNS.LOG_FILES.getTitle());
		logFilesColumn.setWidth(30);
		logFilesColumn.setMoveable(true);

		TreeColumn resourceFilesColumn = new TreeColumn(treeViewer.getTree(),
				SWT.LEFT);
		resourceFilesColumn.setText(COLUMNS.RESOURCE_FILES.getTitle());
		resourceFilesColumn.setWidth(30);
		resourceFilesColumn.setMoveable(true);
		
		// treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new TableViewContentProvider());
		treeViewer.setLabelProvider(new TableTreeVieverLabelProvider());

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent doubleclickevent) {
				IStructuredSelection selection = (IStructuredSelection) doubleclickevent
						.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof Job) {
					Job job = (Job) obj;

					JobDetailInfoDialog dialog = new JobDetailInfoDialog(
							PlatformUI.getWorkbench().getDisplay()
									.getActiveShell(), currentServer, job);
					dialog.open();
				}
			}
		});

		treeViewer.setColumnProperties(new String[] {
				COLUMNS.SHOW_STEPS.getTitle(), COLUMNS.JOB_ID.getTitle(),
				COLUMNS.CREATED_TIME.getTitle(),
				COLUMNS.LAST_UPDATED_TIME.getTitle(),
				COLUMNS.ELAPSED_TIME.getTitle(),
				COLUMNS.AVERAGE_CPU_USAGE.getTitle(),
				COLUMNS.FREE_MEMORY.getTitle(),
				COLUMNS.TOTAL_MEMORY.getTitle(),
				COLUMNS.ACTIVE_THREAD_COUNT.getTitle(),
				COLUMNS.PID.getTitle(),
				COLUMNS.JOB_SEQ.getTitle(),
				COLUMNS.SERVER_IP.getTitle(), 
				COLUMNS.LOG_FILES.getTitle(),
				COLUMNS.RESOURCE_FILES.getTitle()});
		
		TableCellModifier modifier = new TableCellModifier(treeViewer);
		treeViewer.setCellModifier(modifier);
		treeViewer.setCellEditors(new CellEditor[] { null, // SHOW_CHILD

				new TextCellEditor(treeViewer.getTree()), // JOB_ID
				null, // CREATED_TIME
				null, // LAST_UPDATED_TIME
				null, // ELAPSED_TIME
				null, // AVERAGE_CPU_USAGE
				null, // FREE_MEMORY
				null, // TOTAL_MEMORY
				null, // ACTIVE_THREAD_COUNT
				null, // PID
				null, // JOB_SEQ
				null, // SERVER_IP
				new TextCellEditor(treeViewer.getTree()), // LOG_FILES
				null // RESOURCE_FILES
		});
		TreeViewerSorter sorter = new TreeViewerSorter(treeViewer);
		new TreeViewerSorterHandler(treeViewer.getTree(), sorter);
		treeViewer.setSorter(sorter);

	}

	private void displayTreeView(boolean search) throws Exception {
		if(serverListCombo.getSelectionIndex() == 0)
			return;
		
		Date startDate = null, endDate = null;
		try {
			startDate = new SimpleDateFormat("yyyy-MM-dd").parse(fromDate
					.getText());
			endDate = new SimpleDateFormat("yyyy-MM-dd")
					.parse(toDate.getText());
		} catch (ParseException e) {
			MessageUtil.showMessage(e.getMessage(), "Batch Manager");
			return;
		}

		if (search)
			pageIndex = 1;

		String item = statusCombo.getItem(statusCombo.getSelectionIndex());
		JobStatus jobStatus = item.equals("ALL") ? null : JobStatus
				.valueOf(item);
		try {
			if (fatchCount.getText() != null && fatchCount.getText() != "")
				pageSize = Integer.parseInt(fatchCount.getText());

			PageRequest pageRequest = new PageRequest(0, pageIndex, pageSize);

			List<Job> tmpJobList = jobMonitor.listJob(currentServer
					.getAddress().split(":")[0], startDate, endDate, jobStatus,
					jobId.getText(), pageRequest);

			if (tmpJobList == null || tmpJobList.size() == 0) {
				if (!search) {
					MessageUtil.showMessage("There is no more data.",
							"Batch Manager");
				} else {
					rows.setText("0 row");
					treeViewer.setInput(tmpJobList);
					treeViewer.refresh();
				}
				return;
			}

			if (search) {
				jobList = tmpJobList;
			} else
				// Next
				jobList.addAll(tmpJobList);

		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
			e.printStackTrace();
			return;
		}

		rows.setText(jobList.size() + " rows");
		treeViewer.setInput(jobList);
		treeViewer.refresh();

		countEachJobStatus();
	}

	private void countEachJobStatus() {
		int blocking = 0, waiting = 0, ready = 0, running = 0, completed = 0, stopped = 0, failed = 0, garbaged = 0;
		for (Job job : jobList) {
			if (job.getJobStatus() == JobStatus.BLOCKING)
				blocking++;
			else if (job.getJobStatus() == JobStatus.WAITING)
				waiting++;
			else if (job.getJobStatus() == JobStatus.READY)
				ready++;
			else if (job.getJobStatus() == JobStatus.RUNNING)
				running++;
			else if (job.getJobStatus() == JobStatus.COMPLETED)
				completed++;
			else if (job.getJobStatus() == JobStatus.STOPPED)
				stopped++;
			else if (job.getJobStatus() == JobStatus.FAILED)
				failed++;
//			else if (job.getJobStatus() == JobStatus.GARBAGED)
//				garbaged++;
		}

		labelBlockingCount.setText(JobStatus.BLOCKING + "("+blocking+")");
		labelWatingCount.setText(JobStatus.WAITING + "("+waiting+")");
		labelReadyCount.setText(JobStatus.READY + "("+ready+")");
		labelRunCount.setText(JobStatus.RUNNING + "("+running+")");
		labelCompleteCount.setText(JobStatus.COMPLETED + "("+completed+")");
		labelStopCount.setText(JobStatus.STOPPED + "("+stopped+")");
		labelFailCount.setText(JobStatus.FAILED + "("+failed+")");
//		labelGabageCount.setText(JobStatus.GARBAGED + "(" + garbaged + ")");

	}

	@Override
	public void setFocus() {

	}

	@Override
	public void doSave(IProgressMonitor iprogressmonitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite ieditorsite, IEditorInput ieditorinput)
			throws PartInitException {
		setSite(ieditorsite);
		setInput(ieditorinput);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private void getPolicy() {
		treeViewer.setInput(null);
		treeViewer.refresh();
		
		String serverName = serverListCombo.getText();
		currentServer = serverMap.get(serverName);
		
		setTitle("Job Monitor - " + serverName);
		
		try {
			Policy tmp = AgentController.getPolicy(currentServer);
			if (tmp != null)
				policy = tmp;
			AgentCondition condition = AgentController
					.getAgentCondition(currentServer);
			if (condition != null) {
				this.agentCondition = condition;
				updateAgentConditions();
			}


			jobMonitor = (IJobMonitor) ProxyHelper.getProxyInterface(
					currentServer.getAddress(), IJobMonitor.SERVICE_NAME,
					IJobMonitor.class.getName());
			jobStateClient = (IJobStateClient) ProxyHelper.getProxyInterface(
					currentServer.getAddress(), IJobStateClient.SERVICE_NAME,
					IJobStateClient.class.getName());

			treeViewer.setContentProvider(new TableViewContentProvider(
					jobMonitor));
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager",
					AgentUtils.getStackTraceString(e), e);
		}
	}

	private void updateAgentConditions() {
		jobBlocking.setSelection(agentCondition.getClusteredCondition().isBlocking());
		systemAgent.setSelection(agentCondition.isSystemAgent());
	}

	private void createHookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				createHookContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void createHookContextMenu(IMenuManager manager) {
		TreeItem[] selection = treeViewer.getTree().getSelection();

		if (selection.length == 0)
			return;

		TreeItem item = selection[0];
		Job job = (Job) item.getData();

		createJobMenus(manager, job);
		
		createStepMenus(manager, job);

		manager.add(new ExportToExcelAction((List<Job>) treeViewer.getInput()));
	}

	private void createJobMenus(IMenuManager manager, Job job) {
		String logFiles = job.getLogFiles();

		if (logFiles != null) {
			if (job.getParent() == null)
				manager.add(new ExecutionLogAction(currentServer, logFiles));
		}
		
		if (job.getJobStatus() == JobStatus.BLOCKING) {
				manager.add( new UnblockJobAction(jobStateClient, job, this));
		}

		try {
			if (job.getExitMessage() != null
					&& !job.getExitMessage().trim().equals("")) {
				manager.add(new ExecutionErrorLogAction(job.getExitMessage()));
			}
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}
		if(job !=null)
			manager.add(new JobDetailInfoAction(currentServer, job, "View Job Detail Info"));
	}

	private void createStepMenus(IMenuManager manager, Job job) {
		String logFiles = job.getLogFiles();

		if (logFiles != null) {
			// Job Log
			if (job.getParent() != null)
				// Normal Step Log
				if (logFiles.indexOf(";") == -1)
					manager
							.add(new ExecutionLogAction(currentServer, logFiles, "View step log"));
				// Parallel Logs
				else {
					MenuManager fileMenus = new MenuManager("View parallel log files",
							"View parallel log files");

					String[] split = logFiles.split(";");

					for (int i = 0; i < split.length; i++) {
						fileMenus.add(new ExecutionLogAction(currentServer,
								split[i], split[i]));
					}

					manager.add(fileMenus);
				}
		}
		
		
	

		List<Resource> resources = job.getResources();
		if (resources.size() > 0) {

			MenuManager fileMenus = new MenuManager("View files", "Files");

			for (Resource resource : resources) {

				if(resource.getType() == ResourceType.DATABASE)
					continue;
				
				String fullFileName = resource.getResourceName();
				// SAM파일의 경우 임시 파일을 사용하고 STEP이 완료되면 정식 이름으로 변경된다. 따라서 Runtime에서 사용하는 .tmp를
				// File 이름으로 사용한다.
				if(resource.getIoType() == ResourceIoType.WRITE && job.getStepStatus() != StepStatus.COMPLETED) {
					fullFileName += UIConstants.TEMP_EXT;
				}
				
				FileInfoVO file = new FileInfoVO();
				file.setFullPathName(fullFileName);
				file.setName(fullFileName);

				MenuManager aFileMenu = new MenuManager(fullFileName);

				fileMenus.add(aFileMenu);

				aFileMenu.add(new ShowSamAction(currentServer, file,
						SelectFileResourceDialog.ResourceType.SAM));
				aFileMenu.add(new ShowSamAction(currentServer, file,
						SelectFileResourceDialog.ResourceType.VSAM));
			}

			manager.add(fileMenus);
		}
	}

	@Override
	public void jobChanged() {
		try {
			pageIndex = 1;
			displayTreeView(true);
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", AgentUtils
					.getStackTraceString(e), e);
		}
	}
}
