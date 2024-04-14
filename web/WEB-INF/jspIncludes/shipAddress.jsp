<%-- 
    Document   : shipAddress
    Created on : Nov 8, 2018, 2:57:23 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                <tr class="info">
                    <td>${selectedShipAddress.addressId.cityId.countryId.countryName}</td>
                </tr>
                
            </table> <br/> 
                <div style="font-style:italic">
                    ${customer.firstName} ${customer.lastName}<br/>
                    Customer Since: &nbsp; <fmt:formatDate 
                            value="${customer.createDate}" 
                            type="date"
                            pattern="EEE, d MMM, yyyy" />
                </div> 
              