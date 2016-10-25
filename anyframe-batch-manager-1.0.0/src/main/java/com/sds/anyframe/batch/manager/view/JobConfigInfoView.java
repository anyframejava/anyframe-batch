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

package com.sds.anyframe.batch.manager.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.model.ModelObject;


/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class JobConfigInfoView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "com.sds.anyframe.batch.manager.jobconfiginfo";
	
	private TableViewer viewer;
	
	static class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            // do nothing
		}

		public void dispose() {
            // do nothing
		}

		public Object[] getElements(Object parent) {
			Object[] objList = ((ModelObject)parent).toArray();
			if(objList.length==0)
				return new Object[]{""};
			if(!(objList[0] instanceof List))
				return new Object[]{""};
			return ((List)objList[0]).toArray();
		}
	}

	static class ViewLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object obj) {
			return null;
		}
        @Override
		public String getText(Object element) {
            return ((String)element).replaceAll("\t", "  ");
        }
	}

	@Override
	public void createPartControl(Composite parent) {
		this.viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.viewer.setContentProvider(new ViewContentProvider());
		this.viewer.setLabelProvider(new ViewLabelProvider());
		this.viewer.setInput(BatchActivator.getDefault().getModelObject());
        // add listener to model to keep on track. 
        BatchActivator.getDefault().getModelObject().addPropertyChangeListener(this);
	}

	@Override
	public void setFocus() {
//		this.viewer.getControl().setFocus();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if("READ_XML".equalsIgnoreCase(evt.getPropertyName())){
			viewer.refresh();
		}
	}

	@Override
	public void dispose() {
		BatchActivator.getDefault().getModelObject().removePropertyChangeListener(this);
		super.dispose();
	}

}
