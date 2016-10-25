package com.anyframe.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import sample.IsAndGetVo;

public class ClassUtilTests {

	@Test
	public void getInterfaceTypeConvertedParameters() {
		
		Class<?>[] types = new Class<?>[4];
		types[0] = HashMap.class;
		types[1] = ArrayList.class;
		types[2] = int.class;
		types[3] = HashSet.class;
		
		Class<?>[] interfaceInheritedParameters = ClassUtil.getInterfaceTypeConvertedParameters(types);
		
		Assert.assertNotNull(interfaceInheritedParameters);
		Assert.assertEquals(interfaceInheritedParameters[0] == Map.class, true);
		Assert.assertEquals(interfaceInheritedParameters[1] == List.class, true);
		Assert.assertEquals(interfaceInheritedParameters[2] == int.class, true);
		Assert.assertEquals(interfaceInheritedParameters[3] == Set.class, true);
		
		types = new Class<?>[3];
		types[0] = String.class;
		types[1] = Date.class;
		types[2] = int.class;
		
		interfaceInheritedParameters = ClassUtil.getInterfaceTypeConvertedParameters(types);
		
		Assert.assertNull(interfaceInheritedParameters);
		
	}
	
	@Test
	public void testCreateInstance() {
		Object createInstance = ClassUtil.createInstance("java.lang.Integer", new Object[] {"1"}, new Class[] {String.class});
		Assert.assertTrue(createInstance instanceof Integer);
	}
	
	@Test
	public void booleanIsAndGetTest() {
		List<Field> fields = ClassUtil.getAllFields(IsAndGetVo.class);
		
		ClassUtil.getAllGetterSetterMethods(IsAndGetVo.class, fields);
	}
}
