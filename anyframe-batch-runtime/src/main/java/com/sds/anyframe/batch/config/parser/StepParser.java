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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.config.BatchResource.Type;
import com.sds.anyframe.batch.util.StringHolder;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StepParser {

	private static final Log LOGGER = LogFactory.getLog(StepParser.class);

	private BeanDefinitionParser listenerParser = new ListenerParser();
	
	@SuppressWarnings("unchecked")
	public String parse(Element element, ParserContext parserContext) {

		String stepType = element.getAttribute("type");
		String stepName = element.getAttribute("id");
		
		Assert.isTrue(!StringUtils.isEmpty(stepName), "@id should be specified in <step> element.");
		Assert.isTrue(!StringUtils.isEmpty(stepType), "@type should be specified in <step> element.");
		
		BeanDefinitionBuilder stepBuilder = BeanDefinitionBuilder.genericBeanDefinition();

		stepBuilder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		stepBuilder.getRawBeanDefinition().setFactoryBeanName("com.anyframe.batch.stepFactory");
		stepBuilder.getRawBeanDefinition().setFactoryMethodName("getStep");
		stepBuilder.addConstructorArgValue(stepType);

		// step execution listener 등록
		Element listenersElement = DomUtils.getChildElementByTagName(element, "listeners");
		
		if(listenersElement != null) {
			ManagedList listenerList = new ManagedList();
			List<Element> listenerElements = DomUtils.getChildElementsByTagName(listenersElement, "listener");
			
			for (Element listenerElement : listenerElements) {
				listenerList.add(listenerParser.parse(listenerElement, parserContext));
			}
			
			stepBuilder.addPropertyValue("stepExecutionListeners", listenerList);
		}
		
		// step parameters 등록
		Element parametersElement = DomUtils.getChildElementByTagName(element, "parameters");

		if(parametersElement != null) {
			Map<String, String> parameters = ParserUtils.getParameters(parametersElement);
			stepBuilder.addPropertyValue("parameters", parameters);
		}
		
		if("parallel".compareToIgnoreCase(stepType) == 0) {
			// child step 등록 
			List<Element> stepElements = DomUtils.getChildElementsByTagName(element, "step");
			
			Assert.isTrue(stepElements.size() > 0, "parallel step should has at least one step");
			
			ManagedList stepList = new ManagedList();
			for (Element stepElement : stepElements) {
				stepList.add(new RuntimeBeanReference(parse(stepElement, parserContext)));
			}
				
			stepBuilder.addPropertyValue("steps", stepList);
			
		} else if("java".compareToIgnoreCase(stepType) == 0) {
			
			String taskClass = element.getAttribute("class");
			String queryFile = element.getAttribute("query_file");
			
			Assert.isTrue(!StringUtils.isEmpty(stepName), "@class should be specified in <step type=\"java\"> element.");
			
			if(StringUtils.isEmpty(queryFile)) {
				queryFile = getDefaultQueryPath(taskClass);
			}
			
			// tasklet 등록
			BeanDefinitionBuilder taskletBuilder = BeanDefinitionBuilder.genericBeanDefinition(taskClass);
			stepBuilder.addPropertyValue("tasklet", taskletBuilder.getBeanDefinition());
			

			Element resourcesElement = DomUtils.getChildElementByTagName(element, "resources");
			
			if(resourcesElement != null) {
				List<BatchResource> resourcesList = new ArrayList<BatchResource>();
				
				// <reader> element
				List<Element> readers = DomUtils.getChildElementsByTagName(resourcesElement, "reader");
				ManagedMap readerMap = new ManagedMap();
				
				for(Element reader : readers) {
					BatchResource resourceDef = convert(reader, Mode.READ, queryFile);
					readerMap.put(resourceDef.getId(), getItemHandler(resourceDef));
					resourcesList.add(resourceDef);
				}
				stepBuilder.addPropertyValue("readers", readerMap);
				
				// <writer> element
				List<Element> writers = DomUtils.getChildElementsByTagName(resourcesElement, "writer");
				ManagedMap writerMap = new ManagedMap();
				for(Element writer : writers) {
					BatchResource resourceDef = convert(writer, Mode.WRITE, queryFile);
					writerMap.put(resourceDef.getId(), getItemHandler(resourceDef));
					resourcesList.add(resourceDef);
				}
				stepBuilder.addPropertyValue("writers", writerMap);
				
				// <updater> element
				List<Element> updaters = DomUtils.getChildElementsByTagName(resourcesElement, "updater");
				ManagedMap updaterMap = new ManagedMap();
				for(Element updater : updaters) {
					BatchResource resourceDef = convert(updater, Mode.UPDATE, queryFile);
					updaterMap.put(resourceDef.getId(), getItemHandler(resourceDef));
					resourcesList.add(resourceDef);
				}
				stepBuilder.addPropertyValue("updaters", updaterMap);
				
				stepBuilder.addPropertyValue("resources", resourcesList);
			}
			
		}else if("delete".compareToIgnoreCase(stepType) == 0) {
			
			Element resourcesElement = DomUtils.getChildElementByTagName(element, "resources");
			List<BatchResource> resourcesList = new ArrayList<BatchResource>();
			
			// <resource> element
			List<Element> resources = DomUtils.getChildElementsByTagName(resourcesElement, "resource");
			for(Element resource : resources) {
				BatchResource resourceDef = convert(resource, Mode.DELETE, null);
				resourcesList.add(resourceDef);
			}
			
			stepBuilder.addPropertyValue("resources", resourcesList);
			
		}else if("shell".compareToIgnoreCase(stepType) == 0) {
			
			Element scriptElement = DomUtils.getChildElementByTagName(element, "script");
			String script = DomUtils.getTextValue(scriptElement);
			
			// script를 String 객체로 DI할 경우, script에 포함된 변수(ex. ${BASE_DIR})에 대해서
			// 스프링이 PropertyPlaceHolder를 통해 치환하려고 하며, 변수값이 없는 경우 에러 발생함
			// => script에 포함된 변수 치환을 막기위해 StringHolder로 DI함
			stepBuilder.addPropertyValue("scriptHolder", new StringHolder(script));
		}

		parserContext.registerBeanComponent(new BeanComponentDefinition(stepBuilder.getBeanDefinition(), stepName));

		LOGGER.debug("Generate step definition. " + stepName + ": " + stepBuilder.getBeanDefinition());
		return stepName;

	}

	private Object getItemHandler(BatchResource resourceDef) {
		BeanDefinitionBuilder resourceBuilder = BeanDefinitionBuilder.genericBeanDefinition();

		resourceBuilder.getRawBeanDefinition().setFactoryBeanName("com.anyframe.batch.resourceFactory");
		resourceBuilder.getRawBeanDefinition().setFactoryMethodName("getResourceHandler");
		resourceBuilder.addConstructorArgValue(resourceDef);
		
		if(resourceDef.getType() == Type.DB) {
			resourceBuilder.addPropertyReference("dataSource", resourceDef.getUrl());
		}
		
		
		return resourceBuilder.getBeanDefinition();
	}
	
	
	private BatchResource convert(Element element, Mode mode, String queryFile) {
		BatchResource resource = new BatchResource();
		resource.setAttributes(getNodeAttributes(element));

		String url  = element.getAttribute("url");
		String id   = element.getAttribute("id");
		String att_type = element.getAttribute("type");
		
		Type type = "DB".compareToIgnoreCase(att_type) == 0 ? Type.DB : Type.FILE; 

		if(type == Type.DB)
			resource.setAttribute("query_file", queryFile);
		
		resource.setType(type);
		resource.setUrl(url);
		resource.setId(id);
		resource.setMode(mode);
		
		String[] urls = url.split(";");
		if(urls.length > 1) {
			for(String subUrl : urls) {
				BatchResource childResource = new BatchResource();
				childResource.setAttributes(getNodeAttributes(element));
				childResource.setType(type);
				childResource.setUrl(subUrl);
				childResource.setId(id);
				childResource.setMode(mode);
					
				resource.addChildResource(childResource);
			}
		}
		return resource;
	}

	private String getDefaultQueryPath(String clazz) {
		
		return "classpath:" + StringUtils.replaceChars(clazz, '.', '/') + "_SQL.xml";

	}
	
	private Map<String, String> getNodeAttributes(Node node) {
		Map<String, String> map = new HashMap<String, String>();
		NamedNodeMap attributes = node.getAttributes();
		
		for(int j=0; j<attributes.getLength(); j++) {
			Node attr = attributes.item(j);
			String key = attr.getNodeName();
			String value = attr.getNodeValue();
			
			map.put(key, value);
		}
		
		return map;
	}

}
