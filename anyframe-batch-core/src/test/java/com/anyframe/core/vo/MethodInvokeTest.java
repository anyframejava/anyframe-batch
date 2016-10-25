package com.anyframe.core.vo;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MethodInvokeTest {
	private static final int _COUNT = 10;

	public AbstractVo buildFullVo() {
		SampleVo vo = new SampleVo();
		
		vo.setAge(new Integer(29));
		vo.setMoney(new BigDecimal(2990000));
		vo.setName("John");
		vo.setNumber(182);
		
		List<String> listString = new ArrayList<String> ();
		listString.add("monkey");
		listString.add("Donkey");
		listString.add("Snake");
		listString.add("Elephent");
		listString.add("Seal");
		
		vo.setListString(listString);
		
		List<SubVo> listVo = new ArrayList<SubVo> ();
		
		for(int i=1; i<4; i++) {
			SubVo e = new SubVo();
			e.setAge(30+i);
			e.setName("List_Name_" + i);
			listVo.add(e);
		}
		
		vo.setListVo(listVo);
		
		SubVo subVo = new SubVo();
		subVo.setAge(10);
		subVo.setName("subVoName");
		vo.setSubVo(subVo);
		
		return vo;
	}
	
	
	public AbstractVo buildVoWithNull() {
		SampleVo vo = new SampleVo();
		
		vo.setAge(new Integer(29));
		vo.setMoney(new BigDecimal(2990000));
//		vo.setName("John");
		vo.setNumber(182);
		
//		List<String> listString = new ArrayList<String> ();
//		listString.add("monkey");
//		listString.add("Donkey");
//		listString.add("Snake");
//		listString.add("Elephent");
//		listString.add("Seal");
//		
//		vo.setListString(listString);
		
		List<SubVo> listVo = new ArrayList<SubVo> ();
		
		for(int i=1; i<4; i++) {
			SubVo e = new SubVo();
			e.setAge(30+i);
			e.setName("List_Name_" + i);
			listVo.add(e);
		}
		
		vo.setListVo(listVo);
		
//		SubVo subVo = new SubVo();
//		subVo.setAge(10);
//		subVo.setName("subVoName");
//		vo.setSubVo(subVo);
		
		return vo;
	}
	
	@Before
	public void prepare() {

		SampleVo vo2 = (SampleVo) buildFullVo();

		long currentTimeMillis = System.currentTimeMillis();
		
		for(int i =0;i<_COUNT;i++) {
			vo2.setName("john");
			String name = vo2.getName();
		}
		
		System.out.println((System.currentTimeMillis() - currentTimeMillis)/1000);
	}
	
	@Test
	public void testPojo() {

		SampleVo vo2 = (SampleVo) buildFullVo();

		long currentTimeMillis = System.currentTimeMillis();
		
		for(int i =0;i<_COUNT;i++) {
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			vo2.setName("john");
			String name = vo2.getName();
		}
		
		System.out.println((System.currentTimeMillis() - currentTimeMillis)/1000);
	}
	
	@Test
	public void testMethodInvoke() throws Exception {
		
		SampleVo vo2 = (SampleVo) buildFullVo();
		
		Method setMethod = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod1 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod2 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod3 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod4 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod5 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod6 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod7 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method setMethod8 = vo2.getClass().getMethod("setName", new Class[] {String.class});
		Method getMethod = vo2.getClass().getMethod("getName", null);
		
		long currentTimeMillis = System.currentTimeMillis();
		
		for(int i =0;i<_COUNT;i++) {
			setMethod.invoke(vo2, "john");
			setMethod1.invoke(vo2, "john");
			setMethod2.invoke(vo2, "john");
			setMethod3.invoke(vo2, "john");
			setMethod4.invoke(vo2, "john");
			setMethod5.invoke(vo2, "john");
			setMethod6.invoke(vo2, "john");
			setMethod7.invoke(vo2, "john");
			setMethod8.invoke(vo2, "john");
			String name = (String) getMethod.invoke(vo2, null);
		}
		
		System.out.println((System.currentTimeMillis() - currentTimeMillis)/1000);
	}
	
	
	
	
	
//	@Test
//	public void testMethodInvoke() throws Exception {
//
//		SampleVo vo2 = (SampleVo) buildFullVo();
//
//		
//		long currentTimeMillis = System.currentTimeMillis();
//		
//		for(int i =0;i<_COUNT;i++) {
//			Method setMethod = vo2.getClass().getMethod("setName", new Class[] {String.class});
//			Method getMethod = vo2.getClass().getMethod("getName", null);
//			setMethod.invoke(vo2, "john");
//			String name = (String) getMethod.invoke(vo2, null);
//		}
//		
//		System.out.println((System.currentTimeMillis() - currentTimeMillis)/1000);
//	}
}
