<%-- 
    Document   : newjsp
    Created on : Jan 11, 2017, 9:23:07 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
     <head>
        <meta  charset="UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" />
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" rel="stylesheet" />
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script>
        <title>Data Transaction Error</title>
       
    </head>
    <body>
        <div class="container">
        <h1>Transaction Exception</h1>
        <h3>Please contact 123-1234 to complete your order.</h3><br/>
        <c:if test="${not empty recoverPath}">
            <h4 style="color:#00BB00">This error may be temporary.</h4>
            <h4>You may retry using this link. &nbsp;
            <a href='<c:url value="${recoverPath}" />'>
                ${recoverPath}</a></h4><br/>
        </c:if>     
        <h4><a href="<c:url value='/spring/homeOnDbError' />">Home</a></h4>
        <hr/>
        <h5 class="plus" style="cursor:pointer">Technical Support</h5>
        <div style="display:none">
            
            <p>Request Url: ${url}</p>
            <p>Exception: <c:out value="${exception.message}" escapeXml="true" /></p>
            <p>Exception Class: ${exception.class.name} </p>
            <p>Handler: ${handler} </p>
            <p>Message Trace:</p>
            <p><c:out value="${messages}" escapeXml="false" /></p>
            <p>Stack Trace:</p>
            <p>${trace}</p>
            
        </div>
        </div><!--end container-->
        <script>
            $(document).ready(function(){
                $("h5").click(function(){
                    $(this).next().toggle(400);
                    $(this).toggleClass("plus minus");
                });
            });
        </script>
    </body>
</html>
