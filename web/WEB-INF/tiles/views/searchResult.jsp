<%-- 
    Document   : searchResult
    Created on : Sep 17, 2018, 8:19:55 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<form action="<c:url value='/search/doSearch' />" method="get">
    <div class="panel panel-info">

        <div class="panel-body"> 
            
            <div style="width:60%;margin:auto;font-size:12pt">
                <label>Search Title:</label>
                <input type="text" name="text" size="60" style="height:40px"/>
                <button class="btn btn-info btn-sm"><span class="glyphicon glyphicon-search"></span></button>
                <br/><br/>
                <div style="font-size:12pt;color:#FF6600;font-style:italic">
                    ${searchController.message} 
                </div>
            </div>
            
            
            <div style="width:600px;margin:auto">
                <c:forEach var="film" items="${searchController.films}">
                <div style="width:300px;float:left;padding:15px;font-size:12pt;border-bottom:1px solid #DDDDDD">
                    <c:url var="url" value='/product/request'>
                        <c:param name="id" value="${film.filmId}" />
                    </c:url>
                    <a href="${url}">${film.title}</a><br/>
                    Rental Rate: <fmt:formatNumber type="currency" 
                                      currencyCode="USD" value="${film.rentalRate}" /><br/>
                    Duration: ${film.rentalDuration} days <br/>
                    <c:forEach var="itm" items="${film.filmCategories}">
                        Category: ${itm.category.name}<br/>
                    </c:forEach>                    
                </div>
                </c:forEach>
            </div><!-- end listing -->    
            
        </div><!--end panel body-->

    </div><!--end panel-->
</form>
    
   
       
       
  
