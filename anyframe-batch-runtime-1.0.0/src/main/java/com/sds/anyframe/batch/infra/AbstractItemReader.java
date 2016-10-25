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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.MarkFailedException;
import org.springframework.batch.item.ResetFailedException;

import com.sds.anyframe.batch.core.item.support.AnyframeItemReader;
import com.sds.anyframe.batch.util.AddableLong;
import com.sds.anyframe.batch.vo.transform.Transform;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AbstractItemReader implements AnyframeItemReader, ItemStream {

	private static final Log LOGGER = LogFactory.getLog(AbstractItemReader.class);
	
	protected long itemCount = 0;
	protected AddableLong refCount = new AddableLong(0);
	protected Transform transform;
	
	public boolean exists() {
		return true;
	}

	
	public long getItemCount() {
		return itemCount;
	}
	
	public void increaseItemCount() {
		this.itemCount++;
		this.refCount.increment();
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
	
	
	public void mark() throws MarkFailedException {

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
	 * raw APIs for file
	 */
	
	public void setColumnSize(int[] columnSize) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}
	
	public long getTotalLineCount() throws IOException {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
		
		return 0;
	}


	/*
	 * raw APIs for Database
	 */
	
	public ResultSet getResultSet() throws Exception {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
		return null;
	}
	
	
	public void setFetchSize(int fetchSize) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	public void reset() throws ResetFailedException {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	public void loadSQL(String queryID) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	public void setQuery(String query) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}
	
	public void setQueryPath(String path) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	public void setSqlParameters(Object... obj) throws SQLException {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

	
	public void showQueryLog(boolean bShow) {
		if(LOGGER.isDebugEnabled())
			throw new UnsupportedOperationException();
	}

}
