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

package com.sds.anyframe.batch.vo.transform.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.FieldMeta;
import com.sds.anyframe.batch.vo.meta.FieldMeta.FieldType;
import com.sds.anyframe.batch.vo.meta.VoMeta;
import com.sds.anyframe.batch.vo.reflector.Reflector;
import com.sds.anyframe.batch.vo.transform.Transform;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class TransformJDBC implements Transform {
	
	private boolean    readNull        = BatchDefine.READER_DB_NULL_ALLOW;
	private String     readStringRepl  = BatchDefine.READER_DB_NULL_STRING_REPL;
	private BigDecimal readDecimalRepl = BatchDefine.READER_DB_NULL_DECIMAL_REPL;
	
	private boolean    writeNull        = BatchDefine.WRITER_DB_NULL_ALLOW;
	private String 	   writeStringRepl  = BatchDefine.WRITER_DB_NULL_STRING_REPL;
	private BigDecimal writeDecimalRepl = BatchDefine.WRITER_DB_NULL_DECIMAL_REPL;
	
	// resultSet column meta
	Map<String, String> columnMap = null;
	
	@Override
	public Object decodeVo(Object rawData, Class<?> voClass) {
		
		ResultSet rs = (ResultSet)rawData;
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(voClass);
		Reflector reflector = CoreContext.getMetaManager().getReflector(voClass);
		
		if(this.columnMap == null){
			try {
				this.columnMap = buildNameMap(rs);
			} catch (Exception e) {
				throw new BatchRuntimeException("fail to get resultSet MetaData");
			}
		}
		
		int fieldCount = voMeta.getFields().size();
		List<FieldMeta> fields = voMeta.getFields();
		Object[] values = new Object[fieldCount];
		
		for (int index = 0; index < fieldCount; index++) {
			FieldMeta fieldInfo = fields.get(index);
			
			try {
				values[index] = decodeField(rawData, fieldInfo);
				
			} catch (Exception ex) {
				throw new EncodingException("fail to decode vo field. "
						+ fieldInfo.getFieldName() + " = " + values[index], ex);
			}
			
		}
			
		Object vo = voMeta.newInstance();
		reflector.setValues(vo, values);

		return vo;
		
	}
	
	@Override
	public Object decodeField(Object rawData, FieldMeta fieldMeta) {

		ResultSet rs = (ResultSet) rawData;

		if (fieldMeta.isCollection() || fieldMeta.isArray()) {
			throw new BatchRuntimeException("collection or array is not supported");
		}

		Object result = null;
		FieldType type = fieldMeta.getFieldType();
		String fieldName = fieldMeta.getFieldName();
		Object initValue = fieldMeta.getInitValue();

		try {
			switch (type) {
			case PRIMITIVE_BOOLEAN:
			case BOOLEAN: {
				result = readBoolean(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_SHORT:
			case SHORT: {
				result = readShort(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case BIGINTEGER: {
				result = readBigInteger(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_LONG:
			case LONG: {
				result = readLong(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_FLOAT:
			case FLOAT: {
				result = readFloat(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_DOUBLE:
			case DOUBLE: {
				result = readDouble(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case SQL_DATE: {
				result = readDate(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_BYTE:
			case BYTE: {
				result = readByte(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case PRIMITIVE_CHARACTER:
			case CHARACTER: {
				result = readCharacter(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case SQL_TIME: {
				result = readTime(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}
			case SQL_TIMESTAMP: {
				result = readTimestamp(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;
			}//
			case STRING:
				result = readString(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;

			case INTEGER:
			case PRIMITIVE_INTEGER:
				result = readInt(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;

			case BIGDECIMAL:
				result = readBigDecimal(rs, fieldName);
				result = getSafeDefaultObject(result, this.readNull, initValue);
				break;

			case VO:
			default:
				throw new BatchRuntimeException("Unknown field type: " + type);
			}

			return result;

		} catch (Exception e) {
			throw new BatchRuntimeException("decoding error at " + fieldName, e);
		}
	}
	
	Timestamp readTimestamp(ResultSet rs, String fieldName) throws SQLException {
		String lowerName = fieldName.toLowerCase();
		Timestamp result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getTimestamp(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getTimestamp(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Time readTime(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Time result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getTime(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getTime(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Character readCharacter(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Character result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getString(columnMap.get(lowerName)).charAt(0);
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getString(columnMap.get(attrLabel)).charAt(0);
			}
		}
		return result;
	}

	Byte readByte(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Byte result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getByte(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getByte(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Date readDate(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Date result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getDate(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getDate(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Double readDouble(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Double result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getDouble(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getDouble(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Float readFloat(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Float result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getFloat(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getFloat(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Long readLong(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Long result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getLong(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getLong(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	BigInteger readBigInteger(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		BigInteger result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = BigInteger.valueOf(rs.getLong(columnMap.get(lowerName)));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = BigInteger.valueOf(rs.getLong(columnMap.get(attrLabel)));
			}
		}
		return result;
	}

	Short readShort(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Short result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getShort(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getShort(columnMap.get(attrLabel));
			}
		}
		return result;
	}

	Boolean readBoolean(ResultSet rs, String fieldName) throws SQLException{
		String lowerName = fieldName.toLowerCase();
		Boolean result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getBoolean(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getBoolean(columnMap.get(attrLabel));
			}
		}
		return result;
	}
	
	@Override
	public Object encodeVo(Object vo, Object target, List<String> parameterNames) {

		PreparedStatement ps = (PreparedStatement)target;
		VoMeta voMeta = CoreContext.getMetaManager().getMetadata(vo.getClass());
		Reflector reflector = CoreContext.getMetaManager().getReflector(vo.getClass());
		
		Map<String, Integer> namedMap = voMeta.getNamedMap();
		
		if(parameterNames == null)
			parameterNames = new ArrayList<String>(namedMap.keySet());
		
		List<FieldMeta> fields = voMeta.getFields();
		Object[] params = new Object[parameterNames.size()];
		
		for(int index = 0; index < parameterNames.size(); index++) {
			
			String parameter = parameterNames.get(index);
			
			if(namedMap.containsKey(parameter) == false) {
				throw new BatchRuntimeException("invalid parameter name: " + parameter);
			}
			
			FieldMeta fieldMeta = fields.get(namedMap.get(parameter));

			if (fieldMeta.isCollection() || fieldMeta.isArray()) {
				throw new BatchRuntimeException("collection or array is not supported");
			}
			
			Object value = reflector.getValue(vo, parameter);
			Object initValue = fieldMeta.getInitValue();
			FieldType fieldType = fieldMeta.getFieldType();
			
			try {
				switch (fieldType) {
				case PRIMITIVE_BOOLEAN:
				case BOOLEAN: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setBoolean(index + 1, (Boolean) val);
					params[index] = val;
					break;
				}
				case PRIMITIVE_SHORT:
				case SHORT: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setShort(index + 1, (Short) val);
					params[index] = val;
					break;
				}
				case BIGINTEGER: {
					Object val = getSafeDefaultObject(value, this.writeNull,initValue);
					ps.setLong(index + 1, ((BigInteger) val).longValue());
					params[index] = val;
					break;
				}
				case PRIMITIVE_LONG:
				case LONG: {
					Object val = getSafeDefaultObject(value, this.writeNull,initValue);
					ps.setLong(index + 1, (Long) val);
					params[index] = val;
					break;
				}
				case PRIMITIVE_FLOAT:
				case FLOAT: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setFloat(index + 1, (Float) val);
					params[index] = val;
					break;
				}
				case PRIMITIVE_DOUBLE:
				case DOUBLE: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setDouble(index + 1, (Double) val);
					params[index] = val;
					break;
				}
				case SQL_DATE: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setDate(index + 1, (Date) val);
					params[index] = val;
					break;
				}
				case PRIMITIVE_BYTE:
				case BYTE: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setByte(index + 1, (Byte) val);
					params[index] = val;
					break;
				}
				case PRIMITIVE_CHARACTER:
				case CHARACTER: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setString(index + 1, ((Character) val).toString());
					params[index] = val;
					break;
				}
				case SQL_TIME: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setTime(index + 1, (Time) val);
					params[index] = val;
					break;
				}
				case SQL_TIMESTAMP: {
					Object val = getSafeDefaultObject(value, this.writeNull, initValue);
					ps.setTimestamp(index + 1, (Timestamp) val);
					params[index] = val;
					break;
				}
					//
				case STRING: {
					String strVal = getSafeString(value, this.writeNull, this.writeStringRepl);
					ps.setString(index + 1, strVal);
					params[index] = strVal;
					break;
				}

				case INTEGER:
				case PRIMITIVE_INTEGER: {
					int intVal = getSafeInt(value, this.writeNull, 0);
					ps.setInt(index + 1, intVal);
					params[index] = intVal;
					break;
				}

				case BIGDECIMAL:
					BigDecimal bigVal = getSafeBigDecimal(value, this.writeNull, this.writeDecimalRepl);
					ps.setBigDecimal(index + 1, bigVal);
					params[index] = bigVal;
					break;

				case VO:
				default:
					throw new BatchRuntimeException("Unknown field type: " + fieldType);
				}
				
			} catch (Exception e) {
				throw new BatchRuntimeException("encoding error at "
						+ voMeta.getVoClass().getSimpleName() + "."
						+ fieldMeta.getFieldName(), e);
			}
		}
		
		return params;
	}
	
	@Override
	public void encodeField(Object value, FieldMeta fieldMeta, Object target) {
		
	}
	
	private Object getSafeDefaultObject(Object input, boolean nullable, Object replacement){
		
		Object result = input;
		
		if (!nullable && result == null ){
			result = replacement;
		}
		
		return result;
	}
	
	private String getSafeString(Object input, boolean nullable, String replacement) {
		
		String result = (String)input;
		
		if (!nullable && ( result == null || StringUtils.isEmpty(result))){
			result = replacement;
		}
		
		return result;
	}
	
	private int getSafeInt(Object input, boolean nullable, int replacement) {
		
		Integer result = (Integer)input;
		
		if (result == null){
			result = replacement;
		}
		
		return result;
		
	}
	
	private BigDecimal getSafeBigDecimal(Object input, boolean nullable, BigDecimal replacement) {
		
		BigDecimal result = (BigDecimal)input;
		
		if (!nullable && result == null) {
			result = replacement;
		}
		
		return result;
		
	}
	
	private Map<String, String> buildNameMap(ResultSet rs) throws Exception {
		
		HashMap<String, String> nameMap = new HashMap<String, String>();
		ResultSetMetaData rsm = rs.getMetaData();
		
		int count = rsm.getColumnCount();

		for(int i=1; i<=count ;i++) {
			String columnName = rsm.getColumnLabel(i);		// EMP_CNT_TOT
			String lowerName  = deTransform(columnName.toLowerCase()); 	// empcnttot
			nameMap.put(lowerName, columnName);
		}
		return nameMap;
	}

	String readString(ResultSet rs, String fieldName) throws SQLException {
		
		String lowerName = fieldName.toLowerCase();
		String result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getString(columnMap.get(lowerName));
			
		} else {			
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getString(columnMap.get(attrLabel));
			}
		}

		result = getSafeString(result, this.readNull, this.readStringRepl);
		return result;
	}
	
	int readInt(ResultSet rs, String fieldName) throws SQLException {
		
		String lowerName = fieldName.toLowerCase();
		
		if (columnMap.containsKey(lowerName)) {
			return rs.getInt(columnMap.get(lowerName));
			
		} else {
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				return rs.getInt(columnMap.get(attrLabel));
			}
		}
		return 0;
	}
	
	BigDecimal readBigDecimal(ResultSet rs, String fieldName) throws SQLException {
		
		String lowerName = fieldName.toLowerCase();
		BigDecimal result = null;
		
		if (columnMap.containsKey(lowerName)) {
			result = rs.getBigDecimal(columnMap.get(lowerName));
			
		} else {
			String attrLabel = deTransform(lowerName);
			if (columnMap.containsKey(attrLabel)) {
				result = rs.getBigDecimal(columnMap.get(attrLabel));
			}
		}

		result = getSafeBigDecimal(result, this.readNull, this.readDecimalRepl);
		return result;
	}
	
	
	
	private String deTransform(String columnLabel) {
		return columnLabel.replace("_", "");
	}

	@Override
	public void setEncoder(ICharsetEncoder encoder) {
		// do nothing
		
	}

	@Override
	public void setDecoder(ICharsetDecoder decoder) {
		// do nothing
		
	}

	@Override
	public void setTrim(boolean bTrim) {
		// do nothing
		
	}

	@Override
	public void clear() {
		this.columnMap = null;
		
	}

}
