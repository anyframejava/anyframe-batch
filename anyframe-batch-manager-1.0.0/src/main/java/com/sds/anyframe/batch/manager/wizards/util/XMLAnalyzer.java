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

package com.sds.anyframe.batch.manager.wizards.util;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sds.anyframe.batch.manager.core.StringUtil;


// derived from BatchAgent's StepInfoList.java
/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class XMLAnalyzer {
	
	// get ALL elements;
	public static List<Map<String, String>> getData(String xmlFileName, String nodePath) throws Exception {
		return getData(xmlFileName, nodePath, null);
	}
	
	// get SPECIFIED elements;
	public static List<Map<String, String>> getData(String xmlFileName, String nodePath, String[] elems) throws Exception {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();

		DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
		Document doc = builder.parse(new FileInputStream(xmlFileName));
		XPathExpression expr = xpath.compile(nodePath);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);

		NodeList nodeList = (NodeList) result;

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			Map<String, String> step = new HashMap<String, String>();
			
			if(elems != null) {
				for(String elem : elems) {
					step.put(elem, getAttribute(node, elem));
				}
			} else {
				NamedNodeMap attrs = node.getAttributes();
				for (int idx = 0; idx < attrs.getLength(); idx++) {
					Node attr = attrs.item(idx);
					step.put(attr.getNodeName(), StringUtil.null2str(attr.getNodeValue()));
				}
			}
			
			list.add(step);
		}
		return list;
	}

	private static String getAttribute(Node node, String name) {
		Node namedItem = node.getAttributes().getNamedItem(name);
		if (namedItem == null)
			return "";
		return namedItem.getNodeValue();
	}
}
