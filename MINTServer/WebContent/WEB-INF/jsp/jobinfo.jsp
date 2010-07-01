<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Job Information</title>
</head>
<body>
<c:if test="${not empty jobinfo}">
<h1>Job Information for Job ID: ${jobinfo.id}</h1>
	<ul>
		<li>
		<ul>
			<li class='UID'>${jobinfo.id}</li>
			<li>Study ID: ${jobinfo.studyID}</li>
			<li>Status: ${jobinfo.status}</li>
			<li>Status Description: ${jobinfo.statusDescription}</li>
			<li>Create Time: ${jobinfo.createTime}</li>
			<li>Update Time: ${jobinfo.updateTime}</li>
		</ul>
		</li>
	</ul>
</c:if>
</body>
</html>