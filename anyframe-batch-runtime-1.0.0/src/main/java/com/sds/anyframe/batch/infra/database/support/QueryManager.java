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

package com.sds.anyframe.batch.infra.database.support;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sds.anyframe.batch.exception.BatchRuntimeException;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class QueryManager {
	
	private static Map<String, Document> docs = new HashMap <String, Document>();
	
	private static ResourceLoader resourceLoader = new DefaultResourceLoader() {
		
		protected Resource getResourceByPath(String path) {
			return new FileSystemResource(path);
		}
		
	};
	
	private static Document getDocument(String path) {
		
		try{
			
			if(path == null || StringUtils.isEmpty(path))
				throw new IllegalArgumentException("query file path should not be null or empty");
			
			Document doc = (Document) docs.get(path);
			
			if(doc != null)
				return doc;
			
			InputStream inputStream = resourceLoader.getResource(path).getInputStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
				
			doc = builder.parse(inputStream);
			docs.put(path, doc);
	
			return doc;
		} catch(Exception e) {
			throw new BatchRuntimeException("fail to open SQL file [" + path + "]", e);
		}
	}
	
	
	// synchronized 가 아니면 multi-thread 에서 문제가 될 수 있다. (parallel step)
	public static synchronized String getQueryByID(String path, String id) {
		
		Document document = getDocument(path);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/sql/query[@id='" + id + "']";
		
		try {
			Node node = (Node)xpath.evaluate(expression, document, XPathConstants.NODE);
			
			if(node == null)
				return null;
			
			return node.getTextContent().trim();
			
		} catch (XPathExpressionException e) {
			throw new BatchRuntimeException("Can not find query. query id[" + id + "]", e);
		}
	}
}
