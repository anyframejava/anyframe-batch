/*                                                                           
 * Copyright 2010-2012 Samsung SDS Co., Ltd.                                 
 *                                                                           
 * Licensed under the Apache License, Version 2.0 (the "License");         
 * you may not use this file except in compliance with the License.          
 * You may obtain a copy of the License at                                   
 *                                                                           
 *     http://www.apache.org/licenses/LICENSE-2.0                            
 *                                                                           
 * Unless required by applicable law or agreed to in writing, software       
 * distributed under the License is distributed on an "AS IS" BASIS,       
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 * See the License for the specific language governing permissions and       
 * limitations under the License.                                            
 *                                                                           
 */                                                                          

package com.anyframe.core.vo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.anyframe.core.vo.meta.FieldMeta;
import com.anyframe.core.vo.meta.VoMeta;
import com.anyframe.core.vo.proxy.VoProxy;
import com.anyframe.core.vo.proxy.impl.DefaultVoProxy;

/**
 * AbstractVo use proxy to handle object of field in Value Object class.
 * Also this class provides override methods eqauls and hashCode as customized algorithm.
 * 
 * @author prever.kang
 *
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AbstractVo implements Externalizable {

	private VoProxy proxy;

	public AbstractVo() {
		this.proxy = new DefaultVoProxy(this.getClass());
	}

	public AbstractVo(AbstractVo vo) {
		this.proxy = new DefaultVoProxy(this.getClass());
		VoUtil.copy(vo, this);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		return (T) proxy.getValue(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(int index) {
		return (T) proxy.getValue(index);
	}

	public <T> void setValue(int index, T value) {
		proxy.setValue(index, value);
	}

	public <T> void setValue(String key, T value) {
		proxy.setValue(key, value);
	}

	@XmlTransient
	public VoProxy getProxy() {
		return this.proxy;
	}

	public void setProxy(VoProxy proxy) {
		this.proxy = proxy;
	}

	public String toString() {
		VoMeta info = this.proxy.getVoMeta();
		StringBuilder sb = new StringBuilder("{");

		int index = 0;
		for (FieldMeta field : info.getFields()) {
			sb.append(field.getFieldName()).append("[" + field.getLocalName() + "]").append("=")
					.append(proxy.getValue(index++)).append(", ");
		}

		sb.delete(sb.length() - 2, sb.length()).append("}");

		return sb.toString();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		writeObjects(out);
	}

	private void writeObjects(ObjectOutput out) throws IOException {
		Object[] values = proxy.getValues();
		for (Object obj : values)
			out.writeObject(obj);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		readObjects(in);
	}

	private void readObjects(ObjectInput in) throws IOException, ClassNotFoundException {
		proxy = new DefaultVoProxy(this.getClass());
		Object[] values = proxy.getValues();
		for (int i = 0; i < values.length; i++)
			values[i] = in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		writeObjects(out);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		readObjects(in);
	}

	/**
	 * AbstaractVo의 subclass가 Complex type의 property를 가지는 경우
	 * 해당 객체의 equals() 메소드가 override되지 않았다면 
	 * Object.equals()로 비교하기 때문에 equals() 결과가 false로 나올 수 있다.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		AbstractVo vo2 = (AbstractVo) obj;

		Object[] values1 = proxy.getValues();
		Object[] values2 = vo2.getProxy().getValues();

		if (values1 == null || values2 == null)
			return false;

		if (values1.length != values2.length)
			return false;

		int count = values1.length;

		for (int i = 0; i < count; i++) {
			Object obj1 = values1[i];
			Object obj2 = values2[i];

			if (obj1 != null && obj1.getClass().isArray()) {
				if (obj1 != null ? !Arrays.deepEquals((Object[]) obj1, (Object[]) obj2) : obj2 != null)
					return false;
				continue;
			}
			if (obj1 != null ? !obj1.equals(obj2) : obj2 != null)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int i = 1;
		Object[] values = getProxy().getValues();

		for (Object obj : values) {
			i = 31 * i + (obj != null ? obj.hashCode() : 0);
		}

		return i;
	}
}
