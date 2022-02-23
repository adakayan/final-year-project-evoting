<%@ page import="eVote.DAO" language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 
int id1;
id1=Integer.parseInt(request.getParameter("id"));
new DAO().deleteelection(id1);
%>
<script type="text/javascript">alert("Election has been deleted successfully!")</script>
</body>
</html>