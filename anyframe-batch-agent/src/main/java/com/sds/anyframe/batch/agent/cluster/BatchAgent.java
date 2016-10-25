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

import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.util.Util;

import com.sds.anyframe.batch.agent.cluster.Request.Type;
import com.sds.anyframe.batch.agent.concurrent.IResourceLockManager;
import com.sds.anyframe.batch.agent.management.ClusteredCondition;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.properties.AgentConfigurations;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.agent.utils.BatchUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class BatchAgent extends BatchReceiveAdapter {

	private static Logger log = Logger.getLogger(BatchAgent.class);
	
	private String PROPS = "agent-tcp.xml";

	Channel ch;

	Address systemAddr;

	private int rank = -1;

	final ConcurrentMap<Long, Entry> tasks = new ConcurrentHashMap<Long, Entry>();
	
	private static BatchAgent batchAgent;

	private ScheduledExecutorService scheduler;

	private IResourceLockManager resourceLockManager;

	AgentConfigurations configuration = AgentConfigurations.getConfigurations();

	public AgentConfigurations getConfiguration() {
		return configuration;
	}

	private MessageManager messageManager;
	
	public BatchAgent() throws Exception {
		try {

			InputStream resourceAsStream = ClassLoader
					.getSystemResourceAsStream(PROPS);
			if (resourceAsStream == null)
				throw new Exception("Can not load the file " + PROPS
						+ " to create JGroups channel");

			ch = MyChannelFactory.getChannel(PROPS);

			messageManager = new MessageManager(this);
			
			ch.setReceiver(this);
			ch.addChannelListener(this);
			ch.connect(configuration.getGroupName());
			
		} catch (ChannelException e) {
			throw new RuntimeException(e);
		}
		initMasterAgent();
	}

	void initMasterAgent() {
		View view = ch.getView();
		Vector<Address> members = view.getMembers();
		systemAddr = members.get(0);
	}

	public void stop() {
		log.info("Shutting down JGroups Channel");
		ch.disconnect();
		ch.close();
		if (scheduler != null)
			scheduler.shutdown();
	}

	@Override
	public void receive(Message msg) {
		try {
			Request req = (Request) Util.streamableFromByteBuffer(
					Request.class, msg.getRawBuffer(), msg.getOffset(), msg
							.getLength());
			Object result = null;
			
			switch (req.type) {
			case ResultFromMaster:
				Entry entry = tasks.get(req.getArgument().getId());

				if(entry  == null) {
					log.warn("Can not get a sequence id from message map. Probably there was timeout error and the sequence id might removed.");
					return;
				}
				
				log.info("Received a result from " + msg.getSrc()
						+ ", Command: " + Type.values()[entry.task.getCmd()].name());
				
				tasks.remove(req.getArgument().getId());
				entry.promise.setResult(req.getResult());
				break;
			case LockResources:
				log.info("Checking resource status requested by Primary Agent at "
						+ msg.getSrc());
				try {
					result = resourceLockManager.lockResources((Step) req
							.getArgument().getArgument());
				} catch (Exception e) {
					result = e;
				} finally {
					sendResult(msg.getSrc(), req.getArgument().getId(), result);
				}
				break;
			case ReleaseResources:
				log.info("Releasing resource requested by Primary Agent at "
						+ msg.getSrc());
				result = Boolean.valueOf(true);
				try {
					resourceLockManager.releaseResources((Step) req
							.getArgument().getArgument());
				} catch (Exception e) {
					result = e;
				} finally {
					sendResult(msg.getSrc(), req.getArgument().getId(), result);
				}
				break;
			case SendClusteredCondition:
			{
				result = Boolean.valueOf(true);
				ClusteredCondition clusteredCondtion = (ClusteredCondition)req.getArgument().getArgument();
				log.info("Clustered Condition received from "+ msg.getSrc() + " with condition [" + clusteredCondtion.toString() + "]");
				AgentManagement.getAgentCondition().setClusteredCondition(clusteredCondtion);
				sendResult(msg.getSrc(), req.getArgument().getId(), result);
			}
				break;
			case GetClusteredCondition:
			{
				log.info("Clustering information requests received from "+ msg.getSrc());
				ClusteredCondition clusteredCondition = AgentManagement.getAgentCondition().getClusteredCondition();
				
				clusteredCondition.setServerName(ch.getAddress().toString());
				sendResult(msg.getSrc(), req.getArgument().getId(), clusteredCondition);
			}
				break;
			case GetPhysicalAddress:
			{
				result = AgentUtils.getIp();
				log.info("GetPhysicalAddress called[ "+ result + "]");
				sendResult(msg.getSrc(), req.getArgument().getId(), result);
			}
				break;
			case KillProcess:
			{
				log.info("Killing message recevied from "+ msg.getSrc());
				KillMessage killMessage = (KillMessage) req.getArgument().getArgument();
				BatchUtils.killProcess(killMessage.getJob(), killMessage.getKillerIp());
				
				result = Boolean.valueOf(true);
				sendResult(msg.getSrc(), req.getArgument().getId(), result);
			}
				break;
			default:
				throw new IllegalArgumentException("type " + req.type
						+ " is not recognized");
			}
		} catch (Exception e) {
			err("Exception message receiving from " + msg.getSrc(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void viewAccepted(View view) {
		Address local_addr = ch.getAddress();

		Vector<Address> members = view.getMembers();
		
		log.info("Member view: " + members);
		
		systemAddr = members.get(0);
		

		int old_rank = rank;
		for (int i = 0; i < members.size(); i++) {
			Address tmp = members.get(i);
			if (tmp.equals(local_addr)) {
				rank = i;
				break;
			}
		}
		
		if (old_rank == -1 || old_rank != rank)
			log.info("My rank -> " + rank);

		if (rank == 0)
			AgentManagement.setSystemAgent(true);
		else
			AgentManagement.setSystemAgent(false);
	}

	@Override
	public void channelConnected(Channel channel) {
		if (channel.isConnected()) {
			messageManager.synchronizeJobOptionFromMyFriend();
		}
	}
	
	private static void err(String msg, Throwable t) {
		log.error(msg + ", ex=" + t);
	}


	public void setResourceLockManager(IResourceLockManager resourceLockManager) {
		this.resourceLockManager = resourceLockManager;
	}

	private boolean sendResult(Address srcAddr, long id, Object result) {
		Request response = new Request(Request.Type.ResultFromMaster,
				new TaskArgument(id), result);
		
		try {
			byte[] buf = Util.streamableToByteBuffer(response);
			log.debug("Sending result to -> " + srcAddr + " ,[result] : "
					+ result);
			ch.send(new Message(srcAddr, null, buf));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public static synchronized BatchAgent createServer() throws Exception {
		if (batchAgent == null)
			batchAgent = new BatchAgent();
		return batchAgent;
	}

	public Address getMasterView() {
		return systemAddr;
	}

	public Channel getChannel() {
		return ch;
	}

	public static void sendClusteredConditions(List<ClusteredCondition> targets) {
		try {
			createServer().messageManager.sendClusteredConditions(targets);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	
}
