<%@ page import="service.SendingService" %>
<%--
  Created by IntelliJ IDEA.
  User: Oleg
  Date: 03.11.2014
  Time: 21:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<%
    String source = request.getParameter("source");

    boolean showResults = false;
    String results = "";
    if (request.getParameter("submitButton") != null) {
        results = SendingService.sendCodeToCompile(source);
        showResults = true;
    }
%>

<form method="post">

    <label for="source">
        Paste code to compile:
    </label>
    <br>
    <textarea id="source" name="source" style="width:250pt; height: 250pt">
        <%= source != null ? source : "" %>
    </textarea>

    <br>
    <input type="submit" name="submitButton" value="Compile"/>

    <%
        if (showResults) {
    %>

    <br>
    <label for="results">
        Compilation results:
    </label>
    <br>
    <textarea id="results" style="width:250pt; height: 250pt;
     <%= results.contains("COMPILATION SUCCESSFUL")
     ? "color:green"
     : "color:red;"%>">

        <%= results %>
    </textarea>


    <%
        }
    %>


</form>

<%

%>

</body>
</html>
