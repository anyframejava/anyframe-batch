package com.anyframe.core.annotation;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.AnnotationVo;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.reflector.Reflector;

public class AnnotationTests {

	@Test
	public void defaultValue() {
		CoreContext.getMetaManager().setClassLoader(AnnotationTests.class.getClassLoader());
		
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(AnnotationVo.class.getName());
		Reflector reflector = CoreContext.getMetaManager().getReflector(AnnotationVo.class.getName());

		Object obj = metadata.newInstance();
		
		// Test default Value
		
		Object value = reflector.getValue(obj, "name");
		Assert.assertNotNull(value);
		System.out.println(value);
		Assert.assertEquals(value.toString().equals("John"), true);
		
		value = reflector.getValue(obj, "number");
		Assert.assertNotNull(value);
		System.out.println(value);
		Assert.assertEquals(((Integer)value) == 123, true);
		
		value = reflector.getValue(obj, "admin");
		Assert.assertNotNull(value);
		System.out.println(value);
		Assert.assertTrue(((Boolean)value) == true);
		
		value = reflector.getValue(obj, "rate");
		Assert.assertNotNull(value);
		System.out.println(value);
		Assert.assertTrue(((Float)value) == 1.1f);
		

		value = reflector.getValue(obj, "money");
		Assert.assertNotNull(value);
		System.out.println(value);
		Assert.assertTrue(((BigDecimal)value).toString().equals("12.3"));
		
	}
}
