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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public 
class AnyframeBufferedOutputStream extends FilterOutputStream {
    /**
     * The internal buffer where data is stored. 
     */
    protected byte buf[];

    protected int count;
    
    public AnyframeBufferedOutputStream(OutputStream out) {
	this(out, 8192*16);
    }

    public AnyframeBufferedOutputStream(OutputStream out, int size) {
	super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
	buf = new byte[size];
    }

    /** Flush the internal buffer */
    private void flushBuffer() throws IOException {
        if (count > 0) {
	    out.write(buf, 0, count);
	    count = 0;
        }
    }

    public void write(int b) throws IOException {
	if (count >= buf.length) {
	    flushBuffer();
	}
	buf[count++] = (byte)b;
    }

    public void write(byte b[], int off, int len) throws IOException {
	if (len >= buf.length) {
	    /* If the request length exceeds the size of the output buffer,
    	       flush the output buffer and then write the data directly.
    	       In this way buffered streams will cascade harmlessly. */
	    flushBuffer();
	    out.write(b, off, len);
	    return;
	}
	if (len > buf.length - count) {
	    flushBuffer();
	}
	System.arraycopy(b, off, buf, count, len);
	count += len;
    }

    public void flush() throws IOException {
        flushBuffer();
	out.flush();
    }
}
