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


package com.anyframe.core.vo.transform.vo.bci;

import java.lang.reflect.Method;

/**
 * 
 * Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * http://code.google.com/p/reflectasm/
 * 
 * @author Jeryeon Kim
 * 
 */
class AccessClassLoader extends ClassLoader {
	AccessClassLoader (ClassLoader parent) {
		super(parent);
	}

	protected synchronized java.lang.Class<?> loadClass (String name, boolean resolve) throws ClassNotFoundException {
		// These classes come from the classloader that loaded AccessClassLoader.
		if (name.equals(FieldAccess.class.getName())) return FieldAccess.class;
		if (name.equals(MethodAccess.class.getName())) return MethodAccess.class;
		// All other classes come from the classloader that loaded the type we are accessing.
		return super.loadClass(name, resolve);
	}

	Class<?> defineClass (String name, byte[] bytes) throws ClassFormatError {
		try {
			// Attempt to load the access class in the same loader, which makes protected and default access members accessible.
			Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] {String.class, byte[].class, int.class,
				int.class});
			method.setAccessible(true);
			return (Class)method.invoke(getParent(), new Object[] {name, bytes, new Integer(0), new Integer(bytes.length)});
		} catch (Exception ignored) {
		}
		return defineClass(name, bytes, 0, bytes.length);
	}
}
