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
        <title>Select Shipping Address</title>
        <style>
            .parentAddressContainer {
                width: 900px;
            }
            
            .customerContainer {
                border-bottom: 1px solid #0000BB;
                width:300px;
                float:left;
            }
            #selectAlertDiv{
                position:relative;
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
                <div id="selectAlertDiv">
                <c:if test="${not empty emptySelectionMsg}">
                    <div class="alert alert-danger alert-dismissable">
                        ${emptySelectionMsg}
                    </div>
                </c:if>
                </div><!--end selectAlertDiv--><!--backup for JavaScript evaluation-->
                <c:if test="${not empty shipAddressNotFoundMessage}">
                    <div class="alert alert-danger">
                        ${shipAddressNotFoundMessage}
                    </div>
                </c:if>
                <form action="<c:url value='/shippingAddress/customerAction'/>" method="post" >
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            
                            <input type="submit" name="Select" value="Select" class="btn btn-sm btn-success" />
                                        &nbsp;&nbsp;
                            <input type="submit" name="Edit" value="Edit" class="btn btn-sm btn-default" />
                                        &nbsp;&nbsp;
                            <input type="submit" name="Create" value="Add New" class="btn btn-sm btn-info" />

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
                            <table class="table">
                                <tr>
                                    <td>
                                       <input type="checkbox" value="${id}_${status.index}" name="selectedAddress"
                                       <c:if 
                                           test="${status.index eq shippingAddressController.previousSelected}">
                                           checked</c:if> />
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
                                        ${postal.addressId.district}&nbsp;${postal.addressId.postalCode}</td>
                                </tr>
                                <tr>
                                    <td>${postal.addressId.cityId.countryId.countryName}</td>
                                </tr>                                
                            </table> 
                        </div><!--end customer container-->  
                        </c:forEach>

                    </div><!--end parentAddressContainer-->  

                </form>

            </div><!--end center_content-->
        </div><!--end container-->
    </body>
</html>
