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

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class EventInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String eventClassName;
	
	private String eventName;
	
	private AtomicLong count = new AtomicLong();
	
	private long lasttime;
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String enentName) {
		this.eventName = enentName;
	}
	public String getEventClassName() {
		return eventClassName;
	}
	public void setEventClassName(String eventClassName) {
		this.eventClassName = eventClassName;
	}
	
	public AtomicLong getCount() {
		return count;
	}
	public void setCount(AtomicLong count) {
		this.count = count;
	}
	
	public long getLasttime() {
		return lasttime;
	}
	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}
	public long incrementAndGet() {
		return count.incrementAndGet();
	}
	@Override
	public String toString() {
		return "EventInfo [count=" + count + ", lasttime=" + lasttime
				+ ", eventName=" + eventName + ", eventClassName="
				+ eventClassName + "]";
	}
}
