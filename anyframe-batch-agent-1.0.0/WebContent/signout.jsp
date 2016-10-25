<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.sds.anyframe.batch.agent.web.SignBean"%>
<html>
<head>
<title>Batch Agent Sign in</title>
</head>
<body>
<form method=post>
<%
if(request.getSession().getAttribute(SignBean.SINGIN_STATUS) == null || request.getSession().getAttribute(SignBean.SINGIN_STATUS).equals(SignBean.SUCCESS)) {
		
	request.getSession().removeAttribute(SignBean.SINGIN_STATUS);
%>
<jsp:forward page='/signin.jsp'/> 
<%
}
%> 
</form>
</body>
</html>