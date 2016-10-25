package com.sds.anyframe.batch.vo.transform;

import java.util.List;

import com.sds.anyframe.batch.charset.ICharsetDecoder;
import com.sds.anyframe.batch.charset.ICharsetEncoder;
import com.sds.anyframe.batch.vo.meta.FieldMeta;

public interface Transform {

	public Object decodeVo(Object rawData, Class<?> voClass);
	
	public Object encodeVo(Object vo, Object target, List<String> parameterNames);

	public Object decodeField(Object rawData, FieldMeta fieldMeta);
	
	public void encodeField(Object value, FieldMeta fieldMeta, Object target);

	public void setEncoder(ICharsetEncoder encoder);
	
	public void setDecoder(ICharsetDecoder decoder);
	
	public void setTrim(boolean bTrim);

	public void clear();
	
}
