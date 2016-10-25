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

package com.sds.anyframe.batch.manager.view.xmlEditor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.manager.model.JobTreeNode;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class StringEditorInput implements IStorageEditorInput, IStorage {
	
	protected byte[] xml;
	private final String ip;
	private final JobInfo jobInfo;
	private JobTreeNode jobTreeNode;
	private String name;

	public StringEditorInput(String name, byte[] xml, String ip, JobInfo jobInfo, JobTreeNode jobTreeNode) {
		this.name = name;
		this.xml = xml;
		this.ip = ip;
		this.jobInfo = jobInfo;
		this.jobTreeNode = jobTreeNode;
	}


	public void setContent(byte[] xml) {
		this.xml = xml;
	}

	public JobInfo getJobInfo() {
		return jobInfo;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(xml);
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
		return "";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getIp() {
		return ip;
	}

	@Override
    public boolean equals(Object o) {
    	if(o == this) return true;
        if (!(o instanceof StringEditorInput)) return false;
        StringEditorInput sei = (StringEditorInput) o;
        return sei.name.equals(name);
    }

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public byte[] getXml() {
		return xml;
	}


	public JobTreeNode getJobTreeNode() {
		return jobTreeNode;
	}
	
}
