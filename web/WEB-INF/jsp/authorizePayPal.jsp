<%-- 
    Document   : authorizePayPal
    Created on : Nov 7, 2015, 5:01:24 PM
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
        <title>Authorize PayPal Payment</title>
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/payPalAuthorize.css"  rel="stylesheet"/>
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>
        <style>
            h5 {
                background:url(http://localhost:8080${pageContext.request.contextPath}/resources/images/plus1.jpg)
                    no-repeat left top;
                padding-left:30px;
                cursor:pointer;
                font-weight: 500;
                font-size:14pt;    
            }
            .minus {
               background:url(http://localhost:8080${pageContext.request.contextPath}/resources/images/minus1.jpg)
                    no-repeat left top; 
            }
            .closed {display:none}
        </style>
    </head>
   
    <body>
        <div class="container">
        <div id="top">
             <span class="captionTop" style="padding-left:50px;">DVD Store</span>
        </div>
        <br/>    
        <h1>Authorize PayPal Transaction</h1><br/>
        <div style="float:left;width:100px">
            <form action="<c:url value='/paypal/execute' />" method="POST">
                <input type="submit" value="Authorize" class="btn btn-default" />
            </form>
        </div>
        <div style="float:left;width:100px">           
            <form action="<c:url value='/paypal/cancelAuthorize' />" method="POST">    
                <input type="submit" value="Cancel Payment" class="btn btn-default" />          
            </form>
        </div>
        <br style="clear:both" />    
        <div class="divInfoPay">
            <jsp:include page="../jspIncludes/cartTotals.jsp" />
            <jsp:include page="../jspIncludes/cartItems.jsp" />
        </div>        
        <div class="divInfoCust">        
            <table class="table">
                <caption>Ship To:</caption>
                <tr class="info">
                    <td>${selectedShipAddress.firstName} ${selectedShipAddress.lastName}</td>
                </tr>
                <tr class="info">
                    <td>${selectedShipAddress.addressId.address1}</td>
                </tr>
                <tr class="info">
                    <td>${selectedShipAddress.addressId.cityId.cityName},
                        ${selectedShipAddress.addressId.district}&nbsp;${selectedShipAddress.addressId.postalCode}</td>
                </tr>                
            </table> <br/> 
                <div style="font-style:italic">
                    ${customer.firstName} ${customer.lastName}<br/>
                    Customer Since: &nbsp; <fmt:formatDate 
                            value="${customer.createDate}" 
                            type="date"
                            pattern="EEE, d MMM, yyyy" />
                </div>    
        </div><!--end divInfo-->
        <br style="clear:both"/>
        <h5>Support Information</h5>
        <div class="closed">
            <table class="table">       
                <tr><td>Payer Id:</td>
                    <td>${payerId}</td>
                </tr>
                <tr><td>Access Token:</td> 
                    <td> ${tokenResponse.accessToken}" </td>
                </tr>
                <tr><td>Resource Id:</td>
                    <td>${payment.id}</td>
                </tr>
                <tr><td>Create Time:</td>
                    <td>${payment.createTime}</td>
                </tr>
                <tr><td>State:</td>
                    <td>${payment.state}</td>
                </tr> 
            </table>
        </div>
        <br/>
        </div> <!--end container-->
        <script>
         $(document).ready(function(){
            $("h5").click(function(){
                $(this).next().slideToggle(400);
                $(this).toggleClass("minus");
                  
              });
          });
        
        </script>
    </body>
</html>
