<%-- 
    Document   : customerInfo
    Created on : Mar 31, 2017, 3:22:21 PM
    Author     : Dinah
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="divInfoCust"><!--payPalAuthorize.css-->
           
            <table class="table">
                <caption>Ship To:</caption>
                <tr class="info">
                    <td>${customer.firstName} ${customer.lastName}</td>
                </tr>
                <tr class="info">
                    <td>${customer.addressId.address1}</td>
                </tr>
                <tr class="info">
                    <td>${customer.addressId.cityId.cityName},
                        ${customer.addressId.district}&nbsp;${customer.addressId.postalCode}</td>
                </tr>
                <tr class="info">
                    <td>${customer.addressId.cityId.countryId.countryName}</td>
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

