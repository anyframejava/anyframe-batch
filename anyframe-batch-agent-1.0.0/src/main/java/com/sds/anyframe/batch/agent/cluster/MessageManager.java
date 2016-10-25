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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.ChannelException;
import org.jgroups.Message;
import org.jgroups.TimeoutException;
import org.jgroups.util.Util;

import com.sds.anyframe.batch.agent.cluster.Request.Type;
import com.sds.anyframe.batch.agent.exception.NoAvailableDestinationAgentException;
import com.sds.anyframe.batch.agent.management.ClusteredCondition;
import com.sds.anyframe.batch.agent.managment.AgentManagement;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class MessageManager {
	private static Logger log = Logger.getLogger(MessageManager.class);

	private static final int RETRY_COUNT_LIMIT = 3;
	
	private final long PROMISE_TIMEOUT = 120000;

	private final long DELAY_INITIATE_MASTER_AGENT = 5;
	
	private BatchAgent batchAgent;

	final AtomicLong atomicId = new AtomicLong();
	
	public MessageManager(BatchAgent batchAgent) {
		this.batchAgent = batchAgent;
	}

	private Address getPrimaryOrSecondarAddress() {
		Vector<Address> members = batchAgent.ch.getView().getMembers();

		Address local_addr = batchAgent.ch.getAddress();

		String strAddr = local_addr.toString();
		strAddr = strAddr.substring(0, strAddr.lastIndexOf("-"));

		for (Address addr : members) {
			if (!addr.equals(local_addr) && addr.toString().startsWith(strAddr)) {
				return addr;
			}
		}
		return null;
	}

	public void synchronizeJobOptionFromMyFriend() {

		Address friend = getPrimaryOrSecondarAddress();

		if (friend == null) {
			log.info("There is no my friend(Primay or Secondary), therefore don't need to inquire clustered information");
			return;
		}

		log.info("Clustered information will inquire to my friend(pirmay or secondary)["
				+ friend.toString() + "]");

		long id = atomicId.getAndIncrement();

		try {
			ClusteredCondition clusteredCondtion = (ClusteredCondition) sendMessageToAgent(
					friend, null, id, Request.Type.GetClusteredCondition);
			log
					.info("Clustered information received from my friend(pirmay or secondary)["
							+ clusteredCondtion.toString() + "]");

			clusteredCondtion.setServerName(batchAgent.ch.getAddress()
					.toString());
			AgentManagement.getAgentCondition().setClusteredCondition(
					clusteredCondtion);
		} catch (Exception e) {
			log.error(e);
			batchAgent.tasks.remove(id);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ClusteredCondition> getClusteredCondtions() {
		Vector<Address> members = batchAgent.ch.getView().getMembers();

		List<ClusteredCondition> clusteredConditions = new ArrayList<ClusteredCondition>();

		for (Address addr : members) {

			long id = atomicId.getAndIncrement();

			try {
				log.info("Clustered Condition requested for " + addr.toString());

				ClusteredCondition clusteredCondition = (ClusteredCondition) sendMessageToAgent(
						addr, null, id, Request.Type.GetClusteredCondition);
				log.info("Clustered Condition received from " + addr.toString()
						+ "[" + clusteredCondition.toString() + "]");

				clusteredConditions.add(clusteredCondition);
			} catch (Exception e) {
				log.error(e);
				batchAgent.tasks.remove(id);
			}
		}
		
		@SuppressWarnings("rawtypes")
		class ServerNameSorter implements Comparator {
			
			public int compare(Object arg0, Object arg1) {
				String serverName = ((ClusteredCondition)arg0).getServerName();
				String serverName2 = ((ClusteredCondition)arg1).getServerName();
				
				return serverName.compareTo(serverName2);
			}
		}
		
		Collections.sort(clusteredConditions, new ServerNameSorter());
		
		return clusteredConditions;
	}
	
	public void sendClusteredConditions(List<ClusteredCondition> targets) {
		Vector<Address> members = batchAgent.ch.getView().getMembers();

		for (ClusteredCondition clusteredCondtion : targets) {
			boolean bSent = false;

			for (int i = 0; i < members.size(); i++) {
				Address addr = members.get(i);
				if (clusteredCondtion.getServerName().equals(addr.toString())) {
					requestToEachMember(addr, clusteredCondtion,
							Request.Type.SendClusteredCondition);
					bSent = true;
					break;
				}
			}
			if (!bSent)
				log.warn("Can not send the clustered condition to "
						+ clusteredCondtion.getServerName());
		}
	}

	private void requestToEachMember(Address addr,
			ClusteredCondition clusteredCondtion, Request.Type type) {
		log.info("Clustered Condition will be sent to " + addr.toString());

		long id = atomicId.getAndIncrement();
		log.debug("requestToEachMember() id : " + id);

		try {
			sendMessageToAgent(addr, (Object) clusteredCondtion, id, type);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			batchAgent.tasks.remove(id);
		}
	}

	private Object sendMessageToAgent(Address addr, Object obj, long id,
			Request.Type type) throws Exception {
		TaskArgument argument = new TaskArgument(obj, id, type.ordinal());
		Entry entry = new Entry(argument);

		Request req = new Request(type, argument, null);
		byte[] buf = null;
		try {
			buf = Util.streamableToByteBuffer(req);
			batchAgent.tasks.put(id, entry);

			return requestToAgent(addr, entry, buf);
		} catch (Exception ex) {
			// ChannelException, TimeoutException 일 때만 retry
			if (ex instanceof ChannelException
					|| ex instanceof TimeoutException
					|| ex.getCause().getClass() == IllegalArgumentException.class) {
				return retryMessage(addr, entry, buf);
			} else {
				throw new RuntimeException(ex);
			}
		}
	}
	
	public boolean lockResources(Step step) throws Exception {
		log.info("Lock resources to System Agent:" + AgentUtils.toStepString(step));

		long id = atomicId.getAndIncrement();

		log.debug("lockResources() id : " + id);
		try {
			return (Boolean)sendMessageToSystemAgent((Object)step, id, Request.Type.LockResources);
		} catch (Exception e) {
			batchAgent.tasks.remove(id);
			throw e;
		}
	}

	public boolean releaseResources(Step step) throws Exception {
		log.info("Release resources to System Agent:"
				+ AgentUtils.toStepString(step));
		long id = atomicId.getAndIncrement();
		log.debug("releaseResources() id : " + id);

		try {
			return (Boolean)sendMessageToSystemAgent(step, id, Request.Type.ReleaseResources);
		} catch (Exception e) {
			batchAgent.tasks.remove(id);
			throw e;
		}
	}
	
	private Object sendMessageToSystemAgent(Object obj, long id,
			Request.Type type) throws Exception {
		TaskArgument argument = new TaskArgument(obj, id, type.ordinal());
		Entry entry = new Entry(argument);

		Request req = new Request(type, argument, null);
		byte[] buf = null;
		try {
			buf = Util.streamableToByteBuffer(req);
			batchAgent.tasks.put(id, entry);

			return requestToAgent(batchAgent.systemAddr, entry, buf);
		} catch (Exception ex) {
			log.warn(ex.getMessage() + " - " + batchAgent.systemAddr.toString(), ex);
			
			// ChannelException, TimeoutException 일 때만 retry
			if (ex instanceof ChannelException
					|| ex instanceof TimeoutException
					|| ex.getCause().getClass() == IllegalArgumentException.class) {
				try {
					return retrySendMessageToSystemAgent(entry, buf);
				} catch (Exception e) {
					log.error("Error occured to send retrying messsage at " + batchAgent.systemAddr.toString(), e);
					throw e;
				}
			} else {
				throw new RuntimeException("Exception occured to send message at " + batchAgent.systemAddr.toString(), ex);
			}
		}
	}
	
	private Object requestToAgent(Address addr, Entry entry, byte[] buf)
			throws Exception {
		batchAgent.ch.send(new Message(addr, null, buf));
		Object result = entry.promise.getResultWithTimeout(PROMISE_TIMEOUT);
		if (result instanceof Exception) {
			throw (Exception) result;
		}
		return result;
	}

	private Object retrySendMessageToSystemAgent(Entry entry, byte[] buf) throws Exception {
		// re-try 횟수설정 : 5회
		int retryCount = 0;
		while (true) {
			try {
				if (retryCount == RETRY_COUNT_LIMIT) {
					String msg = "Communication failure occured to check resource availability to System Agent";
					log.error(msg);
					throw new Exception(msg);
				}

				TimeUnit.SECONDS.sleep(DELAY_INITIATE_MASTER_AGENT);
				retryCount++;
				log
						.info("Primary agent is retrying to send message at the System Agent[retry count : "
								+ retryCount + "] for " + Type.values()[entry.task.getCmd()].name());
				return requestToAgent(batchAgent.systemAddr, entry, buf);
			} catch (Exception e) {
				if (retryCount == RETRY_COUNT_LIMIT) {
					throw e;
				} else
					log.warn("Error occured to retry sending messsage at " + batchAgent.systemAddr.toString(), e);
			}
		}
	}
	
	private Object retryMessage(Address addr, Entry entry, byte[] buf) throws Exception {
		// re-try 횟수설정 : 5회
		int retryCount = 0;
		while (true) {
			try {
				if (retryCount == RETRY_COUNT_LIMIT) {
					String msg = "Communication failure occured to check resource availability to System Agent";
					log.error(msg);
					throw new Exception(msg);
				}

				TimeUnit.SECONDS.sleep(DELAY_INITIATE_MASTER_AGENT);
				retryCount++;
				log
						.info("Primary agent is retrying to send message at the Agent[retry count : "
								+ retryCount + "] for " + Type.values()[entry.task.getCmd()].name());
				return requestToAgent(addr, entry, buf);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (retryCount == RETRY_COUNT_LIMIT) {
					throw e;
				}
			}
		}
	}

	public void killProcessOnRemote(KillMessage killMessage) throws Exception {
		Vector<Address> members = batchAgent.ch.getView().getMembers();

		for (Address addr: members) {

			long id = atomicId.getAndIncrement();

			try {
				String physicalAddress = (String) sendMessageToAgent(addr, killMessage, id, 
						Request.Type.GetPhysicalAddress);
				
				if(killMessage.getJob().getIp().equals(physicalAddress)) {
					id = atomicId.getAndIncrement();
					log.info("Process killing message will be sent to agent[" + physicalAddress + "]");
					sendMessageToAgent(addr, killMessage, id, Request.Type.KillProcess);
					return;
				}
			} catch (Exception e) {
				batchAgent.tasks.remove(id);
				throw e;
			}
		}
		
		throw new NoAvailableDestinationAgentException("Can not kill the process because of no available agent in the server[" + killMessage.getJob().getIp() + "]");
	}
}
