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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.security.Policy;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.controller.JobExecuteAction;
import com.sds.anyframe.batch.manager.controller.JobFileDeleteAction;
import com.sds.anyframe.batch.manager.controller.ListJobAction;
import com.sds.anyframe.batch.manager.controller.ReadJobConfigInfoAction;
import com.sds.anyframe.batch.manager.controller.SelectDatabaseResourceDialogAction;
import com.sds.anyframe.batch.manager.controller.SelectLogFileDialogAction;
import com.sds.anyframe.batch.manager.controller.GetResourcesAction;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;
import com.sds.anyframe.batch.manager.service.AgentController;
import com.sds.anyframe.batch.manager.service.JobTreeNodeInfo;
import com.sds.anyframe.batch.manager.service.StepInfoService;
import com.sds.anyframe.batch.manager.utils.BatchUtil;
import com.sds.anyframe.batch.manager.utils.IconImageUtil;
import com.sds.anyframe.batch.manager.utils.ServerListUtil;
import com.sds.anyframe.batch.manager.view.support.ExecState;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobExplorerView extends ViewPart implements PropertyChangeListener {

	public JobExplorerView() {
	}

	public static final String ID = "com.sds.anyframe.batch.manager.jobexplorer";

	private Tree jobTree;
	private Text searchText;
	private Combo serverListCombo;
	private TreeViewer treeViewer;
	private ListJobAction listJobAction = null;

	private ServerInfo currentServer;

	private Map<String, ServerInfo> serverMap;

	private IStatusLineManager statusLineManager;

	private static Policy policy = new Policy();

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		createLeftComposite(parent);
		statusLineManager = getViewSite().getActionBars()
				.getStatusLineManager();
	}

	private void createLeftComposite(Composite parent) {
		Composite leftPanel = new Composite(parent, SWT.NONE);
		GridLayout leftLayout = new GridLayout(1, false);
		leftLayout.verticalSpacing = 0;
		leftPanel.setLayout(leftLayout);
		GridData fillHorizontalData = new GridData(GridData.FILL_HORIZONTAL);
		leftPanel.setLayoutData(fillHorizontalData);

		Composite serverComposite = new Composite(leftPanel, SWT.NONE);
		GridLayout serverLayout = new GridLayout(2, false);
		serverComposite.setLayout(serverLayout);
		serverComposite.setLayoutData(fillHorizontalData);

		Label serverLabel = new Label(serverComposite, SWT.NONE);
		serverLabel.setText("Server: ");
		serverListCombo = new Combo(serverComposite, SWT.READ_ONLY);
		serverListCombo.setLayoutData(fillHorizontalData);

		try {
			serverMap = ServerListUtil.fillServerListCombo(serverListCombo);
			if(serverMap != null)
				serverListCombo.setVisibleItemCount(serverMap.size());
		} catch (Exception e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e),
					"Batch Manager");
		}

		serverListCombo.select(0);
		serverListCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if(serverListCombo.getSelectionIndex() == 0)
					return;
				showJobList(true);
			}
		});

		Composite searchComposite = new Composite(leftPanel, SWT.NONE);
		GridLayout searchLayout = new GridLayout(3, false);
		searchComposite.setLayout(searchLayout);

		searchComposite.setLayoutData(fillHorizontalData);

		Label jobId = new Label(searchComposite, SWT.NONE);
		jobId.setText("Job ID: ");

		searchText = new Text(searchComposite, SWT.BORDER);
		searchText.setLayoutData(fillHorizontalData);
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyevent) {
				if (keyevent.keyCode == SWT.CR)
					showJobList();
			}
		});

		ToolBar toolBar = new ToolBar(searchComposite, SWT.FLAT);
		toolBar.setLayoutData(BatchUtil.getWidthSizedGridData(30));
		ToolItem searchButtonItem = new ToolItem(toolBar, SWT.BUTTON1);

		Image findImage = IconImageUtil.getIconImage("find.gif");
		searchButtonItem.setImage(findImage);
		searchButtonItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				showJobList();
			}
		});

		jobTree = new Tree(leftPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.SINGLE);
		jobTree
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
						1));

		treeViewer = new TreeViewer(jobTree);
		treeViewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object parentElement) {
				return ((JobTreeNode) parentElement).getChildren().toArray();
			}

			public Object getParent(Object element) {
				return ((JobTreeNode) element).getParent();
			}

			public boolean hasChildren(Object element) {
				return ((JobTreeNode) element).getChildren().size() > 0;
			}

			public Object[] getElements(Object inputElement) {
				return ((JobTreeNode) inputElement).getChildren().toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});

		treeViewer.setLabelProvider(new LabelProvider() {
			private final Image categoryImage = IconImageUtil
					.getIconImage("category.gif");
			private final Image packageImage = IconImageUtil
					.getIconImage("package_obj.gif");
			private final Image jobImage = IconImageUtil
					.getIconImage("job.gif");

			@Override
			public Image getImage(Object element) {
				JobTreeNode node = (JobTreeNode) element;
				if (JobInfo.TYPE_CATEGORY.equals(node.getJobInfo().getType()))
					return categoryImage;
				if (JobInfo.TYPE_PACKAGE.equals(node.getJobInfo().getType()))
					return packageImage;
				if (JobInfo.TYPE_JOB.equals(node.getJobInfo().getType()))
					return jobImage;
				return null;
			}

			@Override
			public String getText(Object obj) {
				JobInfo job = ((JobTreeNode) obj).getJobInfo();
				if (JobInfo.TYPE_CATEGORY.equals(job.getType()))
					return job.getWorkName();
				if (JobInfo.TYPE_PACKAGE.equals(job.getType()))
					return job.getJobPackageName();
				if (JobInfo.TYPE_JOB.equals(job.getType()))
					return job.getJobName();
				return "";
			}
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				TreeItem item = jobTree.getSelection()[0];
				JobTreeNode treeNode = (JobTreeNode) item.getData();
				JobInfo jobInfo = treeNode.getJobInfo();
				if (jobInfo != null && jobInfo.getType() != null
						&& jobInfo.getType().equals(JobInfo.TYPE_PACKAGE)
						&& !treeNode.hasChildren()) {
					displayJobOfPackageList();
				} else if (!treeViewer.getExpandedState(treeNode)) {
					showXml(treeNode, jobInfo);
				} else {
					treeViewer.collapseToLevel(treeNode, 1);
					treeViewer.refresh();
				}
			}

			private void showXml(JobTreeNode treeNode, JobInfo jobInfo) {
//				if (!policy.canViewXml()) {
//					MessageUtil
//							.showMessage(
//									"You can not view this job because BatchAgent's policy does not allow it",
//									"Batch Manager");
//					return;
//				}

				if (jobInfo.getType().equals(JobInfo.TYPE_JOB))
					new ReadJobConfigInfoAction(currentServer, getJobName(),
							jobInfo, treeNode).run();
				treeViewer.expandToLevel(treeNode, 2);
				treeViewer.refresh();
			}

			private void displayJobOfPackageList() {
				TreeItem item = jobTree.getSelection()[0];
				JobTreeNode treeNode = (JobTreeNode) item.getData();
				JobInfo node = treeNode.getJobInfo();

				if (!treeNode.hasChildren()) {

					List<JobInfo> resultList = JobTreeNodeInfo
							.newJobOfPackageInstance(currentServer, "",
									node.getJobPath()).getJobList();
					if (resultList != null) {
						Collections.sort(resultList); // 결과 가져올 때 sort도 고려,
						// JobTreeMaker 내부에서
						// sort 실행하므로 외부에서~
						for (JobInfo result : resultList) {
							JobTreeNode childNode = new JobTreeNode(result);
							treeNode.addChild(childNode);
						}
					}
					treeViewer.expandToLevel(treeNode, 2);
				}
			}
		});

		hookContextMenu();
		BatchActivator.getDefault().getModelObject().addPropertyChangeListener(
				this);

		//showJobList(true);
	}

	private void showJobList() {
		showJobList(false);
	}

	private void showJobList(boolean byCombo) {
		if (StringUtils.isEmpty(serverListCombo.getText()))
			return;
		currentServer = serverMap.get(serverListCombo.getText());

		if (byCombo) {
			Policy policy = AgentController.getPolicy(currentServer);
			if (policy != null)
				this.policy = policy;
		}

		listJobAction = new ListJobAction(currentServer, searchText.getText(),
				treeViewer);
		listJobAction.run();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				hookContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void hookContextMenu(IMenuManager manager) {
		TreeItem item = jobTree.getSelection()[0];
		JobTreeNode node = (JobTreeNode) item.getData();
		JobInfo jobInfo = getJobInfo();
		if (jobInfo.getType().equals(JobInfo.TYPE_JOB)) {
			manager.add(new ReadJobConfigInfoAction(currentServer,
					getJobName(), jobInfo, node));
			manager.add(new SelectLogFileDialogAction(currentServer, jobInfo));
			createShowResourceMenu(manager);
			if (jobInfo.getJobPath().endsWith(
					BatchConstants.JOB_CONFIG_TEMP_FILE_SUFFIX + ".xml")) {
				manager.add(new Separator());
				manager.add(new JobFileDeleteAction(currentServer, jobInfo
						.getJobPath(), treeViewer, getJobName()));
			}
			if (policy.canRunJob())
				createLaunchMenu(manager);
		}
	}

	private void createShowResourceMenu(IMenuManager manager) {
		MenuManager subSingle = new MenuManager("Show resource", "sub");
		subSingle.add(new GetResourcesAction(currentServer,
				getJobInfo(), policy));
		subSingle.add(new SelectDatabaseResourceDialogAction(currentServer));
		manager.add(subSingle);
	}

	private void createLaunchMenu(IMenuManager manager) {
		String xmlFile = getXMLFileName();
		String launchFileName = StringUtils.substringBetween(xmlFile, policy
				.getFileSeparator()
				+ policy.getBuildPath() + policy.getFileSeparator(), ".xml");

		manager.add(new Separator());
		manager.add(new JobExecuteAction(currentServer, launchFileName, "", "",	ExecState.Job, true));
		manager.add(new JobExecuteAction(currentServer, launchFileName, "", "",	ExecState.Job));
		
		List<Map<String, String>> list = new StepInfoService(currentServer,
				xmlFile).getStepList();
		if (list == null)
			return;

		MenuManager subSingle = new MenuManager("Launch a single step", "sub");

		for (Map<String, String> stepInfo : list) {
			subSingle.add(new JobExecuteAction(currentServer, launchFileName,
					stepInfo.get("id"), stepInfo.get("name"),
					ExecState.SingleStep, true));
		}
		manager.add(subSingle);

		MenuManager subSeq = new MenuManager("Launch steps from...", "sub");
		for (Map<String, String> stepInfo : list) {
			subSeq.add(new JobExecuteAction(currentServer, launchFileName,
					stepInfo.get("id"), stepInfo.get("name"),
					ExecState.FromThisStep, true));
		}
		manager.add(subSeq);
	}

	private String getXMLFileName() {
		TreeItem item = jobTree.getSelection()[0];
		JobTreeNode node = (JobTreeNode) item.getData();
		return node.getJobInfo().getJobPath();
	}

	private JobInfo getJobInfo() {
		TreeItem item = jobTree.getSelection()[0];
		JobTreeNode node = (JobTreeNode) item.getData();
		return node.getJobInfo();
	}

	private String getJobName() {
		TreeItem item = jobTree.getSelection()[0];
		JobTreeNode node = (JobTreeNode) item.getData();
		String jobName = node.getJobInfo().getJobName();
		return jobName == null ? null : jobName.replace("_CFG", "");
	}

	public ServerInfo getCurrentServer() {
		return currentServer;
	}

	@Override
	public void setFocus() {
		statusLineManager.setMessage("");
		this.searchText.setFocus();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// we just react on add events
		if ("ITEM_ADD".equals(evt.getPropertyName())) {
			this.jobTree.setData(evt.getNewValue());
		}
	}

	@Override
	public void dispose() {
		BatchActivator.getDefault().getModelObject()
				.removePropertyChangeListener(this);
		super.dispose();
	}

	public static Policy getPolicy() {
		return policy;
	}

}
