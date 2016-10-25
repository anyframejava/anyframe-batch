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

package com.anyframe.core.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.anyframe.core.event.Event;
import com.anyframe.core.event.EventDistributor;
import com.anyframe.core.event.EventHandler;
import com.anyframe.core.event.EventInfo;
import com.anyframe.core.util.ClassUtil;

/**
 * Event Distributor distributes occurred event to registered event handlers. 
 * Each framework instance has only 1 event distributor and that event distributor spread all the events. 
 *  
 * @author Bryan 
 *
 */
public class EventDistributorImpl implements EventDistributor {
	private final static Logger logger = Logger.getLogger("eventDistributorLogger");
	
	private static EventDistributor eventDistributor;
	
	
	// Variable for List of Event & EventHandler 
	private Map<String, Map<Class<? extends EventHandler>, EventHandler>> eventHandlerMap = new ConcurrentHashMap<String, Map<Class<? extends EventHandler>, EventHandler>>();
	
	private ConcurrentHashMap<String, EventInfo> eventStatistics = new ConcurrentHashMap<String, EventInfo>();

	private EventDistributorImpl() {
	}
	
	/**
	 * Initialize the Whole EventHandlerMap 
	 * <li> Create new EventHandlerMap and register passed EventHandlers to the new EventHandlerMap.
	 * <li> when user deploys framework, Spring property configuration must be set for event handler setting.  
	 * <li> Events Handler are set via Spring Property. 
	 * @param passedEventHandlerMap
	 */
	public void setEvents(Map<Event, List<EventHandler>> passedEventHandlerMap) {
		logger.info("Event Distributor builds events and handlers.");

		Map<String, Map<Class<? extends EventHandler>, EventHandler>> newEventHandlerMap = new ConcurrentHashMap<String, Map<Class<? extends EventHandler>, EventHandler>>();
		
		if(passedEventHandlerMap != null){
			
			Iterator<Entry<Event, List<EventHandler>>> iterator = passedEventHandlerMap.entrySet().iterator();
			while(iterator.hasNext()){
				
				 Entry<Event, List<EventHandler>> next = iterator.next();
				 Class<? extends Event> eventClass = (Class<? extends Event>) ClassUtil.getRealClass(next.getKey());
				 
				 createOrGetEventInfo(next.getKey().toString(), eventClass.getName());
				 
				 List<EventHandler> handlerList = next.getValue();
				 // Copy passed eventHandler to new Map. 
				 registerEventhandler(eventClass.getName(), handlerList, newEventHandlerMap);
			}
			
			Map<String, Map<Class<? extends EventHandler>, EventHandler>> removeEvents = null;
			removeEvents = this.eventHandlerMap;
			// Replace to the new HandlerMap. 
			this.eventHandlerMap = newEventHandlerMap;
			removeEvents.clear();
		}
	}

	/**
	 * Register one EventHandler 
	 * 
	 */
	public void registerEventhandler(String eventName, EventHandler handler){
		Map<Class<? extends EventHandler>, EventHandler> handlers = (Map<Class<? extends EventHandler>, EventHandler>)eventHandlerMap.get(eventName);	
		if( handlers == null ) {
			handlers = new ConcurrentHashMap<Class<? extends EventHandler>, EventHandler>();
			eventHandlerMap.put(eventName, handlers);
		}
		handlers.put(handler.getClass(), handler);
		createOrGetEventInfo(eventName, eventName);
		
		logger.info("Event handler("+handler.getHandlerClass()+") registered for the event '"+eventName+"'. ");
	}
	

	/**
	 * Register Whole Event Handlers
	 * @TODO remove the duplicated method. 
	 */
	public void registerEventhandler(String eventName, List<EventHandler> newHandlers, Map<String, Map<Class<? extends EventHandler>, EventHandler>> tempEvents) {
		Map<Class<? extends EventHandler>, EventHandler> handlers = (Map<Class<? extends EventHandler>, EventHandler>)tempEvents.get(eventName);	
		if( handlers == null )
			handlers = new ConcurrentHashMap<Class<? extends EventHandler>, EventHandler>();
			
		for(EventHandler handler : newHandlers){
			logger.info("Event[id : " + eventName + ", handler: "+handler.getHandlerClass()+"]");
			handlers.put(handler.getClass(), handler);
		}
		
		tempEvents.put(eventName, handlers);
	}	
	
	/**
	 * Event Firing Sequence
	 * <li> 1. Find adequate eventHandlers via eventCassName from EventHandlerMap. 
	 * <li> 2. Call every eventHandlers one-by-one.  
	 */
	public void fireEvent(Event event) {
		addEventStatistics(event);
		
		if(logger.isDebugEnabled())
			logger.debug("Fire Event Called for ["+event.getEventClass()+"]");
		Map<Class<? extends EventHandler>, EventHandler> handlerList = (Map<Class<? extends EventHandler>, EventHandler>)eventHandlerMap.get(event.getClass().getName());	
		if(handlerList == null){
			if(logger.isDebugEnabled())
				logger.debug("No registered events[" + event.getEventClass().getName() + "]");
			return;
		}
		
		Set<Entry<Class<? extends EventHandler>, EventHandler>> entrySet = handlerList.entrySet();
		Iterator<Entry<Class<? extends EventHandler>, EventHandler>> iterator = entrySet.iterator();
		
		while(iterator.hasNext()) {
			Entry<Class<? extends EventHandler>, EventHandler> next = iterator.next();
			EventHandler eventHandler = next.getValue();
			// TODO: Exception handling? What if the handler must be accomplished even if there is one of the handler has an exception.
			eventHandler.handleEvent(event);		
		}
	}

	private EventInfo createOrGetEventInfo(String enentName, String eventClass) {
		EventInfo eventInfo = eventStatistics.get(eventClass);
		if(eventInfo == null) {
			 eventInfo = new EventInfo();
			 
			 eventInfo.setEventClassName(eventClass);
			 eventInfo.setEventName(enentName);
			 
			 EventInfo putIfAbsent = eventStatistics.putIfAbsent(eventClass, eventInfo);
			 if(putIfAbsent != null)
				 eventInfo = putIfAbsent;
			 
		 }		
		return eventInfo;
	}
	
	/**
	 * Accumulate event statistics for an event.
	 * <li> Event statistics maintain event occurred count.   
	 * <li> EventInfo class contains the statistics of each event. 
	 * @param event
	 */
	private void addEventStatistics(Event event) {
		EventInfo eventInfo = createOrGetEventInfo(event.toString(), event.getClass().getName());
		eventInfo.setLasttime(System.currentTimeMillis());
		eventInfo.incrementAndGet();
	}

	public static EventDistributor getInstance() {
		if(eventDistributor == null)
			eventDistributor = new EventDistributorImpl();
		return eventDistributor;
	}
	
	public List<EventInfo> getEventStatistics() {
		return new ArrayList<EventInfo>(eventStatistics.values());
	}
}
