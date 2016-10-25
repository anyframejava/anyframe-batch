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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jgroups.util.Streamable;
import org.jgroups.util.Util;


/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class Request implements Streamable {
    public static enum Type {LockResources, ResultFromMaster, ReleaseResources, SendClusteredCondition, GetClusteredCondition, GetPhysicalAddress, KillProcess};

    public Type type;
    private Object result;
	private TaskArgument argument;
    
    public TaskArgument getArgument() {
		return argument;
	}

	public void setArgument(TaskArgument argument) {
		this.argument = argument;
	}

	public Request() {
	}
	
	public Request(Type type, TaskArgument argument, Object result) {
        this.type=type;
        this.result=result;
        this.argument = argument;
    }

    public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(type.ordinal());
        try {
            Util.objectToStream(argument, out);
        }
        catch(Exception e) {
            IOException ex=new IOException("failed marshalling of task " + argument);
            ex.initCause(e);
            throw ex;
        }
        
        try {
            Util.objectToStream(result, out);
        }
        catch(Exception e) {
            IOException ex=new IOException("failed to marshall result object");
            ex.initCause(e);
            throw ex;
        }
    }

    public void readFrom(DataInputStream in) throws IOException, IllegalAccessException, InstantiationException {
        int tmp=in.readInt();
        
        Type[] values = Type.values();
        type = values[tmp];
        
        try {
            argument=(TaskArgument)Util.objectFromStream(in);
        }
        catch(Exception e) {
            InstantiationException ex=new InstantiationException("failed reading task from stream");
            ex.initCause(e);
            throw ex;
        }
        try {
            result=Util.objectFromStream(in);
        }
        catch(Exception e) {
            IOException ex=new IOException("failed to unmarshal result object");
            ex.initCause(e);
            throw ex;
        }
    }
}