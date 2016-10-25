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

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.utils.ByteBufferHelper;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class VsamViewer extends Page {
	private final Logger logger = Logger.getLogger(VsamViewer.class);
	
	public static final String SERVICE_NAME = "vsamViewer";
	
	@Override
	public PageRequest getPage(PageRequest request) throws Exception {
		
		logger.info("VSAM File Service has been requested with following options:"
				+ request.toString());

		request.setResult(null);
		String fileName = (String) request.getParameter();

		byte[] result = ByteBufferHelper.readByteBuffer(fileName, request.pageSize,
				(int)request.startPosition);

		request.setResult(result);
		return request;
	}

	@Override
	public PageRequest getTopPage(PageRequest request) throws Exception {
		request.pageNo = 1;
		request.startPosition = 0L;
		return getPage(request);
	}

	@Override
	public PageRequest getBottomPage(PageRequest request) throws Exception {
		request.setResult(null);

		String fileName = (String) request.getParameter();
		File file = new File(fileName);
		if (!file.exists()) {
			String msg = "The requested file does not exist[" + fileName + "]";
			logger.error(msg);
			throw new RuntimeException(msg);
		}

		request.totalPageCount = (int) (file.length() % request.pageSize == 0 ? file
				.length()
				/ request.pageSize
				: file.length() / request.pageSize + 1);

		request.startPosition = file.length() >= request.pageSize ? (request.totalPageCount - 1)
				* request.pageSize
				: 0;

		request.pageNo = request.totalPageCount;
		return getPage(request);
	}

	@Override
	public PageRequest next(PageRequest request) throws Exception {
		request.setResult(null);

		long startPosition = request.startPosition + request.pageSize;
		String fileName = (String) request.getParameter();
		File file = new File(fileName);

		long length = file.length();

		if (startPosition > length)
			return request;

		request.startPosition = startPosition;
		request.pageNo++;
		return getPage(request);
	}

	@Override
	public PageRequest previous(PageRequest request) throws Exception {
		request.setResult(null);

		long startPosition = request.startPosition - request.pageSize;
		if (startPosition < 0)
			return request;

		request.startPosition = startPosition;
		request.pageNo--;
		return getPage(request);
	}

	@Override
	public PageRequest pageByNo(PageRequest request) throws Exception {
		request.setResult(null);
		long startPosition = (request.pageNo - 1) * request.pageSize;

		String fileName = (String) request.getParameter();
		File file = new File(fileName);

		if (startPosition > file.length())
			return request;

		request.startPosition = startPosition;

		return getPage(request);
	}

}
