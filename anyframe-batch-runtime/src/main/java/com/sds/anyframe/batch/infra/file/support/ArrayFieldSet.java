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

package com.sds.anyframe.batch.infra.file.support;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.batch.item.file.mapping.FieldSet;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ArrayFieldSet implements FieldSet {

	private final static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * The fields wrapped by this '<code>FieldSet</code>' instance.
	 */
	private String[] tokens;

	private List<String> names;
	
	private boolean trimString = true;

	public ArrayFieldSet(String[] tokens, boolean trimString) {
		this.tokens = tokens;
		this.trimString = trimString;
	}

	public ArrayFieldSet(String[] tokens, String[] names) {
		Assert.notNull(tokens);
		Assert.notNull(names);
		if (tokens.length != names.length) {
			throw new IllegalArgumentException("Field names must be same length as values: names="
					+ Arrays.asList(names) + ", values=" + Arrays.asList(tokens));
		}
		this.tokens = tokens;
		this.names = Arrays.asList(names);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#getNames()
	 */
	public String[] getNames() {
		if (names == null) {
			throw new IllegalStateException("Field names are not known");
		}
		return (String[]) names.toArray();
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.FieldSet#hasNames()
	 */
	public boolean hasNames() {
		return names!=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#getValues()
	 */
	public String[] getValues() {
		return (String[]) tokens.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readString(int)
	 */
	public String readString(int index) {
		if(trimString)
			return readAndTrim(index);
		else
			return readRawString(index);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readString(java.lang.String)
	 */
	public String readString(String name) {
		return readString(indexOf(name));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readRawString(int)
	 */
	public String readRawString(int index) {
		return tokens[index];
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readRawString(java.lang.String)
	 */
	public String readRawString(String name) {
		return readRawString(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBoolean(int)
	 */
	public boolean readBoolean(int index) {
		return readBoolean(index, "true");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBoolean(java.lang.String)
	 */
	public boolean readBoolean(String name) {
		return readBoolean(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBoolean(int,
	 * java.lang.String)
	 */
	public boolean readBoolean(int index, String trueValue) {
		Assert.notNull(trueValue, "'trueValue' cannot be null.");

		String value = readAndTrim(index);

		return trueValue.equals(value) ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBoolean(java.lang.String,
	 * java.lang.String)
	 */
	public boolean readBoolean(String name, String trueValue) {
		return readBoolean(indexOf(name), trueValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readChar(int)
	 */
	public char readChar(int index) {
		String value = readAndTrim(index);

		Assert.isTrue(value.length() == 1, "Cannot convert field value '" + value + "' to char.");

		return value.charAt(0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readChar(java.lang.String)
	 */
	public char readChar(String name) {
		return readChar(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readByte(int)
	 */
	public byte readByte(int index) {
		return Byte.parseByte(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readByte(java.lang.String)
	 */
	public byte readByte(String name) {
		return readByte(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readShort(int)
	 */
	public short readShort(int index) {
		return Short.parseShort(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readShort(java.lang.String)
	 */
	public short readShort(String name) {
		return readShort(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readInt(int)
	 */
	public int readInt(int index) {
		return Integer.parseInt(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readInt(java.lang.String)
	 */
	public int readInt(String name) {
		return readInt(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readInt(int,
	 * int)
	 */
	public int readInt(int index, int defaultValue) {
		String value = readAndTrim(index);

		return StringUtils.hasLength(value) ? Integer.parseInt(value) : defaultValue;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readInt(java.lang.String,
	 * int)
	 */
	public int readInt(String name, int defaultValue) {
		return readInt(indexOf(name), defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readLong(int)
	 */
	public long readLong(int index) {
		return Long.parseLong(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readLong(java.lang.String)
	 */
	public long readLong(String name) {
		return readLong(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readLong(int,
	 * long)
	 */
	public long readLong(int index, long defaultValue) {
		String value = readAndTrim(index);

		return StringUtils.hasLength(value) ? Long.parseLong(value) : defaultValue;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readLong(java.lang.String,
	 * long)
	 */
	public long readLong(String name, long defaultValue) {
		return readLong(indexOf(name), defaultValue);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readFloat(int)
	 */
	public float readFloat(int index) {
		return Float.parseFloat(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readFloat(java.lang.String)
	 */
	public float readFloat(String name) {
		return readFloat(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDouble(int)
	 */
	public double readDouble(int index) {
		return Double.parseDouble(readAndTrim(index));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDouble(java.lang.String)
	 */
	public double readDouble(String name) {
		return readDouble(indexOf(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBigDecimal(int)
	 */
	public BigDecimal readBigDecimal(int index) {
		return readBigDecimal(index, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBigDecimal(java.lang.String)
	 */
	public BigDecimal readBigDecimal(String name) {
		return readBigDecimal(name, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBigDecimal(int,
	 * java.math.BigDecimal)
	 */
	public BigDecimal readBigDecimal(int index, BigDecimal defaultValue) {
		String candidate = readAndTrim(index);

		try {
			return (StringUtils.hasText(candidate)) ? new BigDecimal(candidate) : defaultValue;
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Unparseable number: " + candidate);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readBigDecimal(java.lang.String,
	 * java.math.BigDecimal)
	 */
	public BigDecimal readBigDecimal(String name, BigDecimal defaultValue) {
		try {
			return readBigDecimal(indexOf(name), defaultValue);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage() + ", name: [" + name + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDate(int)
	 */
	public Date readDate(int index) {
		return readDate(index, DEFAULT_DATE_PATTERN);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDate(java.lang.String)
	 */
	public Date readDate(String name) {
		return readDate(name, DEFAULT_DATE_PATTERN);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDate(int,
	 * java.lang.String)
	 */
	public Date readDate(int index, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setLenient(false);
		Date date;
		String value = readAndTrim(index);
		try {
			date = sdf.parse(value);
		}
		catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage() + ", pattern: [" + pattern + "]");
		}
		return date;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#readDate(java.lang.String,
	 * java.lang.String)
	 */
	public Date readDate(String name, String pattern) {
		try {
			return readDate(indexOf(name), pattern);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage() + ", name: [" + name + "]");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#getFieldCount()
	 */
	public int getFieldCount() {
		return tokens.length;
	}

	/**
	 * Read and trim the {@link String} value at '<code>index</code>'.
	 * 
	 * @throws NullPointerException if the field value is <code>null</code>.
	 */
	protected String readAndTrim(int index) {
		String value = tokens[index];

		if (value != null) {
			return value.trim();
		}
		else {
			return null;
		}
	}

	/**
	 * Read and trim the {@link String} value from column with given '<code>name</code>.
	 * 
	 * @throws IllegalArgumentException if a column with given name is not
	 * defined.
	 */
	protected int indexOf(String name) {
		if (names == null) {
			throw new IllegalArgumentException("Cannot access columns by name without meta data");
		}
		int index = names.indexOf(name);
		if (index >= 0) {
			return index;
		}
		throw new IllegalArgumentException("Cannot access column [" + name + "] from " + names);
	}

	public String toString() {
		if (names != null) {
			return getProperties().toString();
		}

		return tokens == null ? "" : Arrays.asList(tokens).toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object instanceof ArrayFieldSet) {
			ArrayFieldSet fs = (ArrayFieldSet) object;

			if (this.tokens == null) {
				return fs.tokens == null;
			}
			else {
				return Arrays.equals(this.tokens, fs.tokens);
			}
		}

		return false;
	}

	public int hashCode() {
		// this algorithm was taken from java 1.5 jdk Arrays.hashCode(Object[])
		if (tokens == null) {
			return 0;
		}

		int result = 1;

		for (int i = 0; i < tokens.length; i++) {
			result = 31 * result + (tokens[i] == null ? 0 : tokens[i].hashCode());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.item.file.mapping.IFieldSet#getProperties()
	 */
	public Properties getProperties() {
		if (names == null) {
			throw new IllegalStateException("Cannot create properties without meta data");
		}
		Properties props = new Properties();
		for (int i = 0; i < tokens.length; i++) {
			String value = readAndTrim(i);
			if (value != null) {
				props.setProperty((String) names.get(i), value);
			}
		}
		return props;
	}

}
