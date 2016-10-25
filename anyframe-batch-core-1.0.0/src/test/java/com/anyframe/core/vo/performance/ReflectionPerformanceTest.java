package com.anyframe.core.vo.performance;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.VoUtil;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.reflector.Reflector;

public class ReflectionPerformanceTest {

	private PrintStream out = System.out;
	private static final int REPEAT_COUNT = 1000000;

	@BeforeClass
	public static void setUp() {
		CoreContext.getMetaManager().setClassLoader(ReflectionPerformanceTest.class.getClassLoader());
	}

	@Test
	public void test1() {
		long before = 0;
		long after = 0;

		out.println("Reflection Performance Test Start.");
		out.println("Case 1) Pure Method Call - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();

		TestObject tObj = new TestObject();
		for (int i = 0; i < REPEAT_COUNT; i++) {
			tObj.setField1("hello1");
			tObj.setField2("hello2");
			tObj.setField3("hello3");
		}

		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void test2() {

		long before = 0;
		long after = 0;

		out.println("Look Up Method List for Reflection. ");
		before = System.currentTimeMillis();

		TestObject tObj = new TestObject();
		Method[] med = tObj.getClass().getMethods();

		for (int i = 0; i < med.length; i++) {
			out.println(" Method [" + i + "] : " + med[i].getName());
		}

		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void test3() {

		long before = 0;
		long after = 0;

		out.println("Case 2) Reflection - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();

		TestObject tObj = new TestObject();
		try {
			for (int i = 0; i < REPEAT_COUNT; i++) {
				Method[] medArray = tObj.getClass().getMethods();
				Method med = medArray[0];
				med.invoke(tObj, "hello");
				Method med2 = medArray[1];
				med2.invoke(tObj, "hello");
				Method med3 = medArray[2];
				med3.invoke(tObj, "hello");

			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void test4() {

		long before = 0;
		long after = 0;

		out.println("Case 3) ReflectionOnce - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();

		TestObject tObj = new TestObject();
		Method[] medArray = tObj.getClass().getMethods();
		HashMap<String, Method> medMap = new HashMap<String, Method>();
		medMap.put("targetMethod", medArray[0]);
		medMap.put("targetMethod2", medArray[1]);
		medMap.put("targetMethod3", medArray[2]);

		try {
			for (int i = 0; i < REPEAT_COUNT; i++) {
				Method medCall = medMap.get("targetMethod");
				medCall.invoke(tObj, "hello");
				Method medCall2 = medMap.get("targetMethod2");
				medCall2.invoke(tObj, "hello");
				Method medCall3 = medMap.get("targetMethod3");
				medCall3.invoke(tObj, "hello");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void test5() {

		long before = 0;
		long after = 0;

		out.println("Case 4) fromMap/ToMap - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();
		TestObject tObj = new TestObject();

		for (int i = 0; i < REPEAT_COUNT; i++) {
			HashMap<String, Object> srcMap = new HashMap<String, Object>();
			srcMap.put("field1", "hello");
			srcMap.put("field2", "hello");
			srcMap.put("field3", "hello");
			tObj.fromMap(srcMap);
		}
		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void test6() {

		long before = 0;
		long after = 0;

		out.println("Case 5) fromMap/ToMap by VoUtil - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();
		TestObject tObj = new TestObject();

		for (int i = 0; i < REPEAT_COUNT; i++) {
			HashMap<String, Object> srcMap = new HashMap<String, Object>();
			srcMap.put("field1", "hello");
			srcMap.put("field2", "hello");
			srcMap.put("field3", "hello");
			VoUtil.fromMap(srcMap, TestObject.class);
		}
		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + " ms.");

	}

	@Test
	public void methodInvoke() {
		Reflector reflector = CoreContext.getMetaManager().getReflector(TestObject.class);
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(TestObject.class);
		for (int j = 0; j < 10; j++) {
			long currentTimeMillis = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				Object obj = new TestObject();
				reflector.setValue(obj, "field1", "hello");
				reflector.setValue(obj, "field2", "hello");
				reflector.setValue(obj, "field3", "hello");
			}
			System.out.println(System.currentTimeMillis() - currentTimeMillis);
		}
	}

	@Test
	public void fromMap() {

		for (int j = 0; j < 10; j++) {
			long currentTimeMillis = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				TestObject tObj = new TestObject();
				HashMap<String, Object> srcMap = new HashMap<String, Object>();
				srcMap.put("field1", "hello");
				srcMap.put("field2", "hello");
				srcMap.put("field3", "hello");
				tObj.fromMap(srcMap);
			}
			System.out.println(System.currentTimeMillis() - currentTimeMillis);
		}
	}

}
