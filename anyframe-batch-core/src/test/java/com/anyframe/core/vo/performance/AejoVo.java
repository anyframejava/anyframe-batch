package com.anyframe.core.vo.performance;

import java.math.BigDecimal;

import com.anyframe.core.vo.AbstractVo;

public class AejoVo extends AbstractVo {

	String name;
	String address;
	String address2;
	int number;
	int number2;
	int number3;
	int number4;
	BigDecimal income;
	BigDecimal income2;
	BigDecimal income3;

	public String getName() {
		this.name = super.getValue(0);
		return name;
	}

	public void setName(String name) {
		super.setValue(0, name);
		this.name = name;
	}

	public String getAddress() {
		this.address = super.getValue(1);
		return address;
	}

	public void setAddress(String address) {
		super.setValue(1, address);
		this.address = address;
	}

	public String getAddress2() {
		this.address2 = super.getValue(2);
		return address2;
	}

	public void setAddress2(String address2) {
		super.setValue(2, address2);
		this.address2 = address2;
	}

	public int getNumber() {
		this.number = (Integer) super.getValue(3);
		return number;
	}

	public void setNumber(int number) {
		super.setValue(3, number);
		this.number = number;
	}

	public int getNumber2() {
		this.number2 = (Integer) super.getValue(4);
		return number2;
	}

	public void setNumber2(int number2) {
		super.setValue(4, number2);
		this.number2 = number2;
	}

	public int getNumber3() {
		this.number3 = (Integer) super.getValue(5);
		return number3;
	}

	public void setNumber3(int number3) {
		super.setValue(5, number3);
		this.number3 = number3;
	}

	public int getNumber4() {
		this.number4 = (Integer) super.getValue(6);
		return number4;
	}

	public void setNumber4(int number4) {
		super.setValue(6, number4);
		this.number4 = number4;
	}

	public BigDecimal getIncome() {
		this.income = super.getValue(7);
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.setValue(7, income);
		this.income = income;
	}

	public BigDecimal getIncome2() {
		this.income2 = super.getValue(8);
		return income2;
	}

	public void setIncome2(BigDecimal income2) {
		this.setValue(8, income2);
		this.income2 = income2;
	}

	public BigDecimal getIncome3() {
		this.income3 = super.getValue(9);
		return income3;
	}

	public void setIncome3(BigDecimal income3) {
		this.setValue(9, income3);
		this.income3 = income3;
	}

	@Override
	public String toString() {
		return "PojoVo [name=" + name + ", number=" + number + ", income=" + income + "]";
	}
}
