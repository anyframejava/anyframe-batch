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

package com.sds.anyframe.batch.config.parser;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.sds.anyframe.batch.core.job.SequentialJob;
import com.sds.anyframe.batch.define.BatchDefine;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class JobParser extends AbstractSingleBeanDefinitionParser {

	@SuppressWarnings("unused")
	private static final Log LOGGER = LogFactory.getLog(JobParser.class);
	
	private StepParser stepParser = new StepParser();

	private BeanDefinitionParser listenerParser = new ListenerParser();
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return SequentialJob.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {

		String name = element.getAttribute("id");
		if (!StringUtils.isEmpty(name)) {
			builder.addPropertyValue("name", name);
		}

		String concurrent = element.getAttribute("concurrent");
		if (!StringUtils.isEmpty(concurrent)) {
			builder.addPropertyValue("concurrent", Boolean.valueOf(concurrent));
		}
		
		builder.addPropertyReference("jobRepository", BatchDefine.BEAN_NAME_JOB_REPOSITORY);

		CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(
				element.getTagName(), parserContext.extractSource(element));

		parserContext.pushContainingComponent(compositeDef);

		// job execution listener 등록
		Element listenersElement = DomUtils.getChildElementByTagName(element, "listeners");
		
		if(listenersElement != null) {
			ManagedList listenerList = new ManagedList();
			List<Element> listenerElements = DomUtils.getChildElementsByTagName(listenersElement, "listener");
			
			for (Element listenerElement : listenerElements) {
				listenerList.add(listenerParser.parse(listenerElement, parserContext));
			}
			
			builder.addPropertyValue("jobExecutionListeners", listenerList);
		}
		
		// job parameters 등록
		Element parametersElement = DomUtils.getChildElementByTagName(element, "parameters");

		if(parametersElement != null) {
			Map<String, String> parameters = ParserUtils.getParameters(parametersElement);
			builder.addPropertyValue("parameters", parameters);
		}
		
		// step 등록
		ManagedList managedList = new ManagedList();
		List<Element> stepElems = DomUtils.getChildElementsByTagName(element, "step");
		
		for (Element stepElem : stepElems) {
			managedList.add(new RuntimeBeanReference(stepParser.parse(stepElem, parserContext)));
		}
		
		builder.addPropertyValue("steps", managedList);
	}

	@Override
	protected String resolveId(Element element,
			AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		if (shouldGenerateId()) {
			return parserContext.getReaderContext().generateBeanName(definition);
			
		} else {
			String id = element.getNodeName();
			if (StringUtils.isEmpty(id) && shouldGenerateIdAsFallback()) {
				id = parserContext.getReaderContext().generateBeanName(
						definition);
			}
			return id;
		}
	}
}
