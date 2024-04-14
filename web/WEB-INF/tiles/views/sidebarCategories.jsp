<%-- 
    Document   : sidebarCategories
    Created on : Oct 16, 2017, 11:54:07 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

 <ul>
                    <c:forEach var="itm" items="${applicationAttributes.categories}">
                        <c:url var="selectedCategoryUrl" value="/category/selectedCategory">
                            <c:param name="categoryId" value="${itm.categoryId}" />
                        </c:url>
                        <li><a href="${selectedCategoryUrl}"><c:out value="${itm.name}"/></a></li>
                    </c:forEach>
  </ul>
