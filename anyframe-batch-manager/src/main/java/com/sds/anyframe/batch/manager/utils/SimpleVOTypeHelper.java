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

package com.sds.anyframe.batch.manager.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.sds.anyframe.batch.manager.core.ETypeUtils;
import com.sds.anyframe.batch.manager.core.MetaConstants;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class SimpleVOTypeHelper {
	private static Log log = LogFactory.getLog(SimpleVOTypeHelper.class);
	public static Map<String, Integer> getFields(IType type) throws JavaModelException {
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();		// List가 훨씬 좋을 듯 하구나~~~

		ICompilationUnit icu = type.getCompilationUnit();
		TypeDeclaration td 	= null;
		
		if(icu != null) {
			td = ETypeUtils.getTypeDeclaration(icu);
		}

		IField[] fields;
		try {
			fields = type.getFields();
			
			for (int i = 0; i < fields.length; i++) {

				IField field = fields[i];
				String fieldName = field.getElementName();
				
				// Field must have both set and get methods.
				if (ETypeUtils.isGetMethod(fieldName, type) && ETypeUtils.isSetMethod(fieldName, type)) {
		
					// for Jdoc Analyze Using AST
					Map<String, String> jdocTags = null;
					int length = 0;
					if(td != null) {
						FieldDeclaration fd = getFieldDeclaration(td, fieldName); 
						Javadoc jdoc = fd.getJavadoc();
						jdocTags = generateJdocTagsMap(jdoc);
						if(jdocTags.containsKey(MetaConstants.LENGTH)) {
							try{
								length = Integer.parseInt(jdocTags.get(MetaConstants.LENGTH));
							} catch(NumberFormatException nfe) {
								nfe.printStackTrace();
							}
						}
					}
					result.put(fieldName, length);
					}
				}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		log.debug(result);
		return result;
	}

	// ##### bonobono : get FieldDeclaration by field name
	public static FieldDeclaration getFieldDeclaration(TypeDeclaration type, String fieldName) {

		for (FieldDeclaration field : type.getFields()) {
			for (Object fragObj : field.fragments()) {
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragObj;
				if (fieldName.equals(fragment.getName().getIdentifier())) {
					return field;
				}
			}
		}
		return null;
	}
	
	// 동일명 tag가 여러 개인 거 고려 안함(overwrite)
	public static Map<String, String> generateJdocTagsMap(Javadoc jdoc) {
		Map<String, String> jdocTags = new HashMap<String, String>();
		if (jdoc != null) {
			List taglist = jdoc.tags();
			for (int j = 0; j < taglist.size(); j++) {
				TagElement tag = (TagElement) taglist.get(j);
				String tagname = tag.getTagName();
				String tagvalue = "";

				if (tagname == null) {
					tagname = MetaConstants.DESCRIPTION;
				}
				if(tagname != null) {
					List fraglist = tag.fragments();
					for (int k = 0; k < fraglist.size(); k++) {
						if (k == fraglist.size() - 1) {
							tagvalue = tagvalue.concat(fraglist.get(k).toString().trim());
						} else {
							tagvalue = tagvalue.concat(fraglist.get(k).toString().trim().concat("\n"));
						}
					}
					if (tagname.startsWith("@")) {
						jdocTags.put(tagname, tagvalue);
					}
				}
			}
		}
		return jdocTags;
	}
}
