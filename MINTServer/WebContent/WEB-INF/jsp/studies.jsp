<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><%@ taglib prefix="f"
	uri="http://java.sun.com/jsp/jstl/fmt"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html version="-//W3C//DTD HTML 4.01 Transitional//EN" dir="ltr"
	lang="en">
<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
<head>
<title>List of all studies</title>
</head>
<body>
<h1>List of all studies</h1>
<c:if test="${not empty Studies}">
	<ul>
		<c:forEach var="study" items="${Studies}" varStatus="studyStatus">
			<li>
			<ul>
				<li class='UID'>${study.studyInstanceUID}</li>
				<li class='meta'><a href='studies/${study.id}/DICOM/metadata'>Meta
				Data</a></li>
				<li class='summary'><a href='studies/${study.id}/DICOM/summary'>Study
				Summary</a></li>
			</ul>
			</li>
		</c:forEach>
	</ul>
</c:if>
</body>
</html>
