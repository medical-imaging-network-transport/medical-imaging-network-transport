<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Dictionaries</title>
<meta http-equiv='Content-Type' content='text/html;charset=utf-8' />
<style type="text/css">
dl { margin: 0; padding: 0; }
dt { margin: 0; padding: 0; font-weight: bold; }
dd { margin: 0 0 1em 0; padding: 0; }
</style>
</head>
<body>
<h1>Dictionaries Supported by Server</h1>
<c:if test="${not empty dictionaries}">
	<ul>
		<c:forEach var="dictionary" items="${dictionaries}">
			<li>
			<dl id='${dictionary}'>
				<dt>Dictionary Name</dt>
				<dd class='DictionaryName'>${dictionary}</dd>
				<dt>Links</dt>
				<dd class='DictionaryFile'><a href='<%=request.getContextPath()%>/dictionary/${dictionary}'>Dictionary File</a></dd>
			</dl>
			</li>
		</c:forEach>
	</ul>
</c:if>
</body>
</html>