package com.anyframe.core.vo;

import com.anyframe.core.annotation.ArraySize;
import com.anyframe.core.annotation.Length;
import com.anyframe.core.annotation.LocalName;

@LocalName("sampleVo")
public class PrimitiveVo extends AbstractVo {

	@Length(1) @ArraySize(2)
	private byte[] bytes;
	
	public byte[] getBytes() {
		Object value = super.getValue("bytes");
		bytes = (byte[]) value;
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		super.setValue("bytes", bytes);
		this.bytes = bytes;
	}
}
