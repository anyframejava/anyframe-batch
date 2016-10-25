package com.anyframe.core.vo.performance;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTestFromMap extends AbstractPerformanceTest {

	@Test
	public void testFromMap() throws Exception {
		System.out.println("Case 7) PojoVo fromMap - repeat " + ITERATION + " times.");

		for (int j = 0; j < AVGCOUNT; j++) {
			long currentTimeNanos = System.nanoTime();

			for (int i = 0; i < ITERATION; i++) {
				HashMap<String, Object> srcMap = new HashMap<String, Object>();

				srcMap.put("name", NAME);
				srcMap.put("address", ADDRESS);
				srcMap.put("address2", ADDRESS2);
				srcMap.put("number", NUMBER);
				srcMap.put("number2", NUMBER2);
				srcMap.put("number3", NUMBER3);
				srcMap.put("number4", NUMBER4);
				srcMap.put("income", INCOME);
				srcMap.put("income2", INCOME2);
				srcMap.put("income3", INCOME3);

				PojoVo vo = new PojoVo();
				vo.fromMap(srcMap);
				vo.setAddress(ADDRESS + i);
			}

			System.out.println(System.nanoTime() - currentTimeNanos);
		}

	}

}
