<%@ page import="java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>RattleBits</title>
<style>
.error {
	color: red;
}
.error input {
	border: 1px solid red;
}
</style>
</head>
<body>
<form method="POST" action='/apsi_lab2/RattleBits' name="register">
<ul class="error">
<%
    List<String> messages = (List<String>)request.getAttribute("messages");
	Iterator<String> it = messages.iterator();
    while (it.hasNext()) {
%>
	<li><%= it.next() %></li>
<% } %>
</ul>
<table>
	<tr>
		<th>Username:</th>
		<td><input type="text" name="username" value="${param.username}"/></td>
	</tr>
	<tr>
		<th>Password:</th>
		<td><input type="password" name="password" value="${param.password}" /></td>
	</tr>
		<tr>
		<th>Repeat Password:</th>
		<td><input type="password" name="passwordrepeat" value="${param.passwordrepeat}" /></td>
	</tr>
	<tr>
		<th>Company:</th>
		<td><input type="text" name="firma" value="${param.firma}"/></td>
	</tr>
	<tr>
		<th>Address:</th>
		<td><input type="text" name="address" value="${param.address}" /></td>
	</tr>
	<tr>
		<th>Postal Code/Zip:</th>
		<td><input type="text" name="plz" value="${param.plz}" /></td>
	</tr>
	<tr>
		<th>Town:</th>
		<td><input type="text" name="town" value="${param.town}" /></td>
	</tr>
	<tr>
		<th>E-Mail:</th>
		<td><input type="text" name="mail" value="${param.mail}" /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" name="register" value="Register" /></td>
	</tr>
</table>
</form>
</body>
</html>