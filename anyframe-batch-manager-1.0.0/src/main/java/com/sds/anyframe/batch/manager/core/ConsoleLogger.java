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

package com.sds.anyframe.batch.manager.core;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ConsoleLogger {

	private MessageConsole messageConsole;
	private BufferedWriter bufferedWriter;
	private boolean isBWOpen = false;
	private static ConsoleLogger consoleLogger;

	public static ConsoleLogger getLogger() {
		if (consoleLogger == null) {
			consoleLogger = new ConsoleLogger();
		}
		return consoleLogger;
	}

	public void open() {
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(
				getLoggingStream()));
		isBWOpen = true;
	}

	public void write(String str) {
		try {

			if (!isBWOpen) {
				open();
				isBWOpen = true;
			}
			bufferedWriter.write(str + "\n");
			bufferedWriter.flush();
		} catch (Exception ex) {
			PluginLogger.error(ex);
		}
	}

	public void close() {
		try {
			if (isBWOpen) {
				bufferedWriter.close();
				isBWOpen = false;
			}
		} catch (Exception ex) {
			PluginLogger.error(ex);
		}
	}

	private OutputStream getLoggingStream() {
		messageConsole = null;

		if (messageConsole == null) {
			ImageDescriptor imageDescriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(
							"org.eclipsecon.tmtutorial", "icons/category.gif"); //$NON-NLS-1$ //$NON-NLS-2$
			// messageConsole = new MessageConsole("Server Deploy Log",
			// imageDescriptor);
			messageConsole = new MessageConsole("Batch Console Log",
					imageDescriptor);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { messageConsole });
		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(
				messageConsole);
		return messageConsole.newOutputStream();
	}

}
