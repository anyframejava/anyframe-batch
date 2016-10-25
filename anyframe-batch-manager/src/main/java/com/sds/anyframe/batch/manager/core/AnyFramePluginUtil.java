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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class AnyFramePluginUtil {
	private static final String ANYFRAME_TMP_PATH = "/.anyframe/";

	public static String getWorkspacePath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}

	public static String getAnyframeTempPath() {
		return getWorkspacePath() + ANYFRAME_TMP_PATH;
	}

	
	public static String getProjectPath(IJavaProject project) {
		project.getPath().makeAbsolute().toString();
		IPath location = ResourcesPlugin.getWorkspace().getRoot().findMember(project.getPath()).getLocation();
		return location.toString();
	
	}

	public static String getProjectPath(IProject project) {
		if(project == null)
			return null;
		
		IProjectNature nature = null;
		try {
			nature = project.getNature(JavaCore.NATURE_ID);
			return AnyFramePluginUtil.getProjectPath((IJavaProject) nature);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static IJavaProject getProject(String projectName) {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject(projectName);
	}

	public static String getAnyframeProperty(IProject project, String property) {
		if(project == null)
			return null;
		
		try {
			IProjectNature nature = project.getNature(JavaCore.NATURE_ID);
			String projectPath = AnyFramePluginUtil.getProjectPath((IJavaProject) nature);
			return AnyframePropertyHandler.getProperty(projectPath, property);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public static String getAnyframeProperty(IJavaProject javaproject, String projectCharacterset) {
		return getAnyframeProperty(javaproject.getProject(), projectCharacterset);
	}

}
