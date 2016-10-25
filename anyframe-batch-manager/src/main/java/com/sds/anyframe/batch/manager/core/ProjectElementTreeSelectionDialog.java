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

/******************************************************************************
 * 나중에 Core와 통합을 용이하게 하기위해 Core 참조 부분을 본 Package에 옮겨둠! 
 * 본 Package의 Class들은 수정/개선 하지 말 것!
 * bonobono, 090706
 ******************************************************************************/

package com.sds.anyframe.batch.manager.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.sds.anyframe.batch.manager.BatchActivator;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ProjectElementTreeSelectionDialog {
	IProject project;

	private IWorkspaceRoot workspace;
	
	final static int FOLDER_DIALOG = 1;
	final static int FILE_DIALOG = 2;
	final static int FOLDER_FILE_DIALOG = 3;
	
	public ProjectElementTreeSelectionDialog(IProject project) {
		this.project = project;
	}
	
	public ProjectElementTreeSelectionDialog(IProject project, IWorkspaceRoot workspace) {
		this.project = project;
		this.workspace = workspace;
	}
	
	// folder selection dialog
	public String openFolderDialog(String filtered) {
		return openResourceDialog(filtered, FOLDER_DIALOG);
	}

	// file selection dialog
	public String openFileDialog(String filtered) {
		return openResourceDialog(filtered, FILE_DIALOG);
	}

	// file & folder selection dialog
	public String openDialog(String filtered) {
		return openResourceDialog(filtered, FOLDER_FILE_DIALOG);
	}
	
	// inner common dialog
	private String openResourceDialog(String filtered, int DialogTypeVal) {
		String webapp = project.getName() + "/";
		
		Class[] acceptedClasses = null;
		if(DialogTypeVal == FOLDER_DIALOG) {
			acceptedClasses = new Class[] { IFolder.class };
		} else if (DialogTypeVal == FILE_DIALOG) {
			acceptedClasses = new Class[] { IFile.class };
		} else if (DialogTypeVal == FOLDER_FILE_DIALOG) {
			acceptedClasses = new Class[] { IFolder.class, IFile.class };
		}
		
		TypedElementSelectionValidator validator = new TypedElementSelectionValidator(
				acceptedClasses, true);
		ILabelProvider lp = new WorkbenchLabelProvider();
		ITreeContentProvider cp = new WorkbenchContentProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				BatchActivator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getShell(), lp, cp);
		dialog.setValidator(validator);
		dialog.setTitle("Search");

		if(DialogTypeVal == FOLDER_DIALOG) {
			dialog.setMessage("Following folders are available");
			dialog.addFilter(new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parent, Object element) {
					// If the element was excluded
					// if (element instanceof IFile) {
					// IFile file = (IFile)element;
					// if(file.getFileExtension().equals("jar")){
					// return true;
					// }
					// }
					// else if (element instanceof IContainer) {
					// // IProject, IFolder
					// try {
					// IResource[] resources = ((IContainer) element).members();
					// for (int i = 0; i < resources.length; i++) {
					// // recursive! Only show containers that contain an archive
					// if (select(viewer, parent, resources[i])) {
					// return true;
					// }
					// }
					// }
					// catch (CoreException e) {
					// e.printStackTrace();
					// }
					// }
					if (element instanceof IFolder)
						return true;
					return false;
				}
			});
		} else if (DialogTypeVal == FILE_DIALOG) {
			dialog.setMessage("Following files are available");
/* if wanna see *.vm files only, remove block comment
			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parent, Object element) {
					// If the element was excluded
					if (element instanceof IFile) {
						IFile file = (IFile) element;
						if (!file.getFileExtension().equals("vm")) {
							return false;
						}
					}
					return true;
				}
			});
*/
		} else if (DialogTypeVal == FOLDER_FILE_DIALOG) {
			dialog.setMessage("Following folders and files are available");
		}
		
		if (workspace != null){
			dialog.setInput(workspace);
		}
		else {
			String webappDir = webapp.substring(project.getName().length() + 1);
			if ("".equals(webappDir)) {
				dialog.setInput(project);
			} else {
				dialog.setInput(project.getFolder(webappDir));
			}
		}
		dialog.setInitialSelection(project);
		
		if (dialog.open() == Window.OK) {
			String str = null;
			String strde = null;
			if (dialog.getResult()[0] instanceof IResource) {
				str = ((IResource) dialog.getResult()[0]).getFullPath().toString();
				strde = StringUtil.unqualifySpringName(str.substring(1,str.length()));
				
//				if (strde != null && !strde.startsWith("/")) {
//					strde = "/" + strde;
//					return strde;
//				}
				if (strde != null && !strde.startsWith("/")) {
					if (workspace != null){
//						String project = ((IResource) dialog.getResult()[0]).getProject().toString();
//						strde = "/" + project + "/" + strde;
						strde = str;
					}else{
						strde = "/" + strde;
					}
					return strde;
				}
			}

			
		}
		return null;
	}
}
