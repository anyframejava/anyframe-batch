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



/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public abstract class Page implements PageSupport {
	
	public abstract PageRequest getPage(PageRequest request) throws Exception;

	public PageRequest getTopPage(PageRequest request) throws Exception {
		request.startRowNumber = 0;
		request.pageNo = 1;
		return getPage(request);
	}

	public PageRequest getBottomPage(PageRequest request) throws Exception {
		request.setResult(null);
		request.startRowNumber = request.totalRowNumber >= request.pageSize ? (request.totalPageCount - 1) * request.pageSize : 0;
		request.pageNo = request.totalPageCount;
		return getPage(request);
	}

	public PageRequest next(PageRequest request) throws Exception {
		request.setResult(null);
		int start = (int) (request.startRowNumber+request.pageSize);
		if(start > request.totalRowNumber)
			return request;
		request.startRowNumber = start;
		request.pageNo = request.startRowNumber/request.pageSize+1;
		return getPage(request);
	}

	public PageRequest previous(PageRequest request) throws Exception {
		request.setResult(null);
		int start = (int) (request.startRowNumber-request.pageSize);
		if(start < 0)
			return request;
		request.startRowNumber = start;
		request.pageNo = request.startRowNumber/request.pageSize+1;
		return getPage(request);
	}
	
	public PageRequest pageByNo(PageRequest request) throws Exception {
		request.setResult(null);
		if(request.pageNo > request.totalPageCount)
			return request;
		request.startRowNumber = (request.pageNo-1)*request.pageSize;
		return getPage(request);
	}
}
