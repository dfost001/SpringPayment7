<%-- 
    Document   : confirmCart
    Created on : Nov 6, 2015, 4:40:14 PM
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
        <title>Confirm Cart</title>
        <link href='<c:url value="/resources/css/bootstrap.min.css" />' rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/payPalAuthorize.css"  rel="stylesheet"/>
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>         
    </head>
   
    <body>
              
        <div class="container">
        
            <jsp:include page="../jspIncludes/navigation.jsp" />  
            
            <c:if test="${IS_CANCELLED}">
                <div class="alert alert-danger">
                    <ul> 
                        <li>If you have started a payment, it has been stopped. </li>                    
                    </ul>
                </div>
            </c:if>    
         
        <h1>Confirm Cart</h1><br/>    
            
        
        <div class="panel panel-info">
            <div class="panel-body">
                
                 <div style="float:left; padding-left:5pt">
                        <form action="${pageContext.request.contextPath}/shippingAddress/showSelect" method="GET">
                            <input type="submit" value="Edit Customer" 
                                   class="btn btn-default btn-sm"/>
                        </form>
                    </div>    
                    <div style="float:left; padding-left:5pt">    
                        <form action="${pageContext.request.contextPath}/viewCart/requestView" method="GET">
                            <input type="submit" value="Edit Cart" 
                                   class="btn btn-default btn-sm"/>
                        </form> 
                    </div> 
                    <div style="float:left; padding-left:5pt">    
                        <form action="${pageContext.request.contextPath}/shippingAddress/showSelect" method="GET">
                            <input type="submit" value="Edit Ship To"
                                    class="btn btn-default btn-sm"/>
                        </form> 
                    </div>
                    <div style="float:right; padding-left:5pt; margin-right:300px">          
                            <form action="<c:url value='/paypalCheckout' />" method="post">
                                <input type="image" alt="Login with PayPal" 
                                       src="${pageContext.request.contextPath}/resources/images/paypalgraph2.png" 
                                       width="75" height="47" />
                            </form>
                    </div>
                    <div style="float:right; padding-left:5pt">          
                            <form action="<c:url value='/paypalDirect' />" method="post">
                                 <input type="image" alt="Checkout with credit card" style="float:right"
                                     src="${pageContext.request.contextPath}/resources/images/creditcard2.png" 
                                     width="200" height="36" />
                            </form> 
                    </div>
            </div><!--end panel-body-->
            
        </div><!--end panel-->       
       
      
        <br style="clear:right" />       
       

            <div class="divInfoCust"><!--payPalAuthorize.css-->

                <table class="table">
                    <caption>Ship To:</caption>
                    <tr> 
                        <td>${selectedShipAddress.firstName} ${selectedShipAddress.lastName}</td>
                    </tr>
                    <c:if test="${not empty selectedShipAddress.addressId.address2}">
                        <tr class="info">
                            <td>${selectedShipAddress.addressId.address2}</td>
                        </tr>
                    </c:if>
                    <tr>
                        <td>${selectedShipAddress.addressId.address1}</td>
                    </tr>
                    <tr>
                        <td>${selectedShipAddress.addressId.cityId.cityName},
                            ${selectedShipAddress.addressId.district}&nbsp;
                            ${selectedShipAddress.addressId.postalCode}</td>
                    </tr>
                    <tr>
                        <td>${selectedShipAddress.addressId.cityId.countryId.countryName}</td>
                    </tr>
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td style="font-weight:bolder">Notifications: ${selectedShipAddress.email}</td>
                    </tr>

                </table> <br/> 
                <div style="font-style:italic">
                    ${customer.firstName} ${customer.lastName}<br/>
                    Customer Since: &nbsp; <fmt:formatDate 
                        value="${customer.createDate}" 
                        type="date" 
                        pattern="EEE, d MMM, yyyy" /><br/>
                    Customer Id: &nbsp; ${customer.customerId}
                </div> 
                <br/>
                
                
            </div><!--end divInfoCustomer-->              

                   
        
        <c:if test="${cart.count gt 0}"> 
             <div class="divInfoPay"><!--payPalAuthorize.css-->
                 <jsp:include page="../jspIncludes/cartTotals.jsp" />
                 <jsp:include page="../jspIncludes/cartItems.jsp" />
             </div>
        </c:if>     
        
        </div><!--end container -->
    </body>
</html>
