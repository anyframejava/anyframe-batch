package com.anyframe.core.vo.meta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;
import com.anyframe.core.vo.AbstractVo;

/**
 * 
 * @author koyoonwon
 */
public class EmployeeListVo extends AbstractVo {

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
		this.employeeVoList = super.getValue(0);
		return this.employeeVoList;
	}

	/**
	 * 리스트VO Setter Method
	 * 
	 * @param List 리스트VO
	 */
	@LocalName("리스트VO Setter Method")
	public void setEmployeeVoList(List<EmployeeVo> employeeVoList) {
	    super.setValue(0, employeeVoList);
		this.employeeVoList = employeeVoList;
	}
	
	public String getDesc() {
		this.desc = super.getValue(1);
		return this.desc;
	}

	public void setDesc(String desc) {
        super.setValue(1, desc);
		this.desc = desc;
	}
	
	public int getNumber() {
		this.number = (Integer)super.getValue(2);
		return this.number;
	}

	public void setNumber(int number) {
        super.setValue(2, number);
		this.number = number;
	}
	
	public BigDecimal getBigdecimal() {
		this.bigdecimal = super.getValue(3);
		return this.bigdecimal;
	}

	public void setBigdecimal(BigDecimal bigdecimal) {
        super.setValue(3, bigdecimal);
		this.bigdecimal = bigdecimal;
	}
	
	public BigInteger getBiginteger() {
		this.biginteger = super.getValue(4);
		return this.biginteger;
	}

	public void setBiginteger(BigInteger biginteger) {
        super.setValue(4, biginteger);
		this.biginteger = biginteger;
	}
	
	public Date getDate() {
		this.date = super.getValue(5);
		return this.date;
	}

	public void setDate(Date date) {
        super.setValue(5, date);
		this.date = date;
	}
	
	public Timestamp getTimestamp() {
		this.timestamp = super.getValue(6);
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
        super.setValue(6, timestamp);
		this.timestamp = timestamp;
	}
	
	public EmployeeVo getEmployeeVo() {
		this.employeeVo = super.getValue(7);
		return this.employeeVo;
	}

	public void setEmployeeVo(EmployeeVo employeeVo) {
        super.setValue(7, employeeVo);
		this.employeeVo = employeeVo;
	}
	
}