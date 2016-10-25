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

package com.anyframe.core.vo.meta.impl;

import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.meta.MetadataResolverListener;
import com.anyframe.core.vo.meta.VoMeta;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PojoMetadataResolverListenerImpl implements MetadataResolverListener {

	private String pojoClassIncludePrefix;
	
	public void setPojoClassIncludePrefix(String pojoClassIncludePrefix) {
		this.pojoClassIncludePrefix = pojoClassIncludePrefix;
	}
	
	public void valueObjectResolved(VoMeta voMeta) {
	}

	public void fieldMetaResolved(FieldMeta fieldMeta) {
		if(fieldMeta.getFieldType() == FieldType.UNKNOWN && fieldMeta.getFieldClass().getCanonicalName().startsWith(pojoClassIncludePrefix))
			fieldMeta.setFieldType(FieldType.VO);
	}

}
