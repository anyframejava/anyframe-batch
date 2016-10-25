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

package com.sds.anyframe.batch.infra.delegate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.MarkFailedException;
import org.springframework.batch.item.NoWorkFoundException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.ResetFailedException;
import org.springframework.batch.item.UnexpectedInputException;

import com.sds.anyframe.batch.core.item.support.AnyframeItemReader;
import com.sds.anyframe.batch.infra.AbstractItemReader;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class MultiItemReader extends AbstractItemReader {
	private List<AnyframeItemReader> readers = new ArrayList<AnyframeItemReader>();
	private AnyframeItemReader currentReader = null;
	private int currentIndex = 0;
	private boolean noInput;
	
	public MultiItemReader() {
		super();
	}

	public void addReader(AnyframeItemReader itemReader) {
		this.readers.add(itemReader);
	}
	
	@Override
	public long getItemCount() {
		long count = 0;
		for(int index = 0; index <= currentIndex ; index++)
			count += readers.get(index).getItemCount();
		
		return count;
	}
	
	@Override
	public String getURL() {
		return currentReader.getURL();
	}
	
	@Override
	public boolean deleteResource() {
		for(AnyframeItemReader reader : readers)
			reader.deleteResource();
		
		return true;
	}

	@Override
	public boolean exists() {
		return currentReader.exists();
	}
	
	
	public void setVo(Object vo) {
		for(AnyframeItemReader reader : readers)
			reader.setVo(vo);
	}

	@Override
	public void mark() throws MarkFailedException {
		currentReader.mark();
	}

	
	public Object read() throws Exception, UnexpectedInputException,
			NoWorkFoundException, ParseException {
		
		if (noInput) {
			return null;
		}
		
		Object item = currentReader.read();

		while (item == null) {

			if (currentIndex+1 == readers.size()) {
				return null;
			}
			currentIndex++;
			
			currentReader = readers.get(currentIndex);
			item = currentReader.read();
		}
		
		return item;
		
	}

	@Override
	public void reset() throws ResetFailedException {
		currentReader.reset();
	}

	@Override
	public void loadSQL(String queryID) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setQuery(String query) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setQueryPath(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSqlParameters(Object... obj) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResultSet getResultSet() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTotalLineCount() throws IOException {
		long count = 0;
		for(AnyframeItemReader reader : readers)
			count+= reader.getTotalLineCount();
		
		return count;
	}

	@Override
	public void setFetchSize(int fetchSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws Exception {
		return currentReader.getBigDecimal(index);
	}

	@Override
	public int getInt(int index) throws Exception {
//		return anyframeItemReader.getInt(index, anyframeItemReader.getEncoding());
		return currentReader.getInt(index);
	}

	@Override
	public String getString(int index) throws Exception {
		return currentReader.getString(index);
	}

	@Override
	public void setColumnSize(int[] columnSize) {
		for(AnyframeItemReader reader : readers)
			reader.setColumnSize(columnSize);
	}
		
	@Override
	public byte[] getBytes(int index) throws Exception {
		return currentReader.getBytes(index);
	}
	
	@Override
	public void showQueryLog(boolean showLog) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close(ExecutionContext executionContext)
			throws ItemStreamException {
		for(AnyframeItemReader reader : readers) {
			if(reader instanceof ItemStream)
				((ItemStream)reader).close(executionContext);
		}
		
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		
		this.noInput = false;
		
		if (this.readers.size() == 0) {
			this.noInput = true;
			return;
		}
		
		for(AnyframeItemReader reader : readers) {
			if(reader instanceof ItemStream)
				((ItemStream)reader).open(executionContext);
		}
		
		this.currentIndex = 0;
		this.currentReader = readers.get(this.currentIndex);
		
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		if(currentReader instanceof ItemStream)
			((ItemStream)currentReader).update(executionContext);
	}

	@Override
	public Boolean getBoolean(int index) throws Exception {
		return currentReader.getBoolean(index);
	}

	@Override
	public Timestamp getTimestamp(int index) throws Exception {
		return currentReader.getTimestamp(index);
	}

	@Override
	public Time getTime(int index) throws Exception {
		return currentReader.getTime(index);
	}

	@Override
	public Character getCharacter(int index) throws Exception {
		return currentReader.getCharacter(index);
	}

	@Override
	public Byte getByte(int index) throws Exception {
		return currentReader.getByte(index);
	}

	@Override
	public Date getDate(int index) throws Exception {
		return currentReader.getDate(index);
	}

	@Override
	public Double getDouble(int index) throws Exception {
		return currentReader.getDouble(index);
	}

	@Override
	public Float getFloat(int index) throws Exception {
		return currentReader.getFloat(index);
	}

	@Override
	public Long getLong(int index) throws Exception {
		return currentReader.getLong(index);
	}

	@Override
	public BigInteger getBigInteger(int index) throws Exception {
		return currentReader.getBigInteger(index);
	}

	@Override
	public Short getShort(int index) throws Exception {
		return currentReader.getShort(index);
	}
}
