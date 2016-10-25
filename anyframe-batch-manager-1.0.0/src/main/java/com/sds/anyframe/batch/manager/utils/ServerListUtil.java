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

package com.sds.anyframe.batch.manager.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sds.anyframe.batch.agent.util.XMLUtil;
import com.sds.anyframe.batch.manager.BatchConstants;
import com.sds.anyframe.batch.manager.core.MessageUtil;
import com.sds.anyframe.batch.manager.view.ServerInfo;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ServerListUtil {
	
	private static final String SERVER_NODE = "servers/server";
	
	private ServerListUtil(){
	}
	
    /**
     * 콤보박스에 담을 서버리스트를 생성한다.
     * @param serverListCombo 서버리스트를 담아둘 콤보박스
     * @return	서버명과 서버정보VO가 매핑되어있는 Map
     * @throws Exception 
     */
    public static Map<String, ServerInfo> fillServerListCombo(Combo serverListCombo) throws Exception {
		Map<String, ServerInfo> serverMap = new HashMap<String, ServerInfo>();
		
		List<ServerInfo> list = ServerListUtil.getServerList();
		if(list==null || list.size()==0){
			MessageUtil.showMessage("Server list XML file is invalid. Please check Server configuration file path in Window - Preferences - Anyframe Batch - Batch Manager menu. And restart your Eclipse", "Batch Manager");
			return null;
		}
		serverListCombo.add("Select a agent");
		
		Iterator<ServerInfo> iterator = list.iterator();
		while(iterator.hasNext()) {
			ServerInfo info = iterator.next();
			serverListCombo.add(info.getServerName());
			serverMap.put(info.getServerName(), info);
		}
		return serverMap;
	}
    
    /**
     * 
     * @return XML파일에 저장된 서버정보 List
     * @throws Exception 
     */
	public static List<ServerInfo> getServerList() throws Exception {
		XMLUtil xmlUtil = new XMLUtil();
		String filePath = PlatformUI.getPreferenceStore().getString(BatchConstants.BATCH_MANAGER_PREFERENCE);
		if(filePath == null || filePath.length()==0){
			MessageUtil.showMessage("Batch Server List path is not configured on the preferece page.", "Batch Manager");
			return null;
		}
		NodeList nodeList = xmlUtil.getNodeList(filePath, SERVER_NODE);
		List<ServerInfo> list = new ArrayList<ServerInfo>();
		if(nodeList==null) return list;
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			ServerInfo info = new ServerInfo();
			info.setAddress(node.getAttributes().getNamedItem("ip").getNodeValue());
			info.setServerName(node.getAttributes().getNamedItem("name").getNodeValue());
			list.add(info);
		}
		return list;
	}
}
