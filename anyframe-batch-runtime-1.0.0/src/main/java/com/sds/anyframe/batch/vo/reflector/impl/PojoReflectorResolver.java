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

package com.sds.anyframe.batch.vo.reflector.impl;


import com.sds.anyframe.batch.vo.meta.VoMeta;
import com.sds.anyframe.batch.vo.reflector.Reflector;
import com.sds.anyframe.batch.vo.reflector.ReflectorResolver;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PojoReflectorResolver implements ReflectorResolver {

	@Override
	public Reflector getReflector(VoMeta voMeta) {
		return new PojoReflector(voMeta);
	}

}
