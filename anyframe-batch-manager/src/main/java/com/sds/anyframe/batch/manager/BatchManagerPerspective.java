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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.sds.anyframe.batch.manager.view.ExecutionLogView;
import com.sds.anyframe.batch.manager.view.JobExplorerView;


/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class BatchManagerPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		layout.addStandaloneView(JobExplorerView.ID,  true, IPageLayout.LEFT, 0.2f, editorArea);
		IFolderLayout bottomView = layout.createFolder("bottomView", IPageLayout.BOTTOM, 0.7f, editorArea);
		bottomView.addView(ExecutionLogView.ID);
	}
}
