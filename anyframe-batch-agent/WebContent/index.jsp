<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Properties"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.sds.anyframe.batch.agent.PropertyConstants"%>
<%@ page import="com.sds.anyframe.batch.agent.util.PropertiesUtil"%>
<%@page import="com.sds.anyframe.batch.agent.BatchAgentConditionManager"%>
<%@page import="java.util.TreeSet"%>
<%@page import="com.sds.anyframe.batch.agent.BatchAgentListener"%>
<%@page import="com.sds.anyframe.batch.agent.managment.AgentManagement"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Anyframe Enterprise Batch Agent</title>
</head>
<body>
<p><strong>Anyframe Enterprise Batch Agent Server(3.7.2.0)</strong><BR>
Batch Agent started time=<%=BatchAgentListener.getStartedTime()%><BR>
<p><strong>Server Configuration [<%=PropertyConstants.SERVER_PROPERTIES_FILE%>
]</strong><br>
<%
	Properties prop = PropertiesUtil.getProperties(PropertyConstants.SERVER_PROPERTIES_FILE);
Set<Object> keySet = prop.keySet();
TreeSet<Object> treeSet = new TreeSet<Object>(keySet);
for (Object key: treeSet) {
%> <%=key + "="+prop.getProperty((String)key)%><BR>
<%
	}
%>

<p>
<strong>Runtime variables</strong><br>
<%
	prop = PropertiesUtil.getProperties(PropertyConstants.RUNTIME_PROPERTIES_FILE);
keySet = prop.keySet();
treeSet = new TreeSet<Object>(keySet);
for (Object key: treeSet) {
%> <%=key + "="+prop.getProperty((String)key)%><BR>
<%
	}
%>
<p>
<p>
<strong>Policy variables</strong><br>
<%
	prop = PropertiesUtil.getProperties(PropertyConstants.POLICY_PROPERTIES_FILE);
keySet = prop.keySet();
treeSet = new TreeSet<Object>(keySet);
for (Object key: treeSet) {
%> <%=key + "="+prop.getProperty((String)key)%><BR>
<%
	}
%>
<p>
<p>
<strong>Server Conditions</strong><br>
<%
	boolean result = BatchAgentConditionManager.validateDatabase();
%>
Batch Agent Database Connection Test=<%=result%><BR>
<p>
<p>
<%
	result = AgentManagement.isBlocking();
%>
Job Blocking=<%= result %><BR>
<p>
<p>
<strong>Agent Type</strong><br>
<%
	if(AgentManagement.getAgentCondition().isSystemAgent()) {
%>
System Agent<BR>
<%
	} else {
%>
Normal Agent<BR>
<%
	}
%>
<p>
<p>
</body>
</html>