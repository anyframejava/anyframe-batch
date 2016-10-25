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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ClearFailedException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.FlushFailedException;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.charset.AbstractDecoder;
import com.sds.anyframe.batch.charset.AbstractEncoder;
import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Align;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Padding;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemWriter;
import com.sds.anyframe.batch.infra.file.support.AnyframeBufferedOutputStream;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteWriter extends AbstractItemWriter {

	private static final Log LOGGER = LogFactory.getLog(ByteWriter.class);
	private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");

	private Resource resource;
	private File outputFile = null;
	private long maxSize = -1;
	private int bufferSize = BatchDefine.WRITER_BUFFER_SIZE_DEFAULT_KB * 1024;
	private AnyframeBufferedOutputStream outputStream = null;
	
	private String encoding = BatchDefine.DEFAULT_ENCODING;
	private ICharsetEncoder encoder = AbstractEncoder.newCharsetEncoder(encoding);
	private ICharsetDecoder decoder = AbstractDecoder.newCharsetDecoder(encoding);
	
	private byte[] lineSeparatorBytes = DEFAULT_LINE_SEPARATOR.getBytes();
	private byte[] lineBuffer = null;
	private int lineBufferSize = 0;
	
	private int[] columnLengths = null;
	private int[] columnOffsets = null;
	
	private boolean deleteEmpty;
	
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

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	
	public void setEncoding(String newEncoding) {
		this.encoder = null;
		this.decoder = null;
		
		this.encoding = newEncoding;
		this.encoder = AbstractEncoder.newCharsetEncoder(newEncoding);
		this.decoder = AbstractDecoder.newCharsetDecoder(newEncoding);
		this.transform.setEncoder(this.encoder);
		this.transform.setDecoder(this.decoder);
	}

	public void setBufferSize(int newSize) {
		this.bufferSize = newSize;
	}
	
	public void setDeleteEmpty(boolean deleteEmpty) {
		this.deleteEmpty = deleteEmpty;
	}

	public void setLineSeparator(String lineSeparator) {
		if(lineSeparator == null) {
			this.lineSeparatorBytes = null;
			
		} else {
			try {
				this.lineSeparatorBytes = lineSeparator.getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				throw new BatchRuntimeException("unsupported charset : " + encoding);
			}
		}
	}
	
	
	public void write(Object vo) throws Exception {
		
		byte[] bytes = null;
			
		try {
			bytes = (byte[]) this.transform.encodeVo(vo, null, null);
			
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + 1 + " in resource="
					+ resource.getDescription(), ex);
		}

		outputStream.write(bytes);
		
		if(lineSeparatorBytes != null)
			outputStream.write(lineSeparatorBytes);
		
		increaseItemCount();
	}
	
	public void write() throws Exception {

		outputStream.write(lineBuffer);
		
		if(lineSeparatorBytes != null)
			outputStream.write(lineSeparatorBytes);
		
		clear();
		
		increaseItemCount();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		
		Assert.notNull(resource, "The resource must be set");
		
		try {
			outputFile = resource.getFile();
			
			if(outputFile.exists()) {
				if(BatchDefine.WRITER_FILE_ERROR_ON_EXIST)
					throw new ItemStreamException("the file to write exists [" + outputFile + "]");
				
				else {
					LOGGER.info("the file to write exists and will be overwritten [" + outputFile + "]");
					if(!outputFile.delete()) {
						throw new ItemStreamException("the file to write exists and fail to delete it [" + outputFile + "]");
					}
				}
				
			}
			
			if(BatchDefine.WRITER_FILE_USE_TMP) {
				String filePath = outputFile.getAbsolutePath();

				if(!filePath.endsWith(BatchDefine.WRITER_FILE_TMP_SUFFIX)) {
					String tmpFilePath = filePath + BatchDefine.WRITER_FILE_TMP_SUFFIX;
					outputFile = new File(tmpFilePath);
				}
			}
			
			FileUtils.setUpOutputFile(outputFile, false, true);

			outputStream = 	new AnyframeBufferedOutputStream(new FileOutputStream(outputFile), bufferSize);
			
		} catch (Exception e) {
			throw new ItemStreamException("Failed to initialize writer", e);
		}	
	}

	@Override
	protected void checkCapacity() {
		if(this.maxSize >= 0) {
			long length = this.outputFile.length();
			
			if(length > this.maxSize) {
				long gigaSize = (maxSize / 1024 / 1024 / 1024);
				throw new BatchRuntimeException("output file["
						+ outputFile.getPath() + "] exceeds max file size of "
						+ gigaSize + "GB");
			}
		}
	}
	
	@Override
	public void close(ExecutionContext executionContext) {
		
		if(this.outputStream == null)
			return;
		
		try {	
			this.outputStream.close();
			
			if(BatchDefine.WRITER_FILE_USE_TMP) {
				
				if(!outputFile.exists()) {
					throw new ItemStreamException("temporary output file does not exist: " + outputFile);
				}
				
				String suffix = BatchDefine.WRITER_FILE_TMP_SUFFIX;
				String tmpFilePath = outputFile.getAbsolutePath();
				
				if(!tmpFilePath.endsWith(suffix)) {
					throw new ItemStreamException("temporary output filename should ends with " + suffix);
				}
				
				String realName = tmpFilePath.substring(0, tmpFilePath.length() - suffix.length());
				File realFile = new File(realName);
			
				if(outputFile.renameTo(realFile)) {
					LOGGER.info("temporary output file " + outputFile + " has been changed to " + realName);
					outputFile = realFile;
				}else {
					throw new ItemStreamException("fail to change temporary file from " + outputFile + " to " + realName);
				}
				
			}
			
			if(this.deleteEmpty && outputFile.exists() && outputFile.length() == 0) {
				if(!outputFile.delete())
					outputFile.deleteOnExit();
			}

		} catch (IOException ioe) {
			throw new ItemStreamException("Unable to close the the ItemWriter", ioe);
			
		} finally {
			outputStream = null;
		}
	}
	
	@Override
	public void update(ExecutionContext executionContext) {

	}

	@Override
	public void flush() throws FlushFailedException {
		try {
			outputStream.flush();
		} catch (IOException e) {
			throw new FlushFailedException("Failed to flush to file: " + resource, e);
		}
	}

	@Override
	public void clear() throws ClearFailedException {
		this.lineBuffer = null;
		this.lineBuffer = new byte[lineBufferSize];
	}


	/**
	 * raw APIs
	 */

	@Override
	public void setBytes(int index, byte[] bytes) throws Exception {
		int length = this.columnLengths[index];
		System.arraycopy(bytes, 0, lineBuffer, columnOffsets[index], length);
	}
	
	@Override
	public void setInt(int index, int value) throws Exception {
		try {
			this.encoder.encodeInteger(value, Padding.ZERO, Align.RIGHT, lineBuffer, columnOffsets[index], columnLengths[index]);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
	}
	
	@Override
	public void setString(int index, String value) throws Exception {
		try {
			char[] chars = value.toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
	}
	
	@Override
	public void setBigDecimal(int index, BigDecimal value) throws Exception {
		try {
			this.encoder.encodeBigDecimal(value, 0, false, Padding.ZERO, Align.RIGHT, lineBuffer, columnOffsets[index], columnLengths[index]);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}
	
	@Override
	public void setColumnSize(int[] columnSize) {
		this.columnLengths = columnSize;
		this.columnOffsets = new int[columnSize.length];

		this.lineBufferSize = 0;
		for (int i = 0; i < columnSize.length; i++) {
			this.columnOffsets[i] = this.lineBufferSize;
			this.lineBufferSize += columnSize[i];
		}
		
		lineBuffer = new byte[this.lineBufferSize];
	}

	@Override
	public void setBoolean(int index, Boolean value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
	}

	@Override
	public void setShort(int index, Short value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeNumber(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.ZERO, Align.RIGHT);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setBigInteger(int index, BigInteger value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeNumber(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.ZERO, Align.RIGHT);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setLong(int index, Long value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeNumber(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.ZERO, Align.RIGHT);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setFloat(int index, Float value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeNumber(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.ZERO, Align.RIGHT);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setDouble(int index, Double value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeNumber(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.ZERO, Align.RIGHT);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setDate(int index, Date value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
	}

	@Override
	public void setByte(int index, Byte value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
	}

	@Override
	public void setCharacter(int index, Character value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setTime(int index, Time value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}

	@Override
	public void setTimestamp(int index, Timestamp value) throws Exception {
		try {
			char[] chars = value.toString().toCharArray();
			this.encoder.encodeChar(chars, 0, chars.length, lineBuffer, columnOffsets[index], columnLengths[index], Padding.SPACE);
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ value + "]", ex);
		}
		
	}
	
}
