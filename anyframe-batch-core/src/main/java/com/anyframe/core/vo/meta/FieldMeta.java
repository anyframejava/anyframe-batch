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

package com.anyframe.core.vo.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.anyframe.core.CoreContext;
import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;
import com.anyframe.core.annotation.Scale;
import com.anyframe.core.vo.AbstractVo;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class FieldMeta {

	public enum FieldType {

		PRIMITIVE_BOOLEAN(new Boolean(false), false, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return Boolean.FALSE;

				return Boolean.valueOf(value.trim());
			}
		},
		BOOLEAN(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Boolean.valueOf(value.trim());
			}
		},
		PRIMITIVE_SHORT(new Short((short) 0), true, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return 0;

				return Short.valueOf(value.trim());
			}
		},
		SHORT(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Short.valueOf(value.trim());
			}
		},
		PRIMITIVE_INTEGER(new Integer(0), true, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return 0;
				return Integer.valueOf(value.trim());
			}
		},
		INTEGER(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Integer.valueOf(value.trim());
			}
		},
		BIGINTEGER(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;

				return new BigInteger(value.trim());
			}
		},
		PRIMITIVE_LONG(new Long(0l), true, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return 0l;
				return Long.valueOf(value.trim());
			}
		},
		LONG(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Long.valueOf(value.trim());
			}
		},
		PRIMITIVE_FLOAT(new Float(0.0f), true, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return 0f;
				return Float.valueOf(value.trim());
			}
		},
		FLOAT(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Float.valueOf(value.trim());
			}
		},
		PRIMITIVE_DOUBLE(new Double(0.0d), true, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return 0d;
				return Double.valueOf(value.trim());
			}
		},
		DOUBLE(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return Double.valueOf(value.trim());
			}
		},
		BIGDECIMAL(null, true, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return new BigDecimal(value.trim());
			}

			@Override
			public String toString(Object fieldValue) {
				return ((BigDecimal) fieldValue).toPlainString();
			}
		},
		STRING(null, false, false) {
			@Override
			public Object fromString(String value) {
				return value;
			}
		},
		SQL_DATE(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return java.sql.Date.valueOf(value.trim());
			}
		},
		BYTE(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.length() == 0)
					return null;
				return Byte.valueOf(value);
			}
		},
		PRIMITIVE_BYTE(new Byte((byte) 0), false, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.length() == 0)
					return 0;
				return Byte.valueOf(value);
			}
		},
		CHARACTER(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.length() == 0)
					return null;
				return convertStringToCharacter(value);
			}

		},
		PRIMITIVE_CHARACTER(new Character('\u0000'), false, true) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.length() == 0)
					return '\u0000';
				return convertStringToCharacter(value);
			}
		},
		SQL_TIME(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return java.sql.Time.valueOf(value.trim());
			}
		},
		SQL_TIMESTAMP(null, false, false) {
			@Override
			public Object fromString(String value) {
				if (value == null || value.trim().length() == 0)
					return null;
				return java.sql.Timestamp.valueOf(value.trim());
			}
		},
		VO(null, false, false) {
			@Override
			public Object fromString(String value) {
				throw new UnsupportedOperationException();
			}
		},
		UNKNOWN(null, false, false) {
			@Override
			public Object fromString(String value) {
				throw new UnsupportedOperationException();
			}
		};

		Object initValue;
		boolean isNumeric;
		boolean isPrimitive;

		FieldType(Object initValue, boolean isNumeric, boolean isPrimitive) {
			this.initValue = initValue;
			this.isNumeric = isNumeric;
			this.isPrimitive = isPrimitive;
		}

		public Object getInitValue(boolean isArray) {
			if (isArray)
				return null;
			return initValue;
		}

		public abstract Object fromString(String value);

		public String toString(Object fieldValue) {
			if (fieldValue == null)
				return "null";

			return fieldValue.toString();
		}

		public boolean isPirmitive() {
			return this.isPrimitive;
		}

		public boolean isNumeric() {
			return this.isNumeric;
		}

		private static Object convertStringToCharacter(String value) {
			char[] charArray = value.toCharArray();
			if (charArray.length > 1)
				throw new IllegalArgumentException("Character must be one letter(" + value + ")");
			return charArray.length == 0 ? null : charArray[0];
		}
	}

	private static final Map<Class<?>, FieldType> typeMap = new HashMap<Class<?>, FieldType>();

	static {
		typeMap.put(boolean.class, FieldType.PRIMITIVE_BOOLEAN);
		typeMap.put(Boolean.class, FieldType.BOOLEAN);

		typeMap.put(short.class, FieldType.PRIMITIVE_SHORT);
		typeMap.put(Short.class, FieldType.SHORT);

		typeMap.put(int.class, FieldType.PRIMITIVE_INTEGER);
		typeMap.put(Integer.class, FieldType.INTEGER);
		typeMap.put(BigInteger.class, FieldType.BIGINTEGER);

		typeMap.put(float.class, FieldType.PRIMITIVE_FLOAT);
		typeMap.put(Float.class, FieldType.FLOAT);

		typeMap.put(long.class, FieldType.PRIMITIVE_LONG);
		typeMap.put(Long.class, FieldType.LONG);

		typeMap.put(double.class, FieldType.PRIMITIVE_DOUBLE);
		typeMap.put(Double.class, FieldType.DOUBLE);

		typeMap.put(byte.class, FieldType.PRIMITIVE_BYTE);
		typeMap.put(Byte.class, FieldType.BYTE);

		typeMap.put(char.class, FieldType.PRIMITIVE_CHARACTER);
		typeMap.put(Character.class, FieldType.CHARACTER);

		typeMap.put(String.class, FieldType.STRING);
		typeMap.put(BigDecimal.class, FieldType.BIGDECIMAL);
		typeMap.put(AbstractVo.class, FieldType.VO);
		// typeMap.put(List.class, FieldType.LIST);

		typeMap.put(java.sql.Date.class, FieldType.SQL_DATE);
		typeMap.put(java.sql.Time.class, FieldType.SQL_TIME);
		typeMap.put(java.sql.Timestamp.class, FieldType.SQL_TIMESTAMP);

	}

	/** 필드에 선언된 &#064;Scale 값. &#064;Scale 선언되지 않은 경우 -1 */
	private int scale = -1;

	/** total length **/
	private int length = -1;

	/** 필드에 선언된 &#064;Size 값. &#064;Size 선언되지 않은 경우 -1 */
	private int arraySize = -1;

	/** 필드에 선언된 &#064;LocalName 값. &#064;LocalName 선언되지 않은 경우 null */
	private String localName;

	/** 필드명 */
	private String name;

	/** 필드의 Class */
	private Class<?> fieldClass;

	/** 필드가 VO, List<VO> 형태인 경우 Sub VO의 메타정보 */
	private VoMeta voMeta;

	/** 필드의 데이터 유형 */
	private FieldType fieldType = FieldType.UNKNOWN;

	private boolean isArray;

	private boolean isCollection;

	private Field field;

	// used by Batch
	private Object initValue;

	/**
	 * field type 에 따른 논리적인 길이. field 유형별로 갖는 값은 다음과 같다.
	 * 
	 * <pre>
	 * Integer    : 1
	 * Money      : 1
	 * BigDecimal : 1
	 * VO         : child vo의 cumulative field length
	 * List       : [array count] * [child vo의 cumulative field length]
	 * </pre>
	 */
	private int fieldCount = 1;
	/** parent vo 기준 byte offset */
	private int byteOffset;
	/** parent vo 기준 field offset */
	private int fieldOffset;

	/** 필드에 선언된 &#064;Length 값. &#064;Length 선언되지 않은 경우 -1 */
	private int fieldLength = -1;

	public Field getField() {
		return field;
	}

	private FieldMeta() {

	}

	public static FieldMeta valueOf(Field field) {
		return valueOf(field, 0, new Stack<StackItem>());
	}

	@SuppressWarnings("unchecked")
	// TODO by jr : PropertyDescriptor를 사용하는 로직으로 수정 필요
	public static FieldMeta valueOf(Field field, int depth, Stack<StackItem> parents) {
		LocalName localName = field.getAnnotation(LocalName.class);
		Length length = field.getAnnotation(Length.class);
		Scale scale = field.getAnnotation(Scale.class);
		ArraySize arraySize = field.getAnnotation(ArraySize.class);

		FieldMeta fieldMeta = new FieldMeta();
		fieldMeta.field = field;
		Class<?> fieldClass = field.getType();

		fieldMeta.name = field.getName();
		fieldMeta.fieldClass = fieldClass;
		fieldMeta.isArray = fieldClass.isArray();
		fieldMeta.isCollection = Collection.class.isAssignableFrom(fieldClass);

		if (localName != null)
			fieldMeta.localName = localName.value();
		else
			// If there is no annotation of LocalName, use the name of field as default.
			fieldMeta.localName = field.getName();

		if (length != null) {
			fieldMeta.fieldLength = length.value();
			fieldMeta.length = fieldMeta.fieldLength; // for batch.
		}

		if (scale != null)
			fieldMeta.scale = scale.value();

		if (arraySize != null)
			fieldMeta.arraySize = arraySize.value();

		fieldMeta.fieldType = getFieldType(fieldClass);

		// length annotation 정보가 있는 경우에만 voMeta와 length 정보 체크 : List 타입도 FieldType이 VO인 이유는?
		if (fieldMeta.fieldType == FieldType.VO) {
			fieldMeta.voMeta = CoreContext.getMetaManager().getMetadata(fieldClass, depth, parents);
			if (fieldMeta.voMeta != null) {
				fieldMeta.length = fieldMeta.voMeta.getLength();
				fieldMeta.fieldCount = fieldMeta.voMeta.getFieldCount();
			}
		}

		if (fieldMeta.isCollection) {
			Type genericType = field.getGenericType();

			if (genericType instanceof ParameterizedType) {
				ParameterizedType pType = (ParameterizedType) genericType;

				Type[] args = pType.getActualTypeArguments();
				Class<?> argClass = null;

				if (args[0] instanceof WildcardType) {
					argClass = (Class<?>) ((WildcardType) args[0]).getUpperBounds()[0];
				} else if (args[0] instanceof ParameterizedType) {
					argClass = (Class<?>) ((ParameterizedType) args[0]).getActualTypeArguments()[0];
				} else {
					argClass = (Class<?>) args[0];
				}

				fieldMeta.fieldType = getFieldType(argClass);
				fieldMeta.fieldClass = argClass;

				// if parameterized type is Vo
				//FIXME List 필드이지만 ArraySize 어노테이션이 없는 경우도 있음.
				fieldMeta.voMeta = CoreContext.getMetaManager().getMetadata(fieldMeta.fieldClass, depth, parents);
				if (AbstractVo.class.isAssignableFrom(fieldMeta.fieldClass)) {
					calculateFieldLength(fieldMeta.fieldClass, arraySize, fieldMeta);
				} else {
					calculateFieldLength(arraySize, fieldMeta);
				}
			}
		} else if (fieldMeta.isArray) {
			Class<?> type = field.getType();
			fieldMeta.fieldType = getFieldType(type.getComponentType());
			fieldMeta.fieldClass = type.getComponentType();
			// if parameterized type is Vo
			fieldMeta.voMeta = CoreContext.getMetaManager().getMetadata(fieldMeta.fieldClass, depth, parents);
			if (AbstractVo.class.isAssignableFrom(fieldMeta.fieldClass)) {
				calculateFieldLength(fieldMeta.fieldClass, arraySize, fieldMeta);
			} else {
				calculateFieldLength(arraySize, fieldMeta);
			}
		}

		return fieldMeta;
	}

	/**
	 * element가 Primitive type인 Array/Collection의 경우 FieldMeta.length 처리
	 * 
	 * 가변길이가 아닌 Array/Collection field의 총길이 = field의 단위 길이 * @ArraySize의 value값
	 * 가변길이 Array/Collection field의 총길이 = 0
	 * 
	 * @param arraySize
	 *            ArraySize annotation
	 * @param fieldMeta
	 */
	private static void calculateFieldLength(ArraySize arraySize, FieldMeta fieldMeta) {
		if (arraySize != null && !arraySize.variable())
			fieldMeta.length = fieldMeta.fieldLength * fieldMeta.arraySize;
		else
			fieldMeta.length = 0;
	}

	/**
	 * element가 VO type인 Array/Collection의 경우 FieldMeta.voMeta,
	 * FieldMeta.fieldCount, FieldMeta.length 처리
	 * 
	 * 가변길이가 아닌 Array/Collection field의 총길이 = field의 단위 길이 * @ArraySize의 value값
	 * 가변길이 Array/Collection field의 총길이 = 0
	 * 
	 * @param clazz
	 *            Class of VO
	 * @param arraySize
	 *            ArraySize annotation
	 * @param fieldMeta
	 */
	private static void calculateFieldLength(Class<?> clazz, ArraySize arraySize, FieldMeta fieldMeta) {
		// TODO 계산로직에서 voMeta를 만들어내는 것은 이상함. 호출부 상단으로 옮김
		//fieldMeta.voMeta = CoreContext.getMetaManager().getMetadata(clazz);
		// TODO by jr : fieldCount is used by Anyframe Batch. 몇번째 필드인지를 나타내는 값 - 고윤원
		if (fieldMeta.voMeta != null) {
			fieldMeta.fieldCount = fieldMeta.voMeta.getFieldCount() * fieldMeta.arraySize;
		}
		if (arraySize != null && !arraySize.variable())
			// Field인 VO의 총 길이 * @ArraySize의 value 속성 값
			fieldMeta.length = fieldMeta.voMeta.getLength() * fieldMeta.arraySize;
		else
			fieldMeta.length = 0; // 가변길이.
	}

	public int getFieldCount() {
		return fieldCount;
	}

	// TODO by jr : 이게 사용되는 곳이 어딘지 파악해서 BeanUtils.isSimpleValueType()으로 교체하기
	private static FieldType getFieldType(Class<?> fieldClass) {

		for (Entry<Class<?>, FieldType> entry : typeMap.entrySet()) {
			Class<?> refClass = entry.getKey();
			if (refClass.isAssignableFrom(fieldClass))
				return entry.getValue();
		}

		//		return FieldType.UNKNOWN;
		return FieldType.VO;
	}

	public int getScale() {
		return scale;
	}

	public int getLength() {
		return length; // length * arraySize
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public int getArraySize() {
		return arraySize;
	}

	public String getFieldName() {
		return name;
	}

	public String getLocalName() {
		return localName;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public VoMeta getVoMeta() {
		return voMeta;
	}

	public FieldType getType() {
		return fieldType;
	}

	// public FieldType getParameterizedType() {
	// return parameterizedType;
	// }
	//
	// public Class<?> getParameterizedClass() {
	// return parameterizedClass;
	// }

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		try {
			return (T) fieldClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object fromString(String value) {
		Object obj = getType().fromString(value);
		// BigDecimal scale 처리. FieldType.BIGDECIMAL이 thread not safe하여 여기에 추가함.
		if (obj != null && getScale() != -1 && getFieldClass().isAssignableFrom(BigDecimal.class))
			obj = ((BigDecimal) obj).setScale(getScale());
		return obj;
	}

	public String toString(Object value) {
		// BigDecimal scale 처리. FieldType.BIGDECIMAL이 thread not safe하여 여기에 추가함.
		if (value != null && getScale() != -1 && getFieldClass().isAssignableFrom(BigDecimal.class))
			value = ((BigDecimal) value).setScale(getScale());
		return getType().toString(value);
	}

	public Object fromObject(Object value) {
		if (value == null)
			return null;

		if (getFieldClass() == value.getClass())
			return value;
		else if (getFieldClass().isAssignableFrom(value.getClass()))
			return value;
		else
			throw new ClassCastException(value.getClass() + " can not be casted to " + getFieldClass());
	}

	public boolean isArray() {
		return isArray;
	}

	public void setArray(boolean array) {
		this.isArray = array;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean collection) {
		this.isCollection = collection;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return field.getAnnotation(annotationClass);
	}

	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public int getByteOffset() {
		return byteOffset;
	}

	public void setByteOffset(int byteOffset) {
		this.byteOffset = byteOffset;
	}

	public int getFieldOffset() {
		return fieldOffset;
	}

	public void setFieldOffset(int fieldOffset) {
		this.fieldOffset = fieldOffset;
	}

	public Object getInitValue() {
		return initValue;
	}

	public void setInitValue(Object initValue) {
		this.initValue = initValue;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
}
