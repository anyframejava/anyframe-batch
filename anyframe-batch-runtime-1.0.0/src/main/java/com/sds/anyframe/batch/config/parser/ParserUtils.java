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

package com.sds.anyframe.batch.config.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ParserUtils {

	private static final Log LOGGER = LogFactory.getLog(ParserUtils.class);
	
	public static Map<String, String> getParameters(Element paramElem) {
		Map<String, String> params = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		List<Element> parameters = DomUtils.getChildElementsByTagName(paramElem, "parameter");
		
		for(Element parameter : parameters) {
			
			String key = parameter.getAttribute("key");
			Assert.isTrue(!StringUtils.isEmpty(key), "@key should be specified in <parameter> element ");
			
			String value = parameter.getAttribute("value");
			if(StringUtils.isEmpty(value)){
				value = parameter.getTextContent();
			}
			
			Assert.isTrue(!StringUtils.isEmpty(value), "@value or node value should be specified in <parameter> element ");
			
			if(params.containsKey(key)) {
				LOGGER.info("param [key=" + key + ", value=" + params.get(key)
						+ "] has been already specified and will be replaced");
			}
			
			params.put(key, value);
		}
		
		return params;
	}
	
}
