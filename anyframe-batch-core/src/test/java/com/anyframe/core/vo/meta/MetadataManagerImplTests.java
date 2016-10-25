package com.anyframe.core.vo.meta;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.reflector.Reflector;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/meta/metamanager.xml"})
public class MetadataManagerImplTests {
	@Test
	public void diMeteResolver() {
		CoreContext.getMetaManager().setClassLoader(MetadataManagerImplTests.class.getClassLoader());
		
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(Pojo2Vo.class.getName());

		Pojo2Vo object = (Pojo2Vo) metadata.newInstance();

		Reflector reflector = CoreContext.getMetaManager().getReflector(Pojo2Vo.class.getName());
		reflector.setValue(object, "number", 11);
		int number = object.getNumber();
		Assert.assertEquals(number == 11, true);
		number = (Integer)reflector.getValue(object, "number");
		Assert.assertEquals(number == 11, true);
	}
}
