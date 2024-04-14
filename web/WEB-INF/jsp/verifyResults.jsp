<%-- 
    Document   : verifyResults
    Created on : Apr 19, 2018, 10:51:46 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>       
        <meta  charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/form.css" rel="stylesheet" />   
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery.min-3.3.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>
        <title>Verification Results</title> 
        <style>
            .disabled { cursor:not-allowed; }
        </style>
    </head>
    <body>
        <div class="container">
            <div id="top">
                <jsp:include page="../jspIncludes/logo.jsp" />
            </div><!-- end top -->
            <h3>Verification Results</h3>      
            <jsp:include page="../jspIncludes/navigationPanel.jsp" />
           
            
           
            <div class="alert alert-info" id="alertVerify"> 
               <form action="<c:url value='/confirmCart' />" method="post">
                   
                <input type="hidden" name="submitDisabled" value="${submitDisabled}" />
                
               <img src="<c:url value='${addressSvcResult.viewStyle.iconInfo}'/>" alt="info-icon"/>
                ${addressSvcResult.informationMessage}     
                <input type="submit" value="Continue to Checkout" 
                           id="continueToCheckout"
                           class="alert-link" 
                           style="float:right;padding-right:25px;width:190px"/>
                <a href="<c:url value='/shippingAddress/showSelect'/>"
                           class="alert-link"
                           style="float:right;padding-right:30px;width:80px">Change</a><br/> 
                   
                <img src="<c:url value='${addressSvcResult.viewStyle.iconShipTo}'/>" alt="info-icon"/>
                Ship To: ${selectedShipAddress.firstName}&nbsp;${selectedShipAddress.lastName},
                        ${selectedShipAddress.addressId.address1},
                        ${selectedShipAddress.addressId.cityId.cityName}&nbsp;
                        ${selectedShipAddress.addressId.district},
                        ${selectedShipAddress.addressId.postalCode}<br/>                                                
                   
               <img src="<c:url value='${addressSvcResult.viewStyle.iconCustomer}'/>" alt="info-icon"/>
                Customer: ${customer.firstName}&nbsp;${customer.lastName},
                        ${customer.addressId.address1},
                        ${customer.addressId.cityId.cityName}&nbsp;
                        ${customer.addressId.district},
                        ${customer.addressId.postalCode} <br/>
                        
                <c:forEach var="msg" items="${addressSvcResult.mvcAnalysisShipToAddress}">
                   <img src="<c:url value='${addressSvcResult.viewStyle.iconShipTo}'/>" alt="info-icon"/>    
                   Ship To: ${msg}  <br />                
                </c:forEach>
                <c:forEach var="msg" items="${addressSvcResult.mvcAnalysisCustomer}">
                   <img src="<c:url value='${addressSvcResult.viewStyle.iconCustomer}'/>" alt="info-icon"/>    
                   Customer: ${msg}  <br />                
                </c:forEach>   
                </form>               
            </div><!--end alert messages-->
            <div class="${addressSvcResult.viewStyle.panelShipTo}">
                    <div class="panel-heading" style="height:60px">
                        <h4 style="margin-left:25px">
                            <button class="btn btn-link" style="color:${addressSvcResult.viewStyle.colorShipTo}">
                                
                                <span class="glyphicon glyphicon-triangle-bottom"></span> 
                                Ship To Address &nbsp;&nbsp;
                                <img src="<c:url value='${addressSvcResult.viewStyle.iconShipTo}'/>" 
                                     alt="address-info-icon"/> 
                            </button>
                        </h4>
                    </div><!--end panel heading-->
                    <div class="panel-body" style="display:none;color:${addressSvcResult.viewStyle.colorShipTo}">
                        <table class="table">
                            <tr>
                                <td>Delivery address selected:</td>
                                <td>${selectedShipAddress.firstName} &nbsp; ${selectedShipAddress.lastName}<br/>
                                    ${selectedShipAddress.addressId.address1}, 
                                    ${selectedShipAddress.addressId.cityId.cityName}
                                    ${selectedShipAddress.addressId.district},
                                    ${selectedShipAddress.addressId.postalCode}
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <c:if test="${not empty addressSvcResult.svcAnalysisShipAddress}">
                                 <tr>
                                    <td>Delivery Address Uploaded:</td>
                                    <td>${addressSvcResult.svcAnalysisShipAddress.ajaxRequest.street},
                                    ${addressSvcResult.svcAnalysisShipAddress.ajaxRequest.city},
                                    ${addressSvcResult.svcAnalysisShipAddress.ajaxRequest.state}
                                    ${addressSvcResult.svcAnalysisShipAddress.ajaxRequest.zipcode}</td>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td>Delivery Address Evaluated:</td>
                                    <td>${addressSvcResult.svcAnalysisShipAddress.componentsFoundLine}</td>
                                </tr>
                                <tr>
                                    <td>Service Result:</td>
                                    <td>
                                        <ul>
                                        <c:forEach var="msg" items="${addressSvcResult.svcAnalysisShipAddress.svcMessages}">
                                            <li>${msg}</li>
                                        </c:forEach>
                                        </ul>     
                                    </td>
                                    <td>
                                      <form action="<c:url value='/shippingAddress/showSelect' />" method="get">
                                         <input type="submit" value="Edit" class="btn btn-info btn-sm" style="float:left"/>
                                      </form>  </td>
                                </tr>
                            </c:if>
                                <tr>                                  
                                    <td>
                                        Information:
                                    </td>
                                    <td>
                                        <ul>
                                            <c:forEach var="msg" items="${addressSvcResult.mvcAnalysisShipToAddress}">
                                               <li>${msg}</li>
                                            </c:forEach>
                                        </ul>    
                                    </td>
                                    <td>
                                      <form action="<c:url value='/shippingAddress/showSelect' />" method="get">
                                         <input type="submit" value="Edit" class="btn btn-info btn-sm" style="float:left"/>
                                      </form>  
                                    </td>
                                </tr>                             
                        </table>
                        
                    </div><!--end panel-body-->
                    
                </div><!--end panel -->
                        
            
                
                <div class="${addressSvcResult.viewStyle.panelCustomer}" >
                    <div class="panel-heading" style="height:60px"> 
                        <h4 style="margin-left:25px;">
                            <button class="btn btn-link" style="color:${addressSvcResult.viewStyle.colorCustomer}">
                               
                               <span class="glyphicon glyphicon-triangle-bottom"></span> 
                                Customer &nbsp;&nbsp;
                                <img src="<c:url value='${addressSvcResult.viewStyle.iconCustomer}'/>" 
                                     alt="info-icon"/>                            
                            </button>
                        </h4>
                    </div><!--end panel heading-->
                    <div class="panel-body" style="display:none;color:#000088">
                        <table class="table">
                            <tr>
                                <td>
                                    Customer:
                                </td>
                                <td>
                                    ${customer.firstName}&nbsp;${customer.lastName}
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td>Email:</td>
                                <td>${customer.email}</td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td>Phone:</td>
                                <td>${customer.addressId.phone}</td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td>Address Submitted: </td>
                                <td>
                                    ${customer.addressId.address1}, 
                                    ${customer.addressId.cityId.cityName}
                                    ${customer.addressId.district},
                                    ${customer.addressId.postalCode} </td>
                                <td>&nbsp;</td>
                            </tr>
                            <c:if test="${not empty addressSvcResult.svcAnalysisCustomer}">
                                <tr>
                                    <td>Address Uploaded:</td>
                                    <td>${addressSvcResult.svcAnalysisCustomer.ajaxRequest.street},
                                    ${addressSvcResult.svcAnalysisCustomer.ajaxRequest.city},
                                    ${addressSvcResult.svcAnalysisCustomer.ajaxRequest.state},
                                    ${addressSvcResult.svcAnalysisCustomer.ajaxRequest.zipcode}</td>
                                    <td>&nbsp;</td>
                                </tr>
                                <tr>
                                    <td>Service Address Found:</td>
                                    <td>${addressSvcResult.svcAnalysisCustomer.componentsFoundLine}</td>
                                </tr>
                                <tr>
                                    <td>Service Result:</td>
                                    <td>
                                        <ul>
                                        <c:forEach var="msg" items="${addressSvcResult.svcAnalysisCustomer.svcMessages}">
                                            <li>${msg}</li>
                                        </c:forEach>
                                        </ul>     
                                    </td>
                                    <td>
                                       <form action="<c:url value='/shippingAddress/showSelect' />" method="get">
                                          <input type="submit" value="Edit" class="btn btn-info btn-sm" />
                                       </form> </td>
                                </tr>
                            </c:if>
                                <tr>
                                    <td>
                                        <c:if test="${not empty addressSvcResult.mvcAnalysisCustomer}">
                                            Information: </c:if>
                                        <c:if test="${empty addressSvcResult.mvcAnalysisCustomer}">
                                            Information: Same as 'Ship To'</c:if>   
                                    </td>
                                    <td>
                                        <ul>
                                        <c:forEach var="msg" items="${addressSvcResult.mvcAnalysisCustomer}">
                                            <li>${msg}</li>
                                        </c:forEach>
                                        </ul>    
                                    </td>
                                    <td>
                                       <form action="<c:url value='/shippingAddress/showSelect' />" method="get">
                                          <input type="submit" value="Edit" class="btn btn-info btn-sm" />
                                       </form> </td>
                                    </td>
                                </tr>    
                        </table> 
                    </div><!-- end panel-body -->
                </div><!--end panel--> 
                  <div class="panel panel-success" >
                    <div class="panel-heading" style="height:60px">
                        <h4 style="margin-left:25px;">
                            <button class="btn btn-link" style="color:#008800">
                                <span class="glyphicon glyphicon-triangle-bottom"></span> 
                                Shopping Cart &nbsp;&nbsp;
                                <img src="<c:url value='/resources/images/check-20.png'/>" 
                                     alt="check-icon"/>                               
                            </button>
                        </h4>
                    </div><!--end panel heading-->
                    <div class="panel-body" style="display:none;color:#006600">
                        <div style="width:60%">
                        <jsp:include page="../jspIncludes/cartTotals.jsp" />
                        <jsp:include page="../jspIncludes/cartItems.jsp" />
                        </div>
                  </div><!--end panel-body--> 
                </div> <!--end panel--> 
        </div><!-- end container -->
        <script>
            $("h4").click(function(){
                $(this).parent().next().slideToggle(500);
            });
            
            var el = $("input[name='submitDisabled']");
            if(el && el.val() === "true")                
                $("#continueToCheckout").prop("disabled", true)
                     .css("cursor", "not-allowed");                
            
        </script>
    </body>
</html>
