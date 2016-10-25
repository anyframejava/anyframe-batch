<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.sds.anyframe.batch.agent.security.SecurityManager"%>
<%@page import="com.sds.anyframe.batch.agent.security.AuthenticationException"%>
<%@page import="com.sds.anyframe.batch.agent.web.SignBean"%>
<html>
<head>
<title>Batch Agent Sign in</title>
</head>
<body>
<form method=post>
<%
// To Sign in
if(request.getSession().getAttribute(SignBean.SINGIN_STATUS) == null || !request.getSession().getAttribute(SignBean.SINGIN_STATUS).equals(SignBean.SUCCESS)) {
		
	String userId = request.getParameter("userId");
	String password = request.getParameter("password");
	
	if(userId != null && password != null) {
		SecurityManager securityManager = new SecurityManager();
		try {
			securityManager.signIn(userId, password);
			request.getSession().setAttribute(SignBean.SINGIN_STATUS, SignBean.SUCCESS);
			%>
			<jsp:forward page='/apply_ui.jsp'/> 
		<% 
		} catch (AuthenticationException e) {
			e.printStackTrace();
			request.getSession().setAttribute(SignBean.SINGIN_STATUS, e.getMessage());
			System.out.println(request.getSession().getAttribute(SignBean.SINGIN_STATUS));
		%>
			
			<td><%=request.getSession().getAttribute(SignBean.SINGIN_STATUS) %></td>
			 <p><a href='<%= request.getContextPath() + "/signin.jsp" %>'>Back</a> to Sign in page.
</p>
		<%
		} // try
	} else {
		%>
		<table>
		<tr>
			Input your user id and password carefully.
			<td>Enter User ID:</td>
			<td><input type=text name="userId" /></td>
		</tr>
		<tr>
			<td>Enter Password:</td>
			<td><input type=password name="password" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" name="b1" value="Sign in"></td>
		</tr>
		</table>
	<%
	}
	%>

<%
} else if(request.getSession().getAttribute(SignBean.SINGIN_STATUS).equals(SignBean.SUCCESS)) {
%>
<jsp:forward page='/apply_ui.jsp'/> 
<%
}
%> 
</form>
</body>
</html>