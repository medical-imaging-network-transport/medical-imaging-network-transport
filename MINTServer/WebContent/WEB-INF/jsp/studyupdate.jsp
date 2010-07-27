<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.apache.commons.lang.StringUtils"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Update Study</title>
</head>
<body>

<h2>Update Study</h2>

<form method='POST' enctype='multipart/form-data' action='<%=request.getContextPath()%>/jobs/updatestudy'>
Study UUID: <input type=text name=studyUUID><br>
Type: <select name=type>
<!-- TODO dynamically populate this list from the set of types the server knows about -->
<option value="DICOM">DICOM</option>
</select><br>
Metadata to upload: <input type=file name=metadata><br>
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