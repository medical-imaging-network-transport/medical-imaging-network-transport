<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.nema.medical.mint.jobs.JobConstants"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<title>Update Study</title>
</head>
<body>

<h2>Update Study</h2>

<form method='POST' enctype='multipart/form-data' action='<%=request.getContextPath()%>/jobs/updatestudy'>
Study UUID: <input type=text name=<%=JobConstants.HTTP_MESSAGE_PART_STUDYUUID%>><br>
Type: <select name=<%=JobConstants.HTTP_MESSAGE_PART_TYPE%>>
<c:forEach var="typeName" items="${types}">
<option value="${typeName}">${typeName}</option>
</c:forEach>
</select><br>
Metadata to upload: <input type=file name=metadata><br>
Previous version: <input type=text name=<%=JobConstants.HTTP_MESSAGE_PART_OLDVERSION%>><br>
<%
	String numFiles = request.getParameter("numFiles");
	if(!StringUtils.isBlank(numFiles))
	{
		int nf;
		try
		{
			nf = Integer.parseInt(numFiles);
		}catch(NumberFormatException e){
			nf = 0;
		}
		
		for(int x = 0; x < nf; ++x)
		{
%>
File to upload: <input type=file name="binary<%=x%>"><br>
<%
		}
	}
%>
<br>
<input type=submit value="Update Study">
</form>

<form name='get_form' method='GET' action='<%=request.getContextPath()%>/jobs/updatestudy'>
Number of files: <input type=text name=numFiles><br>
<input type=submit value="Change number of binary item files">
</form>

</body>
</html>