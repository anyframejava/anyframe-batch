package com.anyframe.core.vo.meta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;
import com.anyframe.core.annotation.Scale;
import com.anyframe.core.vo.SubVo;

@LocalName("sampleVo")
public class AVO extends BVO {
	
	@Length(10) @LocalName("이름") @NotNull(message="hey what the hell are you doing?")
	private String name;
	
	@Length(5) @LocalName("나이") @NotNull
	private Integer age;
	
	@Length(5) @LocalName("몸무게")
	private BigInteger weight;
	
	@Length(10) @LocalName("금액") @Scale(2)
	private BigDecimal money;

	@Length(15) @LocalName("서브VO")
	private SubVo subVo;
	
	@Length(10) @ArraySize(5) @LocalName("리스트(스트링)")
	private List<String> listString;
	
	@Length(15) @ArraySize(3) @LocalName("리스트(VO)")
	private List<SubVo> listVo;

	@Length(5) @LocalName("넘버")
	private int number;
	
	@Length(5) @ArraySize(2) @LocalName("어레이(스트링)")
	private String[] arrayString;
	
	@Length(15) @ArraySize(2) @LocalName("리스트(subVo)")
	private SubVo[] subVos;
	
	@Length(5)
	private String uPField;
	
	@Length(5)
	private String uPField2;
	
	public String getUPField2() {
		return super.getValue("uPField2");
	}

	public void setUPField2(String uPField2) {
		this.uPField2 = uPField2;
		super.setValue("uPField2", uPField2);
	}

	public String getuPField() {
		return super.getValue("uPField");
	}

	public void setuPField(String uPField) {
		this.uPField = uPField;
		super.setValue("uPField", uPField);
	}

	public SubVo[] getSubVos() {
		this.subVos = (SubVo[])super.getValue("subVos");
		return subVos;
	}

	public void setSubVos(SubVo[] subVos) {
		this.subVos = subVos;
		super.setValue("subVos", subVos);
	}

	public String[] getArrayString() {
		this.arrayString = (String[])super.getValue("arrayString");
		return arrayString;
	}

	public void setArrayString(String[] arrayString) {
		this.arrayString = arrayString;
		super.setValue("arrayString", arrayString);
	}

	
	public int getNumber() {
		this.number = (Integer)super.getValue("number");
		return number;
	}

	
	public void setNumber(int number) {
		this.number = number;
		super.setValue("number", number);
	}

	public AVO() {
		super();
	}
	
	@LocalName("getName")
	public String getName() {
		this.name = super.getValue(0);
		return name;
	}

	public void setName(String name) {
		this.name = name;
		super.setValue(0, name);
	}

	public Integer getAge() {
		this.age = super.getValue("age");
		return age;
	}

	public void setAge(Integer age) {
		super.setValue("age", age);
		this.age = age;
	}

	public BigDecimal getMoney() {
		this.money = super.getValue("money");
		return money;
	}

	public void setMoney(BigDecimal money) {
		super.setValue("money", money);
		this.money = money;
	}

	public SubVo getSubVo() {
		this.subVo = super.getValue(3);
		return subVo;
	}

	public void setSubVo(SubVo subVo) {
		super.setValue("subVo", subVo);
		this.subVo = subVo;
	}
	
	public List<String> getListString() {
		this.listString = super.getValue("listString");
		return listString;
	}

	public void setListString(List<String> listString) {
		super.setValue("listString", listString);
		this.listString = listString;
	}

	public List<SubVo> getListVo() {
		this.listVo = super.getValue("listVo");
		return listVo;
	}

	public void setListVo(List<SubVo> listVo) {
		super.setValue("listVo", listVo);
		this.listVo = listVo;
	}

	public BigInteger getWeight() {
		this.weight = super.getValue("weight");
		return weight;
	}

	public void setWeight(BigInteger weight) {
		this.weight = weight;
		super.setValue("weight", weight);
	}
}
