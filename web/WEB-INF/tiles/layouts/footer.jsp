<%-- 
    Document   : indexfooter
    Created on : Oct 16, 2017, 11:37:41 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<table>
    <tr>
        <td>Today: <%= new java.util.Date() %></td>
        <td>DVD Store Copyright &copy; 2015</td>
        <td>last revised on <fmt:formatDate value="${applicationAttributes.lastUpdate}"
                        pattern="EEE MMM dd, yyyy"/></td>
    </tr>
</table>
