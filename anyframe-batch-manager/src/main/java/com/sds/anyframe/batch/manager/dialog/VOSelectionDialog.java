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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.core.StringUtil;
import com.sds.anyframe.batch.manager.model.ItemHandlerInfo;
import com.sds.anyframe.batch.manager.utils.SimpleVOTypeHelper;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class VOSelectionDialog extends DialogCellEditor {

	public static final SettingAction FOR_FILEINFOVO_ACTION = new ForFileInfoVOAction();
	public static final SettingAction FOR_ITEMHANDLER_ACTION = new ForItemHandlerAction();
	
	Table table = null;
	SettingAction settingAction;

	public VOSelectionDialog(Composite parent, SettingAction settingAction) {
		super(parent);
		if (parent instanceof Table)
			this.table = (Table) parent;
			this.settingAction = settingAction;
	}

	@Override
	protected Object openDialogBox(Control arg0) {
		Shell shell = PlatformUI.getWorkbench().getDisplay()
				.getActiveShell();
		SelectionDialog voSearchDialog;
		try {
			voSearchDialog = JavaUI.createTypeDialog(shell,
					new ProgressMonitorDialog(shell), SearchEngine
							.createWorkspaceScope(),
					IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false);

			voSearchDialog.setTitle("Class Selection Dialog");
			voSearchDialog.setMessage("Select Reference Class");
			voSearchDialog.setHelpAvailable(false);

			TableItem[] tableItems = table.getSelection();
			if (voSearchDialog.open() == IDialogConstants.CANCEL_ID) {
				settingAction.doCancel(tableItems);
				return "";
			}

			Object[] types = voSearchDialog.getResult();
			if (types == null || types.length == 0) {
				return null;
			}
			IType resultType = (IType) types[0];
			String result = resultType.getFullyQualifiedName();
			voSearchDialog.close();

			settingAction.setValue(tableItems, result, resultType);

			return result;
		} catch (JavaModelException e) {
			return null;
		}
	}

	private static interface SettingAction {
		public void setValue(TableItem[] tableItems, String result, IType resultType);	// VO 선택시 해야할 작업
		public void doCancel(TableItem[] tableItems);					// Cancel 버튼 선택시 해야할 작업
	}
	
	// BatchCreationWizardPage 에서 호출할 때 사용
	private static class ForItemHandlerAction implements SettingAction {
		public void setValue(TableItem[] tableItems, String result, IType resultType) {
			ItemHandlerInfo item = (ItemHandlerInfo) tableItems[0].getData();
			item.setItemVoClassQ(result);
			item.setItemVoClass(StringUtil.unqualifyJavaName(result));
			tableItems[0].setData(item);
		}
	
		public void doCancel(TableItem[] tableItems) {
			// Cancel 하면 VO 삭제
			ItemHandlerInfo item = (ItemHandlerInfo) tableItems[0].getData();
			item.setItemVoClassQ("");
			item.setItemVoClass("");
			tableItems[0].setData(item);
		}
	}
	
	// SelectResourceFileDialog 에서 호출할 때 사용
	private static class ForFileInfoVOAction implements SettingAction {
		public void setValue(TableItem[] tableItems, String result, IType resultType) {
			FileInfoVO item = (FileInfoVO) tableItems[0].getData();

			Map<String, Integer> voFields = new LinkedHashMap<String, Integer>();
			
			if (resultType.getParent() instanceof ICompilationUnit)  {
				try {
					voFields = SimpleVOTypeHelper.getFields(resultType);
				} catch(Exception e) {
					MessageUtil.showMessage(AgentUtils.getStackTraceString(e), "Batch Manager");
					e.printStackTrace();
				}
			}
			if(result == null || result.length() == 0){
				item.setVoClass("");
				item.setVoFields(null);
				tableItems[0].setData(item);
			}

			item.setVoClass(result);
			item.setVoFields(voFields);
			tableItems[0].setData(item);
		}
	
		public void doCancel(TableItem[] tableItems) {
			//Cancel 하면 VO는 삭제.
				FileInfoVO item = (FileInfoVO) tableItems[0].getData();
				item.setVoClass("");
				item.setVoFields(null);
				tableItems[0].setData(item);
		}
	}
}
