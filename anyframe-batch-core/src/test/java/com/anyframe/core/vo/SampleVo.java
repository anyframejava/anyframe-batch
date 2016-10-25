package com.anyframe.core.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.DefaultValue;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;
import com.anyframe.core.annotation.Scale;
import com.anyframe.core.vo.meta.PojoVo;

@LocalName("sampleVo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SampleVo extends AbstractVo implements Serializable {

	public static final String uPFieldDefaultValue = "defaultValue";

	@Length(10)
	@LocalName("이름")
	@NotNull(message = "hey what the hell are you doing?")
	private String name;

	@Length(5)
	@LocalName("나이")
	@NotNull
	private Integer age;

	@Length(5)
	@LocalName("몸무게")
	private BigInteger weight;

	@Length(20)
	@LocalName("금액")
	@Scale(2)
	private BigDecimal money;

	@Length(15)
	@LocalName("서브VO")
	private SubVo subVo;

	@Length(10)
	@ArraySize(5)
	@LocalName("리스트(스트링)")
	private List<String> listString;

	@Length(15)
	@ArraySize(3)
	@LocalName("리스트(SubVo)")
	private List<SubVo> listVo;

	@Length(10)
	@ArraySize(variable = true)
	@LocalName("가변길이 리스트(스트링)")
	private List<String> variableListString;

	@Length(15)
	@ArraySize(variable = true)
	@LocalName("가변길이 리스트(SubVo)")
	private List<SubVo> variableListVo;

	@Length(5)
	@LocalName("넘버")
	private int number;

	@Length(10)
	@ArraySize(2)
	@LocalName("어레이(스트링)")
	private String[] arrayString;

	@Length(15)
	@ArraySize(2)
	@LocalName("어레이(SubVo)")
	private SubVo[] arrayVo;

	@Length(10)
	@ArraySize(variable = true)
	@LocalName("가변길이 어레이(스트링)")
	private String[] variableArrayString;

	@Length(15)
	@ArraySize(variable = true)
	@LocalName("가변길이 어레이(SubVo)")
	private SubVo[] variableArrayVo;

	@Length(12)
	@DefaultValue(uPFieldDefaultValue)
	private String uPField;

	@Length(5)
	private String uPField2;

	@Length(1)
	private byte byteType;

	@Length(1)
	private Byte byteObjectType;

	@Length(1)
	private char charType;

	@Length(1)
	private Character charObjectType;

	@Length(155)
	private PojoVo pojoVo;

	public SampleVo() {
		super();
	}

	public SampleVo(AbstractVo vo) {
		super(vo);
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
		this.subVo = super.getValue("subVo");
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

	public byte getByteType() {
		return (Byte) super.getValue("byteType");
	}

	public void setByteType(byte byteType) {
		this.byteType = byteType;
		super.setValue("byteType", byteType);
	}

	public Byte getByteObjectType() {
		return super.getValue("byteObjectType");
	}

	public void setByteObjectType(Byte byteObjectType) {
		this.byteObjectType = byteObjectType;
		super.setValue("byteObjectType", byteObjectType);
	}

	public char getCharType() {
		return (Character) super.getValue("charType");
	}

	public void setCharType(char charType) {
		this.charType = charType;
		super.setValue("charType", charType);
	}

	public Character getCharObjectType() {
		return super.getValue("charObjectType");
	}

	public void setCharObjectType(Character charObjectType) {
		this.charObjectType = charObjectType;
		super.setValue("charObjectType", charObjectType);
	}

	public List<String> getVariableListString() {
		this.variableListString = super.getValue("variableListString");
		return variableListString;
	}

	public void setVariableListString(List<String> variableListString) {
		super.setValue("variableListString", variableListString);
		this.variableListString = variableListString;
	}

	public List<SubVo> getVariableListVo() {
		this.variableListVo = super.getValue("variableListVo");
		return variableListVo;
	}

	public void setVariableListVo(List<SubVo> variableListVo) {
		super.setValue("variableListVo", variableListVo);
		this.variableListVo = variableListVo;
	}

	public String[] getVariableArrayString() {
		this.variableArrayString = super.getValue("variableArrayString");
		return variableArrayString;
	}

	public void setVariableArrayString(String[] variableArrayString) {
		super.setValue("variableArrayString", variableArrayString);
		this.variableArrayString = variableArrayString;
	}

	public SubVo[] getVariableArrayVo() {
		this.variableArrayVo = super.getValue("variableArrayVo");
		return variableArrayVo;
	}

	public void setVariableArrayVo(SubVo[] variableArrayVo) {
		super.setValue("variableArrayVo", variableArrayVo);
		this.variableArrayVo = variableArrayVo;
	}

	public PojoVo getPojoVo() {
		return super.getValue("pojoVo");
	}

	public void setPojoVo(PojoVo pojoVo) {
		this.pojoVo = pojoVo;
		super.setValue("pojoVo", pojoVo);
	}

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

	public SubVo[] getArrayVo() {
		this.arrayVo = (SubVo[]) super.getValue("arrayVo");
		return arrayVo;
	}

	public void setArrayVo(SubVo[] arrayVo) {
		this.arrayVo = arrayVo;
		super.setValue("arrayVo", arrayVo);
	}

	public String[] getArrayString() {
		this.arrayString = (String[]) super.getValue("arrayString");
		return arrayString;
	}

	public void setArrayString(String[] arrayString) {
		this.arrayString = arrayString;
		super.setValue("arrayString", arrayString);
	}

	public int getNumber() {
		this.number = (Integer) super.getValue("number");
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		super.setValue("number", number);
	}
}
