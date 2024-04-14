<%-- 
    Document   : paypalReceipt
    Created on : Jul 10, 2016, 10:16:34 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
         <meta charset="UTF-8">
         <meta name="viewport" content="width=device-width, initial-scale=1">
         <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/receipt.css" rel="stylesheet" />
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/login.js" 
                    type="text/javascript"></script>
        <title>Receipt</title>
    </head>
    <body>
        <div class="container">           
            
            <jsp:include page="../jspIncludes/navigation.jsp" />
            
             <h1>PayPal Receipt</h1><br/>
             <div style="float:right">
                 <form action="${pageContext.request.contextPath}/cancelPayment" method="get">
                     <input type="submit" value="Cancel Transaction" />
                 </form>
             </div>
             <div class="orderInfo"> <!--receipt.css-->              
                 <fieldset>
                     <legend>Order Information</legend>
                     <label>Customer Id:</label><span>${order.customer.customerId}</span><br/> 
                     <label>Order Id:</label><span>${order.orderId}</span><br/>
                     <label>Resource:</label>
                     <span>${paypalReceipt.resourceId}</span><br/>
                     <label>Date Created:</label>
                     <span>
                         <fmt:formatDate value="${order.orderDate}"
                             type="both" pattern="EEE, d MMM, yyyy hh:mm:ss" 
                             timeZone="America/Los_Angeles"/></span><br/>
                     <label>Transaction Id:</label> 
                     <span>
                        ${paypalReceipt.transactionId}</span><br/>                     
                    <label>Amount:</label>
                    <span>
                        <fmt:formatNumber currencyCode="USD" type="currency"
                                          value="${order.orderAmount}" /></span><br/>   
                 </fieldset>
             </div><!--end order info-->
             <div class="lineItems">
                 <h3>Items</h3>
                 <c:forEach var="item" items="${order.lineItems}">
                     <div>
                         <span class="itemTitle">${item.film.title}</span> <br/>
                         Unit Price: <fmt:formatNumber
                             type="currency" 
                             value="${item.film.rentalRate}"
                             currencyCode="USD" /><br/>
                         Ordered Quantity: ${item.quantity} <br/><br/>                        
                     </div>
                 </c:forEach>
             </div>
             <br style="clear:both"/>
             <div class="divInfoCust"><!--payPalAuthorize.css-->
               <table class="table">
                <caption>Ship To:</caption>
                <tr class="info">
                    <td>${paypalReceipt.address.recipientName}</td>
                </tr>
                <tr class="info">
                    <td>${paypalReceipt.address.line1}</td>
                </tr>
                <tr class="info">
                    <td>${paypalReceipt.address.city},
                        ${paypalReceipt.address.state}&nbsp;${paypalReceipt.address.postalCode}</td>
                </tr>
                <tr class="info">
                    <td>${paypalReceipt.address.countryCode}</td>
                </tr>
                
            </table> <br/> 
                <div style="font-style:italic">
                    ${order.customer.firstName} ${order.customer.lastName}<br/>
                    Customer Since: &nbsp; <fmt:formatDate 
                            value="${order.customer.createDate}" 
                            type="date"
                            pattern="EEE, d MMM, yyyy" />
                </div> 
              
             </div>
                 
        </div><!--end container-->
    </body>
</html>
