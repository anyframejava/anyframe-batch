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

import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.jgroups.ReceiverAdapter;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class BatchReceiveAdapter extends ReceiverAdapter implements ChannelListener{

	
	public void channelClosed(Channel channel) {
		
	}

	
	public void channelConnected(Channel channel) {
	}

	
	public void channelDisconnected(Channel channel) {
	}

	
	public void channelReconnected(Address address) {
		
	}

	
	public void channelShunned() {
	}

}
