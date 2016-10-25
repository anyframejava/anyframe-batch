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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * the class CreateVoClass creates VO Class if it doesnt Exist
 */
/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class CreateClass {

	/**
	 * creates VO Class if it doesnt Exist
	 * 
	 * @param project
	 *            Project Object to which SQL query XML belongs
	 * @param path
	 *            VO class path
	 * @param fileName
	 *            VO class name
	 * @param voClassInputStream
	 *            VO class input stream
	 * @param isVoexist
	 *            Indicates wether VO exist or not
	 * @return boolean
	 */
	public static void createClass(IProject project, String fullName, InputStream voClassInputStream)
			throws CoreException {

		String[] fileNameArr = fullName.split("/");
		String path = null;
		String fileName = null;
		for (int i = 0; i < fileNameArr.length; i++) {

			if (i == 0)
				path = fileNameArr[i];
			else if (i < fileNameArr.length - 1)
				path = path + "/" + fileNameArr[i];
			else
				fileName = fileNameArr[i];
		}

		if (path != null && !path.equals("") ) {
			// Create package/folder structer if it doesnt exist
			createFolder(project, path);
		}
		final IFile ifile = project.getFile(new Path(path + "/" + fileName));

		try {
			// check whether VO class exist or not
			// ResourceAttributes attributes = ifile.getResourceAttributes();
			if (ifile.exists()) {

				ifile.setContents(voClassInputStream, true, false, null);
			} else {
				// VO Not Exist and create new VO class
				ifile.create(voClassInputStream, true, null);
			}

			ifile.refreshLocal(3, null);

			voClassInputStream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Create package/folder structer if it doesnt exist
	 * 
	 * @param project
	 *            Project Object to which SQL query XML belongs
	 * @param path
	 *            VO class path
	 */
	public static void createFolder(IProject project, String path) throws CoreException {

		IFolder voClassFolder = null;
		String[] pathArr = path.split("/");
		for (int i = 0; i < pathArr.length; i++) {
			if (i == 0)
				path = pathArr[i];
			else
				path = path + "/" + pathArr[i];
			voClassFolder = project.getFolder(path);
			if (!voClassFolder.exists()) {
				voClassFolder.create(false, true, null);
			}
		}
	}

}
