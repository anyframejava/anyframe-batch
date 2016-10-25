package com.anyframe.core.vo.meta;

import static org.junit.Assert.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.SampleVo;
import com.anyframe.core.vo.meta.FieldMeta.FieldType;
import com.anyframe.core.vo.meta.impl.MetadataResolverImpl;
import com.anyframe.core.vo.reflector.Reflector;

public class FieldMetaTests {

	@Test
	public void testByteArray() {
		byte[] array = new byte[2];
		array[0] = 1;
		array[1] = 2;
		
		Object obj = array;
		
		System.out.println(obj.getClass().isArray());
		System.out.println(obj);
		System.out.println(obj.getClass().getName());
	}
	                             
	@Test
	public void test()  {
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		
		VoMeta voMeta = resolver.getMetaData(SampleVo.class);
		FieldMeta fieldMeta = voMeta.getFieldMeta("subVo");
		
		Assert.assertEquals(fieldMeta.getType() == FieldType.VO, true);
		System.out.println(voMeta.toString());
	}
	
	
	@Test
	public void testGeneric() {
		
		List 						list2 = new ArrayList();
		List<AbstractVo> 			list3 = new ArrayList<AbstractVo>();
		List<? extends AbstractVo> 	list4 = new ArrayList<AbstractVo>();
		
		Class<?> class1 = List.class;
		Class<?> class2 = list2.getClass();
		Class<?> class3 = list3.getClass();
		Class<?> class4 = list4.getClass();
		
		Type type1 = class1;
		Type type2 = class2;
		Type type3 = class3.getGenericSuperclass();
		Type type4 = class3.getGenericSuperclass();
		
		try {
			if(type3 instanceof ParameterizedType) {
				ParameterizedType pType3 = (ParameterizedType)type3;
				Type[] arg3 = pType3.getActualTypeArguments();
				Class argClass3 = (Class)arg3[0];
				System.out.println(argClass3);
			}
			
			if(type4 instanceof ParameterizedType) {
				ParameterizedType pType4 = (ParameterizedType)type4;
				Type[] arg4 = pType4.getActualTypeArguments();
				Class argClass4 = (Class)arg4[0];
				System.out.println(argClass4);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenericList() {
		
		List normalList = new ArrayList();
		List<AbstractVo> genericList = new ArrayList<AbstractVo>();
		List<? extends AbstractVo> unboundList = new ArrayList<AbstractVo>();
		
		Class<?> listClass = List.class;
		Class<?> normalClass = normalList.getClass();
		Class<?> genericClass = genericList.getClass();
		Class<?> unboundClass = unboundList.getClass();
		
		System.out.println(listClass == normalClass);
		System.out.println(listClass.isAssignableFrom(normalClass));
		System.out.println(normalClass.isAssignableFrom(genericClass));
		System.out.println(normalClass.isAssignableFrom(unboundClass));
		
	}
	
	@Test
	public void testTimestamp() {
		
		Calendar cal = Calendar.getInstance();
		
		cal.set(1, Calendar.JANUARY, 0, 24, 0, 0);
		
		long timeInMillis = cal.getTimeInMillis();
		Timestamp ts = new Timestamp(timeInMillis);
		ts.setNanos(123456789);
		
		System.out.println(ts);
		
		Timestamp ts2 = Timestamp.valueOf("0000-01-01 00:00:00.000001000");
		System.out.println(ts2);
		
	}

	@Test
	public void testByte() {
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		Reflector reflector = CoreContext.getMetaManager().getReflector(SampleVo.class);
		
		VoMeta voMeta = resolver.getMetaData(SampleVo.class);
		
		Object newInstance = voMeta.newInstance();
		Object value = reflector.getValue(newInstance, "byteType");
		System.out.println(value);
		value = reflector.getValue(newInstance, "byteObjectType");
		System.out.println(value);
		value = reflector.getValue(newInstance, "charType");
		System.out.println(value);
		value = reflector.getValue(newInstance, "charObjectType");
		System.out.println(value);
		
		FieldMeta fieldMeta = voMeta.getFieldMeta("byteType");
		value = fieldMeta.fromString("8");
		System.out.println(value);
		fieldMeta = voMeta.getFieldMeta("byteObjectType");
		value = fieldMeta.fromString("8");
		System.out.println(value);
		
		fieldMeta = voMeta.getFieldMeta("charType");
		value = fieldMeta.fromString("a");
		System.out.println(value);
		fieldMeta = voMeta.getFieldMeta("charObjectType");
		value = fieldMeta.fromString("z");
		System.out.println(value);
		
		boolean mustError = false;
		try {
			value = fieldMeta.fromString("zz");
		} catch (Exception e) {
			mustError = true;
			e.printStackTrace();
		}
		Assert.assertEquals(mustError, true);
		
	}
	
	@Test
	public void testFieldOffset(){
		
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		
		EmployeeListVo listVo = new EmployeeListVo();
		
		List list = new ArrayList();
		int i=0;
		for (int k=0; k <5; k++){
			EmployeeVo testVo = new EmployeeVo();
			int j=1;
			testVo.setNo1(j);
			testVo.setNo2(new BigDecimal(j));
			testVo.setNo3(new BigInteger(String.valueOf(j)));
			testVo.setDate1(new Date(j, j, j));
			testVo.setTimestamp1(new Timestamp(j));
			
			testVo.setName("이름_"+j);
			testVo.setAddress("주소_"+j);
			testVo.setDescription("설명_"+j);
			list.add(testVo);
		}
		listVo.setEmployeeVoList(list);
		listVo.setBigdecimal(new BigDecimal(i));
		listVo.setBiginteger(new BigInteger(String.valueOf(i)));
		listVo.setDate(new Date(i*10000));
		listVo.setDesc("desc"+i);
		listVo.setNumber(i);
		listVo.setTimestamp(new Timestamp(i));
 		
		
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(listVo.getClass());
		
		Assert.assertEquals(voMeta.getFieldCount(),54);
		
		Assert.assertEquals(voMeta.getFieldMeta("employeeVoList").getFieldCount(), 40);
		Assert.assertEquals(voMeta.getFieldMeta("employeeVoList").getByteOffset(), 0);
		
		Assert.assertEquals(voMeta.getFieldMeta("employeeVo").getFieldCount(), 8);
		
		Assert.assertEquals(voMeta.getFieldMeta("desc").getByteOffset(), 1100);
		Assert.assertEquals(voMeta.getFieldMeta("desc").getFieldCount(), 1);
		
		Assert.assertEquals(voMeta.getFieldMeta("desc").getFieldOffset(), 40);
	}
	
	@Test
	public void setNullValue(){
		Reflector reflector = CoreContext.getMetaManager().getReflector(SampleVo.class);
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		
		VoMeta voMeta = resolver.getMetaData(SampleVo.class);
		
		Object newInstance = voMeta.newInstance();
		reflector.setValue(newInstance, "name", null);
	}
	
	/**
	 * 재귀 호출 또는 circular chain 일 경우, Byte length 길이 체크가 필요한 경우에는 StackOverflow에러 발생<br>
	 * 그 외에는 허용<br>
	 * FIXME 검토 필요
	 */
	@Test
	public void circularChain(){
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		VoMeta voMeta = resolver.getMetaData(CircularVo.class);
		CircularVo circularVo = (CircularVo) voMeta.newInstance();
		assertNotNull(circularVo);
		VoMeta innerListMeta = voMeta.getFieldMeta("list").getVoMeta();
		assertNotNull(innerListMeta);
		assertNotNull(innerListMeta.getFieldMeta("age"));
		//circular chain : meta data null - circularVo.getList().getList() <== null (circularVo.getList() == otherCircularVO, otherCircularVO.getList() = circularVO)
		assertNull(innerListMeta.getFieldMeta("list").getVoMeta());
		
		Reflector reflector = CoreContext.getMetaManager().getReflector(CircularVo.class);
		reflector.setValue(circularVo, "name", "Hong gil dong");
		reflector.setValue(circularVo, "age", "32");
		
		CircularVo circularVo2 = (CircularVo) voMeta.newInstance();
		reflector.setValue(circularVo2, "name", "Kim Nam il");
		reflector.setValue(circularVo2, "age", "34");

		VoMeta voMeta2 = MetadataResolverImpl.getInstance().getMetaData(OtherCircularVo.class);
		OtherCircularVo otherCircularVo = (OtherCircularVo) voMeta2.newInstance();
		reflector.setValue(otherCircularVo, "name", "Lee Young Hee");
		reflector.setValue(otherCircularVo, "age", "21");
		
		reflector.setValue(circularVo, "self", circularVo2);
		reflector.setValue(circularVo, "list", Arrays.asList(otherCircularVo));
		reflector.setValue(circularVo, "array", new OtherCircularVo[]{otherCircularVo});
		
		assertNotSame(circularVo, circularVo.getSelf());
		System.out.println("Vo : "+circularVo);
	}
	
	@Test
	public void circularChainPojo(){
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		VoMeta voMeta = resolver.getMetaData(CircularPojoVo.class);
		
		CircularPojoVo circularPojoVo = (CircularPojoVo) voMeta.newInstance();
		assertNotNull(circularPojoVo);

		Reflector reflector = CoreContext.getMetaManager().getReflector(CircularPojoVo.class);
		reflector.setValue(circularPojoVo, "name", "Hong gil dong");
		reflector.setValue(circularPojoVo, "age", "32");
		
		CircularPojoVo circularVo2 = (CircularPojoVo) voMeta.newInstance();
		reflector.setValue(circularVo2, "name", "Lee Young Hee");
		reflector.setValue(circularVo2, "age", "21");
		
		reflector.setValue(circularPojoVo, "self", circularVo2);
		reflector.setValue(circularPojoVo, "list", Arrays.asList(circularVo2));
		reflector.setValue(circularPojoVo, "array", new CircularPojoVo[]{circularVo2});
		
		assertNotSame(circularPojoVo, circularPojoVo.getSelf());
		System.out.println("Vo : "+circularPojoVo);
	}
	
	@Test
	public void checkMetaData(){
		MetadataResolver resolver = MetadataResolverImpl.getInstance();
		VoMeta voMeta = resolver.getMetaData(NormalVO.class);
		FieldMeta fieldMeta = voMeta.getFieldMeta("list2");
		FieldMeta name = fieldMeta.getVoMeta().getFieldMeta("name");
		assertNotNull(name);
		
	}
}
