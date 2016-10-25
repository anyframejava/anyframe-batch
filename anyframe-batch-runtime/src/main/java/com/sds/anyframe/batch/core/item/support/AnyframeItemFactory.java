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

package com.sds.anyframe.batch.core.item.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AnyframeItemFactory implements AnyframeItemReaderFactory, AnyframeItemWriterFactory {
	
	protected Map<String, AnyframeItemReader> readers = new HashMap<String, AnyframeItemReader>();
	protected Map<String, AnyframeItemWriter> writers = new HashMap<String, AnyframeItemWriter>();
	protected Map<String, AnyframeItemReadWriter> updaters = new HashMap<String, AnyframeItemReadWriter>();

	protected Set<String> rentReaders = new HashSet<String>();
	protected Set<String> rentWriters = new HashSet<String>();
	protected Set<String> rentUpdaters = new HashSet<String>();
	
	
	public void setItemReadWriter(Map<String, AnyframeItemReader> readerMap, 
								  Map<String, AnyframeItemWriter> writerMap, 
								  Map<String, AnyframeItemReadWriter> updaters) {
		this.readers.putAll(readerMap);
		this.writers.putAll(writerMap);
		this.updaters.putAll(updaters);
	}
	
	/*
	 * APIs for ItemReader
	 */
	
	public AnyframeItemReader getItemReader(String resourceId) {
		return getItemReader(resourceId, null);
	}

	public AnyframeItemReader getItemReader(final String resourceId, final Object vo) {

		final AnyframeItemReader reader = readers.get(resourceId);
		
		if(reader == null)
			throw new BatchRuntimeException("reader id[" + resourceId + "] does not exist");
		
		if(rentReaders.add(resourceId) == false)
			throw new BatchRuntimeException("reader id[" + resourceId + "] has already been retrieved.");
		

		if(vo != null) {
			reader.setVo(vo);
		}

		return reader;

	}
	
	
	/*
	 * APIs for ItemWriter 
	 */
	
	public AnyframeItemWriter getItemWriter(final String resourceId) {

		final AnyframeItemWriter writer = writers.get(resourceId);
		if(writer == null)
			throw new BatchRuntimeException("writer id[" + resourceId + "] does not exist");
		
		if(rentWriters.add(resourceId) == false)
			throw new BatchRuntimeException("writer[" + resourceId + "] has already been retrieved.");

		return writer;

	}
	
	/*
	 * APIs for ItemReaderWriter 
	 */
	
	public AnyframeItemReadWriter getItemReadWriter(String resourceId) {
		return getItemReadWriter(resourceId, null);
	}

	public AnyframeItemReadWriter getItemReadWriter(String resourceId, Object vo) {
		
		final AnyframeItemReadWriter updater = updaters.get(resourceId);
		if(updater == null)
			throw new BatchRuntimeException("updater id[" + resourceId + "] does not exist");
		
		if(rentUpdaters.add(resourceId) == false)
			throw new BatchRuntimeException("reader/writer id[" + resourceId + "] has already been retrieved.");
		
		if(vo != null) {
			updater.setVo(vo);
		}

		return updater;
	}
	
	public void doCommit() {
		for(Entry<String, AnyframeItemWriter> entry : writers.entrySet()) {
			AnyframeItemWriter writer = entry.getValue();
			writer.flush();
		}
	}
	
	public void doRollback() {
		for(Entry<String, AnyframeItemWriter> entry : writers.entrySet()) {
			AnyframeItemWriter writer = entry.getValue();
			writer.clear();
		}
	}

}
