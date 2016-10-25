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

package com.anyframe.core.event;


/**
 * EventHandler interface for specification of Event Handler. 
 * 
 * An eventhandler process its own event by itself. 
 * When an event handler accept the event, it must do one of following. 
 * 
 * - process Handling Logic according to the event contents. 
 * - Ignore Event if not appropriate one.  
 * - Throw UnExpectedEventException when really bad event as an input. 
 * 
 * @author Hyoungsoon Kim
 *
 */
public interface EventHandler {
	public void handleEvent(Event event);
	public Class getHandlerClass();
}
