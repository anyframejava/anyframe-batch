package com.anyframe.core.vo.performance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTestAejo extends AbstractPerformanceTest {

	@Test
	public void testAejoReflector() {

		System.out.println("Case 1) AejoVo reflector setValue - repeat " + ITERATION + " times.");
		
		for (int j = 0; j < AVGCOUNT; j++) {
			long currentTimeNanos = System.nanoTime();

			for (int i = 0; i < ITERATION; i++) {
				AejoVo vo = (AejoVo) aejoVoMeta.newInstance();

				aejoReflector.setValue(vo, "name", NAME);
				aejoReflector.setValue(vo, "address", ADDRESS + i);
				aejoReflector.setValue(vo, "address2", ADDRESS2);
				aejoReflector.setValue(vo, "number", NUMBER);
				aejoReflector.setValue(vo, "number2", NUMBER2);
				aejoReflector.setValue(vo, "number3", NUMBER3);
				aejoReflector.setValue(vo, "number4", NUMBER4);
				aejoReflector.setValue(vo, "income", INCOME);
				aejoReflector.setValue(vo, "income2", INCOME2);
				aejoReflector.setValue(vo, "income3", INCOME3);
			}
			System.out.println(System.nanoTime() - currentTimeNanos);
		}
	}

}
