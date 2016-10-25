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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NoWorkFoundException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.charset.AbstractDecoder;
import com.sds.anyframe.batch.charset.AbstractEncoder;
import com.sds.anyframe.batch.charset.DecodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemReader;
import com.sds.anyframe.batch.infra.file.support.AnyframeBufferedInputStream;
import com.sds.anyframe.batch.util.ArrayUtil;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.VoMeta;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteReader extends AbstractItemReader {

	private static final Log LOGGER = LogFactory.getLog(ByteReader.class);
	static private final Object NOT_NULL = new Object();
	
	private int bufferSize = BatchDefine.READER_BUFFER_SIZE_DEFAULT_KB * 1024;
	private String encoding = BatchDefine.DEFAULT_ENCODING;
	
	private ICharsetDecoder decoder = AbstractDecoder.newCharsetDecoder(encoding);
	private ICharsetEncoder encoder = AbstractEncoder.newCharsetEncoder(encoding);

	private Resource resource = null;
	
	private int[] columnSize = null;
	private int[] columnOffsets = null;

	private AnyframeBufferedInputStream inputStream = null;
	
	private boolean useVO = false;
	private boolean noFile = false;
	private boolean fixedRowByte = true;
	private boolean skipCRLF = true;
	private boolean bTrim = true;

	private Class<?> voClass;
	private byte[] lineBytes = null;
	private int lineBytesLength;
	
	public void setTrim(boolean bTrim) {
		this.bTrim  = bTrim;
		this.transform.setTrim(bTrim);
	}
	
	public void setBufferSize(int size) {
		this.bufferSize = size;
	}
	
	public void setFixed(boolean bFixed){
		this.fixedRowByte = bFixed;
	}
	
	public void setSkipCRLF(boolean skipCRLF) {
		this.skipCRLF = skipCRLF;
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
	
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
		this.decoder = null;
		this.encoder = null;
		this.decoder = AbstractDecoder.newCharsetDecoder(encoding);
		this.encoder = AbstractEncoder.newCharsetEncoder(encoding);
		
		this.transform.setEncoder(this.encoder);
		this.transform.setDecoder(this.decoder);
	}

	public void setVo(Object vo) {
		
		if(vo == null) {
			this.useVO = false;
			this.voClass = null;
			
		} else {
			this.useVO = true;
			this.voClass = vo.getClass();
			
			VoMeta voMeta = CoreContext.getMetaManager().getMetadata(vo.getClass());
			setLineByteSize(voMeta.getLength());
		}
	}
	

	public Object read() throws Exception, UnexpectedInputException, NoWorkFoundException, ParseException {
		if(noFile)
			throw new FileNotFoundException(resource.getURI().getPath());
		
		if (inputStream == null)
			throw new ReaderNotOpenException("Reader must be open before it can be read.");
		
		try {
			if(fixedRowByte){
				if(inputStream.readLine(lineBytes, skipCRLF) == -1)
					return null;
			} else {
				lineBytes = null;
				byte[] buffer = null;
				
				if((buffer = inputStream.readLine()) == null)
					return null;
				
				lineBytes = ArrayUtil.copyOf(buffer, lineBytesLength);
			}
		}catch (Exception e) {
			throw new DecodingException("decoding error at line: "
					+ (getItemCount()+1) + " in resource=" + resource.getDescription(), e);
		}
		
		increaseItemCount(); // read count 증가
		
		if(!useVO) {
			return NOT_NULL;
			
		} else {
			try {
				return this.transform.decodeVo(lineBytes, this.voClass);
				
			}catch (Exception ex) {
				throw new DecodingException("decoding error at line: "
						+ getItemCount() + " in resource=" + resource.getDescription()
						+ ", input=[" + new String(lineBytes, encoding) + "]", ex);
			}
			
		}
	}
	
	private void setLineByteSize(int voSize) {
		this.lineBytes = new byte[voSize];
		this.lineBytesLength = voSize;
	}

	@Override
	public void open(ExecutionContext executionContext)	throws ItemStreamException {
		
		Assert.notNull(resource, "Input Resource must not be null");
		
		try {

			if (!resource.exists()) {
				noFile  = true;
				LOGGER.warn("Input resource does not exist:" + resource);
				return;
			}

			inputStream = new AnyframeBufferedInputStream(resource.getInputStream(), bufferSize);

		} catch (Exception e) {
			throw new ItemStreamException("Failed to initialize the reader", e);
		}
		
	}

	@Override
	public void close(ExecutionContext executionContext) throws ItemStreamException {
		try {
			if (inputStream != null)
				inputStream.close();

		} catch (Exception e) {
			throw new ItemStreamException("Error while closing item reader", e);
			
		} finally {
			inputStream = null;
		}
	}

	@Override
	public long getTotalLineCount() throws IOException {
		AnyframeBufferedInputStream is = new AnyframeBufferedInputStream(resource.getInputStream(), bufferSize);

		long lineCount = 0;
		while(is.readLine() != null)
			lineCount++;
			
		is.close();
		return lineCount;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	

	/**
	 * raw APIs
	 */
	
	@Override
	public byte[] getBytes(int index) throws Exception {
		int length = columnSize[index];

		byte[] outbytes = new byte[length];
		System.arraycopy(lineBytes, columnOffsets[index], outbytes, 0, length);

		return outbytes;
	}
	
	@Override
	public int getInt(int index) throws Exception {
		try {
			return decoder.decodeInt(lineBytes, columnOffsets[index], columnSize[index]);
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}
	
	@Override
	public BigDecimal getBigDecimal(int index) throws Exception {
		try {
			return decoder.decodeBigDecimal(lineBytes, columnOffsets[index], columnSize[index]);
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public String getString(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return str;
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public void setColumnSize(int[] columnSize) {
		this.columnSize = columnSize;

		columnOffsets = new int[columnSize.length];
		int bytesLength = 0;

		for (int i = 0; i < columnSize.length; i++) {
			columnOffsets[i] = bytesLength;
			bytesLength += columnSize[i];
		}
		
		setLineByteSize(bytesLength);
	}

	@Override
	public Boolean getBoolean(int index) throws Exception {
		
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Boolean.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Timestamp getTimestamp(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Timestamp.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Time getTime(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Time.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Character getCharacter(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
				char[] charArray = str.toCharArray();
				
			return charArray.length == 0 ? null : charArray[0];
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Byte getByte(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Byte.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Date getDate(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Date.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Double getDouble(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Double.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Float getFloat(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Float.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Long getLong(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Long.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public BigInteger getBigInteger(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return new BigInteger(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

	@Override
	public Short getShort(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBytes, columnOffsets[index], columnSize[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Short.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnSize[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBytes, encoding) + "]", ex);
		}
	}

}
