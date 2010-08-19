<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>ChangeLog</title>
<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
<style type="text/css">
dl { margin: 0; padding: 0; }
dt { margin: 0; padding: 0; font-weight: bold; }
dd { margin: 0 0 1em 0; padding: 0; }
</style>
</head>
<body>
<h1>ChangeLog</h1>
<c:if test="${not empty changes}">
	<ul>
		<c:forEach var="change" items="${changes}">
			<li>
			<dl id='${change.studyID}'>
				<dt>MINT Study UUID</dt>
				<dd class='StudyUUID'>${change.studyID}</dd>
				<dt>Change Number</dt>
				<dd class='ChangeIndex'>${change.index}</dd>
				<dt>Change Type</dt>
				<dd class='ChangeType'>${change.type}</dd>
				<dt>Change Time</dt>
				<dd class='ChangeTime'>${change.dateTime}</dd>
				<dt>Links</dt>
				<dd class='StudyMetadata'><a href='<%=request.getContextPath()%>/studies/${change.studyID}/${change.type}/metadata'>Study Metadata</a></dd>
				<dd class='ChangeMetadata'><a href='<%=request.getContextPath()%>/studies/${change.studyID}/changelog/${change.index}'>Change Metadata</a></dd>
			</dl>
			</li>
		</c:forEach>
	</ul>
</c:if>
</body>
</html>