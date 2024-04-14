<%-- 
    Document   : errHistoryList
    Created on : Apr 10, 2017, 8:39:59 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" %>
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
        <title>Welcome No Session</title>
        <style>
            .message {
                width: 550px;
                height: 385px;
                border: 1px solid #0000BB;
                background-color:#f7f975;
                border-radius:25px;
                margin: 100px auto auto auto;
                padding-top:20px;
            }
            .message > h4, .message > p {
                text-align: center;
            }
            
            hr {
                color:#BBBBBB;
            }
            #support{
                cursor:pointer;
                margin:auto;
                width:550px;
            }
            .plus {
                background-image:url(${pageContext.request.contextPath}/resources/images/plus1.jpg);
                background-repeat:no-repeat;
                background-position:left center;
                padding-left:25px;
               
            }
            .minus {
                background-image:url(${pageContext.request.contextPath}/resources/images/minus1.jpg);
                background-repeat:no-repeat;
                background-position:left center;
                padding-left:25px;
               
            }
            .divSupport {
                 margin:auto; 
                 width:620px;                 
            }   
           
        </style></head>
    <body>
        <div class="container">
        <div id="top">
             <span class="captionTop" style="padding-left:50px;">DVD Store</span>
        </div>     
        <div class="message">
           
                <h4>${requestScope.message}</h4>
                <p>The page you requested cannot be accessed via the browser history/favorites:</p>
                <p>${requestScope.url}</p>
           
            
           
           
            <hr/>
            <p>To continue use the link below</p>
                                             
                    <p><a href="${pageContext.request.contextPath}/home">Continue Shopping</a></p>
                    
               
                    <p>Customer Support: (123) 123-1234</p>          
                   
                    
        </div><!-- end message -->  
        
        <h5 id="support" class="plus">Support</h5>
        <div class="divSupport" style="display:none">
           
            <p>Message: ${pageContext.errorData.throwable.message}</p>
            <p>Exception Class: ${pageContext.exception.class.name} </p>
            <p>Request Uri: ${pageContext.errorData.requestURI}</p>
            <p>Stack Trace:</p>
            <div style="margin-left:20px">
                <c:forEach var="el" items="${pageContext.exception.stackTrace}">
                    <p> ${el.className}.${el.methodName}()[${el.fileName}:${el.lineNumber}]</p>
                </c:forEach>
            </div>
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

