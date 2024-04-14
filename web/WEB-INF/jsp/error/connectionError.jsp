<%-- 
    Document   : connectionError
    Created on : Jul 7, 2016, 9:33:01 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %> 


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" 
              rel="stylesheet" type="text/css" />
         <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <title>PayPal Connection Error</title>
    </head>
    <body>
        <div class="container">
        <h2>Server Error Connecting To Payment Service</h2><br/>        
        
        <h2>Contact: 123-1234</h2>
        
        <c:if test="${recoverableKey}">
            <h3>This problem may be temporary. To retry click the link below:</h3>
            <h3><a href="<c:url value='${recoverablePathKey}'/>">Continue</a></h3>
        </c:if>
            
        <c:if test="${not recoverableKey}"> 
            <h3>Please contact support to complete your order.</h3>
        </c:if>
       
        <h3>You may contact support to complete your order.
            <a href="<c:url value='/customerSupport'/>">Support</a>
        </h3>
        
        <h3>You may select another payment option. <a href="<c:url value='/cancelPayPal'/>">Cancel</a></h3>
        
        <c:if test="${isPaymentReset eq true}">
         <h3>Cancel and Return to home page. <a href='<c:url value="/home" />'>Home</a></h3> 
        </c:if>
        
        <h4 class="plus">Technical Support: </h4>
        <div>
            Handler: ${handler}<br/>
            Cause: ${exception.cause.class.name} <br/><br/>
            Message: ${exception.message}  <br/><br/>
            Friendly: ${exception.friendly}<br/><br/>
            Method: ${exception.method}<br/><br/>
            Recoverable: ${exception.recoverable}<br/><br/>
            Request Url: ${url}<br/>           
        </div>
        
        <h4 class="plus">Messages:</h4>
        <div>${messages}</div>
        
        <h4 class="plus">Trace:</h4>
        <div>${trace}</div>       
        </div><!--end container-->  
        <script>
            $(document).ready(function(){
                $(".plus").next().hide();
                $("h4").click(function(){
                    
                    $(this).toggleClass("plus minus");
                    $(this).next().toggle();
                    
                });
                
            });
        </script>
    </body>
</html>
