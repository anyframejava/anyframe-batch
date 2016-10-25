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
    
function convertData() { 
	
	var form = document.form1;		
	var server_count = form.elements["server_count"].value;

	var targets = "";
	
	for( i = 0 ; i < server_count; i++) { 
		var server_name = "server_name" + i;
		
		var limitCount = "limitCount" + i;
		var blocking = true;
		 
		if(form.elements["blocking" + i].checked != true)
			blocking = false;

		if(form.elements[server_name].checked) { 
			targets = targets + form.elements[server_name].value + ";" + blocking + ";" + form.elements[limitCount].value + "\n";
		}
	}

	form.elements["targets"].value = targets;
	
	form.action = "./apply.jsp";	
} 

function applyAll(checkInputName) { 
	var form = document.form1;		
	
	var server_count = form.elements["server_count"].value;
	var checked = form.elements[checkInputName].checked;
	
	for( i = 0 ; i < server_count; i++) { 
		form.elements[checkInputName +  i].checked = checked;
	}
} 
</script>
</head>

<body>

<form name="form1" method="post" onsubmit="return convertData()">    
	<%
	if (request.getSession().getAttribute(SignBean.SINGIN_STATUS) != null
							&& request.getSession()
									.getAttribute(SignBean.SINGIN_STATUS).equals(
											SignBean.SUCCESS)) {
					
		List<ClusteredCondition> targets = BatchAgent.createServer().getMessageManager().getClusteredCondtions();
						
	%>
		<input type="hidden" name="server_count" value=<%=targets.size()%>>
        <font size="3" color="#853C3C"><b>Server Information</b></font>
        
        <table>
        <tr>
        <td>
		<table border="1" cellspacing="0" cellpadding="2" align="left" style="border-collapse"
		bordercolor="#111111">

        <tr>
        <td width="30" align="left" >
        No
        </td>
        <td width="70" align="center" >Apply
        <input type="checkbox" name="server_name" onClick="applyAll('server_name');">
        </td>
        <td width="200" align="left" >
        Server Name
        </td>
        
        
        <td width="70" align="center" >
       Bocking
       <input type="checkbox" name="blocking" onClick="applyAll('blocking');">
        </td>
        <td width="150" align="center" >
        Job Limit Count
        </td>        
        </tr>
        
       
        <input type="hidden" name="targets">
       
        <%for(int i =0 ;i<targets.size();i++){
        	ClusteredCondition cluster = targets.get(i);
        	
        %>
        <tr>
        <td align="left">
         <%=(i + 1)%>
        </td>
        <td align="center">
         <input type="checkbox" name="server_name<%= i%>" size="20" value="<%=cluster.getServerName()%>">
        </td>
        <td align="left"><b><%=cluster.getServerName()%></b></td>
        <td align="center">
        <%if(cluster.isBlocking()) { %>
         <input type="checkbox" name="blocking<%= i%>" checked="checked" >        
        <% } else { %>
         <input type="checkbox" name="blocking<%= i%>">
       <% } %> 
         </td>        
         <td align="center">
         <input type="text" name="limitCount<%= i%>"  value=<%=cluster.getJobExecutionLimits() %>>
         </td>
         </tr>
         <%} %>
		</table>
		</td>
		</tr>
		<tr>
		<td>
		<table>
		<tr>
		<td align="center"><input type="submit" value="Apply"></input></td>
		</tr>
		</table>
		</td>
		</tr>
		</table>
		<td>----------------------------------------------------------------</td><p>
		<td><a href='<%= request.getContextPath() + "/signout.jsp" %>'>Sign out</a></td>
     		<%
	} else if (request.getSession().getAttribute(SignBean.SINGIN_STATUS) == null
			|| !request.getSession()
			.getAttribute(SignBean.SINGIN_STATUS).equals(
					SignBean.SUCCESS)) {
	%>
		Sign in first.
		<p><a href='<%= request.getContextPath() + "/signin.jsp" %>'>Back</a>to Sign in page.</p>	
	<%
	}
	%>
</form>
</body>
</html>
