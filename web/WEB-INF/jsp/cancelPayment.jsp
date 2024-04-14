<%-- 
    Document   : cancelPayment
    Created on : Jan 4, 2017, 12:49:42 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta  charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/receipt.css" rel="stylesheet" />
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script> 
        <title>Cancel Payment</title>
    </head>
    <body>
        <div class="container">
        <h1>PayPal Cancel Payment Receipt</h1>
       
        <div class="orderInfo">               
                 <fieldset>
                     <legend>Refund Information</legend>
                     <label>Order Id:</label>
                     <span>${customerOrder.orderId}</span><br/>
                     <label>State:</label>
                     <span>${refundResponse.state}</span><br/>
                     <label>Refund Id:</label>
                     <span>${refundResponse.id}</span><br/>
                     <label>Date Created:</label>
                     <span>
                         <fmt:formatDate value="${customerOrder.orderDate}"
                             type="both" pattern="EEE, d MMM, yyyy hh:mm:ss" 
                             timeZone="America/Los_Angeles"/></span><br/>
                     <label>Sale Transaction Id:</label> 
                     <span>
                        ${refundResponse.saleId}</span><br/>                              
                    <label>Resource Id:</label> 
                    <span>${refundResponse.parentPayment}</span><br/>
                    <label>Amount:</label>
                    <span>
                        ${refundResponse.amount.total}</span><br/>   
                 </fieldset>
             </div>
        </div>
    </body>
</html>
