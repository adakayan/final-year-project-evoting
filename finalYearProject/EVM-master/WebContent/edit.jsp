<%@ page import="eVote.DAO" language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Update Election</title>
</head>
<body>
<% 
int id1;
id1=Integer.parseInt(request.getParameter("id"));
String edate=request.getParameter("edate");
String cdate=request.getParameter("cdate");
String constituency=request.getParameter("Constituency");
new DAO().editelection(id1, edate, cdate, constituency);
out.println(id1);
out.println(edate);
out.println(cdate);
out.println(constituency);
%>
<script type="text/javascript">alert("Election has been updated successfully!")</script>
</body>
</html>