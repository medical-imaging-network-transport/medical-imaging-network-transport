<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.apache.commons.lang.StringUtils"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Study</title>
</head>
<body>

<h2>Create Study</h2>

<form name='post_form' method='POST' enctype='multipart/form-data' action='<%=request.getContextPath()%>/jobs/createstudy'>
Type: <select name=type>
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
<input type=submit value="Create Study">
</form>

<form name='get_form' method='GET' action='<%=request.getContextPath()%>/jobs/createstudy'>
Number of files: <input type=text name=numFiles><br>
<input type=submit value="Change number of binary item files">
</form>

</body>
</html>