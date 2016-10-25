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

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_1;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.anyframe.core.util.StringUtil;

/**
 * 
 * Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * http://code.google.com/p/reflectasm/
 * 
 * @author Jeryeon Kim
 * 
 */
public abstract class MethodAccess {
	static public MethodAccess get (Class type) {
		AccessClassLoader loader = new AccessClassLoader(type.getClassLoader());

		ArrayList<Method> methods = new ArrayList();
		Class nextClass = type;
		while (nextClass != Object.class) {
			Method[] declaredMethods = nextClass.getDeclaredMethods();
			for (int i = 0, n = declaredMethods.length; i < n; i++) {
				Method method = declaredMethods[i];
				int modifiers = method.getModifiers();
				if (Modifier.isStatic(modifiers)) continue;
				if (Modifier.isPrivate(modifiers)) continue;
				methods.add(method);
			}
			nextClass = nextClass.getSuperclass();
		}

		String className = type.getName();
		String accessClassName = className + "MethodAccess";
		Class accessClass = null;
		try {
			accessClass = loader.loadClass(accessClassName);
		} catch (ClassNotFoundException ignored) {
		}
		if (accessClass == null) {
			String accessClassNameInternal = accessClassName.replace('.', '/');
			String classNameInternal = className.replace('.', '/');

			ClassWriter cw = new ClassWriter(0);
			MethodVisitor mv;
			cw.visit(V1_1, ACC_PUBLIC, accessClassNameInternal, null, "com/esotericsoftware/reflectasm/MethodAccess", null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "com/esotericsoftware/reflectasm/MethodAccess", "<init>", "()V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invoke", "(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;",
					null, null);
				mv.visitCode();

				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, classNameInternal);
				mv.visitVarInsn(ASTORE, 4);

				mv.visitVarInsn(ILOAD, 2);
				Label[] labels = new Label[methods.size()];
				for (int i = 0, n = labels.length; i < n; i++)
					labels[i] = new Label();
				Label defaultLabel = new Label();
				mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

				int maxArgCount = 0;

				StringBuilder buffer = new StringBuilder(128);
				for (int i = 0, n = labels.length; i < n; i++) {
					mv.visitLabel(labels[i]);
					if (i == 0)
						mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] {classNameInternal}, 0, null);
					else
						mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 4);

					buffer.setLength(0);
					buffer.append('(');

					Method method = methods.get(i);
					Class[] paramTypes = method.getParameterTypes();
					maxArgCount = Math.max(maxArgCount, paramTypes.length);
					for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
						mv.visitVarInsn(ALOAD, 3);
						mv.visitIntInsn(BIPUSH, paramIndex);
						mv.visitInsn(AALOAD);
						Type paramType = Type.getType(paramTypes[paramIndex]);
						switch (paramType.getSort()) {
						case Type.BOOLEAN:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
							break;
						case Type.BYTE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
							break;
						case Type.CHAR:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
							break;
						case Type.SHORT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
							break;
						case Type.INT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
							break;
						case Type.FLOAT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
							break;
						case Type.LONG:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
							break;
						case Type.DOUBLE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
							break;
						case Type.ARRAY:
							mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
							break;
						case Type.OBJECT:
							mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
							break;
						}
						buffer.append(paramType.getDescriptor());
					}

					buffer.append(')');
					buffer.append(Type.getDescriptor(method.getReturnType()));
					mv.visitMethodInsn(INVOKEVIRTUAL, classNameInternal, method.getName(), buffer.toString());

					switch (Type.getType(method.getReturnType()).getSort()) {
					case Type.VOID:
						mv.visitInsn(ACONST_NULL);
						break;
					case Type.BOOLEAN:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
						break;
					case Type.BYTE:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
						break;
					case Type.CHAR:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
						break;
					case Type.SHORT:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
						break;
					case Type.INT:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
						break;
					case Type.FLOAT:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
						break;
					case Type.LONG:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
						break;
					case Type.DOUBLE:
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
						break;
					}

					mv.visitInsn(ARETURN);
				}

				mv.visitLabel(defaultLabel);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				mv.visitInsn(DUP);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("Method not found: ");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
				mv.visitVarInsn(ILOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
				mv.visitInsn(ATHROW);
				mv.visitMaxs(Math.max(5, maxArgCount + 2), 5);
				mv.visitEnd();
			}
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			accessClass = loader.defineClass(accessClassName, data);
		}
		try {
			MethodAccess access = (MethodAccess)accessClass.newInstance();
			access.methods = new HashMap<String, Integer> ();
			
			int size = methods.size();
			
			for(int i =0;i<size;i++) {
				access.methods.put(getFieldName(methods.get(i).getName()), i);
			}
			
			return access;
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing method access class: " + accessClassName, ex);
		}
	}

	private static String getFieldName(String methodName) {
		if(methodName.startsWith("get") || methodName.startsWith("set"))
			return  StringUtil.changeFirstCharacterCase(methodName.substring(3, methodName.length()), false);
		else if(methodName.startsWith("is"))
			return  StringUtil.changeFirstCharacterCase(methodName.substring(2, methodName.length()), false);
			
		return methodName;
	}

	private Map<String, Integer> methods;

	abstract public <T> T invoke (Object object, int methodIndex, Object... args);

	/**
	 * Invokes the first method with the specified name.
	 */
	public <T> T invoke (Object object, String methodName, Object... args) {
		return (T) invoke(object, getIndex(methodName), args);
	}

	/**
	 * Returns the index of the first method with the specified name.
	 */
	public int getIndex (String fieldName) {
//		for (int i = 0, n = methods.length; i < n; i++) {
//			Method method = methods[i];
//			if (method.getName().equals(methodName)) return i;
//		}
		Integer index = methods.get(fieldName);
		if(index == null)
			throw new IllegalArgumentException("Unable to find public method: " + index);
		return index;
	}

//	public int getIndex (String methodName, Class... parameterTypes) {
//		for (int i = 0, n = methods.length; i < n; i++) {
//			Method method = methods[i];
//			if (method.getName().equals(methodName) && Arrays.equals(parameterTypes, method.getParameterTypes())) return i;
//		}
//		throw new IllegalArgumentException("Unable to find public method: " + methodName + " " + Arrays.toString(parameterTypes));
//	}
}
