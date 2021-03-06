package com.anyframe.sample.DB2SAMFile;

import org.junit.Assert;

import org.junit.Test;

import com.sds.anyframe.batch.launcher.BatchJobLauncher;

public class DB2SAMFileTest {
	
	@Test
	public void run() {
		BatchJobLauncher launcher = new BatchJobLauncher();
		
		// class path 기반으로 job configuration을 로딩하여 배치 실행 
		int ret = launcher.execute(new String[]{"classpath:com/anyframe/sample/DB2SAMFile/DB2SAMFile_CFG.xml"});
//		launcher.execute(new String[]{"classpath:com/anyframe/sample/DB2SAMFile/DB2SAMFile_CFG.xml","step=createDataWithoutVo"});
		
		// file path 기반으로 job configuration을 로딩하여 배치 실행
		// launcher.execute(new String[]{"file:build/com/anyframe/sample/DB2VSAMFile/DB2File_CFG.xml"});
		Assert.assertEquals(0, ret);
	}
}