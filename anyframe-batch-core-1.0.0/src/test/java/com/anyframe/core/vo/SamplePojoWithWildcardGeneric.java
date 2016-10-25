package com.anyframe.core.vo;

import java.math.BigDecimal;
import java.util.List;

public class SamplePojoWithWildcardGeneric {

	String name;
	int number;
	BigDecimal income;
	List<? extends SampleSubPojoWithWildcardGeneric> list;

	public List<? extends SampleSubPojoWithWildcardGeneric> getList() {
		return list;
	}

	public void setList(List<? extends SampleSubPojoWithWildcardGeneric> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

}
