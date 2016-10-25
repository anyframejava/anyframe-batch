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

// ##### bonobono : for Batch Program Wizard
package com.sds.anyframe.batch.manager.model;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ItemHandlerInfo implements Comparable<ItemHandlerInfo>{

	private String itemHandler;
	private String itemVoClassQ;
	private String itemVoClass;
	private String itemVoClassVar;
	private String itemType;
	private String itemQueryId;
	private String itemIoType;
	
	public String getItemHandler() {
		return itemHandler;
	}
	public void setItemHandler(String itemHandler) {
		this.itemHandler = itemHandler;
	}
	public String getItemVoClassQ() {
		return itemVoClassQ;
	}
	public void setItemVoClassQ(String itemVoClassQ) {
		this.itemVoClassQ = itemVoClassQ;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getItemQueryId() {
		return itemQueryId;
	}
	public void setItemQueryId(String itemQueryId) {
		this.itemQueryId = itemQueryId;
	}
	public void setItemIoType(String itemIoType) {
		this.itemIoType = itemIoType;
	}
	public String getItemIoType() {
		return itemIoType;
	}
	public void setItemVoClass(String itemVoClass) {
		this.itemVoClass = itemVoClass;
	}
	public String getItemVoClass() {
		return itemVoClass;
	}
	public void setItemVoClassVar(String itemVoClassVar) {
		this.itemVoClassVar = itemVoClassVar;
	}
	public String getItemVoClassVar() {
		return itemVoClassVar;
	}
	
	/**
	 *  주의 : 본 method는 x.compareTo(y)==0) == (x.equals(y))를 만족하지 않는다.
	 */
	public int compareTo(ItemHandlerInfo o) {
		if(!this.itemIoType.equals(o.itemIoType)) {
			return this.itemIoType.compareTo(o.itemIoType);
		} else {
			return this.itemHandler.compareTo(o.itemHandler);
		}
	}
	
}
