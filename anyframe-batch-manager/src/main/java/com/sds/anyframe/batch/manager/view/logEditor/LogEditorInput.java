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

package com.sds.anyframe.batch.manager.view.logEditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.agent.service.PageSupport;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class LogEditorInput implements IStorageEditorInput, IStorage {

	private ServerInfo serverInfo;
	private PageRequest pageRequest;
	private PageSupport logService;
	private byte[] result;

	public LogEditorInput(ServerInfo serverInfo, PageRequest pageRequest,
			PageSupport logService, byte[] result) {
		this.serverInfo = serverInfo;
		this.pageRequest = pageRequest;
		this.logService = logService;
		this.result = result;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(result);
	}

	@Override
	public String getName() {
		return (String) pageRequest.getParameter();
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return this;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "";
	}

	@Override
	public Object getAdapter(Class arg0) {
		return null;
	}

	@Override
	public IPath getFullPath() {
		return null;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (((String) pageRequest.getParameter() == null) ? 0 : ((String) pageRequest.getParameter()).hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final LogEditorInput other = (LogEditorInput) obj;
		
		if ((String) pageRequest.getParameter() == null) {
			if (other.getName() != null)
				return false;
		} else if ( !((String)pageRequest.getParameter()).equals(other.getName()))
			return false;

		return true;
	}

	public PageSupport getLogService() {
		return logService;
	}

	public void setLog(byte[] lines) {
		this.result = lines;
	}

	public PageRequest getPageRequest() {
		return pageRequest;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}
}
