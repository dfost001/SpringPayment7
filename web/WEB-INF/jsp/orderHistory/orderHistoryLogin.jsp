<%-- 
    Document   : orderHstoryLogin
    Created on : Sep 10, 2018, 1:41:23 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1" />        
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/default.css" rel="stylesheet" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Order History Login</title>
    </head>
    <body>
        <div class="container">
            <div id="top">   
                <img src='<c:url value="/resources/images/theatre3.jpg" />' alt="Icon Theatre Faces"/>
                <div class="captionTop">DVD Store</div> 
            </div><!--top-->
            <h2>Order History Login</h2>
            
            <c:if test="${not empty orderHistoryController.missingIdMsg}">
                <div class="alert alert-danger">
                    ${orderHistoryController.missingIdMsg}
                    
                </div>
            </c:if>
             <c:if test="${not empty orderHistoryController.noneFoundMessage}">
            <div class="alert alert-danger">
                ${orderHistoryController.noneFoundMessage}
                  &nbsp;&nbsp;               
            </div>
            </c:if>
            <form action="<c:url value='/orderHistory/cancel' />" method="get">
                <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
                 <input type="submit" value="Cancel" class="btn btn-link btn-md" 
                        style="font-weight:bold;font-size:12pt"/> 
            </form>
            <div class="divOrderHistoryLogin">
                <form method="get" action="<c:url value='/orderHistory/orders' />">
                    <label>Please enter your Customer ID: </label><br/>
                    <input type="text" name="id" size="40" style="height:50px"
                           value="${orderHistoryController.errIdVal}"/>                                     
                    <input type="submit" value="Submit" class="btn btn-info btn-md" /> <br/> 
                    <span class="errorStyle">${orderHistoryController.errIdMessage}</span>   
                    <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
                </form>
            </div>
        </div><!--end container -->
    </body>
</html>
