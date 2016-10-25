/*                                                                           
 * Copyright 2010-2012 Samsung SDS Co., Ltd.                                 
 *                                                                           
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.          
 * You may obtain a copy of the License at                                   
 *                                                                           
 *     http://www.apache.org/licenses/LICENSE-2.0                            
 *                                                                           
 * Unless required by applicable law or agreed to in writing, software       
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and       
 * limitations under the License.                                            
 *                                                                           
 */                                                                          

package com.sds.anyframe.batch.core.step.tasklet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.repeat.ExitStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import com.sds.anyframe.batch.common.util.StepParameterUtil;
import com.sds.anyframe.batch.core.item.support.AnyframeItemFactory;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReaderFactory;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriterFactory;
import com.sds.anyframe.batch.core.transaction.ChainedTransactionManager;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.log.LogManager;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AnyframeAbstractTasklet implements AnyframeTasklet {

	private static final Log logger = LogFactory.getLog(AnyframeAbstractTasklet.class);

	private PlatformTransactionManager transactionManager;
	
	private TransactionStatus transactionStatus;
	
	private AnyframeItemFactory itemFactory;
	
	private int commitCount = 1;
	
	private int commitInterval = BatchDefine.WRITER_COMMIT_INTERVAL;

	protected long startTimeMilli;

	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setItemFactory(AnyframeItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	/**
	 * 현재 업무의 자동 커밋 간격({@code commit interval})을 설정한다. 
	 * 업무 로직에서 {@link #commit()}을 호출한 횟수가 설정된 커밋 간격을 초과하는 경우 자동적으로 커밋을 수행한다. 
	 * <p>기본 최대값은 10,000이며 batch.properties 설정파일의
	 * batch.writer.commitInterval 항목에서 변경할 수 있다.
	 * 
	 * @param commitInterval 설정하려는 자동 커밋 간격
	 * @see #checkCommit()
	 */
	public void setCommitInterval(int commitInterval) {
		if(commitInterval > BatchDefine.WRITER_COMMIT_INTERVAL ) {
			logger.info("commit interval "+commitInterval+
					    " must be less then "+BatchDefine.WRITER_COMMIT_INTERVAL+"."); 
			logger.info("set commit interval as default : "+BatchDefine.WRITER_COMMIT_INTERVAL+"."); 
			this.commitInterval = BatchDefine.WRITER_COMMIT_INTERVAL;
			
		} else {		
			this.commitInterval = commitInterval;
		}
	}

	/**
	 * 트랜잭션 카운트를 증가시키고 증가된 트랜잭션 카운트가 {@link #setCommitInterver()}에 의해 설정된 
	 * commit interval을 초과하는 경우 커밋을 수행한다.
	 * 
	 * @see #setCommitInterval(int)
	 */
	public void checkCommit() {
		if(commitCount % commitInterval == 0) {
			commit();
		} else {
			commitCount++;			
		}
	}

	/**
	 * 현재 트랜잭션을 커밋(commit)한다.
	 * 
	 */
	public void commit() {
		
		itemFactory.doCommit();
		
		if(transactionManager instanceof ChainedTransactionManager) {
			ChainedTransactionManager chainedTxManager = (ChainedTransactionManager)transactionManager;
			chainedTxManager.localCommit(transactionStatus);
			
		} else {
			transactionManager.commit(transactionStatus);
			transactionStatus = transactionManager.getTransaction(new DefaultTransactionAttribute());
		}
		
		commitCount = 1;

	}
	
	/**
	 * 현재 트랜잭션을 롤백(rollback)한다.
	 * 
	 */
	public void rollback() {
		
		itemFactory.doRollback();
		
		if(transactionManager instanceof ChainedTransactionManager) {
			ChainedTransactionManager chainedTxManager = (ChainedTransactionManager)transactionManager;
			chainedTxManager.localRollback(transactionStatus);
			
		} else {
			transactionManager.rollback(transactionStatus);
			transactionStatus = transactionManager.getTransaction(new DefaultTransactionAttribute());
		}
		
		commitCount = 1;
		
	}
	
	/**
	 * 현재 배치업무가 시작된 이후 경과된 시간을 반환한다.
	 * 
	 * @return 경과시간(ms)
	 */
	protected long elapsedTime(){
		return System.currentTimeMillis() - startTimeMilli;
	}
	
	@Override
	public ExitStatus doExecute(AnyframeItemReaderFactory itemReader, AnyframeItemWriterFactory itemWriter, AnyframeAbstractTasklet tasklet) throws Exception {
		
		Log stepLogger = LogManager.getLogger();
		
		transactionStatus = transactionManager.getTransaction(new DefaultTransactionAttribute());

		try {
			
			this.startTimeMilli = System.currentTimeMillis();
			
			// do biz logic 
			tasklet.execute(itemReader, itemWriter);
			
			itemFactory.doCommit();
			
			// end transaction
			transactionManager.commit(transactionStatus);

		} catch(Throwable e) {
			
			// on error, roll-back transaction
			try {
				transactionManager.rollback(transactionStatus);
			} catch(Exception e2) {
				logger.error("Fail to rollback transaction", e2);
			}
			
			stepLogger.error("Error occurs on task execution: " + this.getClass().getName(), e);
			throw new BatchRuntimeException("Error occurs on task execution: " + this.getClass().getName(), e);
			
		}
		
		return ExitStatus.FINISHED;
	}

	public abstract void execute(AnyframeItemReaderFactory readerFactory, AnyframeItemWriterFactory writerFactory) throws Exception;

	/**
	 * 병렬스텝(parallel step)인 경우 @seq 속성으로 지정된 시퀀스 번호를  반환한다.
	 * 
	 * @return 현재 쓰레드의 시퀀스 번호
	 * @deprecated use {@link StepParameterUtil#getStepParam(String)}
	 */
	@Deprecated
	public int getSeq() {
		String sequenceValue = StepParameterUtil.getStepParam("sequence");
		
		if(StringUtils.isEmpty(sequenceValue))
			return -1;
		
		return Integer.valueOf(sequenceValue);
	}
	
	/**
	 * 
	 * @deprecated use Step parameter instead
	 * @return
	 */
	@Deprecated
	public String getSeqString() {
		return StepParameterUtil.getStepParam("sequence");
	}
	
	/**
	 * 현재 트랜잭션을 커밋한다.
	 * 
	 * @deprecated use {@link #commit()} instead
	 * 
	 */
	@Deprecated
	public void forceCommit() {
		checkCommit(true);
	}

	/**
	 * 트랜잭션 카운트를 증가시키거나 현재 트랜잭션을 커밋한다. 
	 * 증가된 트랜잭션 카운트가 {@link #setCommitInterver()}에 의해 설정된 값을 초과하는 경우에는 자동적으로 커밋이 이루어진다.
	 * 
	 * @deprecated use {@link #commit()} instead
	 * @param forceCommit true인 경우 현재 트랜잭션을 커밋하며 false인 경우 트랜잭션 카운트만 증가시킨다.
	 */
	@Deprecated
	public void checkCommit(boolean forceCommit) {

		if(forceCommit || commitCount % commitInterval == 0) {
			commit();
		} else {
			commitCount++;			
		}
	}

}
