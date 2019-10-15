<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Text editor</title>
</head>
<body>
<h4>${fileName}</h4>
<form action="${pageContext.request.contextPath}/edit?fileName=${fileName}" method="post">
    <textarea rows="30" cols="90" name="newText">${text}</textarea></p>
    <input type="submit" value="Edit and return to the start page">
</form>
</body>
</html>
