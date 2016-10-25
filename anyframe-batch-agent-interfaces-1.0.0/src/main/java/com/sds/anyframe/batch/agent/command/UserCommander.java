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

package com.sds.anyframe.batch.agent.command;

import java.util.List;

/**
 * @author prever.kang
 * @version 1.0
 * 
 * UserCommander will generate the arguments which is given 
 * from the client to support user's own command line.
 * Normal arguments has a few information like the job id and other additional parameters.
 * But in the case of some special requirement, the arguments need to be changed. 
 * For example, the shell program should receives user id before execute from command. 
 * The user id comes from the client(Batch Manager or remote call).
 * 
 * Therefore, if the arguments should changed by user's own command line then must implement
 * this interface and JobLauncher will load once the implementation class automatically.
 * JobLauncher will invoke this interface before create a shell process with command line.
 * 
 * If there is no any implementation of this interface, then JobLaucher will use default command line.
 * e.g. 'job id' 'key1=value1' 'key2='value2'.
 */

public interface UserCommander {

	List<String> generateCommandLine(String[] args);

}
