package com.anyframe.core.vo.meta;

import java.util.List;

import com.anyframe.core.vo.AbstractVo;

/**
 * 
 * @stereotype VO
 * @author anonymous
 */
public class OtherCircularVo extends AbstractVo {

	
	private String name;

	
	private String age;

	private List<CircularVo> list;

	private CircularVo[] array;

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

	public List<CircularVo> getList() {
		this.list = super.getValue(2);
		return list;
	}

	public void setList(List<CircularVo> list) {
		super.setValue(2, list);
		this.list = list;
	}

	public CircularVo[] getArray() {
		this.array = super.getValue(3);
		return array;
	}

	public void setArray(CircularVo[] array) {
		super.setValue(3, array);
		this.array = array;
	}
	
	
}