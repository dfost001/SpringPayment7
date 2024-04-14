<%-- 
    Document   : handledDbError
    Created on : Sep 7, 2015, 9:52:51 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="resources/css/exceptionView.css"  rel="stylesheet"/>
        <script src="resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <title>PayPal Client Error</title>
    </head>
    <body>
        <div class="container">
        <h1>PayPal Error</h1>
        <h1>Contact: 123-1234</h1>
        <h3 style="color:#006600">${exception.friendly}</h3>
         <c:if test="${recoverableKey eq true}">
                    <h3>This PayPal problem may be temporary. To retry click the link below:</h3>

                    <h3><a href="<c:url value='${recoverablePathKey}'/>">Continue</a></h3>
        </c:if>
        <c:if test="${validationErrorAddress ne null}">
            <h3 style="color:#AA0000">${validationErrorAddress}
                <a href="<c:url value='${validationErrorPath}' />">Select Ship Address</a></h3>
        </c:if>    
              
        <h3>You may contact support to complete your order.
            <a href="<c:url value='/customerSupport'/>">Support</a>
        </h3>
        <h3>Cancel or Select another payment option. <a href="<c:url value='/cancelPayPal'/>">Cancel Payment</a></h3>
        <c:if test="${isPaymentReset eq true}">
         <h3>Cancel and Return to home page. <a href='<c:url value="/home" />'>Home</a></h3> 
        </c:if>
        
        <hr/>
       
        <h4 class="plus">Technical Support: </h4>
        <div>
            Exception: ${exception.class.canonicalName} <br/>
            <c:if test="${exception.class.cause ne null}">
                Cause: ${exception.class.cause.canonicalName}<br/>
            </c:if>    
            Handler: ${handler}<br/>
            Friendly: ${exception.friendly} <br/>
            Method: ${exception.method} <br/>
            Message: ${exception.message} <br/>
            Response: ${exception.responseCode} <br/>
            <c:if test="${fn:contains(exception.class.canonicalName,'HttpClientException')}">
                Content: ${exception.textMessage} <br/>
                Debug: ${exception.debug} <br/>               
            </c:if>
                
        </div>
        <h4 class="plus">PayPal Issue:</h4>
        <div>
            <c:if test="${(not empty payPalError)}">
                <c:out value="Error name: ${payPalError.name}" /><br/> 
                <c:out value="Message:${payPalError.message}" /><br/>
                <c:forEach var="item" items="${payPalError.details}">
                    <c:out value="Field=${item.field}"/>: <c:out value="Issue=${item.issue}"/><br/>
                </c:forEach>
            </c:if>
            <c:if test="${payPalError eq null}">
                None reported (or did not deserialize).
            </c:if> <br/>
            <p>Debug:</p>  
            <p>${exception.debug}</p>
        </div>
        <h4 class="plus">Messages:</h4>
        <div>${messages}</div> 
        <h4 class="plus">Trace:</h4>
        <div>${trace}</div>       
        </div><!--end container-->
        <script>
            $(document).ready(function(){
                
                $("h4").next().hide();
                
                $("h4").click(function(){
                    
                    $(this).toggleClass("plus minus");
                    $(this).next().toggle();
                    
                });
                
            });
        </script>
    </body>
</html>
