package com.anyframe.core.vo.performance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTestCachedMethodInvoke extends AbstractPerformanceTest {

	@Test
	public void testCachedMethodInvoke() {

		System.out.println("Case 6) Method Invoke  setter- repeat " + ITERATION + " times.");

		for (int j = 0; j < AVGCOUNT; j++) {
			long currentTimeNanos = System.nanoTime();

			for (int i = 0; i < ITERATION; i++) {
				PojoVo vo = (PojoVo) pojoVoMeta.newInstance();

				pojoReflector.setValue(vo, "name", NAME);
				pojoReflector.setValue(vo, "address", ADDRESS + i);
				pojoReflector.setValue(vo, "address2", ADDRESS2);
				pojoReflector.setValue(vo, "number", NUMBER);
				pojoReflector.setValue(vo, "number2", NUMBER2);
				pojoReflector.setValue(vo, "number3", NUMBER3);
				pojoReflector.setValue(vo, "number4", NUMBER4);
				pojoReflector.setValue(vo, "income", INCOME);
				pojoReflector.setValue(vo, "income2", INCOME2);
				pojoReflector.setValue(vo, "income3", INCOME3);
			}
			System.out.println(System.nanoTime() - currentTimeNanos);
		}
	}
}
