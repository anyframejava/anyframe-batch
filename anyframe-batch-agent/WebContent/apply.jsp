<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="com.sds.anyframe.batch.agent.cluster.BatchAgent"%>
<%@page import="com.sds.anyframe.batch.agent.management.ClusteredCondition"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.sds.anyframe.batch.agent.managment.AgentManagement"%>
<%@page import="com.sds.anyframe.batch.agent.web.SignBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>


<html>
<head>
<title>Job Blocking</title>
<script language="javascript" type="text/javascript">
    
function goBack() { 
	document.location.href="./apply_ui.jsp";
} 
</script>
</head>

<body onload="goBack()">   
	<%
	if (request.getSession().getAttribute(SignBean.SINGIN_STATUS) != null
							&& request.getSession()
									.getAttribute(SignBean.SINGIN_STATUS).equals(
											SignBean.SUCCESS)) {
				
		List<ClusteredCondition> targets = null;
		

		String strTargets = request.getParameter("targets");
		if(strTargets != null) {
			targets = new ArrayList<ClusteredCondition>();
			
			StringTokenizer token = new StringTokenizer(strTargets, "\n");
			
			while(token.hasMoreElements()) {
				String server = token.nextToken();
				
				String[] values = server.split(";");
				
				ClusteredCondition e = new ClusteredCondition();
				e.setServerName(values[0]);
				e.setBlocking(Boolean.valueOf(values[1]));
				
				e.setJobExecutionLimits(Integer.valueOf(values[2].trim()));
				
				targets.add(e);
			}
			
			if(targets.size() > 0)
				BatchAgent.createServer().sendClusteredConditions(targets);
		}
	}
	%>
</body>
</html>
