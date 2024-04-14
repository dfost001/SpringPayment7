<%-- 
    Document   : javaException2
    Created on : Jun 20, 2016, 2:11:38 PM
    Author     : Dinah
--%>
<%@page isErrorPage="true" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta  charset="UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" rel="stylesheet" />
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
        <title>Container Servlet Error</title>
       
    </head>
    <body>
        <div class="container">
        <h1>Servlet Exception </h1>
        <h4><a href="<c:url value='/customerSupport' />">Customer Support</a></h4>
        
        <h4 class="plus" style="cursor:pointer">Technical Support</h4>
        <div style="display:none">
            <p>Handler: error-page in web.xml</p>
            <p>Message: ${pageContext.errorData.throwable.message}</p>
            <p>Exception Class: ${pageContext.exception.class.name} </p>
            <p>Request Uri: ${pageContext.errorData.requestURI}</p>
            <p>Stack Trace:</p>
            <div style="margin-left:20px">
                <c:forEach var="el" items="${pageContext.exception.stackTrace}">
                    <p> ${el.className}.${el.methodName}()[${el.fileName}:${el.lineNumber}]</p>
                </c:forEach>
            </div>
            
            
        </div>
        </div><!--end container-->
        <script>
            $(document).ready(function(){
                $("h4").click(function(){
                    $(this).next().toggle(400);
                    $(this).toggleClass("plus minus");
                });
            });
        </script>
    </body>
</html>
