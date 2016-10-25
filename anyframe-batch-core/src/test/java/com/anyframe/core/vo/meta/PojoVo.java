package com.anyframe.core.vo.meta;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.Scale;
import com.anyframe.core.vo.SubVo;

public class PojoVo {

	@Length(10)
	String name;

	@Length(5)
	int number;

	@Length(10)
	@Scale(2)
	BigDecimal income;

	@Length(10)
	@ArraySize(5)
	List<String> list;

	@Length(10)
	@ArraySize(5)
	String[] names;

	@Length(15)
	@ArraySize(2)
	SubVo[] subVos;

	public SubVo[] getSubVos() {
		return subVos;
	}

	public void setSubVos(SubVo[] subVos) {
		this.subVos = subVos;
	}

	@Override
	public String toString() {
		return "PojoVo [name=" + name + ", number=" + number + ", income=" + income + ", list=" + list + ", names="
				+ Arrays.toString(names) + ", subVos=" + Arrays.toString(subVos) + "]";
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
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
	
	@Override
	public boolean equals(Object obj){
		return this.toString().equals(obj.toString());
	}
}
