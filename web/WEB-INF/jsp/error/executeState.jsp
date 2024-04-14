<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%-- 
    Document   : executeState
    Created on : Jul 10, 2016, 8:34:11 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>    
        <link href="${pageContext.request.contextPath}/resources/css/exceptionView.css" rel="stylesheet" />        
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script>
        <title>PayPal State Error</title>
    </head>
    <body>
        <div class="container">
            <div class="alert alert-danger">
                Select another payment option: &nbsp;&nbsp;
                <a href="<c:url value='/cancelPayPal' />" class="alert-link"> Cancel Payment</a>
            </div>    
            <h1>PayPal Transaction Error</h1>            
            <h2>Contact: 123-1234</h2>
            <h2>Use link below to select another payment option:</h2>
            <form action="<c:url value='/cancelPayPal' />" method="GET">
                 
                  <input type="submit" value="Select Payment Option" class="btn btn-link"
                          style="font-size:12pt"/>
            </form>                    
            
            <hr/><br/>
            <h3 class="plus">Technical Support:</h3>
            <div>
                <p> Handler: ${handler}</p>
                <p> Message: ${exception.message} </p>
                <p> Method: ${exception.method} </p> 
                <p> Request Path: ${url} </p>
                <p> Raw HTTP response: ${exception.httpEntity}</p>
                <p> Trace:</p>          
                <div>${trace}</div>
            </div>
        </div><!--end container-->
        <script>
            $("h3").next().hide();
            $("h3").click(function(){
                $(this).toggleClass("plus minus");
                $(this).next().toggle();
                
            });
            
        </script>
    </body>
</html>
