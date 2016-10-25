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

import org.eclipse.jface.viewers.ITreeContentProvider;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public interface ITreeTableContentProvider extends ITreeContentProvider {   
    /**
     * <p>
     * Gets the column value for the specified <code>element</code> at the
     * given <code>columnIndex</code>.
     * </p>
     *
     * @param element the model element for which to query the colum value
     * @param columnIndex the index of the column to query the value for
     *
     * @return the value for the <code>element</code> at the given
     * <code>columnIndex</code>
     */
    public Object getColumnValue(Object element, int columnIndex);
   
   
}
