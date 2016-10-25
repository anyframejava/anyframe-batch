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

package com.anyframe.core.vo.charset;

import java.util.Arrays;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class MalformedInputException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5114208541963824846L;

	public MalformedInputException() {
		super();
	}
	
	public MalformedInputException(String msg) {
		super(msg);
	}
	
    public MalformedInputException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MalformedInputException(Throwable cause) {
        super(cause);
    }
    
    static MalformedInputException newInstance(byte[] bytes, int offset, int length, int errorPos) {
    	byte[] buffer = new byte[length];
    	for(int i=0; i<length; i++)
    		buffer[i] = bytes[i+offset];
    	
    	return new MalformedInputException("Input Byte: " + Arrays.toString(buffer) + " Error Position: " + errorPos);
    }

    static MalformedInputException newInstance(char[] chars, int offset, int length, int errorPos) {
    	char[] buffer = new char[length];
    	for(int i=0; i<length; i++)
    		buffer[i] = chars[i+offset];
    	
    	return new MalformedInputException("Input Char: " + Arrays.toString(buffer) + "Error Position: " + errorPos);
    }
}
