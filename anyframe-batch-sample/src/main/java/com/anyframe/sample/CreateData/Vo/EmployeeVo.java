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

package com.anyframe.sample.CreateData.Vo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.sds.anyframe.batch.annotation.Length;

/**
 * 샘플용 VO 클래스
 * @author koyoonwon
 */

public class EmployeeVo {

    /**
     * 사원 번호
     */
	@Length(20) 
	private int no1;

    /**
     * 사원 번호
     */
	@Length(20) 
	private BigDecimal no2;

    /**
     * 사원 번호
     */
	@Length(30) 
	private BigDecimal no3;

    /**
     * 날짜
     */
	@Length(30) 
	private Date date1;

    /**
     * 시간
     */
	@Length(30) 
	private Timestamp timestamp1;

    /**
     * 사원 이름
     */
	@Length(30) 
	private String name;

    /**
     * 사원 주소
     */
	@Length(30) 
	private String address;

    /**
     * 기타 메모사항
     */
	@Length(30) 
	private String description;


	public int getNo1() {
		return this.no1;
	}

	public void setNo1(int no1) {
		this.no1 = no1;
	}
	
	public BigDecimal getNo2() {
		return this.no2;
	}

	public void setNo2(BigDecimal no2) {
		this.no2 = no2;
	}
	
	public BigDecimal getNo3() {
		return this.no3;
	}

	public void setNo3(BigDecimal no3) {
		this.no3 = no3;
	}
	
	public Date getDate1() {
		return this.date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	
	public Timestamp getTimestamp1() {
		return this.timestamp1;
	}

	public void setTimestamp1(Timestamp timestamp1) {
		this.timestamp1 = timestamp1;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
