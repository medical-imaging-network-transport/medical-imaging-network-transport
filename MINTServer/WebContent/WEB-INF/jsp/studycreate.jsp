<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Study</title>
</head>
<body>

<h2>Create Study</h2>

<form method='POST' enctype='multipart/form-data' action='jobs/createstudy'>
Type: <select name=type>
<!-- TODO dynamically populate this list from the set of types the server knows about -->
<option value="DICOM">DICOM</option>
</select><br>
File to upload: <input type=file name=metadata><br>
File to upload: <input type=file name=binary001><br>
File to upload: <input type=file name=binary002><br>
File to upload: <input type=file name=binary003><br>
<br>
<input type=submit value=Submit>
</form>

</body>
</html>