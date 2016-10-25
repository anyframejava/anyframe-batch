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

package com.sds.anyframe.batch.manager.view.support;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.model.JobTreeNode;
import com.sds.anyframe.batch.manager.view.xmlEditor.StringEditorInput;
import com.sds.anyframe.batch.manager.view.xmlEditor.XMLEditor;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class EditorLauncher {

	public static void openXMLEditor(String name, byte[] xml, String ip, JobInfo jobInfo, JobTreeNode jobTreeNode) {
		open(XMLEditor.ID, name, xml, ip, jobInfo, jobTreeNode);
	}
	
	private static void open(String editorId, String name, byte[] xml, String ip, JobInfo jobInfo, JobTreeNode jobTreeNode) {
		IEditorInput input = new StringEditorInput(name, xml, ip, jobInfo, jobTreeNode);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
		} catch (PartInitException e) {
			MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
			e.printStackTrace();
		}
	}

	// methods for close
	
	// Editor종료시 confirm하나, 가령, Delete 하는 경우 Cancel외에 저장여부는 묻는 게 의미가 없으므로, 사전에 Cancel여부만 묻고 force 종료한다.  
	public static boolean closeByNameAfterConfirm(String name) {
		IEditorPart openedEditor = EditorLauncher.getEditorByName(name);
		if(openedEditor != null) {
			if(openedEditor.isDirty()){
				if(!MessageDialog.openConfirm(new Shell(), "Batch Manager", name + " has been modified.\nWill you continue anyway?")){
					return false;
				}
			}
			return close(openedEditor, false);
		}
		return true;
	}
	
	public static boolean closeByName(String name, boolean save) {
		IEditorPart cur = getEditorByName(name);
		if(cur != null) {
			return close(cur, save);
		}
		return true;
	}

	private static boolean close(IEditorPart target, boolean save) {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(target, save);
	}
	
	private static IEditorPart getEditorByName(String name) {
		IEditorInput input = new StringEditorInput(name, null, "", null, null);
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);		
	}
	
}
