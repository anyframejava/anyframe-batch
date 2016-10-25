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

/******************************************************************************
 * 나중에 Core와 통합을 용이하게 하기위해 Core 참조 부분을 본 Package에 옮겨둠! 
 * 본 Package의 Class들은 수정/개선 하지 말 것!
 * bonobono, 090730
 ******************************************************************************/
package com.sds.anyframe.batch.manager.core;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/*
 * Eclipse Types(Method/Class/Field) utility
 */
/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ETypeUtils {
	public static boolean isSetMethod(String name, IType type) {
		String methodName = "set" + StringUtil.capitalizeFirstLetter(name);

		try {
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				IMethod method = methods[i];

				if (method.getElementName().equalsIgnoreCase(methodName))
					return true;
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	// getFieldName
	public static boolean isGetMethod(String name, IType type) {
		String methodName = "get" + StringUtil.capitalizeFirstLetter(name);

		try {
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				IMethod method = methods[i];

				if (method.getElementName().equalsIgnoreCase(methodName))
					return true;
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		methodName = "is" + StringUtil.capitalizeFirstLetter(name);

		try {
			IMethod[] methods = type.getMethods();
			for (int i = 0; i < methods.length; i++) {
				IMethod method = methods[i];

				if (method.getElementName().equalsIgnoreCase(methodName))
					return true;
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Returns the super type of the given type.
	 */
	public static String getQualifiedName(IType type, String typeName)
			throws JavaModelException {
		String[][] resolvedNames = type.resolveType(typeName);
		if (resolvedNames != null && resolvedNames.length > 0) {
			return resolvedNames[0][0] + "." + resolvedNames[0][1];
		}
		return typeName;
	}

	public static IType getTypeByQualifiedName(IType type, String qualifiedName) {
		try {
			return type.getJavaProject().findType(qualifiedName);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TypeDeclaration getTypeDeclaration(ICompilationUnit icu) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setSource(icu);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		List typelist = cu.types(); //inner class �� �������� ����
		if(typelist.size() == 0)
			return null;
		
		TypeDeclaration td = (TypeDeclaration) typelist.get(0);
		return td;
	}
}
