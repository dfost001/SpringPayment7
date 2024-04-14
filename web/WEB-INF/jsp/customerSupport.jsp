<%-- 
    Document   : customerSupport
    Created on : Apr 19, 2018, 1:50:57 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta  charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/form.css" rel="stylesheet" />   
        <title>Customer Support</title>
    </head>
    <body>
        <div class="container">
            <div id="top" >
                <jsp:include page="../jspIncludes/logo.jsp" />
            </div>
                <jsp:include page="../jspIncludes/navigationPanel.jsp" />
        <h1>Customer Support:</h1>
        <c:if test="${customerSupportController.paymentStarted}">
            <h4>${customerSupportController.paymentMessage}</h4>
        </c:if>
        <h4>${customerSupportController.supportMessage}</h4>    
        <h3>(123) 123-1234</h3>
        <h4>
            <a href="<c:url value='/home' />">Home</a></h4>
        </div><!--end container-->
    </body>
</html>
