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

package com.sds.anyframe.batch.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.support.AopUtils;

import com.sds.anyframe.batch.vo.meta.MethodHolder;

/**
 * Utility class for internal class handling.
 * 
 * @author Hyoungsoon Kim
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClassUtil {

	public static final String CGLIB_CLASS_SEPARATOR = "$$";
	
	private static final Map rawTypeMap = new HashMap(8);

	private static final Map wrapperTypeMap = new HashMap(8);
	
	static {
		wrapperTypeMap.put(Boolean.class, boolean.class);
		wrapperTypeMap.put(Byte.class, byte.class);
		wrapperTypeMap.put(Character.class, char.class);
		wrapperTypeMap.put(Double.class, double.class);
		wrapperTypeMap.put(Float.class, float.class);
		wrapperTypeMap.put(Integer.class, int.class);
		wrapperTypeMap.put(Long.class, long.class);
		wrapperTypeMap.put(Short.class, short.class);
	}
	
	static {
		rawTypeMap.put(String.class, String.class);
		rawTypeMap.put(BigInteger.class, BigInteger.class);
		rawTypeMap.put(BigDecimal.class, BigDecimal.class);
		rawTypeMap.put(java.util.Date.class, java.util.Date.class);
		rawTypeMap.put(java.sql.Date.class, java.sql.Date.class);
		rawTypeMap.put(java.sql.Time.class, java.sql.Time.class);
		rawTypeMap.put(java.sql.Timestamp.class, java.sql.Timestamp.class);
	}

	public static Class<?> toClazz(Object bean) {
		if (AopUtils.isAopProxy(bean)) {
			return AopUtils.getTargetClass(bean);
		}
		return bean.getClass();
	}

	public static Class<?> getRealClass(Object bean) {
		Class<?> cls = toClazz(bean);

		if (cls != null && cls.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = cls.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return cls;
	}

	public static Class<?>[] getClassTypesByObjects(Object[] parameters) {
		if (parameters == null)
			return null;

		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}
		return parameterTypes;
	}

	public static String[] getClassTypeStrings(Class[] classes) {
		if (classes == null)
			return null;

		String[] typeStrings = new String[classes.length];

		for (int i = 0; i < classes.length; i++) {
			typeStrings[i] = classes[i].getName();
		}
		return typeStrings;
	}

	public static boolean isPrimitiveObject(Object obj) {
		
		Class cls = obj.getClass();
		
		if(cls.isPrimitive())
			return true;
		
		if(wrapperTypeMap.containsKey(cls))
			return true;
		
		return false;

	}
	
	public static boolean isValueObject(Object obj) {
		
		if(isPrimitiveObject(obj))
			return false;
		
		Class cls = obj.getClass();
		
		if(rawTypeMap.containsKey(cls))
			return false;
		
		return true;

	}

	public static Class<?>[] getInterfaceTypeConvertedParameters(Class<?>[] parameterTypes) {
		if (parameterTypes == null)
			return null;

		Class<?>[] convertedTypes = new Class[parameterTypes.length];

		boolean found = false;

		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> cls = parameterTypes[i];

			if (List.class.isAssignableFrom(cls)) {
				convertedTypes[i] = List.class;
				found = true;
			} else if (Map.class.isAssignableFrom(cls)) {
				convertedTypes[i] = Map.class;
				found = true;
			} else if (Set.class.isAssignableFrom(cls)) {
				convertedTypes[i] = Set.class;
				found = true;
			} else if (Integer.class.isAssignableFrom(cls)) {
				convertedTypes[i] = int.class;
				found = true;
			} else
				convertedTypes[i] = cls;
		}

		return found ? convertedTypes : null;
	}

	public static List<Field> getAllFields(Class<?> cls) {
		
		List<Field> fields = new ArrayList<Field>();

		Field[] fieldArrary = cls.getDeclaredFields();

		fields.addAll(Arrays.asList(fieldArrary));

		return fields;
	}

	public static Map<String, MethodHolder> getAllGetterSetterMethods(Class<?> cls, List<Field> fields) {
		Map<String, MethodHolder> methods = new HashMap<String, MethodHolder>();

		for (Field field : fields)
			isValidFieldThenBuildSetterGetter(field, methods);

		return methods;
	}

	private static boolean isValidFieldThenBuildSetterGetter(Field field, Map<String, MethodHolder> methods) {
		String methodName = getAccessorName(field.getName());

		try {
			return findAndPut(field, methods, methodName);
		} catch (NoSuchMethodException e) {
			methodName = changeFirstCharacterCase(field.getName(), true);
			try {
				return findAndPut(field, methods, methodName);
			} catch (NoSuchMethodException e2) {
				return false;
			}
		}
	}

	public static MethodHolder getMethodHolder(Field field) {
		String methodName = getAccessorName(field.getName());

		try {
			return getMethodHolder(field, methodName);
		} catch (NoSuchMethodException e) {
			methodName = changeFirstCharacterCase(field.getName(), true);
			try {
				return getMethodHolder(field, methodName);
			} catch (NoSuchMethodException e2) {
				return null;
			}
		}
	}

	private static MethodHolder getMethodHolder(Field field, String methodName) throws NoSuchMethodException {
		Method getter = null;

		try {
			getter = field.getDeclaringClass().getDeclaredMethod("get" + methodName, (Class<?>[])null);
		} catch (NoSuchMethodException e) {
			if (field.getType() == boolean.class)
				getter = field.getDeclaringClass().getDeclaredMethod("is" + methodName, (Class<?>[])null);
			else
				throw e;
		}

		Method setter = field.getDeclaringClass().getDeclaredMethod("set" + methodName, field.getType());

		if (getter != null && setter != null)
			return new MethodHolder(setter, getter);
		return null;
	}

	private static boolean findAndPut(Field field, Map<String, MethodHolder> methods, String methodName)
			throws NoSuchMethodException {
		Method getter = null;

		try {
			getter = field.getDeclaringClass().getDeclaredMethod("get" + methodName, (Class<?>[])null);
		} catch (NoSuchMethodException e) {
			if (field.getType() == boolean.class)
				getter = field.getDeclaringClass().getDeclaredMethod("is" + methodName, (Class<?>[])null);
			else
				throw e;
		}

		Method setter = field.getDeclaringClass().getDeclaredMethod("set" + methodName, field.getType());

		if (getter != null && setter != null) {
			MethodHolder methodHolder = new MethodHolder(setter, getter);
			methods.put(field.getName(), methodHolder);
			return true;
		} else
			return false;
	}

	public static Object createInstance(String className, Object[] objects, Class[] classes) {
		try {
			Class<?> cls = Class.forName(className);
			Constructor<?> declaredConstructor = cls.getDeclaredConstructor(classes);
			return declaredConstructor.newInstance(objects);
		} catch (Exception e) {
			throw new CreateInstanceRuntimeException(e);
		}
	}

	public static String getCallerClassName() {
		StackTraceElement[] ste;
		ste = Thread.currentThread().getStackTrace();
		return ste[3].getClassName();
	}

	public static String getCallerMethodName() {
		StackTraceElement[] ste;
		ste = Thread.currentThread().getStackTrace();
		return ste[3].getMethodName();
	}

	public static int getCallerLineNumber() {
		StackTraceElement[] ste;
		ste = Thread.currentThread().getStackTrace();
		return ste[3].getLineNumber();
	}

	private static String getAccessorName(String fieldName) {
		char[] nameChar = fieldName.toCharArray();

		if (nameChar.length > 0
				&& Character.isLowerCase(nameChar[0])
				&& (nameChar.length == 1 || !Character.isUpperCase(nameChar[1]))) {
			fieldName = changeFirstCharacterCase(fieldName, true);
		}
		return fieldName;
	}
	
	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0)
			return str;
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize)
			sb.append(Character.toUpperCase(str.charAt(0)));
		else
			sb.append(Character.toLowerCase(str.charAt(0)));

		sb.append(str.substring(1));

		return sb.toString();
	}
}
