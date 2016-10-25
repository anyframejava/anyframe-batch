package com.anyframe.core.vo.performance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTestPojo extends AbstractPerformanceTest {

	@Test
	public void testPojo() {

		System.out.println("Case 1) PojoVo  setter - repeat " + ITERATION + " times.");

		for (int j = 0; j < AVGCOUNT; j++) {
			long currentTimeNanos = System.nanoTime();

			for (int i = 0; i < ITERATION; i++) {
				PojoVo vo = new PojoVo();

				vo.setName(NAME);
				vo.setAddress(ADDRESS + i);
				vo.setAddress2(ADDRESS2);
				vo.setNumber(NUMBER);
				vo.setNumber2(NUMBER2);
				vo.setNumber3(NUMBER3);
				vo.setNumber4(NUMBER4);
				vo.setIncome(INCOME);
				vo.setIncome2(INCOME2);
				vo.setIncome3(INCOME3);
			}
			System.out.println(System.nanoTime() - currentTimeNanos);
		}
	}
}
