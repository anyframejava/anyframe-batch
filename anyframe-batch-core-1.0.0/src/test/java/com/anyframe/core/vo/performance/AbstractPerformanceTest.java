package com.anyframe.core.vo.performance;

import java.math.BigDecimal;

import org.junit.BeforeClass;

import com.anyframe.core.CoreContext;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.meta.impl.MetadataManagerImpl;
import com.anyframe.core.vo.reflector.Reflector;

public class AbstractPerformanceTest {

	static final int ITERATION = 10000;
	static final int AVGCOUNT = 30;

	static final String NAME = "zzazazan";
	static final String ADDRESS = "bryan";
	static final String ADDRESS2 = "Jeiryun";

	static final int NUMBER = 0;
	static final int NUMBER2 = 20;
	static final int NUMBER3 = 30;
	static final int NUMBER4 = 50;

	static final BigDecimal INCOME = BigDecimal.ZERO;
	static final BigDecimal INCOME2 = new BigDecimal(100L);
	static final BigDecimal INCOME3 = new BigDecimal(10000L);

	static VoMeta aejoVoMeta = null;
	static VoMeta pojoVoMeta = null;
	static Reflector bciReflector = null;
	static Reflector aejoReflector = null;
	static Reflector pojoReflector = null;

	/**
	 * 최초에 한번 생성되면 Cache되는 Object들은 성능에 측정에서 제외시킨다.
	 */
	@BeforeClass
	public static void setUp() {
		aejoVoMeta = CoreContext.getMetaManager().getMetadata(AejoVo.class);
		pojoVoMeta = CoreContext.getMetaManager().getMetadata(PojoVo.class);
		bciReflector = MetadataManagerImpl.getInstance().getReflector(PojoVo.class, true);
		pojoReflector = MetadataManagerImpl.getInstance().getReflector(PojoVo.class);
		aejoReflector = MetadataManagerImpl.getInstance().getReflector(AejoVo.class);
	}

}
