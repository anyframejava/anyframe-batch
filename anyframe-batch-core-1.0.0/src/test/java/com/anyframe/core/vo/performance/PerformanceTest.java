package com.anyframe.core.vo.performance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.meta.impl.MetadataManagerImpl;
import com.anyframe.core.vo.proxy.VoProxy;
import com.anyframe.core.vo.reflector.Reflector;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml" })
public class PerformanceTest {

	static final int ITERATION = 1000000;

	static final String NAME = "zzazazan";
	static final String ADDRESS = "bryan";
	static final String ADDRESS2 = "jeryeon";

	static final int NUMBER = 0;
	static final int NUMBER2 = 20;
	static final int NUMBER3 = 30;
	static final int NUMBER4 = 50;

	static final BigDecimal INCOME = BigDecimal.ZERO;
	static final BigDecimal INCOME2 = new BigDecimal(100L);
	static final BigDecimal INCOME3 = new BigDecimal(10000L);

	static VoMeta aejoVoMeta = null;
	static VoMeta pojoVoMeta = null;
	static Reflector bciReflector = null;
	static Reflector pojoReflector = null;
	static Reflector aejoReflector = null;

	/**
	 * 최초에 한번 생성되면 Cache되는 Object들은 성능에 측정에서 제외시킨다.
	 */
	@BeforeClass
	public static void setUp() {
		aejoVoMeta = CoreContext.getMetaManager().getMetadata(AejoVo.class);
		pojoVoMeta = CoreContext.getMetaManager().getMetadata(PojoVo.class);
		pojoReflector = MetadataManagerImpl.getInstance().getReflector(PojoVo.class);
		bciReflector = MetadataManagerImpl.getInstance().getReflector(PojoVo.class, true);
		aejoReflector = MetadataManagerImpl.getInstance().getReflector(AejoVo.class);
	}

	@Test
	public void testAejoProxy() {
		System.out.println("Reflection Performance Test Start.");
		System.out.println("Case 1) AejoVo proxy setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		for (int i = 0; i < ITERATION; i++) {
			AejoVo vo = (AejoVo) aejoVoMeta.newInstance();
			VoProxy proxy = vo.getProxy();
			proxy.setValue(0, NAME);
			proxy.setValue(1, ADDRESS);
			proxy.setValue(2, ADDRESS2);
			proxy.setValue(3, NUMBER);
			proxy.setValue(4, NUMBER2);
			proxy.setValue(5, NUMBER3);
			proxy.setValue(6, NUMBER4);
			proxy.setValue(7, INCOME);
			proxy.setValue(8, INCOME2);
			proxy.setValue(9, INCOME3);
		}

		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testAejoSetter() {
		System.out.println("Case 2) AejoVo setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestAejoSetter();
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testAejoReflector() {
		System.out.println("Case 3) AejoVo reflector setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestAejoReflector();
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testPojoSetter() {
		System.out.println("Case 4) PojoVo setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestPojoSetter();

		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testPojoReflector() {
		System.out.println("Case 5) PojoVo reflector setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestPojoReflector();
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testBciReflector() {
		System.out.println("Case 5-1) PojoVo BCI setValue - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestBciReflector();
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testPojoReflection() throws Exception {
		System.out.println("Case 6) PojoVo reflection API - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		performTestPojoReflectionAPI();
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testFromMap() throws Exception {
		System.out.println("Case 7) PojoVo fromMap - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		for (int i = 0; i < ITERATION; i++) {
			HashMap<String, Object> srcMap = makeSourceMap();
			
			PojoVo vo = new PojoVo();
			vo.fromMap(srcMap);
		}
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testFromMapByReflectorByName() throws Exception {
		System.out.println("Case 8) PojoVo using reflector by name - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();

		HashMap<String, Object> srcMap = makeSourceMap();

		for (int i = 0; i < ITERATION; i++) {

			PojoVo vo = new PojoVo();
			pojoReflector.setValue(vo, "name", srcMap.get("name"));
			pojoReflector.setValue(vo, "address", srcMap.get("address"));
			pojoReflector.setValue(vo, "address2", srcMap.get("address2"));
			pojoReflector.setValue(vo, "number", srcMap.get("number"));
			pojoReflector.setValue(vo, "number2", srcMap.get("number2"));
			pojoReflector.setValue(vo, "number3", srcMap.get("number3"));
			pojoReflector.setValue(vo, "number4", srcMap.get("number4"));
			pojoReflector.setValue(vo, "income", srcMap.get("income"));
			pojoReflector.setValue(vo, "income2", srcMap.get("income2"));
			pojoReflector.setValue(vo, "income3", srcMap.get("income3"));
		}

		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testFromMapByReflectorByIndex() throws Exception {
		System.out.println("Case 8) PojoVo using reflector by index - repeat " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();
		for (int i = 0; i < ITERATION; i++) {
			HashMap<String, Object> srcMap = makeSourceMap();

			PojoVo vo = new PojoVo();
			pojoReflector.setValue(vo, 0, srcMap.get("name"));
			pojoReflector.setValue(vo, 1, srcMap.get("address"));
			pojoReflector.setValue(vo, 2, srcMap.get("address2"));
			pojoReflector.setValue(vo, 3, srcMap.get("number"));
			pojoReflector.setValue(vo, 4, srcMap.get("number2"));
			pojoReflector.setValue(vo, 5, srcMap.get("number3"));
			pojoReflector.setValue(vo, 6, srcMap.get("number4"));
			pojoReflector.setValue(vo, 7, srcMap.get("income"));
			pojoReflector.setValue(vo, 8, srcMap.get("income2"));
			pojoReflector.setValue(vo, 9, srcMap.get("income3"));
		}
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testPureMethodInvokeTest() throws Exception {
		System.out.println("Case 10) Pure Method Invoke via Invoke " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();
		PojoVo vo = new PojoVo();

		Method setNameMethod = vo.getClass().getMethod("setName", String.class);
		Method setAddressMethod = vo.getClass().getMethod("setAddress", String.class);
		Method setAddress2Method = vo.getClass().getMethod("setAddress2", String.class);
		Method setNumberMethod = vo.getClass().getMethod("setNumber", int.class);
		Method setNumber2Method = vo.getClass().getMethod("setNumber2", int.class);
		Method setNumber3Method = vo.getClass().getMethod("setNumber3", int.class);
		Method setNumber4Method = vo.getClass().getMethod("setNumber4", int.class);
		Method setIncomeMethod = vo.getClass().getMethod("setIncome", BigDecimal.class);
		Method setIncome2Method = vo.getClass().getMethod("setIncome2", BigDecimal.class);
		Method setIncome3Method = vo.getClass().getMethod("setIncome3", BigDecimal.class);

		for (int i = 0; i < ITERATION; i++) {
			vo = new PojoVo();

			setNameMethod.invoke(vo, NAME);
			setAddressMethod.invoke(vo, ADDRESS);
			setAddress2Method.invoke(vo, ADDRESS2);
			setNumberMethod.invoke(vo, NUMBER);
			setNumber2Method.invoke(vo, NUMBER2);
			setNumber3Method.invoke(vo, NUMBER3);
			setNumber4Method.invoke(vo, NUMBER4);
			setIncomeMethod.invoke(vo, INCOME);
			setIncome2Method.invoke(vo, INCOME2);
			setIncome3Method.invoke(vo, INCOME3);
		}
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testMapCreation() throws Exception {
		System.out.println("Case 11) Map Creation " + ITERATION + " times.");

		long currentTimeNanos = System.nanoTime();
		for (int i = 0; i < ITERATION; i++) {
			HashMap<String, Object> srcMap = makeSourceMap();
		}
		System.out.println("	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
	}

	@Test
	public void testThreadAejoSetterTest() throws Exception {

		for (int i = 0; i < 10; i++) {
			TestAejoSetterRun run = new TestAejoSetterRun();
			run.start();
		}
		Thread.sleep(4000);
	}

	@Test
	public void testThreadAejoReflectorTest() throws Exception {
		for (int i = 0; i < 10; i++) {
			TestAejoReflectorRun run = new TestAejoReflectorRun();
			run.start();
		}
		Thread.sleep(4000);
	}

	@Test
	public void testThreadPojoSetterTest() throws Exception {
		for (int i = 0; i < 10; i++) {
			TestPojoSetterRun run = new TestPojoSetterRun();
			run.start();
		}
		Thread.sleep(1000);
	}

	@Test
	public void testThreadPojoReflectorTest() throws Exception {
		for (int i = 0; i < 10; i++) {
			TestPojoReflectorRun run = new TestPojoReflectorRun();
			run.start();
		}
		Thread.sleep(10000);
	}

	private class TestAejoSetterRun extends Thread {
		public void run() {
			System.out.println("Case 12) Aejo Setter Thread Test " + ITERATION + " times.");
			long currentTimeNanos = System.nanoTime();
			try {
				performTestAejoSetter();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("Case 12)	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
		}
	}

	private class TestAejoReflectorRun extends Thread {

		public void run() {
			System.out.println("Case 13) Aejo Reflector Thread Test " + ITERATION + " times.");
			long currentTimeNanos = System.nanoTime();
			try {
				performTestAejoReflector();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("Case 13) 	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
		}
	}

	private class TestPojoSetterRun extends Thread {
		public void run() {
			System.out.println("Case 14) POJO Setter Thread Test " + ITERATION + " times.");
			long currentTimeNanos = System.nanoTime();
			try {
				performTestPojoSetter();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("Case 14)	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
		}
	}

	private class TestPojoReflectorRun extends Thread {
		public void run() {
			System.out.println("Case 15) POJO Reflector Thread Test " + ITERATION + " times.");
			long currentTimeNanos = System.nanoTime();
			try {
				performTestPojoReflector();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("Case 15) 	Elapsed Time: " + (System.nanoTime() - currentTimeNanos) + " ns.");
		}
	}

	private void performTestAejoSetter() {
		for (int i = 0; i < ITERATION; i++) {
			AejoVo vo = (AejoVo) aejoVoMeta.newInstance();
			vo.setName(NAME);
			vo.setAddress(ADDRESS);
			vo.setAddress2(ADDRESS2);
			vo.setNumber(NUMBER);
			vo.setNumber2(NUMBER2);
			vo.setNumber3(NUMBER3);
			vo.setNumber4(NUMBER4);
			vo.setIncome(INCOME);
			vo.setIncome2(INCOME2);
			vo.setIncome3(INCOME3);
		}
	}

	private void performTestAejoReflector() {
		for (int i = 0; i < ITERATION; i++) {
			AejoVo vo = (AejoVo) aejoVoMeta.newInstance();
			aejoReflector.setValue(vo, 0, NAME);
			aejoReflector.setValue(vo, 1, ADDRESS);
			aejoReflector.setValue(vo, 2, ADDRESS2);
			aejoReflector.setValue(vo, 3, NUMBER);
			aejoReflector.setValue(vo, 4, NUMBER2);
			aejoReflector.setValue(vo, 5, NUMBER3);
			aejoReflector.setValue(vo, 6, NUMBER4);
			aejoReflector.setValue(vo, 7, INCOME);
			aejoReflector.setValue(vo, 8, INCOME2);
			aejoReflector.setValue(vo, 9, INCOME3);
		}
	}

	private void performTestPojoReflector() {
		for (int i = 0; i < ITERATION; i++) {
			PojoVo vo = new PojoVo();
			pojoReflector.setValue(vo, 0, NAME);
			pojoReflector.setValue(vo, 1, ADDRESS);
			pojoReflector.setValue(vo, 2, ADDRESS2);
			pojoReflector.setValue(vo, 3, NUMBER);
			pojoReflector.setValue(vo, 4, NUMBER2);
			pojoReflector.setValue(vo, 5, NUMBER3);
			pojoReflector.setValue(vo, 6, NUMBER4);
			pojoReflector.setValue(vo, 7, INCOME);
			pojoReflector.setValue(vo, 8, INCOME2);
			pojoReflector.setValue(vo, 9, INCOME3);
		}
	}

	private void performTestBciReflector() {
		for (int i = 0; i < ITERATION; i++) {
			PojoVo vo = new PojoVo();
			bciReflector.setValue(vo, 0, NAME);
			bciReflector.setValue(vo, 1, ADDRESS);
			bciReflector.setValue(vo, 2, ADDRESS2);
			bciReflector.setValue(vo, 3, NUMBER);
			bciReflector.setValue(vo, 4, NUMBER2);
			bciReflector.setValue(vo, 5, NUMBER3);
			bciReflector.setValue(vo, 6, NUMBER4);
			bciReflector.setValue(vo, 7, INCOME);
			bciReflector.setValue(vo, 8, INCOME2);
			bciReflector.setValue(vo, 9, INCOME3);
		}
	}

	private void performTestPojoSetter() {
		for (int i = 0; i < ITERATION; i++) {
			PojoVo vo = new PojoVo();
			vo.setName(NAME);
			vo.setAddress(ADDRESS);
			vo.setAddress2(ADDRESS2);
			vo.setNumber(NUMBER);
			vo.setNumber2(NUMBER2);
			vo.setNumber3(NUMBER3);
			vo.setNumber4(NUMBER4);
			vo.setIncome(INCOME);
			vo.setIncome2(INCOME2);
			vo.setIncome3(INCOME3);
		}
	}

	private void performTestPojoReflectionAPI() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		for (int i = 0; i < ITERATION; i++) {
			PojoVo vo = new PojoVo();
			Method setNameMethod = vo.getClass().getMethod("setName", String.class);
			Method setAddressMethod = vo.getClass().getMethod("setAddress", String.class);
			Method setAddress2Method = vo.getClass().getMethod("setAddress2", String.class);
			Method setNumberMethod = vo.getClass().getMethod("setNumber", int.class);
			Method setNumber2Method = vo.getClass().getMethod("setNumber2", int.class);
			Method setNumber3Method = vo.getClass().getMethod("setNumber3", int.class);
			Method setNumber4Method = vo.getClass().getMethod("setNumber4", int.class);
			Method setIncomeMethod = vo.getClass().getMethod("setIncome", BigDecimal.class);
			Method setIncome2Method = vo.getClass().getMethod("setIncome2", BigDecimal.class);
			Method setIncome3Method = vo.getClass().getMethod("setIncome3", BigDecimal.class);

			setNameMethod.invoke(vo, NAME);
			setAddressMethod.invoke(vo, ADDRESS);
			setAddress2Method.invoke(vo, ADDRESS2);
			setNumberMethod.invoke(vo, NUMBER);
			setNumber2Method.invoke(vo, NUMBER2);
			setNumber3Method.invoke(vo, NUMBER3);
			setNumber4Method.invoke(vo, NUMBER4);
			setIncomeMethod.invoke(vo, INCOME);
			setIncome2Method.invoke(vo, INCOME2);
			setIncome3Method.invoke(vo, INCOME3);
		}
	}

	private HashMap<String, Object> makeSourceMap() {
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
		return srcMap;
	}
}
