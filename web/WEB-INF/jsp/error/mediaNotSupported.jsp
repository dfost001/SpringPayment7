<%-- 
    Document   : mediaNotSupported
    Created on : Nov 9, 2023, 3:33:21 PM
    Author     : dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
     <head>
        <meta  charset="UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" rel="stylesheet" />       
        <title>REST Error</title>
       
    </head>
    <body>
        <div class="container">
            <h5> Please set Accept request to application/json </h5>         
        </div>
        
    </body>
</html>
