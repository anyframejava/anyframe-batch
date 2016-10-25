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
package com.sds.anyframe.batch.agent.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.utils.ByteBufferHelper;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class SamViewer extends Page {
	private static final int _1024 = 1024;
	private final Logger logger = Logger.getLogger(SamViewer.class);
	public static final String SERVICE_NAME = "samViewer";
	
	@Override
	public PageRequest getBottomPage(PageRequest request) throws Exception {
		request.setResult(null);

		setUp(request);
		
		request.startRowNumber = request.totalRowNumber >= request.pageSize ? (request.totalPageCount - 1)
				* request.pageSize
				: 0;
		
		request.pageNo = request.totalPageCount;
		
		int size = request.rowLength * request.pageSize;
		long position = (long)request.rowLength * (long)request.startRowNumber;
		
		byte[] result = ByteBufferHelper.readByteBuffer((String) request.getParameter(), size, position);
		
		request.setResult(result);
		
		return request;
	}

	private void setUp(PageRequest request) throws Exception {
		String fileName =  (String) request.getParameter();
		
		File file = new File(fileName);
		if (!file.exists()) {
			String msg = "The requested file does not exist[" +fileName + "]";
			
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		
		InputStream reader = null;
		try {
			reader = new FileInputStream(file);
			
			int length = getLengthOfLine(reader);
			if(length == 0) {
				throw new Exception("The SAM file is empty.");
			}
			
			request.rowLength = length;
		} catch (Exception e) {
			throw e;
		} finally {
			if (reader != null)
				reader.close();
		}
		
		request.totalRowNumber = (int) (file.length() / request.rowLength);
		request.totalPageCount = request.totalRowNumber % request.pageSize == 0 ? request.totalRowNumber
				/ request.pageSize
				: request.totalRowNumber / request.pageSize + 1;
	}
	
	@Override
	public PageRequest getPage(PageRequest request) throws Exception {
		logger.info("SAM File Service has been requested with following options:"
				+ request.toString());
		
		setUp(request); 
		
		int size = request.rowLength * request.pageSize;
		long position = (long)request.rowLength * (long)request.startRowNumber;
		
		byte[] result = ByteBufferHelper.readByteBuffer((String) request.getParameter(), size, position);
		
		request.setResult(result);
		return request;
	}

	private int getLengthOfLine(InputStream reader) throws Exception {
		int available = reader.available();
		
		if(available == 0)
			throw new Exception("The SAM file is empty.");
		
		int length = 0;
		byte[] buffer = new byte[_1024];
		
		int toRead = getToRead(available);
		
		int read = 0;
		
		String msg = "This SAM file does not have a line feed sequence for a line.";
		
		while((read = reader.read(buffer, 0, toRead)) > -1) {
			if(read == 0)
				throw new Exception(msg);
			
			for(int i =0;i<read;i++) {
				if(buffer[i] != '\n')
					length ++;
				else
					return (length+1); //must include this '\n' for count 
			}
			
			available = reader.available();
			if(available == 0)
				throw new Exception(msg);
			
			toRead = getToRead(available);
		}
		
		return length;
	}

	private int getToRead(int available) {
		return available > _1024 ? _1024 : available;
	}
	
}
