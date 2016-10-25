package com.anyframe.core.vo.performance;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.Test;

public class ReflectionMemoryTest {

	private PrintStream out = System.out;
	private static final int REPEAT_COUNT = 1000000;

	@Test
	public void test4() {

		long before = 0;
		long after = 0;

		out.println("Case 3) ReflectionOnce - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();
		reflectionOnceMethodCall();
		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + "ms.");

	}

	@Test
	public void test5() {

		long before = 0;
		long after = 0;

		out.println("Case 4) fromMap/ToMap - repeat " + REPEAT_COUNT + " times.");
		before = System.currentTimeMillis();
		mapMethodCall();
		after = System.currentTimeMillis();
		out.println("       Elapsed " + (after - before) + "ms.");

	}

	private void pureMethodCall() {
		TestObject tObj = new TestObject();
		for (int i = 0; i < REPEAT_COUNT; i++) {
			tObj.setField1("hello");
		}
	}

	private void lookupMethodCall() {
		TestObject tObj = new TestObject();
		Method[] med = tObj.getClass().getMethods();

		for (int i = 0; i < med.length; i++) {
			out.println(" Method [" + i + "] : " + med[i].getName());
		}
	}

	private void reflectionMethodCall() {
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
	}

	private void reflectionOnceMethodCall() {
		TestObject tObj = new TestObject();
		Method[] medArray = tObj.getClass().getMethods();
		Method med = medArray[0];
		HashMap<String, Method> medMap = new HashMap<String, Method>();
		medMap.put("targetMethod", medArray[0]);
		medMap.put("targetMethod2", medArray[1]);
		medMap.put("targetMethod3", medArray[2]);

		try {
			for (int i = 0; i < REPEAT_COUNT; i++) {
				Method medCall = medMap.get("targetMethod");
				medCall.invoke(tObj, "hello");
				Method medCall2 = medMap.get("targetMethod2");
				medCall.invoke(tObj, "hello");
				Method medCall3 = medMap.get("targetMethod3");
				medCall.invoke(tObj, "hello");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void mapMethodCall() {
		TestObject tObj = new TestObject();

		for (int i = 0; i < REPEAT_COUNT; i++) {
			HashMap<String, Object> srcMap = new HashMap<String, Object>();
			srcMap.put("field1", "hello");
			srcMap.put("field2", "hello");
			srcMap.put("field3", "hello");
			tObj.fromMap(srcMap);
		}

	}

}
