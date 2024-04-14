<%-- 
    Document   : orders
    Created on : Sep 11, 2018, 3:01:37 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Order History</title>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />        
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet" type="text/css"/>
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>  
        <style>
            .orderRow {
                border-bottom: 2px solid #DDDDDD;
                font-weight:500;
                color:#036fab;
                font-size:12pt;
                padding: 12px;
            }
            .itemRow {
                color:#ff6600;
                font-size:12pt;
                display:none;
             }
        </style>        
    </head>
    <body>
        <div class="container">
            
        <div id="top">   
                <img src='<c:url value="/resources/images/theatre3.jpg" />' alt="Icon Theatre Faces"/>
                <div class="captionTop">DVD Store</div> 
        </div><!--top--> 
        
        <h1>Orders</h1><br/>      
        
       
        
         <div style="float:right;padding:12pt">
               
             <form action="<c:url value='/orderHistory/cancel' />" method="get">
                        <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
                        <input type="submit" value="Return" class="alert-link" /> 
             </form>
        </div>
   <c:if test="${not empty orderHistoryController.orders}">      
       <div style="width:75%;margin:auto;padding-top:15px" class="divOrders">
           
           <div class="row" style="font-weight:bold">
               <div class="col-md-1">&nbsp;</div>
               <div class="col-md-10">Orders for Customer Id '${orderHistoryController.customer.customerId}'</div>
               <div class="col-md-1">&nbsp;</div>
           </div>
           
           <div class="row" >
               <div class="col-md-1">&nbsp;</div>
               <div class="col-md-1">Order Number:</div>
               <div class="col-md-10">&nbsp;</div>
           </div>
            
            <c:forEach var="order" items="${orderHistoryController.orders}">
                
                <div class="row orderRow"  style="cursor:pointer;padding:8px">
                    <div class="col-md-1" >
                        <span class="glyphicon glyphicon-triangle-bottom"></span>
                    </div>                     
                    <div class="col-md-2">${order.orderId}</div>
                    <div class="col-md-4"><fmt:formatDate type="both" timeZone="America/Los_Angeles"                                  
                            value="${order.orderDate}" pattern="yyyy-MM-dd hh:mm a z" /></div> 
                    <div class="col-md-2"><fmt:formatNumber type="currency" value="${order.orderAmount}"
                                      currencyCode="USD" /></div>                    
                    <div class="col-md-3">&nbsp;</div>
                </div><!--end row -->
                
                <div class="itemRow">

                    <c:forEach var="itm" items="${order.lineItems}">
                        <div class="row" >
                            <div class="col-md-1">&nbsp;</div>
                            <div class="col-md-4">${itm.film.title}</div>
                            <div class="col-md-2">Quantity:</div>
                            <div class="col-md-1">${itm.quantity}</div>
                            <div class="col-md-1">Price:</div>
                            <div class="col-md-1"><fmt:formatNumber type="currency" 
                                              currencyCode="USD" 
                                              value="${itm.film.rentalRate}"/></div>
                            <div class="col-md-1">Subtotal:</div>
                            <div class="col-md-1"><fmt:formatNumber type="currency" 
                                              currencyCode="USD" 
                                              value="${itm.quantity * itm.film.rentalRate}"/></div>

                        </div><!--end row-->
                    </c:forEach> 
                </div><!--end rows-->
            </c:forEach><!--end order-->    
       </div> <!--end divOrders-->  
   </c:if> 
 </div><!--end container--> 
     <script>
         $(".orderRow").click(function(){
             $(this).next().slideToggle(500);             
         });
     </script>
    </body>
</html>
