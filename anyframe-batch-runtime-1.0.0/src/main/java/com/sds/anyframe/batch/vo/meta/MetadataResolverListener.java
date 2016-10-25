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

package com.sds.anyframe.batch.vo.meta;

/**
 * Listener for VoMeta and FieldMeta creation.
 * 
 * This interface's purpose is to manipulate VoMeta and FieldMeta's internal information by customized implementation.
 *
 * @author prever.kang
 *
 */
public interface MetadataResolverListener {

	void valueObjectResolved(VoMeta voMeta);

	void fieldMetaResolved(FieldMeta fieldMeta);
}
