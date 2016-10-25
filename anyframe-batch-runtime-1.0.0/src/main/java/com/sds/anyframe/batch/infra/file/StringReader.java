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

package com.sds.anyframe.batch.infra.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NoWorkFoundException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.FieldSet;
import org.springframework.batch.item.file.separator.ResourceLineReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemReader;
import com.sds.anyframe.batch.infra.file.support.ArrayFieldSet;
import com.sds.anyframe.batch.infra.file.support.CSVReader;

/**
 * Item Reader for VSAM file.   
 * @author Hyoungsoon Kim 
 */

public class StringReader extends AbstractItemReader {
	
	private static final Log LOGGER = LogFactory.getLog(StringReader.class);

	static private final Object NOT_NULL = new Object();
	
	// default encoding for input files
	private String encoding = BatchDefine.DEFAULT_ENCODING;
	private int bufferSize = BatchDefine.READER_BUFFER_SIZE_DEFAULT_KB * 1024;
	private char delim = ',';

	private Resource resource;
	private CSVReader reader = null;
	private boolean escape = false;
	
	private boolean useVO = false;
	private Class<?> voClass = null;

	private boolean noFile = false;
	private FieldSet fieldSet = null;


	public void setTrim(boolean bTrim) {
		this.transform.setTrim(bTrim);
	}
	
	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public void setBufferSize(int newSize) {
		this.bufferSize = newSize;
	}
	
	@Override
	public boolean exists() {
		boolean bExist = false;
		
		if(this.resource != null) {
			try {
				bExist = this.resource.getFile().exists();
			} catch (IOException e) {
				bExist = false;
			}
		}
		
		return bExist;	 
	}
	
	@Override
	public String getURL() {
		String path = null;
		try {
			path =  this.resource.getURI().getPath();
		} catch (IOException e) {
			throw new BatchRuntimeException("error", e);
		}
		
		return path;
	}
	
	@Override
	public boolean deleteResource() {
		ExecutionContext executionContext = new ExecutionContext();
		this.close(executionContext);
		try {
			return this.resource.getFile().delete();
		} catch (IOException e) {
			throw new BatchRuntimeException("error occur while deleting resource", e);
		}
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setVo(Object vo) {
		
		if(vo == null) {
			this.useVO = false;
			this.voClass = null;
			
		} else {
			this.useVO = true;
			this.voClass = vo.getClass();
		} 
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void setDelimiter(char delim) {
		this.delim = delim;
	}

	public Object read() throws Exception, UnexpectedInputException,
			NoWorkFoundException, ParseException {
		
		if(noFile) {
			throw new FileNotFoundException(resource.getURI().getPath());
		}
		
		// read and get tokens from file
		String[] tokens;
		
		if(escape)
			tokens = this.reader.readLineTokenEscape();
		else
			tokens = this.reader.readLineToken();
		
		if(tokens == null) // end of file
			return null;
		
		increaseItemCount();
		
		fieldSet = new ArrayFieldSet(tokens, false);
		
		if(!useVO) {
			return NOT_NULL;
			
		} else {
			try {
				return this.transform.decodeVo(tokens, this.voClass);
			
			} catch (Exception ex) {
				throw new FlatFileParseException("Parsing error at line: " + getItemCount() + " in resource ="
						+ resource.getDescription() + ", input=[" + Arrays.toString(tokens) + "]", ex, Arrays.toString(tokens), (int)getItemCount());
			}
		}
		
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		
		Assert.notNull(resource, "Input Resource must not be null");
		
		try {
			
			if (!resource.exists()) {
				noFile = true;
				LOGGER.warn("Input resource does not exist:" + resource);
				return;
			}

			if (this.reader == null) {
				
				// charset에 해당하는 encoder 생성
				CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
				// Encoding 시 맵핑되지 않는 캐릭터는 대체문자로 대체
				decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
				decoder.onMalformedInput(CodingErrorAction.REPLACE);
				
				// 대체문자는 공백문자(' ')로 한다.
				decoder.replaceWith(" ");
				
				InputStreamReader isr = new InputStreamReader(resource.getInputStream(), decoder);
				//this.reader = new AnyframeBufferedReader(isr, bufferSize);
				//this.reader.setDelimiter(delim);
				
				this.reader = new CSVReader(new BufferedReader(isr, bufferSize), delim);
			}
		}
		catch (Exception e) {
			throw new ItemStreamException("Failed to initialize the reader", e);
		}
		
	}

	@Override
	public void close(ExecutionContext executionContext) throws ItemStreamException {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			throw new BatchRuntimeException("fail to close reader", e);
		}
		finally {
			reader = null;
		}
		
	}
	
	@Override
	public long getTotalLineCount() {
		ResourceLineReader lineReader = new ResourceLineReader(resource, encoding);
		lineReader.open();

		int lineCount = 0;
		while (lineReader.read() != null) {
			lineCount++;
		}
		
		lineReader.close();
		lineReader = null;

		return lineCount;
	}
	
	/**
	 * raw APIs
	 */
	
	@Override
	public byte[] getBytes(int index) throws Exception {
		String strVal = fieldSet.readString(index);
		return strVal.getBytes(this.encoding);
	}
	
	@Override
	public int getInt(int index) throws Exception {
		return fieldSet.readInt(index);
	}

	@Override
	public String getString(int index) throws Exception {
		return fieldSet.readString(index);
	}
	
	@Override
	public BigDecimal getBigDecimal(int index) throws Exception {
		return fieldSet.readBigDecimal(index);
	}

	@Override
	public Boolean getBoolean(int index) throws Exception {
		return fieldSet.readBoolean(index);
	}

	@Override
	public Timestamp getTimestamp(int index) throws Exception {
		return Timestamp.valueOf(fieldSet.readString(index));
	}

	@Override
	public Time getTime(int index) throws Exception {
		return Time.valueOf(fieldSet.readString(index));
	}

	@Override
	public Character getCharacter(int index) throws Exception {
		return fieldSet.readChar(index);
	}

	@Override
	public Byte getByte(int index) throws Exception {
		return fieldSet.readByte(index);
	}

	@Override
	public Date getDate(int index) throws Exception {
		return Date.valueOf(fieldSet.readString(index));
	}

	@Override
	public Double getDouble(int index) throws Exception {
		return fieldSet.readDouble(index);
	}

	@Override
	public Float getFloat(int index) throws Exception {
		return fieldSet.readFloat(index);
	}

	@Override
	public Long getLong(int index) throws Exception {
		return fieldSet.readLong(index);
	}

	@Override
	public BigInteger getBigInteger(int index) throws Exception {
		return new BigInteger(String.valueOf(fieldSet.readLong(index)));
	}

	@Override
	public Short getShort(int index) throws Exception {
		return fieldSet.readShort(index);
	}
}
