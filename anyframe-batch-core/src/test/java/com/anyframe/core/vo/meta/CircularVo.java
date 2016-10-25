package com.anyframe.core.vo.meta;

import java.util.List;

import com.anyframe.core.vo.AbstractVo;

/**
 * 
 * @stereotype VO
 * @author anonymous
 */
public class CircularVo extends AbstractVo {

	private String name;

	private String age;

	private List<OtherCircularVo> list;

	private CircularVo self;

	private CircularVo self2;

	private OtherCircularVo[] array;

	public String getName() {
		this.name = super.getValue(0);
		return this.name;
	}

	public void setName(String name) {
		super.setValue(0, name);
		this.name = name;
	}

	public String getAge() {
		this.age = super.getValue(1);
		return this.age;
	}

	public void setAge(String age) {
		super.setValue(1, age);
		this.age = age;
	}

	public List<OtherCircularVo> getList() {
		this.list = super.getValue(2);
		return list;
	}

	public void setList(List<OtherCircularVo> list) {
		super.setValue(2, list);
		this.list = list;
	}

	public CircularVo getSelf() {
		this.self = super.getValue(3);
		return self;
	}

	public void setSelf(CircularVo self) {
		super.setValue(3, self);
		this.self = self;
	}

	public CircularVo getSelf2() {
		this.self2 = super.getValue(4);
		return self2;
	}

	public void setSelf2(CircularVo self) {
		super.setValue(4, self2);
		this.self2 = self;
	}

	public OtherCircularVo[] getArray() {
		this.array = super.getValue(5);
		return array;
	}

	public void setArray(OtherCircularVo[] array) {
		super.setValue(4, array);
		this.array = array;
	}

}