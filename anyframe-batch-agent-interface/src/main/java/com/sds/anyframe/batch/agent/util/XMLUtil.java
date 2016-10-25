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

package com.sds.anyframe.batch.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sds.anyframe.batch.agent.model.ResourceIoType;


/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class XMLUtil {
	private DocumentBuilderFactory docBuilderFactory = null;
	private XPath xpath = null;
	private DocumentBuilder builder = null;	
	private Document doc = null;
	
	private static final Logger logger = Logger.getLogger(XMLUtil.class);
	public XMLUtil(){
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		XPathFactory factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
	}
	
	public List<Map<String, String>> getStepList(String xmlFileName, String batchStepNode) throws Exception{
		NodeList nodeList = getNodeList(xmlFileName, batchStepNode);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		for(int i=0; i<nodeList.getLength(); i++){
			 Node node = nodeList.item(i);
			 Map<String, String> step = new HashMap<String, String>();
			 step.put("id", getAttribute(node, "id"));
			 step.put("name", getAttribute(node, "name"));
			 step.put("class",getAttribute(node, "class"));
			 
			 list.add(step);
		}
		return list;
	}
	
	
	public List<String> getNodeAttList(String xmlFileName, String batchStepNode, String attributeName) throws Exception {
		NodeList nodeList = getNodeList(xmlFileName, batchStepNode);
		List<String> list = new ArrayList<String>();
		for (int i = 0, length = nodeList.getLength(); i < length ; i++) {
			Node node = nodeList.item(i);
			list.add(getAttribute(node, attributeName));
		}
		return list;
	}
	
	public Map<ResourceIoType, Set<String>> getResourceUrl(String xmlFileName, String nodePattern) throws Exception {
		NodeList nodeList = getNodeList(xmlFileName, nodePattern);
		Set<String> readSet = new HashSet<String>();
		Set<String> writeSet = new HashSet<String>();
		
		for (int i = 0, length = nodeList.getLength(); i < length ; i++) {
			Node node = nodeList.item(i);
			if(getAttribute(node, "inout").equalsIgnoreCase("IN")) readSet.add(getAttribute(node, "url"));
			else if(getAttribute(node, "inout").equalsIgnoreCase("OUT")) writeSet.add(getAttribute(node, "url"));
		}
		Map<ResourceIoType, Set<String>> resultMap = new HashMap<ResourceIoType, Set<String>>();
		resultMap.put(ResourceIoType.READ, readSet);
		resultMap.put(ResourceIoType.WRITE, writeSet);
		return resultMap;
	}
	
	public Map<ResourceIoType, Set<String>> getDeleteResources(String xmlFileName, String nodePattern) throws Exception {
		NodeList nodeList = getNodeList(xmlFileName, nodePattern);
		Set<String> deleteSet = new HashSet<String>();
		
		for (int i = 0, length = nodeList.getLength(); i < length ; i++) {
			Node node = nodeList.item(i);
			deleteSet.add(getAttribute(node, "url"));
		}
		Map<ResourceIoType, Set<String>> resultMap = new HashMap<ResourceIoType, Set<String>>();
		resultMap.put(ResourceIoType.DELETE, deleteSet);
		return resultMap;
	}
	
	public NodeList getNodeList(String xmlFileName, String nodePattern) throws Exception {
		XPathExpression expr = null;
		Object result = null;
		
		InputStream stream = null;
		
		try {
			builder = docBuilderFactory.newDocumentBuilder();
			File file = new File(xmlFileName);
			stream = new FileInputStream(file);
			doc = builder.parse(stream);
			expr = xpath.compile(nodePattern);
			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (Exception e) {
			logger.error("XMLUtil has an error", e);
			throw(e);
		} finally {
			if (stream != null)
				stream.close();
		}
		
		return (NodeList) result;
	}
	
	private String getAttribute(Node node, String name){
		Node namedItem = node.getAttributes().getNamedItem(name);
		if(namedItem==null)
			return "";
		return namedItem.getNodeValue();
	}
	
	/**
	 * CDATA 텍스트 가져오기
	 * @param xmlFileName
	 * @param nodePattern
	 * @return
	 * @throws Exception 
	 */
	public String getTextContent(String xmlFileName, String nodePattern) throws Exception{
		NodeList nodeList = getNodeList(xmlFileName, nodePattern);
		StringBuilder sb = new StringBuilder();
		for (int i = 0, length = nodeList.getLength(); i < length;i++) {
			sb.append(nodeList.item(i).getTextContent()).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * CDATA 섹션에서 resource 정보 가져오기
	 * @param xmlFileName file name
	 * @param nodePattern xpath pattern
	 * @return
	 * @throws Exception 
	 */
	public Map<ResourceIoType, Set<String>> getResourcesFromCDATA(String xmlFileName, String nodePattern) throws Exception{
		String text = getTextContent(xmlFileName, nodePattern);
		Map<ResourceIoType, Set<String>> resultMap = new HashMap<ResourceIoType, Set<String>>();
		Set<String> readSet = new HashSet<String>();
		Set<String> writeSet = new HashSet<String>();
		
		if (text != null && !text.equals("")) {
			for (String string : StringUtils.split(text, "\n")) {
				String line = StringUtils.trimToNull(string);
				String resourceFileName = null;
				if(line != null && line.startsWith("/INFILE")){
					resourceFileName = StringUtils.trimToEmpty(StringUtils.substringAfter(line, "/INFILE="));
					resourceFileName = removeBlankInString(resourceFileName);
					if (resourceFileName != null && !resourceFileName.equals("")) readSet.add(resourceFileName);
				} else if(line != null && line.startsWith("/OUTFILE")){
					resourceFileName = StringUtils.trimToEmpty(StringUtils.substringAfter(line, "/OUTFILE="));
					resourceFileName = removeBlankInString(resourceFileName);
					if (resourceFileName != null && !resourceFileName.equals("")) writeSet.add(resourceFileName);
				}
			}
		}
		resultMap.put(ResourceIoType.READ, readSet);
		resultMap.put(ResourceIoType.WRITE, writeSet);
		return resultMap;
	}

	public List<String> getResourcesFromShell(String xmlFileName, String nodePattern) throws Exception{
		String text = getTextContent(xmlFileName, nodePattern);
		List<String> resources = new ArrayList<String>();
		if (text != null && !text.equals("")) {
			for (String string : StringUtils.split(text, "\n")) {
				String line = StringUtils.trimToNull(string);
				String resourceFileName = null;
				if(line != null && line.startsWith("/INFILE")){
					resourceFileName = StringUtils.trimToEmpty(StringUtils.substringAfter(line, "/INFILE="));
					resourceFileName = removeBlankInString(resourceFileName);
					if(resourceFileName != null && !resourceFileName.equals("")) resources.add(resourceFileName);
				} else if(line != null && line.startsWith("/OUTFILE")){
					resourceFileName = StringUtils.trimToEmpty(StringUtils.substringAfter(line, "/OUTFILE="));
					resourceFileName = removeBlankInString(resourceFileName);
					if(resourceFileName != null && !resourceFileName.equals("")) resources.add(resourceFileName);
				}
			}
		}
		return resources;
	}
	
	public static String removeBlankInString(String resourceFileName) {
		if (StringUtils.contains(resourceFileName, " ")) resourceFileName = StringUtils.substringBefore(resourceFileName, " ");
		if (StringUtils.contains(resourceFileName, "\t")) resourceFileName = StringUtils.substringBefore(resourceFileName, "\t");
		
		 String illegals = "[]()";
	     String pattern = "[" + Pattern.quote(illegals) + "]";
		
		resourceFileName = resourceFileName.replaceAll(pattern, "");
		return resourceFileName;
	}
}
