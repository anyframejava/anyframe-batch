package com.anyframe.core.vo.meta;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.AejoBasedSampleChildVo;
import com.anyframe.core.vo.AejoBasedSampleChildVoForTheSameFieldNameExistRuntimeException;
import com.anyframe.core.vo.SampleVo;
import com.anyframe.core.vo.meta.impl.MetadataManagerImpl;
import com.anyframe.core.vo.meta.impl.MetadataResolverImpl;
import com.anyframe.core.vo.meta.impl.PojoMetadataResolverListenerImpl;
import com.anyframe.core.vo.reflector.NoSuchMethodRuntimeException;
import com.anyframe.core.vo.reflector.Reflector;
import com.anyframe.core.vo.reflector.impl.VoMethodInvokeRunitmeException;

public class VoMetaTests {

	@BeforeClass
	public static void setUp() {
		CoreContext.getMetaManager().setClassLoader(VoMetaTests.class.getClassLoader());
	}
	
	@Test
	public void testAejo() {
		//Using Vo
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(SampleVo.class.getName());

		AbstractVo abstractVo = (AbstractVo) metadata.newInstance();

		abstractVo.setValue("name", "john");
		String name = abstractVo.getValue("name");
		Assert.assertNotNull(name);
		Assert.assertEquals(name.equals("john"), true);

		//Using reflector
		Reflector reflector = CoreContext.getMetaManager().getReflector(SampleVo.class.getName());

		Object obj = metadata.newInstance();

		reflector.setValue(obj, "name", "john");
		name = reflector.getValue(obj, "name");
		Assert.assertEquals(name.equals("john"), true);
		Assert.assertNotNull(name);

		boolean equals = abstractVo.equals(obj);
		Assert.assertEquals(equals, true);

		Object[] values = reflector.getValues(obj);
		Assert.assertEquals(values[0], "john");

		reflector.setValues(obj, values);
		Object value = reflector.getValue(obj, 0);
		Assert.assertEquals(value, "john");

		//Using reflector
		reflector = CoreContext.getMetaManager().getReflector("com.anyframe.core.vo.SampleVo");

		obj = metadata.newInstance();

		reflector.setValue(obj, "name", "john");

		equals = abstractVo.equals(obj);
		Assert.assertEquals(equals, true);
	}

	@Test
	public void testPojo() {
		//Using proxy
		Reflector reflector = CoreContext.getMetaManager().getReflector(PojoVo.class.getName());

		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(PojoVo.class.getName());

		Object obj = voMeta.newInstance();

		reflector.setValue(obj, "name", "john");
		reflector.setValue(obj, "number", 123);
		reflector.setValue(obj, "income", new BigDecimal(123.112));

		String name = reflector.getValue(obj, "name");
		Object[] values = reflector.getValues(obj);
		Assert.assertEquals(values[1], 123);

		reflector.setValues(obj, values);
		Object[] values2 = reflector.getValues(obj);

		Assert.assertEquals(values[0], values2[0]);
		Assert.assertEquals(values[1], values2[1]);
		Assert.assertEquals(values[2], values2[2]);

		Assert.assertNotNull(name);
		System.out.println(obj);
	}

	@Test
	public void testNoSuchMethodRuntimeException() {
		//Using proxy
		Reflector reflector = CoreContext.getMetaManager().getReflector(PojoVo.class.getName());

		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(PojoVo.class.getName());

		Object newInstance = voMeta.newInstance();

		boolean error = false;
		try {
			reflector.setValue(newInstance, "noField", "john");
		} catch (NoSuchMethodRuntimeException e) {
			error = true;
			e.printStackTrace();
		}
		Assert.assertEquals(error, true);
		error = false;

		try {
			String name = reflector.getValue(newInstance, "noField");
		} catch (NoSuchMethodRuntimeException e) {
			error = true;
			e.printStackTrace();
		}
		Assert.assertEquals(error, true);
	}

	@Test
	public void testLowerCaseMethodTest() {
		//Using Vo
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(SampleVo.class.getName());

		SampleVo abstractVo = (SampleVo) metadata.newInstance();
		abstractVo.setValue("uPField", "lower case");

		String name = abstractVo.getValue("uPField");
		Assert.assertEquals(name.equals("lower case"), true);

		abstractVo.setuPField("lower case2");

		name = abstractVo.getValue("uPField");
		Assert.assertEquals(name.equals("lower case2"), true);

		abstractVo.setValue("uPField2", "lower case2");

		name = abstractVo.getValue("uPField2");
		Assert.assertEquals(name.equals("lower case2"), true);

	}


	@Test
	public void theSameFieldNameExistRuntimeException() {
		MetadataManagerImpl metaManager = (MetadataManagerImpl) CoreContext.getMetaManager();
		metaManager.setThrowExceptionWhenTheSameFieldnameExist(true);
		boolean bException = false;
		try {
			VoMeta metadata = CoreContext.getMetaManager().getMetadata(AejoBasedSampleChildVoForTheSameFieldNameExistRuntimeException.class.getName());
		} catch (TheSameFieldNameExistRuntimeException e) {
			e.printStackTrace();
			bException = true;
		}
		Assert.assertEquals(bException, true);

		bException = false;
		try {
			VoMeta metadata = CoreContext.getMetaManager().getMetadata(PojoChildVoTheSameFieldNameExistRuntimeException.class.getName());
		} catch (TheSameFieldNameExistRuntimeException e) {
			e.printStackTrace();
			bException = true;
		}
		Assert.assertEquals(bException, true);
		metaManager.setThrowExceptionWhenTheSameFieldnameExist(false);
	}


	@Test
	public void extendedAejoVoTest() {
		List<Field> fields = new ArrayList<Field> ();

		getAllFields(AejoBasedSampleChildVo.class, fields);

//		for(Field field: fields) {
//			System.out.println(field.getName() + "in " + field.getDeclaringClass().getCanonicalName());
//		}

		VoMeta metadata = CoreContext.getMetaManager().getMetadata(AejoBasedSampleChildVo.class.getName());

		List<FieldMeta> fields2 = metadata.getFields();

		for(FieldMeta fieldMeta: fields2) {
			System.out.println(fieldMeta.getFieldName() + " " + fieldMeta.getFieldClass().getCanonicalName());
		}

		Object object = metadata.newInstance();

		Reflector reflector = CoreContext.getMetaManager().getReflector(AejoBasedSampleChildVo.class.getName());
		reflector.setValue(object, "subId", "sub id");
		Object value = reflector.getValue(object, "subId");
		Assert.assertEquals(value.toString().equals("sub id"), true);

		reflector.setValue(object, "name", "john");
		value = reflector.getValue(object, "name");
		Assert.assertEquals(value.toString().equals("john"), true);
	}

	@Test
	public void pojoVoTest() {
		MetadataManagerImpl metaManager = (MetadataManagerImpl) CoreContext.getMetaManager();
		PojoMetadataResolverListenerImpl metadataResolverListener = new PojoMetadataResolverListenerImpl();
		metadataResolverListener.setPojoClassIncludePrefix("com.anyframe");

		MetadataResolver metadataResolver = MetadataResolverImpl.getInstance();
		metadataResolver.addMetadataResolverListener(metadataResolverListener);

		metaManager.setMetadataResolver(metadataResolver);

		List<Field> fields = new ArrayList<Field> ();

		getAllFields(Pojo2Vo.class, fields);

//		for(Field field: fields) {
//			System.out.println(field.getName() + "in " + field.getDeclaringClass().getCanonicalName());
//		}

		VoMeta metadata = CoreContext.getMetaManager().getMetadata(Pojo2Vo.class.getName());

		List<FieldMeta> fields2 = metadata.getFields();

		for(FieldMeta fieldMeta: fields2) {
			System.out.println(fieldMeta.getFieldName() + " " + fieldMeta.getFieldClass().getCanonicalName());
		}

		Pojo2Vo object = (Pojo2Vo) metadata.newInstance();

		Reflector reflector = CoreContext.getMetaManager().getReflector(Pojo2Vo.class.getName());
		reflector.setValue(object, "number", 11);
		int number = object.getNumber();
		Assert.assertEquals(number == 11, true);
		number = (Integer) reflector.getValue(object, "number");
		Assert.assertEquals(number == 11, true);

		reflector.setValue(object, "name", "john");
		Object value = reflector.getValue(object, "name");
		Assert.assertEquals(value.toString().equals("john"), true);

		reflector.setValue(object, "weight", new BigInteger("58"));
		BigInteger weight = reflector.getValue(object, "weight");
		Assert.assertEquals(weight.intValue() == 58, true);
		weight = object.getWeight();
		Assert.assertEquals(weight.intValue() == 58, true);
	}

	@Test
	public void extendedPojoVoTest() {
		List<Field> fields = new ArrayList<Field> ();

		getAllFields(PojoChildVo.class, fields);

//		for(Field field: fields) {
//			System.out.println(field.getName() + "in " + field.getDeclaringClass().getCanonicalName());
//		}

		VoMeta metadata = CoreContext.getMetaManager().getMetadata(PojoChildVo.class.getName());

		List<FieldMeta> fields2 = metadata.getFields();

		for(FieldMeta fieldMeta: fields2) {
			System.out.println(fieldMeta.getFieldName() + " " + fieldMeta.getFieldClass().getCanonicalName());
		}

		Object object = metadata.newInstance();

		Reflector reflector = CoreContext.getMetaManager().getReflector(PojoChildVo.class.getName());
		reflector.setValue(object, "subId", "sub id");
		Object value = reflector.getValue(object, "subId");
		Assert.assertEquals(value.toString().equals("sub id"), true);

		reflector.setValue(object, "name", "john");
		value = reflector.getValue(object, "name");
		Assert.assertEquals(value.toString().equals("john"), true);

		boolean bError = false;

		try {
			reflector.setValue(object, "name", new Integer("1111"));
		} catch (VoMethodInvokeRunitmeException e) {
			bError = true;
		}
		Assert.assertEquals(bError, true);
	}

	private void getAllFields(Class<?> cls, List<Field> fields) {
		if(cls == java.lang.Object.class || cls == AbstractVo.class)
			return;

		Field[] fields2 = cls.getDeclaredFields();

		fields.addAll(Arrays.asList(fields2));

		if(cls.getSuperclass() != null)
			getAllFields(cls.getSuperclass(), fields);
	}
	
	@Test
	public void getMetaData() {
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(Pojo2Vo.class.getName());
		FieldMeta fieldMeta = metadata.getFieldMeta("notFieldName");
		Assert.assertNull(fieldMeta);
	}
}
