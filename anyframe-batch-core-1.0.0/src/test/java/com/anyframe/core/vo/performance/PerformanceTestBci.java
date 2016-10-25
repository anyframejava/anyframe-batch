package com.anyframe.core.vo.performance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTestBci extends AbstractPerformanceTest {

	@Test
	public void testBci() {

		System.out.println("Case 5) BCI reflector setValue - repeat " + ITERATION + " times.");

		for (int j = 0; j < AVGCOUNT; j++) {
			long currentTimeNanos = System.nanoTime();

			for (int i = 0; i < ITERATION; i++) {
				PojoVo vo = (PojoVo) pojoVoMeta.newInstance();

				bciReflector.setValue(vo, "name", NAME);
				bciReflector.setValue(vo, "address", ADDRESS + i);
				bciReflector.setValue(vo, "address2", ADDRESS2);
				bciReflector.setValue(vo, "number", NUMBER);
				bciReflector.setValue(vo, "number2", NUMBER2);
				bciReflector.setValue(vo, "number3", NUMBER3);
				bciReflector.setValue(vo, "number4", NUMBER4);
				bciReflector.setValue(vo, "income", INCOME);
				bciReflector.setValue(vo, "income2", INCOME2);
				bciReflector.setValue(vo, "income3", INCOME3);
			}
			System.out.println(System.nanoTime() - currentTimeNanos);
		}
	}

}
