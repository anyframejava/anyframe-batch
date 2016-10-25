package com.anyframe.core.vo.meta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

import com.anyframe.core.annotation.Length;
import com.anyframe.core.vo.AbstractVo;

/**
 * 샘플용 VO 클래스
 * @author koyoonwon
 */
public class EmployeeVo extends AbstractVo {

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
     * 사원 이름
     */
	@Length(30) 
	private BigInteger no3;

    /**
     * 사원 주소
     */
	@Length(30) 
	private Date date1;

    /**
     * 기타 메모사항
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
		this.no1 = (Integer)super.getValue(0);
		return this.no1;
	}

	public void setNo1(int no1) {
        super.setValue(0, no1);
		this.no1 = no1;
	}
	
	public BigDecimal getNo2() {
		this.no2 = super.getValue(1);
		return this.no2;
	}

	public void setNo2(BigDecimal no2) {
        super.setValue(1, no2);
		this.no2 = no2;
	}
	
	public BigInteger getNo3() {
		this.no3 = super.getValue(2);
		return this.no3;
	}

	public void setNo3(BigInteger no3) {
        super.setValue(2, no3);
		this.no3 = no3;
	}
	
	public Date getDate1() {
		this.date1 = super.getValue(3);
		return this.date1;
	}

	public void setDate1(Date date1) {
        super.setValue(3, date1);
		this.date1 = date1;
	}
	
	public Timestamp getTimestamp1() {
		this.timestamp1 = super.getValue(4);
		return this.timestamp1;
	}

	public void setTimestamp1(Timestamp timestamp1) {
        super.setValue(4, timestamp1);
		this.timestamp1 = timestamp1;
	}
	
	public String getName() {
		this.name = super.getValue(5);
		return this.name;
	}

	public void setName(String name) {
        super.setValue(5, name);
		this.name = name;
	}
	
	public String getAddress() {
		this.address = super.getValue(6);
		return this.address;
	}

	public void setAddress(String address) {
        super.setValue(6, address);
		this.address = address;
	}
	
	public String getDescription() {
		this.description = super.getValue(7);
		return this.description;
	}

	public void setDescription(String description) {
        super.setValue(7, description);
		this.description = description;
	}
	
}