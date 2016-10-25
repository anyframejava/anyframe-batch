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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ClearFailedException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.FlushFailedException;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.mapping.DefaultFieldSet;
import org.springframework.batch.item.file.mapping.FieldSet;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemWriter;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class StringWriter extends AbstractItemWriter implements ItemStream {

	private static final Log LOGGER = LogFactory.getLog(StringWriter.class);
	
	private Resource resource;

	private String lineSeparator = System.getProperty("line.separator");
	private String encoding = BatchDefine.DEFAULT_ENCODING;

	private int bufferSize = BatchDefine.WRITER_BUFFER_SIZE_DEFAULT_KB * 1024;
	private long maxSize = -1;

	private File outputFile = null;
	private BufferedWriter bufferedWriter = null;
	private LineAggregator lineAggregator = null;
	
	private boolean deleteEmpty;

	private int columnSize = 0;
	private String[] lineTokens = null;

	
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
	
	public void setBufferSize(int newSize) {
		this.bufferSize = newSize;
	}
	
	public void setDeleteEmpty(boolean deleteEmpty) {
		this.deleteEmpty = deleteEmpty;
	}
	
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public void setLineAggregator(LineAggregator lineAggregator) {
		this.lineAggregator = lineAggregator;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	
	public void setEncoding(String newEncoding) {
		this.encoding = newEncoding;
	}
	
	@Override
	public void clear() throws ClearFailedException {
		this.lineTokens = null;
		this.lineTokens = new String[this.columnSize];
	}
	
	public void write() throws Exception {

		FieldSet fieldSet = new DefaultFieldSet(lineTokens);
		bufferedWriter.write(lineAggregator.aggregate(fieldSet) + lineSeparator);
		
		increaseItemCount();
		clear();
		
	}
	
	public void write(Object vo) throws Exception {

		String[] tokens = null;
		
		try {
			tokens = (String[]) this.transform.encodeVo(vo, null, null);
			
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount() + 1 + " in resource="
					+ resource.getDescription(), ex);
		}
		
		FieldSet fieldSet = new DefaultFieldSet(tokens);
		bufferedWriter.write(lineAggregator.aggregate(fieldSet) + lineSeparator);
		
		increaseItemCount();
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
	public void flush() throws FlushFailedException {
		try {
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new FlushFailedException("Failed to flush to output file: " + outputFile, e);
		}
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

			CharsetEncoder encoder = Charset.forName(encoding).newEncoder();
			encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
			encoder.onMalformedInput(CodingErrorAction.REPLACE);
				
			byte[] repl = new byte[] {' '};
			if(encoder.isLegalReplacement(repl))
				encoder.replaceWith(repl);
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile), this.bufferSize);
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(bos, encoder));
			
		} catch(Exception e) {
			throw new ItemStreamException("Failed to initialize the writer", e);
		}
	}
	
	@Override
	public void close(ExecutionContext executionContext) {
		
		if(this.bufferedWriter == null)
			return;

		try {
			this.bufferedWriter.close();
			
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
				
		} catch (IOException e) {
			throw new BatchRuntimeException("fail to close writer", e);
			
		} finally {
			this.bufferedWriter = null;
		}
	}

	/**
	 * raw APIs
	 */
	
	@Override
	public void setBytes(int index, byte[] bytes) throws Exception {
		this.lineTokens[index] = new String(bytes, this.encoding);
	}

	@Override
	public void setInt(int index, int value) throws Exception {
		this.lineTokens[index] = String.valueOf(value);
	}
	

	@Override
	public void setString(int index, String value) throws Exception {
		this.lineTokens[index] = value;
	}

	
	@Override
	public void setBigDecimal(int index, BigDecimal value) throws Exception {
		this.lineTokens[index] = value.toPlainString();
	}
	


	@Override
	public void setColumnSize(int[] columnSize) {
		this.columnSize = columnSize.length;
		this.lineTokens = new String[columnSize.length];
	}

	@Override
	public void setBoolean(int index, Boolean value) throws Exception {
		this.lineTokens[index] = value.toString();
		
	}

	@Override
	public void setShort(int index, Short value) throws Exception {
		this.lineTokens[index] = value.toString();
		
	}

	@Override
	public void setBigInteger(int index, BigInteger value) throws Exception {
		this.lineTokens[index] = value.toString();
		
	}

	@Override
	public void setLong(int index, Long value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setFloat(int index, Float value) throws Exception {
		this.lineTokens[index] = value.toString();		
	}

	@Override
	public void setDouble(int index, Double value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setDate(int index, Date value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setByte(int index, Byte value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setCharacter(int index, Character value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setTime(int index, Time value) throws Exception {
		this.lineTokens[index] = value.toString();
	}

	@Override
	public void setTimestamp(int index, Timestamp value) throws Exception {
		this.lineTokens[index] = value.toString();
	}
}
