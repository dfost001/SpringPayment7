<%-- 
    Document   : newjsp
    Created on : Aug 3, 2017, 12:50:17 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
       
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" 
              rel="stylesheet" type="text/css" />
         <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <title>Ajax Error</title>
    </head>
   
    <body>
        <div class="container">
         <h2>Test Ajax Error</h2>
         <h2>Application Error: Please contact support to complete your order.</h2>
         <h2>Contact: 123-1234</h2><br/>
         <h5>${message}</h5>
        
            <c:if test="${addressType eq 'Customer' and recoverable}">
                <form action="<c:url value='/customerRequest' />" method="post"> 
                    <div class="alert alert-danger">
                        This problem may be temporary. To continue click the link.
                        <input type="submit" value="Enter Customer Information" 
                               class="alert-link"/>
                    </div>    
                </form>    
            </c:if>
            <c:if test="${addressType eq 'ShipAddress' and recoverable}">
                <form action="<c:url value='/shippingAddress/showSelect' />" method="get"> 
                    <div class="alert alert-danger">
                        This problem may be temporary. To continue click the link.
                        <input type="submit" value="Select Ship Address" 
                               class="alert-link"/>
                    </div>    
                </form>    
            </c:if>
            <br/>
            
                <h4>You may contact support to complete your order.
                    <a href="<c:url value='/customerSupport'/>">Support</a></h4>
                <br/>   
                <h4>Return to home page. <a href='<c:url value="/home" />'>Home</a></h4>
                
            <hr/>
            <h5 class="plus" >Support:</h5>            
            <div style="display:none">
                <p>HTTP status: ${status} </p>
                <p>XHR Status: ${xhrStatus} </p>
                <p>Exception: ${exceptionName}</p>               
                <p>URL: ${url}</p>
                <p>Message Trace: 
                    <c:out value="${messages}" escapeXml = "false" />                      
                </p>                
                <p>Stack Trace:</p>
                <p><c:out value="${trace}" escapeXml="false" /></p>
                
            </div>
           
                <script>
                    $("h5.plus").click(function(){
                        $(this).toggleClass("plus minus");
                        $(this).next().slideToggle();
                    })
                </script>
        </div>   
    </body>
</html>
        
           
           
          
           
            
  

    
