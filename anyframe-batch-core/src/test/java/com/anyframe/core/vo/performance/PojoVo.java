package com.anyframe.core.vo.performance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PojoVo {

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public int getNumber2() {
		return number2;
	}

	public void setNumber2(int number2) {
		this.number2 = number2;
	}

	public int getNumber3() {
		return number3;
	}

	public void setNumber3(int number3) {
		this.number3 = number3;
	}

	public int getNumber4() {
		return number4;
	}

	public void setNumber4(int number4) {
		this.number4 = number4;
	}

	public BigDecimal getIncome2() {
		return income2;
	}

	public void setIncome2(BigDecimal income2) {
		this.income2 = income2;
	}

	public BigDecimal getIncome3() {
		return income3;
	}

	public void setIncome3(BigDecimal income3) {
		this.income3 = income3;
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
	public String toString() {
		return "PojoVo [name=" + name + ", number=" + number + ", income=" + income + "]";
	}

	public void fromMap(Map<String, Object> src) {
		this.name = (String) src.get("name");
		this.address = (String) src.get("address");
		this.address2 = (String) src.get("address2");
		this.number = (Integer) src.get("number");
		this.number2 = (Integer) src.get("number2");
		this.number3 = (Integer) src.get("number3");
		this.number4 = (Integer) src.get("number4");
		this.income = (BigDecimal) src.get("income");
		this.income2 = (BigDecimal) src.get("income2");
		this.income3 = (BigDecimal) src.get("income3");
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", this.name);
		map.put("address", this.address);
		map.put("address2", this.address2);
		map.put("number", this.number);
		map.put("number2", this.number2);
		map.put("number3", this.number3);
		map.put("number4", this.number4);
		map.put("income", this.income);
		map.put("income2", this.income2);
		map.put("income3", this.income3);

		return map;
	}
}
