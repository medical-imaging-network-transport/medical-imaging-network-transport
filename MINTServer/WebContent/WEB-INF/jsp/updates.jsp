<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html version="-//W3C//DTD HTML 4.01 Transitional//EN" dir="ltr"
	lang="en">
<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
<head>
<title>List of Updates</title>
</head>
<body>
<h1>List of Updates</h1>
<c:if test="${not empty Updates}">
	<ul>
		<c:forEach var="update" items="${Updates}" varStatus="updateStatus">
			<li>
			<ul>
				<li class='StudyUID'>Study ID: ${update.studyID}</li>
				<li class='UpdateIndex'>Update index: ${update.updateIndex}</li>
				<li class='UpdateDescription'>Update description: ${update.updateDescription}</li>
				<li class='UpdateTime'>Time the update was performed: ${update.updateTime}</li>
				<li class='meta'><a
					href='<%=request.getContextPath()%>/studies/${update.studyID}/changelog/${update.updateIndex}'>Meta
				Data</a></li>
			</ul>
			</li>
		</c:forEach>
	</ul>
</c:if>
</body>
</html>