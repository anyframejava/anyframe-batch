package com.anyframe.core.vo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.charset.ICharsetEncoder.Padding;
import com.anyframe.core.vo.meta.Pojo2Vo;
import com.anyframe.core.vo.meta.PojoVo;
import com.anyframe.core.vo.transform.bytes.ConversionConfiguration;

public class VoUtilTests {

	@BeforeClass
	public static void setUp() {
		CoreContext.getMetaManager().setClassLoader(VoUtilTests.class.getClassLoader());
	}

	@Test
	public void toMap() {

		AbstractVo vo1 = buildFullVo();
		AbstractVo vo2 = buildVoWithNull();

		Map<String, Object> map1 = VoUtil.toMap(vo1);
		Map<String, Object> map2 = VoUtil.toMap(vo2);

		Assert.assertNotNull(map1);
		System.out.println(vo1);
		System.out.println(map1.toString());

		Assert.assertNotNull(map2);
		System.out.println(vo2);
		System.out.println(map2.toString());
	}

	@Test
	public void mapEncodeDecodeOnPojo() {
		Map<String, Object> sourceMap = new HashMap<String, Object>();
		sourceMap.put("name", "john");
		sourceMap.put("income", new BigDecimal(1111));
		sourceMap.put("number", 100);

		PojoVo pojoVo = (PojoVo) VoUtil.fromMap(sourceMap, PojoVo.class);

		Assert.assertEquals(pojoVo.getName().equals("john"), true);
		Assert.assertEquals(pojoVo.getNumber() == 100, true);

		Map<String, Object> map = VoUtil.toMap(pojoVo);
		Assert.assertEquals(map.get("name").equals("john"), true);
		Assert.assertEquals(map.get("number").equals(100), true);

	}

	@Test
	public void fromMap() {
		SampleVo vo1 = (SampleVo) buildFullVo();
		SampleVo vo2 = (SampleVo) buildVoWithNull();

		vo1.setArrayString(new String[] { "12456", "22345" });

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

		System.out.println(vo1);
		System.out.println(vo2);

		Map<String, Object> map1 = VoUtil.toMap(vo1);
		Map<String, Object> map2 = VoUtil.toMap(vo2);

		System.out.println("Map: " + map1);
		System.out.println("Map: " + map2);

		SampleVo vo3 = (SampleVo) VoUtil.fromMap(map1, SampleVo.class);
		SampleVo vo4 = (SampleVo) VoUtil.fromMap(map2, SampleVo.class.getName());

		assertThat(vo3.getName(), is(vo1.getName()));
		assertThat(vo4.getName(), is(vo2.getName()));
		System.out.println(vo1);
		System.out.println(vo2);
	}

	@Test
	public void testNullBytes() throws UnsupportedEncodingException {
		SampleVo vo1 = (SampleVo) buildVoWithNull();

		vo1.setArrayString(new String[] { "12456", "22345" });

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

		System.out.println(vo1);

		byte[] bytes1 = VoUtil.toBytes(vo1, "UTF8");
		System.out.println(new String(bytes1, "UTF8"));
		AbstractVo vo11 = (AbstractVo) VoUtil.fromByte(bytes1, vo1.getClass(), "UTF8", true);

		System.out.println(vo11);
	}

	@Test
	public void testBytes() throws UnsupportedEncodingException {
		SampleVo vo1 = (SampleVo) buildFullVo();

		vo1.setArrayString(new String[] { "12456", "22345" });
		vo1.setByteType((byte) 3);
		vo1.setByteObjectType(new Byte((byte) 9));
		byte byteType = vo1.getByteType();
		System.out.println(byteType);
		vo1.setCharType('%');
		vo1.setCharObjectType(new Character('@'));
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

		System.out.println(vo1);

		byte[] bytes1 = VoUtil.toBytes(vo1, "UTF8");
		System.out.println(new String(bytes1, "UTF8"));
		AbstractVo vo11 = (AbstractVo) VoUtil.fromByte(bytes1, vo1.getClass(), "UTF8", true);
		System.out.println(vo11);
	}

	@Test
	public void arrayOfPrimitiveTypeTest() {
		SampleVo vo1 = (SampleVo) buildFullVo();
		vo1.setArrayString(new String[] { "12456", "22345" });

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

		byte[] bytes = VoUtil.toBytes(vo1, "utf-8");

		SampleVo vo2 = (SampleVo) VoUtil.fromByte(bytes, SampleVo.class, "utf-8");

		Assert.assertEquals(vo1.getArrayString().length == vo2.getArrayString().length, true);
		Assert.assertEquals(vo1.getArrayString()[0].equals(vo2.getArrayString()[0]), true);

		Assert.assertEquals(vo1.getArrayVo().length == vo2.getArrayVo().length, true);
		Assert.assertEquals(vo1.getArrayVo()[0].equals(vo2.getArrayVo()[0]), true);

		// Bytes Compare
		byte[] bytes2 = VoUtil.toBytes(vo1, "utf-8");
		Arrays.equals(bytes, bytes2);

		Map<String, Object> map = VoUtil.toMap(vo1);
		Map<String, Object> map2 = VoUtil.toMap(vo2);
		Assert.assertNotNull(map);
		System.out.println(map);
		Assert.assertNotNull(map2);
		System.out.println(map2);

		vo1 = (SampleVo) VoUtil.fromMap(map, SampleVo.class);
		vo2 = (SampleVo) VoUtil.fromMap(map2, SampleVo.class);

		Assert.assertTrue(vo1.getArrayString().length == 2);
		Assert.assertTrue(vo2.getArrayVo().length == 2);
	}

	@Test
	public void copyPojoVo() {
		PojoVo source = new PojoVo();
		source.setName("john");
		source.setIncome(new BigDecimal(100));
		source.setNumber(111);

		ArrayList<String> list = new ArrayList<String>();
		list.add("Bennz");
		list.add("BMW");

		source.setList(list);

		SubVo[] subVos = new SubVo[2];

		SubVo e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		subVos[0] = e;

		e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		subVos[1] = e;

		source.setNames(new String[] { "Josh", "Jarry" });
		source.setSubVos(subVos);

		PojoVo target = new PojoVo();

		VoUtil.copy(source, target);

		System.out.println(source);
		System.out.println(target);

		Assert.assertEquals(source.getName().equals(target.getName()), true);
	}

	@Test
	public void copyAejoVo() {
		SampleVo source = (SampleVo) buildFullVo();

		SubVo[] arrayVo = new SubVo[2];

		SubVo e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[0] = e;

		e = new SubVo();
		e.setAge(30);
		e.setName("List_1");
		arrayVo[1] = e;

		source.setArrayString(new String[] { "12456", "22345" });

		source.setArrayVo(arrayVo);

		SampleVo target = new SampleVo();

		VoUtil.copy(source, target);

		assertThat(target.getArrayVo()[0].getAge(), is(30));
		assertThat(source, CoreMatchers.equalTo(target));

		System.out.println(source.toString());
		System.out.println(target.toString());
	}

	@Test
	public void testEmptyVo() {
		SampleVo vo = new SampleVo();
		byte[] bytes = VoUtil.toBytes(vo, "utf8");

	}

	@Test
	public void testVariableLength() throws UnsupportedEncodingException {
		VariableSampleVo vo = new VariableSampleVo();
		vo.setName("John");
		vo.setAge(new Integer(29));

		List<String> listString = new ArrayList<String>();
		listString.add("Monkey");
		listString.add("Donkey");
		listString.add("Snake");
		listString.add("Elephent");
		listString.add("Seal");

		vo.setListString(listString);

		List<SubVo> listVo = new ArrayList<SubVo>();
		for (int i = 1; i < 4; i++) {
			SubVo e = new SubVo();
			e.setAge(30 + i);
			e.setName("List_" + i);
			listVo.add(e);
		}
		vo.setListVo(listVo);

		// test data for variable collection
		List<String> variableListString = new ArrayList<String>();
		variableListString.add("Spaghetti");
		variableListString.add("Pizza");
		variableListString.add("Risotto");
		vo.setVariableListString(variableListString);

		List<SubVo> variableListVo = new ArrayList<SubVo>();

		for (int i = 0; i < 2; i++) {
			SubVo e = new SubVo();
			e.setAge(40 + i);
			e.setName("VarList_" + i);
			variableListVo.add(e);
		}
		vo.setVariableListVo(variableListVo);

		// test data for variable array
		String[] stringArray = { "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet" };
		vo.setVariableArrayString(stringArray);

		SubVo[] voArray = new SubVo[2];
		for (int i = 0; i < voArray.length; i++) {
			SubVo subVO = new SubVo();
			subVO.setAge(40 + i);
			subVO.setName("VarArr_" + i);
			voArray[i] = subVO;
		}
		vo.setVariableArrayVo(voArray);

		int length = vo.getProxy().getVoMeta().getLength();

		ConversionConfiguration config = new ConversionConfiguration(10, Padding.SPACE);
		//		byte[] bytes1 = VoUtil.toBytes(vo, "UTF8");
		byte[] bytes1 = VoUtil.toBytes(vo, "UTF8", config);
		Assert.assertTrue(length < bytes1.length);
		//		AbstractVo vo1 = (AbstractVo) VoUtil.fromByte(bytes1, vo.getClass(), "UTF8", true);
		AbstractVo vo1 = (AbstractVo) VoUtil.fromByte(bytes1, vo.getClass(), "UTF8", true, config);
		byte[] bytes2 = VoUtil.toBytes(vo1, "UTF8", config);

		Assert.assertTrue(Arrays.equals(bytes1, bytes2));

	}

	@Test
	public void byteArrayTest() {
		PrimitiveVo vo = new PrimitiveVo();
		vo.getBytes();
	}

	@Test
	public void testCopyPojoToAejo() {
		SampleVo aejoVo = new SampleVo();

		SubVo subAejoVo = new SubVo();
		subAejoVo.setName("SUBAEJOVO");
		subAejoVo.setAge(29);

		PojoVo pojoSubVo = new PojoVo();
		pojoSubVo.setName("SUBPOJOVO");
		pojoSubVo.setNumber(11);
		pojoSubVo.setList(Arrays.asList(new String[] { "Red", "Orange", "Yellow", "Green", "Blue" }));
		pojoSubVo.setIncome(new BigDecimal(110));

		pojoSubVo.setSubVos(new SubVo[] { subAejoVo });

		Pojo2Vo pojoVo = new Pojo2Vo();
		pojoVo.setIncome(new BigDecimal(100));
		pojoVo.setList(Arrays.asList(new String[] { "Spring", "Summer", "Fall", "Winter" }));
		pojoVo.setName("POJOVO");
		pojoVo.setNumber(1);
		pojoVo.setPojoVo(pojoSubVo);
		pojoVo.setSubVo(subAejoVo);

		VoUtil.copy(pojoVo, aejoVo);

		assertEquals(aejoVo.getName(), pojoVo.getName());
		assertEquals(aejoVo.getSubVo().getName(), pojoVo.getSubVo().getName());

		assertNotSame(aejoVo.getPojoVo(), pojoVo.getPojoVo());
		assertEquals(aejoVo.getPojoVo().getName(), pojoVo.getPojoVo().getName());
		assertArrayEquals(aejoVo.getPojoVo().getSubVos(), pojoVo.getPojoVo().getSubVos());
	}

	@Test
	public void testCopyAejoToPojo() {
		SampleVo aejoVo = (SampleVo) buildFullVo();
		Pojo2Vo pojoVo = new Pojo2Vo();

		VoUtil.copy(aejoVo, pojoVo);

		assertEquals(aejoVo.getName(), pojoVo.getName());
		assertEquals(aejoVo.getSubVo().getName(), pojoVo.getSubVo().getName());
		assertEquals(aejoVo.getPojoVo().getName(), pojoVo.getPojoVo().getName());
		assertArrayEquals(aejoVo.getPojoVo().getSubVos(), pojoVo.getPojoVo().getSubVos());
		assertNotSame(aejoVo.getPojoVo(), pojoVo.getPojoVo());
	}

	@Test
	public void testConvertJson() {
		SampleVo aejoVo = (SampleVo) buildFullVo();

		try {
			// test convert from VO to JSON
			String jsonOfAejoVo = VoUtil.toJson(aejoVo);

			// test convert from JSON to VO
			SampleVo result = (SampleVo) VoUtil.fromJson(SampleVo.class, jsonOfAejoVo);

			assertThat(result, equalTo(aejoVo));

			// test convert from VO to JSON
			FileOutputStream fos = new FileOutputStream(new File("test-json.json"));
			VoUtil.toJson(aejoVo, fos);
			fos.close();
			// test convert from JSON to VO
			FileInputStream fis = new FileInputStream(new File("test-json.json"));
			SampleVo readVo = (SampleVo) VoUtil.fromJson(SampleVo.class, fis);
			assertThat(readVo, equalTo(aejoVo));
			fis.close();

			assertThat(new File("test-json.json").delete(), is(true));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Could not write JSON: " + e.getMessage());
		}
	}

	private AbstractVo buildFullVo() {
		SampleVo vo = new SampleVo();

		vo.setAge(new Integer(29));
		vo.setMoney(new BigDecimal("-123.45"));
		vo.setName("John");
		vo.setNumber(-180);

		List<String> listString = new ArrayList<String>();
		listString.add("Monkey");
		listString.add("Donkey");
		listString.add("Snake");
		listString.add("Elephent");
		listString.add("Seal");

		vo.setListString(listString);

		List<SubVo> listVo = new ArrayList<SubVo>();
		for (int i = 1; i < 4; i++) {
			SubVo e = new SubVo();
			e.setAge(30 + i);
			e.setName("List_" + i);
			listVo.add(e);
		}

		vo.setListVo(listVo);

		SubVo subVo = new SubVo();
		subVo.setAge(10);
		subVo.setName("subVoName");
		vo.setSubVo(subVo);

		SubVo subAejoVo = new SubVo();
		subAejoVo.setName("SUBAEJOVO");
		subAejoVo.setAge(29);

		PojoVo pojoSubVo = new PojoVo();
		pojoSubVo.setName("SUBPOJOVO");
		pojoSubVo.setNumber(11);
		pojoSubVo.setList(Arrays.asList(new String[] { "Red", "Orange", "Yellow", "Green", "Blue" }));
		pojoSubVo.setIncome(new BigDecimal(110));

		pojoSubVo.setSubVos(new SubVo[] { subAejoVo, subVo });

		vo.setPojoVo(pojoSubVo);

		// test data for variable collection
		List<String> variableListString = new ArrayList<String>();
		variableListString.add("Spaghetti");
		variableListString.add("Pizza");
		variableListString.add("Risotto");
		vo.setVariableListString(variableListString);

		List<SubVo> variableListVo = new ArrayList<SubVo>();

		for (int i = 0; i < 2; i++) {
			SubVo e = new SubVo();
			e.setAge(40 + i);
			e.setName("VarList_" + i);
			variableListVo.add(e);
		}
		vo.setVariableListVo(variableListVo);

		// test data for variable array
		String[] stringArray = { "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet" };
		vo.setVariableArrayString(stringArray);

		SubVo[] voArray = new SubVo[2];
		for (int i = 0; i < voArray.length; i++) {
			SubVo subVO = new SubVo();
			subVO.setAge(40 + i);
			subVO.setName("VarArr_" + i);
			voArray[i] = subVO;
		}
		vo.setVariableArrayVo(voArray);

		return vo;
	}

	private AbstractVo buildVoWithNull() {
		SampleVo vo = new SampleVo();

		vo.setAge(new Integer(29));
		vo.setMoney(new BigDecimal(2990000));
		// vo.setName("John");
		vo.setNumber(182);

		// List<String> listString = new ArrayList<String> ();
		// listString.add("monkey");
		// listString.add("Donkey");
		// listString.add("Snake");
		// listString.add("Elephent");
		// listString.add("Seal");
		//
		// vo.setListString(listString);

		List<SubVo> listVo = new ArrayList<SubVo>();

		for (int i = 1; i < 4; i++) {
			SubVo e = new SubVo();
			e.setAge(30 + i);
			e.setName("List_" + i);
			listVo.add(e);
		}

		vo.setListVo(listVo);

		// SubVo subVo = new SubVo();
		// subVo.setAge(10);
		// subVo.setName("subVoName");
		// vo.setSubVo(subVo);

		return vo;
	}

	@Test
	public void testPojoToMapWithWildcardGeneric() {
		SampleSubPojoWithWildcardGeneric ssp1 = new SampleSubPojoWithWildcardGeneric();
		ssp1.setName2("이몽룡");
		ssp1.setNumber2(14);
		SampleSubPojoWithWildcardGeneric ssp2 = new SampleSubPojoWithWildcardGeneric();
		ssp2.setName2("성춘향");
		ssp2.setNumber2(16);
		List<SampleSubPojoWithWildcardGeneric> list = new ArrayList<SampleSubPojoWithWildcardGeneric>();
		list.add(ssp1);
		list.add(ssp2);
		SamplePojoWithWildcardGeneric sp = new SamplePojoWithWildcardGeneric();
		sp.setIncome(new BigDecimal("1232.234"));
		sp.setList(list);
		sp.setName("변학도");
		sp.setNumber(22);

		Map<String, Object> map1 = VoUtil.toMap(sp);
		Assert.assertNotNull(map1);
		System.out.println("Original VO : \n" + sp);
		System.out.println("Transformed Map : \n" + map1);
	}

	@Test
	public void testBigDecimalByteConverting() throws UnsupportedEncodingException {

		BigDecimal test = new BigDecimal("12345.7");

		System.out.println("12345.7 ==> Precision : " + test.precision() + ", Scale : " + test.scale()
				+ ", Sign Num : " + test.signum() + ", Unscaled Value : " + test.unscaledValue());

		BigDecimal test2 = new BigDecimal("-12345.7");
		System.out.println("-12345.7 ==> Precision : " + test2.precision() + ", Scale : " + test2.scale()
				+ ", Sign Num : " + test2.signum() + ", Unscaled Value : " + test2.unscaledValue());

		SampleVo vo1 = (SampleVo) buildFullVo();
		vo1.setMoney(new BigDecimal("12345"));

		SampleVo vo2 = (SampleVo) buildFullVo();
		vo2.setMoney(new BigDecimal("12345.6"));

		SampleVo vo3 = (SampleVo) buildFullVo();
		vo3.setMoney(new BigDecimal("-12345.67"));

		SampleVo[] sampleVoArray = { vo1, vo2, vo3 };

		for (SampleVo sampleVo : sampleVoArray) {
			byte[] bytes1 = VoUtil.toBytes(sampleVo, "UTF8");
			System.out.println(new String(bytes1, "UTF8"));
			SampleVo vo11 = (SampleVo) VoUtil.fromByte(bytes1, sampleVo.getClass(), "UTF8", true);
			System.out.println(vo11);
			assertThat(vo11.getMoney().compareTo(sampleVo.getMoney()), is(0));
		}
	}
}
