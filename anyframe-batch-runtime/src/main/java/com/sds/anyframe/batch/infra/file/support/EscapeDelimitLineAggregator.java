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

package com.sds.anyframe.batch.infra.file.support;

import org.springframework.batch.item.file.mapping.FieldSet;
import org.springframework.batch.item.file.transform.LineAggregator;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class EscapeDelimitLineAggregator implements LineAggregator{
	
	private char delimiter = ',';
	private char escapeChar = '"';

	/**
	 * Method used to create string representing object.
	 * 
	 * @param fieldSet arrays of strings representing data to be stored
	 */
	public String aggregate(FieldSet fieldSet) {
		StringBuffer buffer = new StringBuffer();
		String[] args = fieldSet.getValues();
		for (int i = 0; i < args.length; i++) {
			
			if(i>0)
				buffer.append(delimiter);
			
			if(args[i] == null)
				buffer.append("");
			else
				buffer.append(escapeString(args[i]));

		}

		return buffer.toString();
	}

	/**
	 * Sets the character to be used as a delimiter.
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}
	
	private String escapeString(String str) {

        if (str.indexOf(delimiter) >= 0
        		|| str.indexOf('\r') >= 0
                || str.indexOf('\n') >= 0
                || str.indexOf(escapeChar) >= 0) {
            return escapeChar
                    + str.replace(""+escapeChar, "" + escapeChar + escapeChar) 
                    + escapeChar;
        } else {
            // no need to escape
            return str;
        }
    }

	public void setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
	}

}
