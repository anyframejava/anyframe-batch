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

import java.util.List;
import java.util.Map;

/**
 * EventDistributor delivers the occurred event to registered event handler. 
 * 
 * 
 * @author Hyoungsoon Kim
 *
 */
public interface EventDistributor {
	public void registerEventhandler(String eventName, EventHandler eventHandler);
	public void registerEventhandler(String eventName, List<EventHandler> newHandlerList, Map<String, Map<Class<? extends EventHandler>, EventHandler>> tempEvents);
	public void fireEvent(Event event);
	public List<EventInfo> getEventStatistics();
}
