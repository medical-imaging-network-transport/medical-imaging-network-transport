<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="f"
	uri="http://java.sun.com/jsp/jstl/fmt"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html version="-//W3C//DTD HTML 4.01 Transitional//EN" dir="ltr"
	lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<c:if test="${not jobinfo.complete}">
<meta http-equiv="refresh" content="3;URL=${joburi}">
</c:if>
<title>Job Information</title>
</head>
<body>
<c:if test="${not empty jobinfo}">
	<h1>Job Information for Job ID: ${jobinfo.id}</h1>
	<ul>
		<li class='id'>${jobinfo.id}</li>
		<li class='uri'>${joburi}</li>
		<li class='meta'>Job ID: ${jobinfo.id}</li>
		<li class='meta'>Job URI: <a href="${joburi}">${joburi}</a></li>
		<li class='meta'>Study ID: ${jobinfo.studyID}</li>
		<li class='meta'>Status: ${jobinfo.status}</li>
		<li class='meta'>Status Description: ${jobinfo.statusDescription}</li>
		<li class='meta'>Create Time: ${jobinfo.createTime}</li>
		<li class='meta'>Update Time: ${jobinfo.updateTime}</li>
	</ul>
</c:if>
</body>
</html>