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

package com.sds.anyframe.batch.manager.view.support;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class TreeViewerSorterHandler extends SelectionAdapter {

	/**
	 * <p>
	 * The <code>Table</code> that the <code>TreeViewerSorter</code> is bound
	 * to
	 * </p>
	 */
	private final Tree tree;

	/**
	 * <p>
	 * The <code>TableViewerSorter</code> to use for sorting
	 * </p>
	 */
	private final TreeViewerSorter sorter;

	/**
	 * <p>
	 * Creates a new <code>TableViewerSorterHandler</code> instance and binds it
	 * to the specified <code>Table</code> using the given
	 * <code>TableViewerSorter</code> to sort the model elements.
	 * </p>
	 * 
	 * @param tree the <code>Tree</code> to bind this
	 * <code>TableViewerSorterHandler</code> to
	 * @param sorter the <code>TableViewerSorter</code> to use to sort the model
	 * elements
	 */
	public TreeViewerSorterHandler(Tree tree, TreeViewerSorter sorter) {
		this.tree = tree;
		this.sorter = sorter;
		this.registerColumns();
	} // end constructor TableViewerSorterHandler(Table, TableViewerSorter)

	/**
	 * <p>
	 * Disposes this <code>TableViewerSorterHandler</code>.
	 */
	public void dispose() {
		this.unregisterColumns();
	} // end method dispose()

	/**
	 * <p>
	 * Handles the <code>SelectionEvent</code> being triggered when the sorting
	 * column and/or order of the <code>Table</code> changes. The sorting of the
	 * underlying model is done using the selected column to sort by. The sort
	 * direction is reversed, i.e. from ascending to descending and reverse.
	 * </p>
	 * 
	 * @param event Event the <code>SelectionEvent</code> triggered
	 */
	@Override
	public void widgetSelected(SelectionEvent event) {
		if (event.widget instanceof TreeColumn) {
			int columnIndex = this.tree.indexOf((TreeColumn) event.widget);
			this.sort(columnIndex);
		}
	} // end method widgetSelected(SelectionEvent)

	/**
	 * <p>
	 * Sorts the underlying model by the specified column. The sort direction is
	 * reversed.
	 * </p>
	 * 
	 * @param columnIndex int the index of the column to sort
	 */
	public void sort(int columnIndex) {
		this.sort(columnIndex, !this.sorter.isAscending());
	} // end method sort(int)

	/**
	 * <p>
	 * Sorts the underlying model by the specified <code>columnIndex</code>.
	 * </p>
	 * 
	 * @param columnIndex int the index of the column to sort
	 * @param ascending <code>true</code> for ascending, <code>false</code> for
	 * descending sort order
	 */
	public void sort(int columnIndex, boolean ascending) {
		this.sorter.setSortingColumn(columnIndex);
		this.sorter.setAscending(ascending);
		this.sorter.sort();

		TreeColumn column = this.tree.getColumn(columnIndex);
		this.tree.setSortColumn(column);
		this.tree.setSortDirection(sorter.isAscending() ? SWT.UP : SWT.DOWN);
	} // end method sort(int, boolean)

	/**
	 * <p>
	 * Registers all <code>TreeColumns</code> to sort on header single mouse
	 * click. Each single mouse click on the same
	 * <code> <code>TreeColumns</code> reverses the sort order.
	 * </p>
	 */
	private void registerColumns() {
		TreeColumn[] columns = this.tree.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].addSelectionListener(this);
		}
	} // end method registerColumns()

	/**
	 * <p>
	 * Unregisters all <code>TreeColumns</code> from this
	 * <code>TableViewerSorterHandler</code>.
	 * </p>
	 */
	private void unregisterColumns() {
		TreeColumn[] columns = this.tree.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].removeSelectionListener(this);
		}
	} // end method unregisterColumns()

} // end class TableViewerSortHandler
