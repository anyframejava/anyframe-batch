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
package com.sds.anyframe.batch.agent.cluster;

import java.io.Serializable;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class TaskArgument implements Serializable {

	private static final long serialVersionUID = -8844764234732557735L;

	private Object argument;

	private long id;

	private int cmd;
	
	public TaskArgument(long id) {
		this.id = id;
	}
	
	public Object getArgument() {
		return argument;
	}

	public void setArgument(Object argument) {
		this.argument = argument;
	}

	public TaskArgument(Object step, long id, int cmd) {
		this.argument = step;
		this.id = id;
		this.cmd = cmd;
	}

	public long getId() {
		return id;
	}

	public int getCmd() {
		return cmd;
	}

}
