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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.sds.anyframe.batch.manager.BatchActivator;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class MessageUtil {

	public static void showMessage(String message, String title) {
		MessageDialog.openInformation(new Shell(), title, message);

	}

	private static IStatus createStatus(int severity, int code, String message,
			Throwable exception) {
		return new Status(severity, BatchActivator.PLUGIN_ID, code,
				message, exception);
	}

	public static void showErrorMessage(String title, String message,
			Exception e) {
		ErrorDialog.openError(new Shell(), title, null, createStatus(
				IStatus.ERROR, IStatus.OK, message, e));
	}

	public static String printStackTrace(Throwable e) {
//		StringBuilder builder = new StringBuilder();
//		builder.append(e.toString() + "\n");
//		StackTraceElement astacktraceelement[] = e.getStackTrace();
//		for (int i = 0; i < astacktraceelement.length; i++)
//			builder.append(astacktraceelement[i].toString()).append("\n");
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	// public static void showErrorMessage2(String message, String title) {
	// StatusManager.getManager().handle(null, StatusManager.SHOW);
	// }
	//	
	// public void handle(final StatusAdapter statusAdapter, int style) {
	//		
	// if ((style & StatusManager.LOG) == StatusManager.LOG) {
	// StatusManager.getManager().addLoggedStatus(statusAdapter.getStatus());
	// WorkbenchPlugin.getDefault().getLog().log(statusAdapter.getStatus());
	// }
	// }

}
