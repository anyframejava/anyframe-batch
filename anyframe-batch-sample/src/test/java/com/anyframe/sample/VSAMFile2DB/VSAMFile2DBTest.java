package com.anyframe.sample.VSAMFile2DB;

import org.junit.Assert;

import org.junit.Test;

import com.sds.anyframe.batch.launcher.BatchJobLauncher;

public class VSAMFile2DBTest {
	
	@Test
	public void run() {
		BatchJobLauncher launcher = new BatchJobLauncher();
		
		// class path 기반으로 job configuration을 로딩하여 배치 실행 
		int ret = launcher.execute(new String[]{"classpath:com/anyframe/sample/VSAMFile2DB/VSAMFile2DB_CFG.xml"});
//		launcher.execute(new String[]{"classpath:com/anyframe/sample/VSAMFile2DB/VSAMFile2DB_CFG.xml","step=createDataWithoutVo"});
		
		// file path 기반으로 job configuration을 로딩하여 배치 실행
		// launcher.execute(new String[]{"file:build/com/anyframe/sample/File2DB/File2DB_CFG.xml"});
		Assert.assertEquals(0, ret);
	}
}