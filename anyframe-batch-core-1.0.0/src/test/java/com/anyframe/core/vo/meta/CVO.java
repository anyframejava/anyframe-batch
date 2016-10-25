package com.anyframe.core.vo.meta;

import com.anyframe.core.vo.AbstractVo;


public class CVO extends AbstractVo{

	String name;

	public String getName() {
		return super.getValue("name");
	}

	public void setName(String name) {
		super.setValue("name", name);
	}
}
