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

package com.anyframe.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.support.AopUtils;

import com.anyframe.core.vo.AbstractVo;
import com.anyframe.core.vo.meta.MethodHolder;

/**
 * Utility class for internal class handling.
 * 
 * @author Hyoungsoon Kim
 */
public class ClassUtil {

	public static final String CGLIB_CLASS_SEPARATOR = "$$";

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

		if (cls == String.class || cls == Short.class || cls == Integer.class || cls == Long.class
				|| cls == Float.class || cls == Double.class || cls == Boolean.class)
			return true;
		return false;
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

		Class<?> superclass = cls.getSuperclass();
		if (superclass != null) {
			if (superclass == java.lang.Object.class || superclass == AbstractVo.class)
				return fields;

			List<Field> superFields = getAllFields(cls.getSuperclass());
			if (superFields != null)
				fields.addAll(superFields);
		}

		return fields;
	}

	public static Map<String, MethodHolder> getAllGetterSetterMethods(Class<?> cls, List<Field> fields) {
		Map<String, MethodHolder> methods = new HashMap<String, MethodHolder>();

		for (Field field : fields)
			isValidFieldThenBuildSetterGetter(field, methods);

		return methods;
	}

	private static boolean isValidFieldThenBuildSetterGetter(Field field, Map<String, MethodHolder> methods) {
		String methodName = StringUtil.getAccessorName(field.getName());

		try {
			return findAndPut(field, methods, methodName);
		} catch (NoSuchMethodException e) {
			methodName = StringUtil.changeFirstCharacterCase(field.getName(), true);
			try {
				return findAndPut(field, methods, methodName);
			} catch (NoSuchMethodException e2) {
				return false;
			}
		}
	}

	/**
	 * Field의 Getter와 Setter 메소드를 가지고 있는 MethodHolder를 리턴. Field의 Getter나
	 * Setter가 존재하지 않는 경우 null을 리턴.
	 * 
	 * @param field
	 * @param methodName
	 * @return MethodHolder including getter/setter method
	 * @throws NoSuchMethodException
	 */
	public static MethodHolder getMethodHolder(Field field) {
		String methodName = StringUtil.getAccessorName(field.getName());

		try {
			return getMethodHolder(field, methodName);
		} catch (NoSuchMethodException e) {
			methodName = StringUtil.changeFirstCharacterCase(field.getName(), true);
			try {
				return getMethodHolder(field, methodName);
			} catch (NoSuchMethodException e2) {
				return null;
			}
		}
	}

	/**
	 * Field의 Getter와 Setter 메소드를 가지고 있는 MethodHolder를 리턴. Field의 Getter나
	 * Setter가 존재하지 않는 경우 null을 리턴.
	 * 
	 * @param field
	 * @param methodName
	 * @return
	 * @throws NoSuchMethodException
	 */
	private static MethodHolder getMethodHolder(Field field, String methodName) throws NoSuchMethodException {
		Method getter = null;

		try {
			getter = field.getDeclaringClass().getDeclaredMethod("get" + methodName, null);
		} catch (NoSuchMethodException e) {
			if (field.getType() == boolean.class)
				getter = field.getDeclaringClass().getDeclaredMethod("is" + methodName, null);
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
			getter = field.getDeclaringClass().getDeclaredMethod("get" + methodName, null);
		} catch (NoSuchMethodException e) {
			if (field.getType() == boolean.class)
				getter = field.getDeclaringClass().getDeclaredMethod("is" + methodName, null);
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

}
