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

package com.sds.anyframe.batch.core.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ChainedTransactionManager extends AbstractPlatformTransactionManager {
	
	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(ChainedTransactionManager.class);

	private List<PlatformTransactionManager> transactionManagers = new ArrayList<PlatformTransactionManager>();
	private ArrayList<PlatformTransactionManager> reversed;

	public void setTransactionManagers(List<PlatformTransactionManager> transactionManagers) {
		this.transactionManagers = transactionManagers;
		this.reversed = new ArrayList<PlatformTransactionManager>(transactionManagers);
		Collections.reverse(reversed);
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) transaction;
		
		for (PlatformTransactionManager transactionManager : transactionManagers) {
			DefaultTransactionStatus element = (DefaultTransactionStatus) transactionManager.getTransaction(definition);
			list.add(0, element);
		}
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status.getTransaction();
		int i = 0;
		for (PlatformTransactionManager transactionManager : reversed) {
			try {
				transactionManager.commit( list.get(i++));
			} catch (TransactionException e) {
				logger.error("Error in commit", e);
				// Rollback will ensue as long as rollbackOnCommitFailure=true
				throw e;
			}
		}
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return new ArrayList<DefaultTransactionStatus>();
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) status.getTransaction();
		int i = 0;
		TransactionException lastException = null;
		
		for (PlatformTransactionManager transactionManager : reversed) {
			try {
				transactionManager.rollback(list.get(i++));
			} catch (TransactionException e) {
				// Log exception and try to complete rollback
				lastException = e;
				logger.error("Error in rollback", e);
			}
		}
		
		if (lastException != null) {
			throw lastException;
		}
	}
	
	/**
	 * chained transaction manager에 묶인 datasource에 대해
	 * 현재 transaction에 binding된 conneciton을 commit한다.
	 * Transaction을 close 하지 않는다.
	 * @throws SQLException
	 */
	public void localCommit(TransactionStatus status) throws TransactionException {
		
		DefaultTransactionStatus defaultStatus = (DefaultTransactionStatus) status;
		
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) defaultStatus.getTransaction();
		
		for (DefaultTransactionStatus local : list) {
			JdbcTransactionObjectSupport txObject = (JdbcTransactionObjectSupport) local.getTransaction();
			Connection con = txObject.getConnectionHolder().getConnection();

			try {
				con.commit();
			}
			catch (SQLException ex) {
				throw new TransactionSystemException("Could not commit JDBC transaction", ex);
			}
		}
	}
	

	/**
	 * chained transaction manager에 묶인 datasource에 대해
	 * 현재 transaction에 binding된 conneciton을 rollback한다.
	 * Transaction을 close 하지 않는다.
	 * @throws SQLException
	 */
	public void localRollback(TransactionStatus status) throws TransactionException {
		
		DefaultTransactionStatus defaultStatus = (DefaultTransactionStatus) status;
		
		@SuppressWarnings("unchecked")
		List<DefaultTransactionStatus> list = (List<DefaultTransactionStatus>) defaultStatus.getTransaction();
		
		for (DefaultTransactionStatus local : list) {
			JdbcTransactionObjectSupport txObject = (JdbcTransactionObjectSupport) local.getTransaction();
			Connection con = txObject.getConnectionHolder().getConnection();

			try {
				con.rollback();
			}
			catch (SQLException ex) {
				throw new TransactionSystemException("Could not roll back JDBC transaction", ex);
			}
		}
	}
	

}
