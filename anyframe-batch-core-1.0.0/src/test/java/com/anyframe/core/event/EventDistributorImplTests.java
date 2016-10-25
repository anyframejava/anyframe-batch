package com.anyframe.core.event;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.anyframe.core.CoreContext;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/event/event.xml"})
public class EventDistributorImplTests implements EventHandler {
	
	static boolean fired;
	
	public void handleEvent(Event event) {
		fired = true;
	}

	public Class getHandlerClass() {
		return this.getClass();
	}
	
	@Test
	public void fire() {
		EventDistributor eventDistributor = CoreContext.getEventDistributor();
		
		eventDistributor.fireEvent(new BasicEvent(this.getClass(), BasicEvent.class.getName()));
		Assert.assertEquals(fired, true);
	}
	
	@Test
	public void getEventStatistics() {
		fire();
		EventDistributor eventDistributor = CoreContext.getEventDistributor();
		
		List<EventInfo> eventStatistics = eventDistributor.getEventStatistics();
		Assert.assertNotNull(eventStatistics);
		
		System.out.println(eventStatistics);
	}
}
