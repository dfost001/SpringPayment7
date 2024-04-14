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
        <title>Payment Started - Payment Only</title>
        <style>
            .message {
                width: 550px;
                height: 385px;
                border: 1px solid #0000BB;
                background-color:#f7f975;
                border-radius:25px;
                margin: 75px auto auto auto;
                padding-top:20px;
            }            
            .message > h4, .message > p, h5 {
                text-align: center;
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
            You may have navigated away from the PayPal login. <br/>
            Please do not use the browser list to navigate. 
            <a href="${pageContext.request.contextPath}/cancelPayPal" class="alert-link">
                Cancel/Restart Payment</a>
         </div>  <!--end alert --> 
         
        <div class="message">        
            <h5 style="color:#0000AA"> 
            To change your current payment details use the link below.</h5>
            <hr/>
           
            <p><a href="${pageContext.request.contextPath}/cancelPayPal">Cancel/Restart</a></p>            
            
            <h5>To report an application error or to complete your order: </h5>
            
            <p><a href="${pageContext.request.contextPath}/customerSupport">Customer Support</a></p>     
                     
            <p>Customer Support: (123) 123-1234 </p>             
                            
        </div><!-- end message -->        
        <h5 id="support" class="plus">Support:</h5>
        <div class="divSupport">
            <p>${url}</p>
            <p>Handler: ${handler}</p>
            <p>Exception: ${exception.class.canonicalName}
            <p>Message: ${exception.message}</p>
            <c:if test="${not fn:contains(exception.class.canonicalName,
                          'HttpRequestMethodNotSupportedException')}">
                <p>Support: ${exception.technical}</p>
            </c:if>                      
            <p>Trace:</p>
            <p>${trace}</p>
        </div>           
       
        <script>
            $(".divSupport").hide();
            $("#support").click(function(){
                $(this).toggleClass("plus minus");
                $(this).next().toggle(500);
            });
        </script>
        </div><!--end container -->
    </body>
</html>

