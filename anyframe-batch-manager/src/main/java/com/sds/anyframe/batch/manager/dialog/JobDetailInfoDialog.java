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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceStatus;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.service.IJobMonitor;
import com.sds.anyframe.batch.manager.UIConstants;
import com.sds.anyframe.batch.manager.controller.FileServiceAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.sorter.SelectResourceSorter;
import com.sds.anyframe.batch.manager.providers.ResourceTableViewerContentProider;
import com.sds.anyframe.batch.manager.providers.ResourceTableViewerLabelProvider;
import com.sds.anyframe.batch.manager.providers.StepTableViewerContentProider;
import com.sds.anyframe.batch.manager.providers.StepTableViewerLabelProvider;
import com.sds.anyframe.batch.manager.service.JobDetailInfoExecutor;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;
import com.sds.anyframe.batch.manager.utils.ProxyHelper;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobDetailInfoDialog extends Dialog {
	private Composite dialogArea;
	private TableViewer stepTableViewer = null;
	private TableViewer resourceTableViewer = null;
	private JobDetailInfoExecutor executor;
	private ScheduledExecutorService scheduler;
	private IJobMonitor jobMonitor;

	private static final String shelName = "Job Detail Information";
	private ServerInfo serverInfo;
	private List<Job> steps = new ArrayList<Job>();
	private Job job;
	private Label labelJobIdResult;
	private Label labelJobStateResultImage;
	private Label labelJobStateResultText;
	private Label labelJobPidResult;
	private Label labelCreateTimeResult;
	private Label labelUpdateTimeResult;
	private Label labelJobSeqResult;
	private Label labelJobLogFileResult;
	private Label activeThreadCount;
	private Label currentCpuUsage;
	private Label totalCpuUsage;
	private Label freeMemory;
	private Label totalMemory;
	private Display display;
	private Button refreshButton;
	private Label labelEmpty;
	private Label usageMemory;
	private Label labelempty;
	private Label labelLockingResource;
	public static final int ShowTop = 1;
	public static final int ShowBottom = 2;
	private Job lastStep;
	
	public static final String[] stepColumnHeaders = {
		StepTableCOLUMN.STEP_ID.getTitle(),
		StepTableCOLUMN.STEP_STATE.getTitle(),
		StepTableCOLUMN.CREATED_TIME.getTitle(),
		StepTableCOLUMN.LAST_UPDATED_TIME.getTitle(),
		StepTableCOLUMN.ELAPSED_TIME.getTitle(),
		StepTableCOLUMN.AVERAGE_CPU_USAGE.getTitle(),
		StepTableCOLUMN.CURRENT_CPU_USAGE.getTitle(),
		StepTableCOLUMN.FREE_MEMORY.getTitle(),
		StepTableCOLUMN.TOTAL_MEMORY.getTitle(),
		StepTableCOLUMN.ACTIVE_THREAD_COUNT.getTitle()
		 };

	public static final String[] resourceColumnHeaders = {
		ResourceTableCOLUMN.RESOURCE_TYPE.getTitle(),
		ResourceTableCOLUMN.IO_TYPE.getTitle(),
		ResourceTableCOLUMN.RESOURCE_NAME.getTitle(),
		ResourceTableCOLUMN.STATUS.getTitle(),
		ResourceTableCOLUMN.SIZE.getTitle(),
		ResourceTableCOLUMN.MOD_DATE.getTitle(),
		ResourceTableCOLUMN.TRANSACTION_COUNT.getTitle()  };

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		Composite centelPanel = new Composite(parent, SWT.CENTER);
		centelPanel.setLayout(new GridLayout(1, false));
		createButton(centelPanel, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	public JobDetailInfoDialog(Shell activeShell, ServerInfo serverInfo, Job job) {
		super(activeShell);
		int shellType = getShellStyle();
		// Remove Modal from Super
		shellType = shellType ^= SWT.APPLICATION_MODAL;

		setShellStyle(shellType | SWT.RESIZE | SWT.MODELESS);
		this.serverInfo = serverInfo;
		this.job = job;
		display = activeShell.getDisplay();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(shelName);
		// shell.setSize(950, 650);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		dialogArea = (Composite) super.createDialogArea(parent);

		GridLayout layout = new GridLayout(1, false);

		dialogArea.setLayout(layout);
		getDetailJob();

		Composite jobPanel = new Composite(dialogArea, SWT.NULL);
		jobPanel.setLayout(new GridLayout(2, false));
		Composite stepPanel = new Composite(dialogArea, SWT.NULL);
		stepPanel.setLayout(new GridLayout(2, false));
		Composite resourcePanel = new Composite(dialogArea, SWT.NULL);
		resourcePanel.setLayout(new GridLayout(1, false));

		GridData jobData = new GridData(GridData.FILL, GridData.FILL, true,
				false, 20, 1);

		GridData stepData = new GridData(GridData.FILL, GridData.FILL, true,
				true, 1, 20);

		GridData resourceData = new GridData(GridData.FILL, GridData.FILL,
				true, true, 20, 20);

		stepPanel.setLayoutData(stepData);
		jobPanel.setLayoutData(jobData);
		resourcePanel.setLayoutData(resourceData);

		createJobInfo(jobPanel);
		createRefresh(jobPanel);
		createStepInfo(stepPanel);
		createPerformInfo(stepPanel);
		createResourceInfo(resourcePanel);

		return dialogArea;
	}

	private void createRefresh(Composite jobPanel) {

		Composite refreshPanel = new Composite(jobPanel, SWT.NULL);
		refreshPanel.setLayout(new GridLayout(1, false));

		final Button checkboxRefresh = new Button(refreshPanel, SWT.CHECK);
		checkboxRefresh.setText("Auto Refresh");
		checkboxRefresh
				.setFont(new Font(Display.getCurrent(), "", 9, SWT.NONE));
		checkboxRefresh.setSelection(false);
		checkboxRefresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean check = checkboxRefresh.getSelection();
				if (check) {
					executeRefresh();
				} else {
					deAcitveRefresh();
				}
			}
		});

		Label labelInterval = new Label(refreshPanel, SWT.NONE);
		labelInterval.setText("Interval : 10(sec)");
		labelInterval.setFont(new Font(Display.getCurrent(), "", 9, SWT.NONE));

		refreshButton = new Button(refreshPanel, SWT.PUSH | SWT.FLAT);
		refreshButton.setText("Refresh");
		refreshButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});

	}

	private void createPerformInfo(Composite composite) {
		// TODO Auto-generated method stub
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Performance Information");

		GridData data = new GridData(GridData.FILL_VERTICAL,
				GridData.FILL_HORIZONTAL, true, true, 0, 0);

		group.setLayoutData(data);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelActiveThreadCount = new Label(group, SWT.BOLD);
		labelActiveThreadCount.setText("Active Thread Count : ");

		activeThreadCount = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelCurrentCpuUsage = new Label(group, SWT.BOLD);
		labelCurrentCpuUsage.setText("Current CPU Usage : ");

		currentCpuUsage = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelTotalCpuUsage = new Label(group, SWT.BOLD);
		labelTotalCpuUsage.setText("Average CPU Usage : ");

		totalCpuUsage = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelFreeMemory = new Label(group, SWT.BOLD);
		labelFreeMemory.setText("Free Memory(mb): ");

		freeMemory = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelUsageMemory = new Label(group, SWT.BOLD);
		labelUsageMemory.setText("Used Memory(mb): ");

		usageMemory = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		Label labelTotalMemory = new Label(group, SWT.BOLD);
		labelTotalMemory.setText("Total Memory(mb): ");

		totalMemory = new Label(group, SWT.NONE);

		labelempty = new Label(group, SWT.BOLD);
		labelempty = new Label(group, SWT.BOLD);

		refreshLabel();

	}

	private void getDetailJob() {
		try {
			jobMonitor = (IJobMonitor) ProxyHelper.getProxyInterface(serverInfo
					.getAddress(), IJobMonitor.SERVICE_NAME, IJobMonitor.class
					.getName());

			job = jobMonitor.getDetailJob(job);
			steps = job.getChildrens();
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Error occured", e);
		}
	}

	private void createJobInfo(Composite composite) {

		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText("Job Information");

		Composite jobPanelTop = new Composite(group, SWT.NULL);
		jobPanelTop.setLayout(new GridLayout(7, false));

		Label labelJobId = new Label(jobPanelTop, SWT.BOLD);
		labelJobId.setText("Job ID : ");
		labelJobId.setFont(new Font(Display.getCurrent(), "", 9, SWT.BOLD));

		labelJobIdResult = new Label(jobPanelTop, SWT.NONE);
		labelJobIdResult.setText(job.getJobId());

		Label labelJobStateTitle = new Label(jobPanelTop, SWT.BOLD);
		labelJobStateTitle.setText("\tJob State : ");
		labelJobStateTitle.setFont(new Font(Display.getCurrent(), "", 9,
				SWT.BOLD));

		labelJobStateResultImage = new Label(jobPanelTop, SWT.NONE | SWT.LEFT);

		labelJobStateResultText = new Label(jobPanelTop, SWT.NONE);

		Label labelJobPid = new Label(jobPanelTop, SWT.BOLD);
		labelJobPid.setText("\tJob Pid : ");
		labelJobPid.setFont(new Font(Display.getCurrent(), "", 9, SWT.BOLD));

		labelJobPidResult = new Label(jobPanelTop, SWT.NONE);

		Label labelCreateTime = new Label(jobPanelTop, SWT.BOLD);
		labelCreateTime.setText("Create Time : ");
		labelCreateTime
				.setFont(new Font(Display.getCurrent(), "", 9, SWT.BOLD));

		labelCreateTimeResult = new Label(jobPanelTop, SWT.NONE);

		Label labelUpdateTime = new Label(jobPanelTop, SWT.BOLD);
		labelUpdateTime.setText("\tUpdate Time : ");
		labelUpdateTime
				.setFont(new Font(Display.getCurrent(), "", 9, SWT.BOLD));

		labelEmpty = new Label(jobPanelTop, SWT.BOLD);
		labelEmpty.setImage(getState(job.getJobStatus()));
		labelEmpty.setVisible(false);

		labelUpdateTimeResult = new Label(jobPanelTop, SWT.NONE);

		Label labelJobSeq = new Label(jobPanelTop, SWT.BOLD);
		labelJobSeq.setText("\tJob Seq : ");
		labelJobSeq.setFont(new Font(Display.getCurrent(), "", 9, SWT.BOLD));

		labelJobSeqResult = new Label(jobPanelTop, SWT.NONE);

		jobPanelTop = new Composite(group, SWT.NULL);
		jobPanelTop.setLayout(new GridLayout(2, false));

		Label labelJobLogFileTitle = new Label(jobPanelTop, SWT.NONE);
		labelJobLogFileTitle.setText("Log File : ");
		labelJobLogFileTitle.setFont(new Font(Display.getCurrent(), "", 9,
				SWT.BOLD));

		labelJobLogFileResult = new Label(jobPanelTop, SWT.NONE);

	}

	private void createStepInfo(Composite composite) {

		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				true, 1, 1));

		group.setText("Step Information");

		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true,
				1, 1);

		Table table = new Table(group, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(data);

		TableColumn stepIdColumn = new TableColumn(table, SWT.None);
		stepIdColumn.setText(StepTableCOLUMN.STEP_ID.getTitle());
		stepIdColumn.setWidth(190);
		stepIdColumn.setMoveable(true);

		TableColumn stepStateColumn = new TableColumn(table, SWT.None);
		stepStateColumn.setText(StepTableCOLUMN.STEP_STATE.getTitle());
		stepStateColumn.setWidth(120);
		stepStateColumn.setMoveable(true);

		TableColumn createDateColumn = new TableColumn(table, SWT.None);
		createDateColumn.setText(StepTableCOLUMN.CREATED_TIME.getTitle());
		createDateColumn.setWidth(120);
		createDateColumn.setMoveable(true);

		TableColumn updateDateColumn = new TableColumn(table, SWT.None);
		updateDateColumn.setText(StepTableCOLUMN.LAST_UPDATED_TIME.getTitle());
		updateDateColumn.setWidth(120);
		updateDateColumn.setMoveable(true);

		TableColumn elapsedTimeColumn = new TableColumn(table, SWT.None);
		elapsedTimeColumn.setText(StepTableCOLUMN.ELAPSED_TIME.getTitle());
		elapsedTimeColumn.setWidth(100);
		elapsedTimeColumn.setMoveable(true);

		TableColumn averageCpuDateColumn = new TableColumn(table, SWT.None);
		averageCpuDateColumn.setText(StepTableCOLUMN.AVERAGE_CPU_USAGE
				.getTitle());
		averageCpuDateColumn.setWidth(100);
		averageCpuDateColumn.setMoveable(true);

		TableColumn currentCpuDateColumn = new TableColumn(table, SWT.None);
		currentCpuDateColumn.setText(StepTableCOLUMN.CURRENT_CPU_USAGE
				.getTitle());
		currentCpuDateColumn.setWidth(100);
		currentCpuDateColumn.setMoveable(true);

		TableColumn freeMemoryDateColumn = new TableColumn(table, SWT.None);
		freeMemoryDateColumn.setText(StepTableCOLUMN.FREE_MEMORY.getTitle());
		freeMemoryDateColumn.setWidth(120);
		freeMemoryDateColumn.setMoveable(true);

		TableColumn totalMemoryDateColumn = new TableColumn(table, SWT.None);
		totalMemoryDateColumn.setText(StepTableCOLUMN.TOTAL_MEMORY.getTitle());
		totalMemoryDateColumn.setWidth(120);
		totalMemoryDateColumn.setMoveable(true);

		TableColumn activeThreadCountDateColumn = new TableColumn(table,
				SWT.None);
		activeThreadCountDateColumn.setText(StepTableCOLUMN.ACTIVE_THREAD_COUNT
				.getTitle());
		activeThreadCountDateColumn.setWidth(120);
		activeThreadCountDateColumn.setMoveable(true);

	
		stepTableViewer = new TableViewer(table);
		stepTableViewer.setContentProvider(new StepTableViewerContentProider());
		stepTableViewer.setColumnProperties(new String[] {
				StepTableCOLUMN.STEP_ID.getTitle(),
				StepTableCOLUMN.STEP_STATE.getTitle(),
				StepTableCOLUMN.CREATED_TIME.getTitle(),
				StepTableCOLUMN.LAST_UPDATED_TIME.getTitle(),
				StepTableCOLUMN.ELAPSED_TIME.getTitle(),
				StepTableCOLUMN.AVERAGE_CPU_USAGE.getTitle(),
				StepTableCOLUMN.CURRENT_CPU_USAGE.getTitle(),
				StepTableCOLUMN.FREE_MEMORY.getTitle(),
				StepTableCOLUMN.TOTAL_MEMORY.getTitle(),
				StepTableCOLUMN.ACTIVE_THREAD_COUNT.getTitle()});
		stepTableViewer.setLabelProvider(new StepTableViewerLabelProvider());
		stepTableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof Job) {
					Job row = (Job) obj;

					showResources(row);
					labelLockingResource.setText("Locked Job: ");
				}
			}
		});

		Label labelTotalCount = new Label(group, SWT.NONE);
		labelTotalCount.setText("Total Steps : " + steps.size());
		showSteps();
	}

	public enum StepTableCOLUMN {
		STEP_ID("STEP_ID"), STEP_STATE("STEP_STATE"), CREATED_TIME(
				"Created time"), LAST_UPDATED_TIME("Last updated time"),ELAPSED_TIME(
				"Elapsed time"), AVERAGE_CPU_USAGE(
				"Cpu Usage(Avg)"), CURRENT_CPU_USAGE("Cpu Usage(Current)"), FREE_MEMORY("Free Memory"), TOTAL_MEMORY(
				"Total Memory"), ACTIVE_THREAD_COUNT("Active Threads") ;

		private final String title;

		public String getTitle() {
			return title;
		}

		StepTableCOLUMN(String title) {
			this.title = title;
		}
	}

	public static enum ResourceTableCOLUMN {
		RESOURCE_TYPE("Resource Type"), IO_TYPE("IO Type"), RESOURCE_NAME(
				"Resource Name"),STATUS("Status"), SIZE("Size(kb)"), MOD_DATE("Modified Date"),  TRANSACTION_COUNT("Count");

		private final String title;

		public String getTitle() {
			return title;
		}

		ResourceTableCOLUMN(String title) {
			this.title = title;
		}
	}

	private Image getState(JobStatus jobStatus) {
		// TODO Auto-generated method stub
		if (jobStatus == JobStatus.COMPLETED) {
			return IconImageUtil.getIconImage("complete.gif");
		} else if (jobStatus == JobStatus.READY) {
			boolean weired = BatchUtil.isWeiredJob(job.getLastUpdated());
			if (weired)
				return IconImageUtil.getIconImage("weiredRun.gif");
			else
				return IconImageUtil.getIconImage("ready.gif");
		} else if (jobStatus == JobStatus.RUNNING) {
			boolean weired = BatchUtil.isWeiredJob(job.getLastUpdated());
			if (weired)
				return IconImageUtil.getIconImage("weiredRun.gif");
			else
				return IconImageUtil.getIconImage("run.gif");
		} else if (jobStatus == JobStatus.FAILED) {
			return IconImageUtil.getIconImage("fail.gif");
		} else if (jobStatus == JobStatus.GARBAGED) {
			return IconImageUtil.getIconImage("garbaged.gif");
		} else if (jobStatus == JobStatus.STOPPED) {
			return IconImageUtil.getIconImage("stop.gif");
		} else if (jobStatus == JobStatus.WAITING) {
			return IconImageUtil.getIconImage("waiting.gif");
		}
		return null;
	}

	private void showSteps() {
		if (steps.size() > 0) {
			stepTableViewer.setInput(steps);
			stepTableViewer.refresh();
		}
	}

	private void createResourceInfo(Composite resourcePanel) {

		Group group = new Group(resourcePanel, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				true, 1, 20));

		group.setText("Resource Information");

		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true,
				1, 20);

		Table table = new Table(group, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(data);
		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				// determine new sort column and direction
				TableColumn sortColumn = resourceTableViewer.getTable()
						.getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				int direction = resourceTableViewer.getTable()
						.getSortDirection();
				if (sortColumn == currentColumn) {
					direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					resourceTableViewer.getTable().setSortColumn(currentColumn);
					direction = SWT.UP;
				}
				// sort the data based on column and direction
				resourceTableViewer.getTable().setSortDirection(direction);
				resourceTableViewer.setSorter(new SelectResourceSorter(
						currentColumn.getText(), direction));
			}
		};

		TableColumn resourceTypeColumn = new TableColumn(table, SWT.None);
		resourceTypeColumn
				.setText(ResourceTableCOLUMN.RESOURCE_TYPE.getTitle());
		resourceTypeColumn.setWidth(120);
		resourceTypeColumn.addListener(SWT.Selection, sortListener);
		resourceTypeColumn.setMoveable(true);

		TableColumn resourceStateColumn = new TableColumn(table, SWT.None);
		resourceStateColumn.setText(ResourceTableCOLUMN.IO_TYPE
				.getTitle());
		resourceStateColumn.setWidth(120);
		resourceStateColumn.addListener(SWT.Selection, sortListener);
		resourceStateColumn.setMoveable(true);

		TableColumn resourceNameColumn = new TableColumn(table, SWT.None);
		resourceNameColumn
				.setText(ResourceTableCOLUMN.RESOURCE_NAME.getTitle());
		resourceNameColumn.setWidth(350);
		resourceNameColumn.addListener(SWT.Selection, sortListener);
		resourceNameColumn.setMoveable(true);

		TableColumn resourceConditionColumn = new TableColumn(table, SWT.None);
		resourceConditionColumn.setText(ResourceTableCOLUMN.STATUS
				.getTitle());
		resourceConditionColumn.setWidth(120);
		resourceConditionColumn.addListener(SWT.Selection, sortListener);
		resourceConditionColumn.setMoveable(true);

		TableColumn sizeDateColumn = new TableColumn(table, SWT.None);
		sizeDateColumn.setText(ResourceTableCOLUMN.SIZE.getTitle());
		sizeDateColumn.setWidth(120);
		sizeDateColumn.setAlignment(SWT.RIGHT);
		sizeDateColumn.addListener(SWT.Selection, sortListener);
		sizeDateColumn.setMoveable(true);

		TableColumn updateDateColumn = new TableColumn(table, SWT.None);
		updateDateColumn.setText(ResourceTableCOLUMN.MOD_DATE.getTitle());
		updateDateColumn.setWidth(120);
		updateDateColumn.addListener(SWT.Selection, sortListener);
		updateDateColumn.setMoveable(true);

		TableColumn threadCountDateColumn = new TableColumn(table, SWT.None);
		threadCountDateColumn.setText(ResourceTableCOLUMN.TRANSACTION_COUNT
				.getTitle());
		threadCountDateColumn.setWidth(130);
		threadCountDateColumn.addListener(SWT.Selection, sortListener);
		threadCountDateColumn.setMoveable(true);
		threadCountDateColumn.setAlignment(SWT.RIGHT);

		resourceTableViewer = new TableViewer(table);
		resourceTableViewer
				.setContentProvider(new ResourceTableViewerContentProider());
		resourceTableViewer.setColumnProperties(new String[] {
				ResourceTableCOLUMN.RESOURCE_TYPE.getTitle(),
				ResourceTableCOLUMN.IO_TYPE.getTitle(),
				ResourceTableCOLUMN.RESOURCE_NAME.getTitle(),
				ResourceTableCOLUMN.STATUS.getTitle(),
				ResourceTableCOLUMN.SIZE.getTitle(),
				ResourceTableCOLUMN.MOD_DATE.getTitle(),
				ResourceTableCOLUMN.TRANSACTION_COUNT.getTitle() });
		resourceTableViewer
				.setLabelProvider(new ResourceTableViewerLabelProvider(this));
		resourceTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				ResourceAndFile rAndFile = (ResourceAndFile) selection
						.getFirstElement();
				Resource resource = rAndFile.getResource();

				if(resource.getType() == ResourceType.DATABASE) {
					labelLockingResource.setText("Database resource does not has any lock information");
					return;
				}

				if (resource.getStatus() == ResourceStatus.LOCKED) {
					labelLockingResource.setText("Locked Job: ");
				} else if (resource.getStatus() == ResourceStatus.RELEASED) {
					labelLockingResource.setText("This Resource is released");
				} else {
					showLockResource(resource);
				}

			}
		});
		labelLockingResource = new Label(group, SWT.NONE);
		labelLockingResource.setLayoutData(new GridData(GridData.FILL_BOTH));
		labelLockingResource.setText("Locked Job: ");

		// initResourceDailog();
	}

	private void showLockResource(Resource resource) {
		// check if the resources was locked

		try {
			jobMonitor = (IJobMonitor) ProxyHelper.getProxyInterface(serverInfo
					.getAddress(), IJobMonitor.SERVICE_NAME, IJobMonitor.class
					.getName());
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Proxy Interface", e);
			return;
		}

		Resource lockedResouirce = jobMonitor.getOwnerOfLockedResource(resource
				.getResourceName());
		String lockedJob = null;

		if (lockedResouirce != null)

			lockedJob = resource.getResourceName() + " is occupied by Job ID : "
					+ lockedResouirce.getJobId() + ", Step ID : "
					+ lockedResouirce.getStepId() + ", Job Seq: "
					+ lockedResouirce.getJobSeq();

		else
			lockedJob = "There is no locked resource for this item";

		labelLockingResource.setText("Locked Job: " + lockedJob);
	}
	
	
	private void showResources(Job job) {

		lastStep = job;
		resourceTableViewer.setInput(null);
		resourceTableViewer.refresh();

		List<Resource> resources = job.getResources();

		List<String> files = new ArrayList<String>();

		if (resources.size() > 0) {

			for (Resource resource : resources) {
				if(resource.getType() == ResourceType.FILE) {
					String fileName = resource.getResourceName();
					
					if(resource.getIoType() == ResourceIoType.WRITE && job.getStepStatus() != StepStatus.COMPLETED) {
						fileName += UIConstants.TEMP_EXT;
					}
					files.add(fileName);
				}
			}
		}

		FileServiceAction selectStepResourceAction = new FileServiceAction(
				serverInfo);
		
		List<FileInfoVO> physicalFiles = selectStepResourceAction.getFiles(files);

		List<ResourceAndFile> uiList = new ArrayList<ResourceAndFile>();

		if (resources.size() > 0) {
			for (Resource resource : resources) {

				ResourceAndFile rNf = new ResourceAndFile(resource, new FileInfoVO());
				uiList.add(rNf);
				
				if(resource.getType() == ResourceType.DATABASE)
					continue;
				
				String file = resource.getResourceName();
				if(resource.getIoType() == ResourceIoType.WRITE && job.getStepStatus() != StepStatus.COMPLETED) {
					file += UIConstants.TEMP_EXT;
				}
				
				file = file.replace('\\', '/');
				for (FileInfoVO fileVo : physicalFiles) {
					String fullPathName = fileVo.getFullPathName().replace(
							'\\', '/');
					if (file.equalsIgnoreCase(fullPathName)) {
						rNf.setFile(fileVo);
						break;
					}
				}
			}
		}

		resourceTableViewer.setInput(uiList);
		resourceTableViewer.refresh();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			cancelPressed();
		}
	}

	public static Object getStepColumnTextFromTable(Job job, int columnIndex) {

		StepTableCOLUMN[] values = StepTableCOLUMN.values();
		
		switch (values[columnIndex]) {
		case STEP_ID:
			return job.getCurrentStepId();
		case STEP_STATE:
			return job.getStepStatus().toString();
		case CREATED_TIME:
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getCreatedDate());
		case LAST_UPDATED_TIME:
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(job
					.getLastUpdated());
		
		case AVERAGE_CPU_USAGE:
			return Integer.toString((int) (job.getPerformance()
					.getTotalCpuUsage() * 100.0));
		case CURRENT_CPU_USAGE:
			return Integer.toString((int) (job.getPerformance()
					.getCurrentCpuUsage() * 100.0));
		case FREE_MEMORY:
			return Long.toString(BatchUtil.byteToMega(job.getPerformance()
					.getFreeMemory()));

		case TOTAL_MEMORY:
			return Long.toString(BatchUtil.byteToMega(job.getPerformance()
					.getTotalMemory()));
		case ACTIVE_THREAD_COUNT:
			return Integer
					.toString(job.getPerformance().getActiveThreadCount());

		case ELAPSED_TIME:
			return BatchUtil.getElapsedTime(job.getCreatedDate(), job
					.getLastUpdated());
		default:
			return "";
		}
	}

	public static Object getResourceColumnTextFromTable(ResourceAndFile rAndf,
			int columnIndex) {
		Object result = null;
		
		ResourceTableCOLUMN[] values = ResourceTableCOLUMN.values();
		
		switch (values[columnIndex]) {

		case RESOURCE_TYPE:
			return rAndf.getResource().getType().toString();

		case IO_TYPE:
			return rAndf.getResource().getIoType().toString();

		case RESOURCE_NAME:
			return rAndf.getResource().getResourceName();
		case STATUS:
			return rAndf.getResource().getStatus().toString();
		case SIZE:
			return rAndf.getFile().getSize();
			
		case MOD_DATE:
			if(rAndf.getResource().getType()==ResourceType.DATABASE){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
				return format.format(rAndf.getResource().getUpdateTime());
			}else{
				result = rAndf.getFile().getCreatedDate();
				if(result ==null)
					return "";
				else
					return result;
			}
		case TRANSACTION_COUNT:
			return rAndf.getResource().getTransactedCount().get();

		default:
			return "";
		}
	}

	public void refresh() {
		// Job과 Step 정보를 가지고 온다.
		getDetailJob();

		// Step Table을 Refresh 해 준다.
		showSteps();

		// Resource 정보를 가지고 오고 Resource Table을 Refresh 해 준다.
		if (steps.size() > 0)
			showResources(steps.get(0));

		// Job과 Performance Label 값을 Refresh해 준다.
		refreshLabel();
	}

	private void refreshLabel() {
		// Job 정보 Refresh
		labelJobIdResult.setText(job.getJobId());
		labelJobStateResultImage.setImage(getState(job.getJobStatus()));
		labelJobStateResultImage.setToolTipText(getToolTip(job));
		
		labelJobStateResultText.setText(job.getJobStatus().toString());
		labelJobStateResultText.setToolTipText(getToolTip(job));
		
		labelJobPidResult.setText(Long.toString(job.getPid()));
		labelCreateTimeResult.setText(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(job.getCreatedDate()));
		labelUpdateTimeResult.setText(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(job.getLastUpdated()));
		labelJobSeqResult.setText(Long.toString(job.getJobSeq()));

		if (job.getLogFiles() != null)
			labelJobLogFileResult.setText(job.getLogFiles());

		// Performance 정보 Refresh
		freeMemory.setText(Long.toString(BatchUtil.byteToMega(job
				.getPerformance().getFreeMemory()))
				+ "   ");
		totalCpuUsage.setText(Integer.toString((int) (job.getPerformance()
				.getTotalCpuUsage() * 100.0))
				+ "%" + "   ");
		currentCpuUsage.setText(Integer.toString((int) (job.getPerformance()
				.getCurrentCpuUsage() * 100.0))
				+ "%" + "   ");
		activeThreadCount.setText(Integer.toString(job.getPerformance()
				.getActiveThreadCount())
				+ "   ");
		totalMemory.setText(Long.toString(BatchUtil.byteToMega(job
				.getPerformance().getTotalMemory()))
				+ "       ");
		usageMemory.setText(Long.toString((BatchUtil.byteToMega(job
				.getPerformance().getTotalMemory()
				- job.getPerformance().getFreeMemory())))
				+ "       ");

	}

	private String getToolTip(Job job) {
		if(job.getJobStatus() == JobStatus.READY || job.getJobStatus() == JobStatus.RUNNING) {
			return "This job's status is not correct, check the process in OS";
		}
		return null;
	}

	public void executeRefresh() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		executor = new JobDetailInfoExecutor(display, this);
		scheduler.scheduleWithFixedDelay(executor, 0, 10, TimeUnit.SECONDS);

		refreshButton.setEnabled(false);
	}

	private void deAcitveRefresh() {
		scheduler.shutdown();
		refreshButton.setEnabled(true);
	}

	@Override
	public boolean close() {
		if (scheduler != null && !scheduler.isTerminated())
			scheduler.shutdown();
		return super.close();
	}

	public Job getLastStep() {
		return lastStep;
	}
}
