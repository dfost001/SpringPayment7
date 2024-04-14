
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    <table class="table table-condensed">
        <caption>Items:</caption>
    <c:forEach var="item" items="${cart.mapAsList()}">
        <tr>
            <td colspan="2">
                <span style="color:#036fab;">${item.film.title}</span>
            </td>
        </tr>
        <tr>
            <td>
               <span>Item Price:</span> 
            </td> 
            <td>               
               <fmt:formatNumber 
                value="${item.film.rentalRate}" type="currency" currencyCode="USD"/>
            </td>
        <tr>
            <td>
                <span>Quantity:</span> 
            </td>
            <td>
                ${item.qty}
            </td>
        </tr> 
        <tr>
            <td>
                 <span>Ext. Price</span>
            </td>
            <td>
               <fmt:formatNumber 
                value="${item.extPrice}" type="currency" currencyCode="USD"/> 
            </td>
        </tr>       
    </c:forEach>
    </table><!--end div table-->    



