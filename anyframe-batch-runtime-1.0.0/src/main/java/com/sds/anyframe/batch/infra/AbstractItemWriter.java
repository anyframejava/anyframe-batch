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

package com.sds.anyframe.batch.infra;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ClearFailedException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.FlushFailedException;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import com.sds.anyframe.batch.core.item.support.AnyframeItemWriter;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.util.AddableLong;
import com.sds.anyframe.batch.vo.transform.Transform;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AbstractItemWriter implements AnyframeItemWriter, ItemStream {
	
	private static final Log LOGGER = LogFactory.getLog(AbstractItemReader.class);
	
	protected long itemCount = 0;
	
	protected AddableLong refCount = new AddableLong(0);

	protected Transform transform;
	
	
	public long getItemCount() {
		return itemCount;
	}
	
	public void increaseItemCount() {
		this.itemCount++;
		this.refCount.increment();
		
		// write count 가 증가함에 따라 주기적으로 용량제한을 체크 
		if( BatchDefine.WRITER_FILE_MAX_SIZE_ON && this.itemCount % 5000 == 0) {
			checkCapacity();
		}
	}
	
	protected void checkCapacity() {
		// do nothing by default
	}

	public void setItemCount(long count) {
		this.itemCount = count;
		this.refCount.set(count);
	}
	
	public void setItemCountRef(AddableLong refLong) {
		this.refCount = refLong;
	}
	
	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	public String getURL() {
		return null;
	}
	
	
	public boolean deleteResource() {
		return false;
	}
	
	
	public void clear() throws ClearFailedException {

	}

	
	public void reset() {

	}
	
	
	public void flush() throws FlushFailedException {

	}
	
	/*
	 * APIs for ItemStream
	 */
	
	
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		
	}
	
	
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		
	}
	
	
	public void close(ExecutionContext executionContext) throws ItemStreamException {
		
	}

	/*
	 * APIs for File Writer
	 */

	public void setColumnSize(int[] columnSize) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	/*
	 * APIs for DB Writer
	 */
	
	public int getSqlRowCount() {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
		return -1;
	}
	
	public void loadSQL(String queryID) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
		
	}
	
	public void setQuery(String query) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}
	
	public void setSqlParameters(Object... obj) throws SQLException {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
		
	}
	
	public void setQueryPath(String path) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	public void showQueryLog(boolean bShow) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

}
