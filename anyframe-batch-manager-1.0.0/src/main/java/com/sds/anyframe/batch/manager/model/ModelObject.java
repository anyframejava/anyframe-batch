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

package com.sds.anyframe.batch.manager.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;

import com.sds.anyframe.batch.agent.service.PageRequest;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ModelObject extends PlatformObject {
    
    private List objectList = new ArrayList();
    
    protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private PageRequest pageRequest = null;
    
    public void addPropertyChangeListener(PropertyChangeListener l){
        if (l == null) {
            throw new IllegalArgumentException();
        }
        this.listeners.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l){
        this.listeners.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String prop, Object old, Object newValue){
        if (this.listeners.hasListeners(prop)) {
            this.listeners.firePropertyChange(prop, old, newValue);
        }
    }
    
    public void choose(Object o){
    	this.objectList.add(o);
    	firePropertyChange("CHOOSE_READ_ITEM", null, o);
    }

    public Object [] toArray() {
        try {
        	return this.objectList.toArray();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
    }

	public List getObjectList() {
		return objectList;
	}


	public void setObjectList(List objectList) {
		this.objectList = objectList;
		firePropertyChange("CHOOSE_READ_ITEM", null, objectList);
	}
	
	public void addPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest; 
	}

	public PageRequest getPageRequest() {
		return this.pageRequest;
	}

	public void readXml(List lineList) {
		this.objectList.clear();
		this.objectList.add(lineList);
		firePropertyChange("READ_XML", null, lineList);
	}
	
	public void readExcutionLog(List lineList) {
		this.objectList.clear();
		this.objectList.add(lineList);
		firePropertyChange("Excution_Log", null, lineList);
	}
	
	public void readResource(List lineList) {
		this.objectList.clear();
		this.objectList.add(lineList);
		firePropertyChange("Read_Resource", null, lineList);
	}
	
}
