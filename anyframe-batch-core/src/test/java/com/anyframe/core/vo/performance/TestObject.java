package com.anyframe.core.vo.performance;

import java.util.Map;

public class TestObject {

	private String field1 = null;
	private String field2 = null;
	private String field3 = null;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public void fromMap(Map<String, Object> src) {
		this.field1 = (String) src.get("field1");
		this.field2 = (String) src.get("field2");
		this.field3 = (String) src.get("field3");

	}

}
