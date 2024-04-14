<%-- 
    Document   : errHistoryList
    Created on : Apr 10, 2017, 8:39:59 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 

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
        <title>${title}</title>
        <style>
            .message {
                width: 550px;
                height: 310px;
                border: 1px solid #0000BB;
                background-color:#f7f975;
                border-radius:25px;
                margin: 40px auto auto auto;
                padding-top:20px;
            }              
            .message > h4, .message > p, h5 {
                text-align: center;
            }
            .title {
                text-align:center;
                font-size: 12pt;
                color:#660000;
                font-weight:500;
            }
            hr {
                color:#BBBBBB;
            }
            #support{
                cursor:pointer;
                width: 550px;
                margin:auto;
            }
            .plus {
                background-image:url(${pageContext.request.contextPath}/resources/images/plus1.jpg);
                background-repeat:no-repeat;
                padding-left:25px;
               
            }
            .minus {
                background-image:url(${pageContext.request.contextPath}/resources/images/minus1.jpg);
                background-repeat:no-repeat;
                padding-left:25px
            }
            .divSupport {
                margin:auto;
                width:620px;    
            }
            .container {
               padding-bottom:2%
            }
           
        </style></head>
    <body>
        <div class="container">
        <div id="top">
             <span class="captionTop" style="padding-left:50px;">DVD Store</span>
        </div> 
        
         <div class="alert alert-danger">  
            <c:choose>
               
               <c:when test="${cart.count eq 0}">
                   Please add items to your cart. <a href="<c:url value='/home' />" class="alert-link">
                       Home</a><br/>
               </c:when>
               <c:when
                   test="${fn:contains(exception.class.canonicalName, 'ExpiredLoginRequest')}">
                        Please use the checkout button on the navigation panel.
                        <a href="<c:url value="/home" />" class="alert-link">
                        Home</a>                    
                </c:when>                 
               <c:when test="${empty customer}">
                   Please log in using the checkout button.
                   <a href="<c:url value='/home' />" class="alert-link">Home</a><br/>
               </c:when>
               <c:when test="${empty bindingResult}">
                   
                   <form action="<c:url value='/home' />"
                          method="get" >
                       Please submit your update customer from the checkout button.
                   <input type="submit" class="alert-link" value='Submit Form' />
                   </form>
               </c:when>  
                <c:when
                   test="${fn:contains(exception.class.canonicalName, 'ConfirmCartException')}">
                    <c:if test="${bindingResult.errorCount gt 0}"> 
                        <form action="<c:url value='/home'/>" method="get" >
                                   Your customer data has errors. Please use the checkout button.                                  
                                   <input type="submit" class="alert-link" value='Edit' />
                        </form>
                    </c:if>
                </c:when>                 
                <c:when test="${empty selectedShipAddress}">
                         Please select a shipping address.
                         <a href="<c:url value="/shippingAddress/showSelect" />"
                          class='alert-link' >Select</a>
                </c:when> 
                <c:when test="${fn:contains(exception.class.canonicalName, 'NonCurrentUpdateRequest')}">
                   Expired request for a Customer or Shipping Address update.
                     <a href="<c:url value='/home' />" class="alert-link"> Home</a><br/>
               </c:when>          
                <c:otherwise>
                    View not accessible. You may be using the Browser, History or Favorites to navigate.
                    <a href="<c:url value='/home' />"  class='alert-link'>
                             Return Home</a>
                </c:otherwise>
           </c:choose> 
               <br/>         
               View your most recent payments. 
                   <a href="<c:url value='/orderHistory/login' />"
                      class="alert-link">Order History</a><br/>        
         </div>  <!--end alert -->      
        <div class="message">  
            
             <c:if test="${not fn:contains(exception.class.canonicalName, 'MethodNotSupported')
                  and not fn:contains(exception.class.canonicalName, 'HttpSessionRequired')
                  and not fn:contains(exception.class.canonicalName, 'MissingServletRequestParameter')}">
           
                <h5 class="title">${exception.message}</h5> 
            
             </c:if>
            
                <h5>View not accessible. </h5>
                <h5> You may be using the Browser, History or Favorites to navigate.</h5>
            
            <h5>To report an application error or to complete your order: </h5>
            
            <p><a href="${pageContext.request.contextPath}/customerSupport">Customer Support</a></p>     
           
            <hr/>
                           
                           
            <p><a href="${pageContext.request.contextPath}/home">Continue Shopping</a></p>
            
            <c:url var="ordersUrl" value="/orderHistory/login" />            
            
            <p><a href="${ordersUrl}">Recent Payments</a></p>
            
            <p style="font-weight:bolder">Customer Support: (123) 123-1234</p>
                    
        </div><!-- end message --> 
        <br/>
        <h5 id="support" class="plus">Support</h5>
        <div class="divSupport" style="display:none">
            <p>${url}</p>
            <p>Handler: ${handler}</p>
            <p>Exception: ${exception.class.simpleName}</p>
            <p>Message: ${exception.message} </p>
           <c:if test="${not fn:contains(exception.class.canonicalName, 'MethodNotSupported')
                  and not fn:contains(exception.class.canonicalName, 'HttpSessionRequired')
                  and not fn:contains(exception.class.canonicalName, 'MissingServletRequestParameter')}">
               <p>Technical: ${exception.technical}</p> 
            </c:if>
            <p>Message Trace:</p>
            <p>${messages}</p>
            <p>Stack Trace:</p>
            <p>${trace}</p>
        </div>           
       
        <script>
            
            $("#support").click(function(){
                $(this).toggleClass("plus minus");
                $(this).next().toggle(500);
            });
        </script>
        </div><!--end container -->
    </body>
</html>

