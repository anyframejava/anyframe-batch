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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.util.ArrayUtil;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class AnyframeBufferedInputStream extends FilterInputStream {

	private static int defaultBufferSize = 1024*128;
	protected volatile byte buf[];
	private static final AtomicReferenceFieldUpdater<AnyframeBufferedInputStream, byte[]> bufUpdater = AtomicReferenceFieldUpdater
			.newUpdater(AnyframeBufferedInputStream.class, byte[].class, "buf");

	protected int count;
	protected int pos;
	protected int markpos = -1;
	protected int marklimit;
	
	private InputStream getInIfOpen() throws IOException {
		InputStream input = in;
		if (input == null)
			throw new IOException("Stream closed");
		return input;
	}

	private byte[] getBufIfOpen() throws IOException {
		byte[] buffer = buf;
		if (buffer == null)
			throw new IOException("Stream closed");
		return buffer;
	}

	public AnyframeBufferedInputStream(InputStream in) {
		this(in, defaultBufferSize);
	}

	public AnyframeBufferedInputStream(InputStream in, int size) {
		super(in);
		if (size <= 0) {
			throw new IllegalArgumentException("Buffer size <= 0");
		}
		buf = new byte[size];
	}

	private void fill() throws IOException {
		byte[] buffer = getBufIfOpen();
		if (markpos < 0)
			pos = 0; /* no mark: throw away the buffer */
		else if (pos >= buffer.length) /* no room left in buffer */
			if (markpos > 0) { /* can throw away early part of the buffer */
				int sz = pos - markpos;
				System.arraycopy(buffer, markpos, buffer, 0, sz);
				pos = sz;
				markpos = 0;
			} else if (buffer.length >= marklimit) {
				markpos = -1; /* buffer got too big, invalidate mark */
				pos = 0; /* drop buffer contents */
			} else { /* grow buffer */
				int nsz = pos * 2;
				if (nsz > marklimit)
					nsz = marklimit;
				byte nbuf[] = new byte[nsz];
				System.arraycopy(buffer, 0, nbuf, 0, pos);
				if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
					// Can't replace buf if there was an async close.
					// Note: This would need to be changed if fill()
					// is ever made accessible to multiple threads.
					// But for now, the only way CAS can fail is via close.
					// assert buf == null;
					throw new IOException("Stream closed");
				}
				buffer = nbuf;
			}
		count = pos;
		int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
		if (n > 0)
			count = n + pos;
	}

	public int read() throws IOException {
		if (pos >= count) {
			fill();
			if (pos >= count)
				return -1;
		}
		return getBufIfOpen()[pos++] & 0xff;
		// return buf[pos++] & 0xff;
	}
	
    /**
     * Binary 파일로부터 1줄을 읽는다. 한줄은 CR(\r)이나 LF(\n)로 끝나거나 또는 CR+LF로 끝난다.
     * 
     * @return     한줄의 byte 내용을 담은 Array. (개행문자를 포함하지 않는다)
     *             파일 끝에 도달한 경우 null을 반한홤
     *
     * @exception  IOException  If an I/O error occurs
     */
	public byte[] readLine() throws IOException {
		int readCount = 0;
		int length = 1024;
		byte[] buffer = new byte[length];
		
		for(;;) {
			if(pos >= count)
				fill();
			if(pos >= count) {
				if(readCount != 0)
					return ArrayUtil.copyOf(buffer, readCount);
				else
					return null;
			}
			
			for(; pos<count; pos++) {
				if(readCount >= length) {	// double up buffer
					length *= 2;
					buffer = ArrayUtil.copyOf(buffer, length);
				}
				
				if(buf[pos] == '\r') {	// check next LF and if exist, skip it
					pos++;
					
					if(pos >= count)
						fill();
					if(pos >= count)	// end of file
						return ArrayUtil.copyOf(buffer, readCount);
					
					if(buf[pos] == '\n')	// skip LF
						pos++;
					
					return ArrayUtil.copyOf(buffer, readCount);
					
				} else if (buf[pos] == '\n') {
					pos++;
					return ArrayUtil.copyOf(buffer, readCount);
				}
				buffer[readCount++] = buf[pos];
			}
		}
	}
	
	/**
	 * 파라메터로 전달된 버퍼({@code buffer})의 길이를 한줄 길이로 가정하여
	 * 파일로부터 한줄을  {@code buffer}로 읽는다.
	 * 
	 * @param buffer	파일로부터 한줄 내용을 담을 버퍼 (버퍼 사이즈는 한줄의 크기와 같아야 한다)
	 * @param skipCRLF	개행문자를 포함할지 여부. 개행문자를 포함하지 않을 경우에는 
	 * 					{@code buffer.length}의 데이터 이후에는 개행문자가 와야 하며
	 * 					그렇지 않은 경우 예외를 발생시킨다. 
	 * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the stream has been reached
	 * @throws IOException
	 */
	public int readLine(byte[] buffer, boolean skipCRLF) throws IOException {
		int readCnt = read(buffer, 0, buffer.length);
		
		if(skipCRLF) {
			if(pos >= count)
				fill();
			
			if(pos >= count)
				return readCnt;
			
			if(buf[pos] == '\r') {	// check next LF and if exist, skip it
				pos++;
				
				if(pos >= count)
					fill();
				if(pos >= count)
					return readCnt;
				
				if(buf[pos] == '\n')	// skip LF
					pos++;
				
			} else if(buf[pos] == '\n')
				pos++;
			else {
				int lineLen = buffer.length;
				throw new BatchRuntimeException(
						"A line should be terminated at " + lineLen +"(byte) with any one of a line feed ('\\n'), " +
						"a carriage return ('\\r'), or a carriage return followed immediately by a line feed.");
			}
		}
		return readCnt;
	}

	private int read1(byte[] b, int off, int len) throws IOException {
		int avail = count - pos;
		if (avail <= 0) {
			/*
			 * If the requested length is at least as large as the buffer, and
			 * if there is no mark/reset activity, do not bother to copy the
			 * bytes into the local buffer. In this way buffered streams will
			 * cascade harmlessly.
			 */
			if (len >= getBufIfOpen().length && markpos < 0) {
				return getInIfOpen().read(b, off, len);
			}
			fill();
			avail = count - pos;
			if (avail <= 0)
				return -1;
		}
		int cnt = (avail < len) ? avail : len;
		System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
		pos += cnt;
		return cnt;
	}

	public int read(byte b[], int off, int len) throws IOException {
		getBufIfOpen(); // Check for closed stream
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int n = 0;
		for (;;) {
			int nread = read1(b, off + n, len - n);
			if (nread <= 0)
				return (n == 0) ? nread : n;
			n += nread;
			if (n >= len)
				return n;
			// if not closed but no bytes available, return
			InputStream input = in;
			if (input != null && input.available() <= 0)
				return n;
		}
	}

	public long skip(long n) throws IOException {
		getBufIfOpen(); // Check for closed stream
		if (n <= 0) {
			return 0;
		}
		long avail = count - pos;

		if (avail <= 0) {
			// If no mark position set then don't keep in buffer
			if (markpos < 0)
				return getInIfOpen().skip(n);

			// Fill in buffer to save bytes for reset
			fill();
			avail = count - pos;
			if (avail <= 0)
				return 0;
		}

		long skipped = (avail < n) ? avail : n;
		pos += skipped;
		return skipped;
	}

	public int available() throws IOException {
		return getInIfOpen().available() + (count - pos);
	}

	public void mark(int readlimit) {
		marklimit = readlimit;
		markpos = pos;
	}

	public void reset() throws IOException {
		getBufIfOpen(); // Cause exception if closed
		if (markpos < 0)
			throw new IOException("Resetting to invalid mark");
		pos = markpos;
	}

	public boolean markSupported() {
		return true;
	}

	public void close() throws IOException {
		byte[] buffer;
		while ((buffer = buf) != null) {
			if (bufUpdater.compareAndSet(this, buffer, null)) {
				InputStream input = in;
				in = null;
				if (input != null)
					input.close();
				return;
			}
			// Else retry in case a new buf was CASed in fill()
		}
	}
}
