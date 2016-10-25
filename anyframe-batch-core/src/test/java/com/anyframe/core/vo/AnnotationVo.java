package com.anyframe.core.vo;

import java.math.BigDecimal;

import com.anyframe.core.annotation.DefaultValue;
import com.anyframe.core.annotation.LocalName;

@LocalName("sampleVo")
public class AnnotationVo extends AbstractVo {

	@DefaultValue("John")
	private String name;

	@DefaultValue("123")
	private int number;

	@DefaultValue("12.3")
	private BigDecimal money;

	@DefaultValue("1.1")
	private float rate;

	@DefaultValue("true")
	private boolean admin;

	public String getName() {
		name = super.getValue("name");
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		number =  (Integer)super.getValue("number");
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public BigDecimal getMoney() {
		money = super.getValue("money");
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public float getRate() {
		rate = (Float)super.getValue("rate");
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public boolean getAdmin() {
		admin = (Boolean)super.getValue("admin");
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
