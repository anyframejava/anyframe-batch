package com.anyframe.core.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;

@LocalName("variableSampleVo")
public class VariableSampleVo extends AbstractVo {

	@Length(10)
	@LocalName("이름")
	@NotNull(message = "hey what the hell are you doing?")
	private String name;

	@Length(5)
	@LocalName("나이")
	@NotNull
	private Integer age;

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

	public VariableSampleVo() {
		super();
	}

	public VariableSampleVo(AbstractVo vo) {
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

}
