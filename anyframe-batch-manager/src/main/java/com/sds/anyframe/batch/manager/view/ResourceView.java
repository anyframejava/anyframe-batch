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

/**
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.manager.BatchActivator;
import com.sds.anyframe.batch.manager.model.ModelObject;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ResourceView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "com.sds.anyframe.batch.manager.resource";
	TableViewer viewer;
	private Label resourceLabel;
	private Label resourceFile;
	private Label voClassLabel;
	private Label voClass;
	private FileInfoVO fileInfoVO;

	public void createPartControl(Composite parent) {
		createComposite(parent);
	}


	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		createLabel(parent);
		createTableWithHeader(parent);
		
	}


	private void createLabel(Composite parent) {
		Composite composite  = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		
		
		gridData = new GridData(SWT.LEFT);
		resourceLabel = new Label(composite, SWT.NONE);
		resourceLabel.setText("Resource file : ");
		resourceLabel.setLayoutData(gridData);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		resourceFile = new Label(composite, SWT.NONE);
		resourceFile.setText("");
		resourceFile.setLayoutData(gridData);
        
		gridData = new GridData(SWT.LEFT);
		voClassLabel = new Label(composite, SWT.NONE);
		voClassLabel.setText("VO Class : ");
		voClassLabel.setLayoutData(gridData);
		
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		voClass = new Label(composite, SWT.NONE);
		voClass.setText("");
		voClass.setLayoutData(gridData);
	}


	private void createTableWithHeader(Composite parent) {

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 10);
		Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION	| SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(gridData);
		
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		viewer.setInput(BatchActivator.getDefault().getModelObject());
        BatchActivator.getDefault().getModelObject().addPropertyChangeListener(this);
        
		displayResourceView();
	}

	private void createTableWithoutHeader(Composite parent) {
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 10);
		Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION	| SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		table.setLayoutData(gridData);
		
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		viewer.setInput(BatchActivator.getDefault().getModelObject());
        BatchActivator.getDefault().getModelObject().addPropertyChangeListener(this);
	}

	public void displayResourceView() {
		String innerResultString;
		
		ModelObject resourceList = (BatchActivator.getDefault().getModelObject());
		List<?> resultList = (List<?>)resourceList.getObjectList();
		
		for(int i = 0 ; i < resultList.size() ; i++){
			List<Object> resultString = (List<Object>)resultList.get(i);
			
			for(int j = 0 ; j < resultString.size() ; j++){
				innerResultString = (String)resultString.get(j);
			}
		}
	}


	public void setLabels() {
		if(fileInfoVO.getFullPathName() != null)
			resourceFile.setText(fileInfoVO.getFullPathName());
		if(fileInfoVO.getVoClass() != null)
			voClass.setText(fileInfoVO.getVoClass());
	}


	public void setFocus() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// we just react on add events
		if ("Read_Resource".equals(evt.getPropertyName())) {
//			this.jobTree.setData((Item) evt.getNewValue());
			viewer.refresh();
		}

	}

	public void dispose() {
		BatchActivator.getDefault().getModelObject().removePropertyChangeListener(this);
		super.dispose();
	}

//	private void initializeToolBar() {
//		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
//	}

	static class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            // do nothing
		}

		public void dispose() {
            // do nothing
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent) {
			Object[] objList = ((ModelObject)parent).toArray();
			if(objList.length==0)
				return new Object[]{""};
			if(!(objList[0] instanceof List))
				return new Object[]{""};
			return ((List)objList[0]).toArray();
//			return ((ObjectList)parent).toArray().length == 0? new Object[]{""} : ((List)((ObjectList)parent).toArray()[0]).toArray();
		}
	}

	static class ViewLabelProvider extends LabelProvider {
		public Image getImage(Object obj) {
			return null;
		}
        public String getText(Object element) {
            return ((String)element).replaceAll("\t", "  ");
        }
	}
	
	public void setFileInfoVO(FileInfoVO fileInfoVO) {
		this.fileInfoVO = fileInfoVO;
	}
}
