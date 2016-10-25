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
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.sds.anyframe.batch.annotation.ArraySize;
import com.sds.anyframe.batch.annotation.Length;
import com.sds.anyframe.batch.annotation.LocalName;

/**
 * 
 * @author koyoonwon
 */


public class EmployeeListVo {
    /**
     * 샘플용 VO 클래스
     */
	@LocalName("리스트VO") 
	@ArraySize(5)
	private List<EmployeeVo> employeeVoList;

	@Length(20) 
	private String desc;

	@Length(15) 
	private int number;

	@Length(15) 
	private BigDecimal bigdecimal;

	@Length(15) 
	private BigInteger biginteger;

	@Length(20) 
	private Date date;

	@Length(25) 
	private Timestamp timestamp;

    /**
     * 샘플용 VO 클래스
     */
	
	private EmployeeVo employeeVo;


	/**
	 * 리스트VO Getter Method
	 * 
	 * @return 리스트VO
	 */
	@LocalName("리스트VO Getter Method")
	public List<EmployeeVo> getEmployeeVoList() {
		return this.employeeVoList;
	}

	/**
	 * 리스트VO Setter Method
	 * 
	 * @param List 리스트VO
	 */
	@LocalName("리스트VO Setter Method")
	public void setEmployeeVoList(List<EmployeeVo> employeeVoList) {
		this.employeeVoList = employeeVoList;
	}
	
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public BigDecimal getBigdecimal() {
		return this.bigdecimal;
	}

	public void setBigdecimal(BigDecimal bigdecimal) {
		this.bigdecimal = bigdecimal;
	}
	
	public BigInteger getBiginteger() {
		return this.biginteger;
	}

	public void setBiginteger(BigInteger biginteger) {
		this.biginteger = biginteger;
	}
	
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public EmployeeVo getEmployeeVo() {
		return this.employeeVo;
	}

	public void setEmployeeVo(EmployeeVo employeeVo) {
		this.employeeVo = employeeVo;
	}
	
}
