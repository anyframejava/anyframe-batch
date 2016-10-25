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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.batch.item.ClearFailedException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.FlushFailedException;
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
import com.sds.anyframe.batch.charset.EncodingException;
import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Align;
import com.sds.anyframe.batch.charset.ICharsetEncoder.Padding;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.AbstractItemReadWriter;
import com.sds.anyframe.batch.infra.file.support.AnyframeBufferedInputStream;
import com.sds.anyframe.batch.vo.CoreContext;
import com.sds.anyframe.batch.vo.meta.VoMeta;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteUpdater extends AbstractItemReadWriter {

	private static final Object NOT_NULL = new Object();
	
	private String encoding = BatchDefine.DEFAULT_ENCODING;
	
	private ICharsetDecoder decoder = AbstractDecoder.newCharsetDecoder(encoding);
	private ICharsetEncoder encoder = AbstractEncoder.newCharsetEncoder(encoding);

	private Resource resource = null;

	private RandomAccessFile rFile = null;
	
	private boolean useVO = false;
	private Class<?> voClass = null;
	
	private int lineLength = 0;
	private byte[] lineBuffer = null;
	private byte[] lineSeparator = null;
	
	private int[] columnLengths = null;
	private int[] columnOffsets = null;
	
	private boolean bTrim = true;
	private boolean deleteEmpty;
	private long currentPoint = 0;
	private long nextPoint = 0;

	
	
	public void setTrim(boolean bTrim) {
		this.bTrim  = bTrim;
		this.transform.setTrim(bTrim);
	}
	
	public void setBufferSize(int size) {
	}
	
	public void setFixed(boolean bFixed){
		if(bFixed == false)
			throw new BatchRuntimeException("it should read fixed block");
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
		this.encoder = null;
		this.decoder = null;
		
		this.encoding = encoding;
		this.encoder = AbstractEncoder.newCharsetEncoder(encoding);
		this.decoder = AbstractDecoder.newCharsetDecoder(encoding);
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
		
		if (rFile == null)
			throw new ReaderNotOpenException("Reader must be open before it can be read.");
		
		try {
			rFile.seek(nextPoint);
			
			if(rFile.read(lineBuffer) != lineLength)
				return null;

			// skip CR/LF
			rFile.skipBytes(this.lineSeparator.length);
			
			this.currentPoint = nextPoint;
			this.nextPoint = rFile.getFilePointer();
			
		}catch (Exception e) {
			throw new DecodingException("decoding error at line: "
					+ (getItemCount()+1) + " in resource=" + resource.getDescription(), e);
		}
		
		increaseItemCount(); // read count 증가
		
		if(!useVO)
			return NOT_NULL;
		else {
			try {
				return this.transform.decodeVo(lineBuffer, this.voClass);
					
			} catch (Exception ex) {
				throw new DecodingException("decoding error at line: "
						+ getItemCount() + " in resource=" + resource.getDescription()
						+ ", input=[" + new String(lineBuffer, encoding) + "]", ex);
			}
		}
		

	}
	
	private void setLineByteSize(int voSize) {
		this.lineLength = voSize;
		this.lineBuffer = new byte[lineLength];
	}

	@Override
	public void open(ExecutionContext executionContext)	throws ItemStreamException {

		Assert.notNull(resource, "Input Resource must not be null");

		try {
			if (!resource.exists()) {
				throw new FileNotFoundException(resource.getFile().getAbsolutePath());
			}

			rFile = new RandomAccessFile(resource.getFile(), "rw");
			
			
			// 개행문자 유형을 체크하여  line seperator 를 설정함
			String readLine = rFile.readLine();
			if(readLine != null) {
				long filePointer = rFile.getFilePointer();
				
				rFile.seek(filePointer-1);
				byte byte1 = rFile.readByte();
				
				if(byte1 == '\r') {		// CR Only
					this.lineSeparator = new byte[]{'\r'};
					
				} else if(byte1 == '\n') {
					rFile.seek(filePointer-2);
					byte byte2 = rFile.readByte();
					
					if(byte2 == '\r')	// CRLF
						this.lineSeparator = new byte[]{'\r', '\n'};
					else		// LF Only
						this.lineSeparator = new byte[]{'\n'};
					
				} else {		// end with no CRLR. maybe just one line without CRLF. default LF	
					this.lineSeparator = new byte[]{'\n'};
				}
				
				rFile.seek(0);
			}
			
		} catch (Exception e) {
			throw new ItemStreamException("Failed to initialize the resource", e);
		}
		
	}

	@Override
	public void close(ExecutionContext executionContext) {
		
		if(this.rFile == null)
			return;
		
		try {	
			this.rFile.close();
			
			File file = this.resource.getFile();
			if(this.deleteEmpty && file.length() == 0)
				file.delete();

		} catch (IOException ioe) {
			throw new ItemStreamException("Unable to close the the ItemWriter", ioe);
		}
	}
	

	public void flush() throws FlushFailedException {
		
	}


	public void clear() throws ClearFailedException {
		this.lineBuffer = null;
		this.lineBuffer = new byte[lineLength];
	}
	
	

	@Override
	public long getTotalLineCount() throws IOException {
		AnyframeBufferedInputStream is = new AnyframeBufferedInputStream(resource.getInputStream());

		long lineCount = 0;
		while(is.readLine() != null)
			lineCount++;
			
		is.close();
		return lineCount;
	}


	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setDeleteEmpty(boolean deleteEmpty) {
		this.deleteEmpty = deleteEmpty;
	}


	public void write(Object vo) throws Exception {
		
		byte[] bytes = null;
		
		try {
			bytes = (byte[]) this.transform.encodeVo(vo, null, null);
			
		} catch (Exception ex) {
			throw new EncodingException("encoding error at line: "
					+ getItemCount()+1 + " in resource=" + resource.getDescription(), ex);
		}
		
		rFile.seek(this.currentPoint);
		
		rFile.write(bytes);
		rFile.write(lineSeparator);
		
	}
	

	public void write() throws Exception {

		rFile.seek(this.currentPoint);
		
		rFile.write(lineBuffer);
		rFile.write(lineSeparator);
		
		clear();
		
	}

	/**
	 * raw APIs
	 */


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
	

	public void setBytes(int index, byte[] bytes) throws Exception {
		int length = this.columnLengths[index];
		System.arraycopy(bytes, 0, lineBuffer, columnOffsets[index], length);
	}

	@Override
	public void setColumnSize(int[] columnLengths) {
		this.columnLengths = columnLengths;
		this.columnOffsets = new int[columnLengths.length];

		int bytesLength = 0;
		for (int i = 0; i < columnLengths.length; i++) {
			this.columnOffsets[i] = bytesLength;
			bytesLength += columnLengths[i];
		}
		
		setLineByteSize(bytesLength);
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws Exception {
		try {
			return decoder.decodeBigDecimal(lineBuffer, columnOffsets[index], columnLengths[index]);
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public int getInt(int index) throws Exception {
		try {
			return decoder.decodeInt(lineBuffer, columnOffsets[index], columnLengths[index]);
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public String getString(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return str;
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}
	

	@Override
	public byte[] getBytes(int index) throws Exception {
		int length = columnLengths[index];

		byte[] outbytes = new byte[length];
		System.arraycopy(lineBuffer, columnOffsets[index], outbytes, 0, length);

		return outbytes;
	}
	
	@Override
	public Boolean getBoolean(int index) throws Exception {
		
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Boolean.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Timestamp getTimestamp(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Timestamp.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Time getTime(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Time.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Character getCharacter(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
				char[] charArray = str.toCharArray();
				
			return charArray.length == 0 ? null : charArray[0];
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Byte getByte(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Byte.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Date getDate(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return java.sql.Date.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Double getDouble(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Double.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Float getFloat(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Float.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Long getLong(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Long.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public BigInteger getBigInteger(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return new BigInteger(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
	}

	@Override
	public Short getShort(int index) throws Exception {
		try {
			String str = decoder.decodeChar(lineBuffer, columnOffsets[index], columnLengths[index]);
			
			if(this.bTrim)
				str = str.trim();
			
			return Short.valueOf(str);
					
		} catch (Exception ex) {
			throw new DecodingException("decoding error at line: "
					+ getItemCount() + " offset:" + columnOffsets[index]
					+ " column length: " + columnLengths[index]
					+ " in resource=" + resource.getDescription() + ", input=["
					+ new String(lineBuffer, encoding) + "]", ex);
		}
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
