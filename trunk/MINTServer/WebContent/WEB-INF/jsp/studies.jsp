<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Studies</title>
<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
<style type="text/css">
dl { margin: 0; padding: 0; }
dt { margin: 0; padding: 0; font-weight: bold; }
dd { margin: 0 0 1em 0; padding: 0; }
</style>
</head>
<body>
<h1>Studies</h1>
<c:if test="${not empty studies}">
	<ol>
		<c:forEach var="study" items="${studies}">
			<li>
			<dl>
				<dt>MINT Study UUID</dt>
				<dd class='StudyUUID'>${study.id}</dd>
				<dt>Last Modified</dt>
				<dd class='LastModified'>${study.lastModified}</dd>
				<dt>Study Version</dt>
				<dd class='StudyVersion'>${study.studyVersion}</dd>
				<dt>Links</dt>
				<dd class='StudySummary'><a href='<%=request.getContextPath()%>/studies/${study.id}/DICOM/summary'>Summary</a></dd>
				<dd class='StudyMetadata'><a href='<%=request.getContextPath()%>/studies/${study.id}/DICOM/metadata'>Metadata</a></dd>
				<dd class='StudyChangeLog'><a href='<%=request.getContextPath()%>/studies/${study.id}/changelog'>ChangeLog</a></dd>
			</dl>
			</li>
		</c:forEach>
	</ol>
</c:if>
</body>
</html>
