package com.anyframe.core.vo;

import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;
import com.anyframe.core.vo.AbstractVo;

public class SubVo extends AbstractVo {

	@LocalName("이름")
	@Length(10)
	private String name;

	@LocalName("나이")
	@Length(5)
	private Integer age;

	public String getName() {
		this.name = super.getValue(0);
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		super.setValue(0, name);
	}

	public Integer getAge() {
		this.age = super.getValue(1);
		return this.age;
	}

	public void setAge(Integer age) {
		this.age = age;
		super.setValue(1, age);
	}
	
	
}
