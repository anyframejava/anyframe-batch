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

package com.sds.anyframe.batch.core.step;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.batch.repeat.ExitStatus;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.common.util.StepParameterUtil;
import com.sds.anyframe.batch.core.item.support.AnyframeItemFactory;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReadWriter;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReader;
import com.sds.anyframe.batch.core.item.support.AnyframeItemWriter;
import com.sds.anyframe.batch.core.step.tasklet.AnyframeAbstractTasklet;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JavaStep extends AnyframeAbstractStep {

	private final CompositeItemStream stream = new CompositeItemStream();

	private Map<String, AnyframeItemReader> readers = new HashMap<String, AnyframeItemReader>();
	private Map<String, AnyframeItemWriter> writers = new HashMap<String, AnyframeItemWriter>();
	private Map<String, AnyframeItemReadWriter> updaters = new HashMap<String, AnyframeItemReadWriter>();
	
	private final AnyframeItemFactory itemFactory = new AnyframeItemFactory();

	private AnyframeAbstractTasklet tasklet;


	@Override
	protected void open(ExecutionContext ctx) throws Exception {
		
		stream.open(ctx);
	}

	@Override
	protected ExitStatus doExecute(StepExecution stepExecution) throws Exception {

		initializeStepParam();
		
		ExitStatus exitStatus = ExitStatus.CONTINUABLE;

		this.itemFactory.setItemReadWriter(readers, writers, updaters);
		this.tasklet.setItemFactory(this.itemFactory);
		this.tasklet.setTransactionManager(getTransactionManager());
		
		exitStatus = tasklet.doExecute(itemFactory, itemFactory, tasklet);

		return exitStatus;
	}
	
	private void initializeStepParam() {
		
		StepParameterUtil.clear();
		
		if(this.parameters != null) {
			for(Entry<String, String> entry : this.parameters.entrySet() )
				StepParameterUtil.addStepParam(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	protected void close(ExecutionContext ctx) throws Exception {
		stream.close(ctx);
	}

	public void registerStream(ItemStream stream) {
		this.stream.register(stream);
	}

	public void setTasklet(AnyframeAbstractTasklet tasklet) {
		
		if(tasklet == null)
			return;
		
		this.tasklet = tasklet;
		
		if (tasklet instanceof StepExecutionListener) {
			registerStepExecutionListener((StepExecutionListener) tasklet);
		}
	}
	
	public void setReaders(Map<String, AnyframeItemReader> readers) {
		for(Entry<String, AnyframeItemReader> entry : readers.entrySet()) {
			AnyframeItemReader reader = entry.getValue();
			
			if(reader instanceof ItemStream) {
				registerStream((ItemStream)reader);
			}
			
			if (reader instanceof StepExecutionListener) {
				registerStepExecutionListener((StepExecutionListener) reader);
			}
		}
		this.readers.putAll(readers);
	}

	public void setWriters(Map<String, AnyframeItemWriter> writers) {
		for(Entry<String, AnyframeItemWriter> entry : writers.entrySet()) {
			AnyframeItemWriter writer = entry.getValue();
			
			if(writer instanceof ItemStream) {
				registerStream((ItemStream)writer);
			}
			
			if (writer instanceof StepExecutionListener) {
				registerStepExecutionListener((StepExecutionListener) writer);
			}
		}
		this.writers.putAll(writers);
	}

	public void setUpdaters(Map<String, AnyframeItemReadWriter> updaters) {
		for(Entry<String, AnyframeItemReadWriter> entry : updaters.entrySet()) {
			AnyframeItemReadWriter updater = entry.getValue();
			
			if(updater instanceof ItemStream) {
				registerStream((ItemStream)updater);
			}
			
			if (updater instanceof StepExecutionListener) {
				registerStepExecutionListener((StepExecutionListener) updater);
			}
		}
		this.updaters.putAll(updaters);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(tasklet, "Tasklet is mandatory");
	}

}
