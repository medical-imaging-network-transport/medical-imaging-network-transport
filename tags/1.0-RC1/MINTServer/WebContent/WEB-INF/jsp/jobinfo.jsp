<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="ISO-8859-1"%><%@ taglib prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<c:if test="${not job.complete}">
<meta http-equiv="refresh" content="3;URL=${joburi}"/>
</c:if>
<title>JobInfo</title>
<meta http-equiv='Content-Type' content='text/html;charset=utf-8'/>
<style type="text/css">
dl { margin: 0; padding: 0; }
dt { margin: 0; padding: 0; font-weight: bold; }
dd { margin: 0 0 1em 0; padding: 0; }
</style>
</head>
<body>
	<h1>Job Information</h1>
	<dl>
		<dt>JobID</dt>
		<dd class='JobID'>${job.id}</dd>
		<dt>Job Info</dt>
		<dd class='JobInfo'><a href="${joburi}">link</a></dd>
		<dt>Study ID</dt>
		<dd class='StudyID'>${job.studyID}</dd>
		<dt>Job Status</dt>
		<dd class='JobStatus'>${job.status}</dd>
		<dd class='JobStatusDesc'>${job.statusDescription}</dd>
		<dt>Job Created</dt>
		<dd class='JobCreated'>${job.createTime}</dd>
		<dt>Job Updated</dt>
		<dd class='JobUpdated'>${job.updateTime}</dd>
	</dl>
</body>
</html>