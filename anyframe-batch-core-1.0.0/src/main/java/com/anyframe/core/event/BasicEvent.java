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
 * Basic Event Object 
 * Event have following basic attribute. 
 * 1) EventSource : place of Event Occurs, Normally event occuring class canonical name.  
 * 2) EventName : Name of Event. Given by the event Occuring class. 
 * 
 * @author ch.h.baek
 *
 */
public class BasicEvent implements Event {

	String eventSource = null;
	String eventName = null;
	String eventDescription = null;
	long eventOccurTime = 0; // Time of Occurring time. 

	public BasicEvent() {
	}

	protected BasicEvent(Class sourceClass, String eventName) {
		this(sourceClass, eventName, null);
	}

	protected BasicEvent(Class sourceClass, String eventName, String eventDescription) {
		this.eventSource = sourceClass.getCanonicalName();
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.eventOccurTime = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return ("BasicEvent [" + eventName + "] from " + eventSource + ".");
	}

	public String getEventSource() {
		return eventSource;
	}

	public String getEventName() {
		return eventName;
	}

	/**
	 * Return the description of this Event.
	 * 
	 * @return the description of this Event
	 * @deprecated as of Anyframe Enterprise 4.5, in favor of {@link #getEventDescription}
	 */
	public String getDescription() {
		return eventDescription;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public long getEventOccurTime() {
		return eventOccurTime;
	}

	public Class getEventClass() {
		return BasicEvent.class;
	}

}
