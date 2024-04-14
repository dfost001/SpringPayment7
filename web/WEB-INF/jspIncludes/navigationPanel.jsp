<%-- 
    Document   : navigationPanel
    Created on : Oct 4, 2018, 11:50:44 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="panel panel-primary">
    <div class="panel-body">                 
       
        
        <div class="divCart">           
            <a href="<c:url value='/viewCart/requestView' />">
                <img src="${pageContext.request.contextPath}/resources/images/cart.gif" 
                     alt="Cart Icon"/>
                &nbsp;
                ${cart.count} item(s)
            </a>   
               
        </div>     
                  
            <div class="divHome">
                <a href="${pageContext.request.contextPath}/home">Home</a>
            </div>
        
        
    </div><!--end panel body-->     
</div><!--end panel-->
 
