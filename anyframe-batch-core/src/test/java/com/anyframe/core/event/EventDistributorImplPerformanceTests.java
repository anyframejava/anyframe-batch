package com.anyframe.core.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.springframework.util.StopWatch;

public class EventDistributorImplPerformanceTests {
	
	private Map<Integer, AtomicInteger> eventStatistics = new ConcurrentHashMap<Integer, AtomicInteger>();
	
	@Test
	public void testPerformance() {

		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		int count = 1000;
		
		for(int i=0;i<count;i++) {
			addEventStatistics(i);
		}
		stopWatch.stop();
		
		long totalTimeMillis = stopWatch.getTotalTimeMillis()/count;
		System.out.println("TOtal eplased time: " + totalTimeMillis);
	}
	
	private synchronized void addEventStatistics(int i) {
		AtomicInteger atomicInteger = eventStatistics.get(i);
		if(atomicInteger == null) {
			atomicInteger = new AtomicInteger();
			eventStatistics.put(i, atomicInteger);
		}
		
		atomicInteger.incrementAndGet();
	}
}
