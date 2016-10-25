package com.anyframe.core.vo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AbstractVoTest {
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
	
	@Test
	public void testSerialize() throws Exception {
		AbstractVo vo = buildFullVo();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		oos.writeObject(vo);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object readObject = ois.readObject();
		
		System.out.println(vo);
		System.out.println(readObject);
		
		Assert.assertTrue(vo.toString().compareTo(readObject.toString()) == 0);
	}
	
	@Test
	public void equals() {
		SampleVo vo1 = (SampleVo) buildFullVo();
		
		SubVo subVo = new SubVo();
		subVo.setAge(10);
		subVo.setName("subVoName");
		vo1.setSubVo(subVo);
		
		SubVo[] arrayVo = new SubVo[2];
		
		SubVo e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[0] = e;
		
		e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[1] = e;
		
		vo1.setArrayVo(arrayVo);
		
		boolean equals = vo1.equals(vo1);
		Assert.assertEquals(equals, true);
		
		SampleVo vo2 = (SampleVo) buildFullVo();
		
		arrayVo = new SubVo[2];
		
		e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[0] = e;
		
		e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[1] = e;
		
		vo2.setArrayVo(arrayVo);
		
		equals = vo1.equals(vo2);
		Assert.assertEquals(equals, true);
		
		((SampleVo)vo2).getArrayVo()[0].setName("other ");
		
		equals = vo1.equals(vo2);
		Assert.assertEquals(equals, false);
		
		SampleVo vo3 = (SampleVo) buildFullVo();
		vo3.setAge(null);
		
		equals = vo1.equals(vo3);
		Assert.assertEquals(equals, false);
		
	}
	
	public static int calculate_hash(String input) {
	    int h = 0;
	    int len = input.length();
	    for (int i = 0; i < len; i++) {
	        h = 31 * h + input.charAt(i);
	    }
	    return h;
	}
	
	@Test
	public void hashCodeTest() {
		AbstractVo vo1 = buildFullVo();
		
		SampleVo vo2 = (SampleVo) buildFullVo();
		
		int hashCode = vo1.hashCode();
		int hashCode2 = vo2.hashCode();

		System.out.println(hashCode + "==" + hashCode2);
		
		boolean equals = hashCode == hashCode2;
		
		Assert.assertEquals(equals, true);
		
		vo2.setName("ohter name");
		
		hashCode = vo1.hashCode();
		hashCode2 = vo2.hashCode();

		System.out.println(hashCode + "!=" + hashCode2);
		
		equals = hashCode != hashCode2;
		
		Assert.assertEquals(equals, true);
	}
}
