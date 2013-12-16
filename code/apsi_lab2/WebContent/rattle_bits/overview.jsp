<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="java.util.*" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>RattleBits</title>
</head>
<body>
<form method="POST" action='/apsi_lab2/RattleBits' name="ChangePassword">

<table>
	<tr>
		<th>Old Password:</th>
		<td><input type="password" name="password" value="${param.oldpassword}" /></td>
	</tr>
	<tr>
		<th>New Password:</th>
		<td><input type="password" name="password" value="${param.newpassword}" /></td>
	</tr>
		<tr>
		<th>Repeat New Password:</th>
		<td><input type="password" name="passwordrepeat" value="${param.passwordrepeat}" /></td>
	</tr>

	<tr>
		<td colspan="2"><input type="submit" name="change" value="Change Password" /></td>
	</tr>
</table>
</form>
</body>
</html>