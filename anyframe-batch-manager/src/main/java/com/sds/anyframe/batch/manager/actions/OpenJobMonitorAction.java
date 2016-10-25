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

package com.sds.anyframe.batch.manager.actions;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.view.JobMonitorView;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class OpenJobMonitorAction implements IWorkbenchWindowActionDelegate {

	public OpenJobMonitorAction() {
	}

	public void run(IAction action) {
		IEditorInput input = new JobMonitorInput("Job Monitor");
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, JobMonitorView.ID);
		} catch (PartInitException e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
	
	private class JobMonitorInput implements IStorageEditorInput, IStorage {
		
		private String name;

		public JobMonitorInput(String name) {
			this.name = name;
		}

		public IStorage getStorage() {
			return this;
		}

		public IPath getFullPath() {
			return null;
		}

		public boolean isReadOnly() {
			return false;
		}

		public boolean exists() {
			return true;
		}

		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		public IPersistableElement getPersistable() {
			return null;
		}

		public String getToolTipText() {
			return name;
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

	    public boolean equals(Object o) {
			return false;
	    }

		public String getName() {
			return name;
		}

		public InputStream getContents() throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
