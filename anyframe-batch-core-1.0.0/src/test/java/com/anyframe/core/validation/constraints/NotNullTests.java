package com.anyframe.core.validation.constraints;

import javax.validation.constraints.NotNull;

import junit.framework.Assert;

import org.junit.Test;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.SampleVo;
import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.reflector.Reflector;


public class NotNullTests {
	
	@Test
	public void notNull() {
		VoMeta metadata = CoreContext.getMetaManager().getMetadata(SampleVo.class);
		Reflector reflector = CoreContext.getMetaManager().getReflector(SampleVo.class);
		
		SampleVo sampleVo = new SampleVo();
		
		boolean isNotNull = false;
		for(FieldMeta fieldMeta: metadata.getFields()) {
			
			Object value = reflector.getValue(sampleVo, fieldMeta.getFieldName());
			
			NotNull notNull = fieldMeta.getAnnotation(NotNull.class);
			try {
				if (notNull != null && value == null)
					throw new NullPointerException(fieldMeta.getFieldName() + " " + notNull.message());
			} catch (NullPointerException e) {
				isNotNull = true;
				e.printStackTrace();
			}
		}
		
		Assert.assertTrue(isNotNull);
	}
}