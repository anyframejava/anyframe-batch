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

package com.sds.anyframe.batch.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.sds.anyframe.batch.manager.core.AnyFramePluginUtil;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.BatchProgramType;
import com.sds.anyframe.batch.manager.model.ModelObject;
import com.sds.anyframe.batch.manager.wizards.util.XMLAnalyzer;

/**
 * The activator class controls the plug-in life cycle
 */
/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "anyframe-batch-manager";

	private static BatchActivator plugin;

	private static ModelObject model = new ModelObject();

	private static IProject project;

	private static List<BatchProgramType> batchTypes;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static BatchActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public ModelObject getModelObject() {
		return model;
	}

	public static IProject getProject() {
		return project;
	}

	public static void setProject(IProject project) {
		BatchActivator.project = project;
	}

	public Shell getActiveShell() {
		return getActiveWorkbenchPage().getActivePart().getSite().getShell();
	}

	public IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage activePage = BatchActivator.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
		return activePage;
	}

	public static List<BatchProgramType> getBatchTypes() {
			loadBatchTypes();
		return batchTypes;
	}

	private static void loadBatchTypes() {
		if (project == null)
			return;
		String templatePath = AnyFramePluginUtil.getAnyframeProperty(project, BatchConstants.BATCH_TEMPLATE_PATH);

		if (templatePath == null || templatePath.equals("")) {
			MessageUtil.showMessage("Please Configure Template Path", BatchConstants.BATCH_WIZARD_MESSAGE_TITLE);
			return;
		} else {
			templatePath = AnyFramePluginUtil.getProjectPath(project) + "/" + templatePath;
			templatePath = templatePath.replace('\\', '/');
			templatePath = templatePath.replaceAll("//", "/");
			String xmlFileName = templatePath + "/" + BatchConstants.BATCH_PATTERN_CONFIG_FILENAME;
			List<Map<String, String>> patterns = null;
			BatchProgramType bt = null;
			try {

				patterns = XMLAnalyzer.getData(xmlFileName, BatchConstants.BATCH_PATTERN_CONFIG_ROOT);
				if (patterns.size() > 0) {
					batchTypes = new ArrayList<BatchProgramType>();
					for (Map<String, String> pattern : patterns) {
						bt = new BatchProgramType();
						bt.setTypeId(pattern.get(BatchConstants.XML_TAG_ID));
						bt.setTypeName(pattern.get(BatchConstants.XML_TAG_NAME));
						bt.setBatchServiceClassTemplateName(pattern.get(BatchConstants.XML_TAG_SERVICE_NAME));
						bt.setBatchServiceQueryTemplateName(pattern.get(BatchConstants.XML_TAG_QUERY_NAME));
						
						// reader, writer Number Check
						try {	// reader
							String[] range = pattern.get(BatchConstants.XML_TAG_READER_NUMBER).split("~");
							if(range.length == 2) {	// range
								bt.setMinReader(Integer.parseInt(range[0]));
								bt.setMaxReader(Integer.parseInt(range[1]));
							} else if(range.length == 1) {// fixed number
								bt.setMinReader(Integer.parseInt(range[0]));
								bt.setMaxReader(Integer.parseInt(range[0]));
							} else {
								bt.setMinReader(BatchConstants.MININUM_ITEM_HANDLER_DEFAULT);
								bt.setMaxReader(BatchConstants.MAXIMUM_ITEM_HANDLER_DEFAULT);
							}
						} catch(Exception e) {
							bt.setMinReader(BatchConstants.MININUM_ITEM_HANDLER_DEFAULT);
							bt.setMaxReader(BatchConstants.MAXIMUM_ITEM_HANDLER_DEFAULT);
						}
						
						try {	// writer
							String[] range = pattern.get(BatchConstants.XML_TAG_WRITER_NUMBER).split("~");
							if(range.length == 2) {	// range
								bt.setMinWriter(Integer.parseInt(range[0]));
								bt.setMaxWriter(Integer.parseInt(range[1]));
							} else if(range.length == 1) {// fixed number
								bt.setMinWriter(Integer.parseInt(range[0]));
								bt.setMaxWriter(Integer.parseInt(range[0]));
							} else {
								bt.setMinWriter(BatchConstants.MININUM_ITEM_HANDLER_DEFAULT);
								bt.setMaxWriter(BatchConstants.MAXIMUM_ITEM_HANDLER_DEFAULT);
							}
						} catch(Exception e) {
							bt.setMinWriter(BatchConstants.MININUM_ITEM_HANDLER_DEFAULT);
							bt.setMaxWriter(BatchConstants.MAXIMUM_ITEM_HANDLER_DEFAULT);
						}
												
						batchTypes.add(bt);
					}
				}
			} catch (Exception e) {
				MessageUtil.showMessage("Error occurs during Batch Pattern Loading", BatchConstants.BATCH_WIZARD_MESSAGE_TITLE);
				return;
			}

		}

	}
}
