<%-- 
    Document   : cartTotals
    Created on : Apr 30, 2017, 4:25:43 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<table class="table table-condensed">
                <caption>Purchase Amount:</caption>
                <tr>
                    <td>Subtotal:</td>
                    <td class="rightAlign">
                        <fmt:formatNumber value = "${cart.subtotal}" 
                                          type="currency"
                                          currencyCode="USD" /></td>
                </tr>
                <tr>
                    <td>Shipping Fee:</td>
                    <td class="rightAlign"><fmt:formatNumber value = "${cart.shippingFee}" 
                                          type="currency"
                                          currencyCode="USD" /></td>
                </tr>
                <tr>
                    <td>Tax:</td>
                    <td class="rightAlign">${cart.taxAmount}</td>
                </tr>
                <tr>
                    <td>Grand Total:</td>
                    <td class="rightAlign">
                        <fmt:formatNumber value = "${cart.grandTotal}" 
                                          type="currency"
                                          currencyCode="USD" /></td>
                </tr>
            </table>