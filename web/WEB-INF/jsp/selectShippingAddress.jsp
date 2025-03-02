<%-- 
    Document   : selectShippingAddress
    Created on : Oct 17, 2018, 3:38:21 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
        <script src="${pageContext.request.contextPath}/resources/javascript/selectShipAddress.js"></script>
        <title>Address Selection</title>
        <style>
            .parentAddressContainer {
                width: 1000px;
            }
            
            .customerContainer {
                border-bottom: 1px solid #0000BB;
                width:333px;
                float:left;
            }
            #selectAlertDiv{
                position:relative;
            }
            .checkedAddress {
                color: #CCCCFF
            }
           
           
            
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../jspIncludes/navigation.jsp" />
            <div class="center_content" >

                <div>
                    <span class="customerCaption">Select Shipping Address</span>  
                </div>
                <c:if test="${(not empty customerAttributes.shipAddressSuccessMsg)
                               or (not empty customerAttributes.successMessage)}">
                    <div class="alert alert-info">
                        ${customerAttributes.successMessage}                  
                        <br/>   
                        ${customerAttributes.shipAddressSuccessMsg}                                        
                    </div><!--end alert-info-->
                </c:if>
                
                <c:if test="${not empty shippingAddressController.shipAddressNotFoundMessage}">
                    <div class="alert alert-danger">                        
                        ${shippingAddressController.shipAddressNotFoundMessage}<br />
                        To report an application error and complete your order: 
                        <a href="<c:url value='/customerSupport' />" class="alert-link">Customer Support</a>
                    </div>
                </c:if>
               
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            
                            <form action="<c:url value='/shippingAddress/create' />" method="post">
                               <input type="submit" name="Create" value="Add New" class="btn btn-sm btn-info" />
                               <input type="hidden" name="formTime" value="${customerAttributes.formTime}" />
                            </form>
                        </div>
                    </div> 
                    <div class="parentAddressContainer">
                      
                        <c:forEach var="postal" items="${shippingAddressController.addressList}" varStatus="status">  
                            
                            <c:if test="${status.index > 0}">
                                <c:set var="id" value="${postal.shipId}" />
                            </c:if> 
                            <c:if test="${status.index eq 0}">
                                <c:set var="id" value="${postal.customerId}" />
                            </c:if>

                        <div class="customerContainer">
                           <form action="<c:url value='/shippingAddress/customerAction/${id}_${status.index}'/>" method="post" >
                            <input type="hidden" name="hashCode" value="${shippingAddressController.hashList[status.index]}" />
                            <input type="hidden" name="selectedAddress" value="${id}_${status.index}" />
                            <input type="hidden" name="addressNameUpdated" 
                                   value="${postal.firstName}_${postal.lastName}" />
                            <input type="hidden" name="formTime" value="${customerAttributes.formTime}" />
                            <table class="table">
                                <tr>
                                    <td>
                                       <input type="checkbox" id="${id}_${status.index}" <c:if 
                                           test="${status.index eq shippingAddressController.previousSelected}">
                                           checked</c:if>  name="checkedAddress"/>
                                    </td>
                                </tr>
                                <tr> 
                                    <td>${postal.firstName} ${postal.lastName}</td>
                                </tr>                               
                                <tr>
                                    <td>${postal.addressId.address1}</td>
                                </tr>
                                <tr>
                                    <td>${postal.addressId.cityId.cityName},
                                        ${postal.addressId.district}&nbsp;</td>
                                </tr>
                                <tr>
                                    <td>${postal.addressId.postalCode}</td></tr>
                                <tr>
                                    <td>${postal.addressId.cityId.countryId.countryName}</td>
                                </tr>
                                <tr>
                                    <td><input type="submit" name="cmdName" value="Select" 
                                               class="btn btn-sm btn-success" />
                                        &nbsp;&nbsp;
                                        <input type="submit" name="cmdName" value="Edit" 
                                               class="btn btn-sm btn-default" />
                                        &nbsp;&nbsp;
                                        <c:if test="${status.index > 0}">
                                           <input type="submit" name="cmdName" value="Delete" 
                                               class="btn btn-sm btn-danger" />
                                        </c:if>
                                    </td>
                                </tr>                                
                            </table> 
                           </form>      
                        </div><!--end customer container-->  
                        </c:forEach>

                    </div><!--end parentAddressContainer-->  

               

            </div><!--end center_content-->
        </div><!--end container-->
    </body>
</html>
