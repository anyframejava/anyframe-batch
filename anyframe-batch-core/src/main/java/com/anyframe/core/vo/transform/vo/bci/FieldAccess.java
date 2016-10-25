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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_1;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 
 * Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * http://code.google.com/p/reflectasm/
 * 
 * @author Jeryeon Kim
 * 
 */
public abstract class FieldAccess {
	static public FieldAccess get(Class type) {
		AccessClassLoader loader = new AccessClassLoader(type.getClassLoader());

		ArrayList<Field> fields = new ArrayList<Field>();
		Class nextClass = type;
		while (nextClass != Object.class) {
			Field[] declaredFields = nextClass.getDeclaredFields();
			for (int i = 0, n = declaredFields.length; i < n; i++) {
				Field field = declaredFields[i];
				int modifiers = field.getModifiers();
				if (Modifier.isStatic(modifiers))
					continue;
				if (Modifier.isPrivate(modifiers))
					continue;
				fields.add(field);
			}
			nextClass = nextClass.getSuperclass();
		}

		String className = type.getName();
		String accessClassName = className + "FieldAccess";
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
			cw.visit(V1_1, ACC_PUBLIC, accessClassNameInternal, null, "com/anyframe/core/vo/transform/vo/bci/FieldAccess",
					null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, "com/anyframe/core/vo/transform/vo/bci/FieldAccess", "<init>", "()V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ILOAD, 2);

				Label[] labels = new Label[fields.size()];
				for (int i = 0, n = labels.length; i < n; i++)
					labels[i] = new Label();
				Label defaultLabel = new Label();
				mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

				for (int i = 0, n = labels.length; i < n; i++) {
					Field field = fields.get(i);
					Type fieldType = Type.getType(field.getType());

					mv.visitLabel(labels[i]);
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, classNameInternal);
					mv.visitVarInsn(ALOAD, 3);

					switch (fieldType.getSort()) {
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
						mv.visitTypeInsn(CHECKCAST, fieldType.getDescriptor());
						break;
					case Type.OBJECT:
						mv.visitTypeInsn(CHECKCAST, fieldType.getInternalName());
						break;
					}

					mv.visitFieldInsn(PUTFIELD, classNameInternal, field.getName(), fieldType.getDescriptor());
					mv.visitInsn(RETURN);
				}

				mv.visitLabel(defaultLabel);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				mv.visitInsn(DUP);
				mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
				mv.visitInsn(DUP);
				mv.visitLdcInsn("Field not found: ");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
				mv.visitVarInsn(ILOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>",
						"(Ljava/lang/String;)V");
				mv.visitInsn(ATHROW);
				mv.visitMaxs(5, 4);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitVarInsn(ILOAD, 2);

				Label[] labels = new Label[fields.size()];
				for (int i = 0, n = labels.length; i < n; i++)
					labels[i] = new Label();
				Label defaultLabel = new Label();
				mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

				for (int i = 0, n = labels.length; i < n; i++) {
					Field field = fields.get(i);

					mv.visitLabel(labels[i]);
					mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, classNameInternal);
					mv
							.visitFieldInsn(GETFIELD, classNameInternal, field.getName(), Type.getDescriptor(field
									.getType()));

					Type fieldType = Type.getType(field.getType());
					switch (fieldType.getSort()) {
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
				mv.visitLdcInsn("Field not found: ");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
				mv.visitVarInsn(ILOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>",
						"(Ljava/lang/String;)V");
				mv.visitInsn(ATHROW);
				mv.visitMaxs(5, 3);
				mv.visitEnd();
			}
			cw.visitEnd();
			byte[] data = cw.toByteArray();
			accessClass = loader.defineClass(accessClassName, data);
		}
		try {
			FieldAccess access = (FieldAccess) accessClass.newInstance();
			access.fields = new HashMap<String, Integer>();
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				access.fields.put(fields.get(i).getName(), i);
			}
			return access;
		} catch (Exception ex) {
			throw new RuntimeException("Error constructing field access class: " + accessClassName, ex);
		}
	}

	private Map<String, Integer> fields;

	abstract public void set(Object object, int fieldIndex, Object value);

	abstract public <T> T get(Object object, int fieldIndex);

	public int getIndex(String fieldName) {
		Integer index = fields.get(fieldName);
		if (index == null)
			throw new IllegalArgumentException("Unable to find public field: " + fieldName);
		return index;
	}

	public void set(Object object, String fieldName, Object value) {
		set(object, getIndex(fieldName), value);
	}

	public <T> T get(Object object, String fieldName) {
		return (T) get(object, getIndex(fieldName));
	}
}
