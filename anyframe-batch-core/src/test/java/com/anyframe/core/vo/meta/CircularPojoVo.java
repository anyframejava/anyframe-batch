package com.anyframe.core.vo.meta;

import java.util.List;

/**
 * 
 * @stereotype VO
 * @author anonymous
 */
public class CircularPojoVo{

	
	private String name;

	
	private String age;

	private List<CircularPojoVo> list;

	private CircularPojoVo[] array;

	private CircularPojoVo self;
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAge() {
		return this.age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public List<CircularPojoVo> getList() {
		return list;
	}

	public void setList(List<CircularPojoVo> list) {
		this.list = list;
	}

	public CircularPojoVo getSelf() {
		return self;
	}

	public void setSelf(CircularPojoVo self) {
		this.self = self;
	}

	public CircularPojoVo[] getArray() {
		return array;
	}

	public void setArray(CircularPojoVo[] array) {
		this.array = array;
	}
	
	
}