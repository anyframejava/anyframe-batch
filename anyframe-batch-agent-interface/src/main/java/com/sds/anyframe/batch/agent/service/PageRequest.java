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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class PageRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Object parameter;
	private Object result;
	private Map<String, Object> requestMap = new HashMap<String, Object>();

	public long startPosition;
	public int pageNo;
	public int pageSize;
	public int startRowNumber;
	public int totalPageCount;
	public int totalRowNumber;
	public int readCount;
	public int rowLength;
	private int totalCount;

	public PageRequest(int startRowNumber, int pageSize, int pageNo) {
		this.startRowNumber = startRowNumber;
		this.pageSize = pageSize;
		this.pageNo = pageNo;
	}

	public int compareTo(Object object, int columinIndex) {
		return 0;
	}

	public PageRequest() {
	}

	public int getStartRowNumber() {
		return startRowNumber;
	}

	public void setStartRowNumber(int startRowNumber) {
		this.startRowNumber = startRowNumber;
	}

	public int getPageSize() {
		return pageSize;
	}
	
	public int getPageNo(){
		return pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void add(String key, Object object) {
		requestMap.put(key, object);
	}

	public Object get(String key) {
		return requestMap.get(key);
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object fileName) {
		this.parameter = fileName;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void setTotalCount(int size) {
		this.totalCount = size;
	}

	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("page No = ").append(pageNo).append(", pageSize = ")
				.append(pageSize).append(", startRowNumber = ").append(
						startRowNumber).append(", startPosition = ").append(
						startPosition).append(", totalPageCount = ").append(
						totalPageCount).append(", totalRowNumber = ").append(
						totalRowNumber).append(",  totalCount = ").append(
						totalCount).append(", parameter = ").append(
						parameter == null ? "" : parameter.toString()).append(
						", additional parameters = ").append(
						requestMap.toString());
		return builder.toString();
	}
}
