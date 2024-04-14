<%-- 
    Document   : navigation
    Created on : Oct 16, 2017, 11:08:49 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

 <form action="<c:url value="/customerRequest"/>" method="POST">
<div class="panel panel-primary">
    <div class="panel-body">
        
        <div class="divLogin">              
            <c:if test="${(cart.count > 0)}">                                  
                 
                      <input type="submit" id="linkCustId"
                             value="Customer Checkout" 
                             class="btn btn-link"/>  
            </c:if> 
            &nbsp;WELCOME&nbsp;${cookie.customerName.value}          
        </div><!--end divLogin-->
        
        <div class="divCart">
            <a href="<c:url value='/viewCart/requestView' />"> 
                <img src="${pageContext.request.contextPath}/resources/images/cart.gif" alt="View Cart"/>
               ${cart.count} item(s)&nbsp;</a>
        </div>   
        
        <div class="divOrderHistory">
            <c:url var="orderUrl" value='/orderHistory/login' >
                <c:param name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
            </c:url> 
            
            <a href="${orderUrl}">Order History</a>
        </div>    
            
        <div class="divSearch">
            <c:url var="searchUrl" value='/search/request' >
                <c:param name="IdErrorRedirectUrl" value="${requestScope.IdErrorRedirectUrl}" />
            </c:url>      
          
           <a href="${searchUrl}">
               <img src="${pageContext.request.contextPath}/resources/images/search-25.png" alt="Search Icon"/>
               Search Title</a>           
               
        </div>     
                  
        <div class="divHome">
                <a href="${pageContext.request.contextPath}/home">Home</a>
        </div>
        
        
    </div><!--end panel body-->     
</div><!--end panel-->
 </form>
 <!--Javascript dropdown login-->                
<c:if test="${(sessionScope.customer eq null) and (cart.count gt 0)}">    
    <div class="dropdownLogin" >
        <div class="loginCloseGlyph">
            <a href="#"><span class="glyphicon glyphicon-remove-circle"></span></a></div>
        <form method="POST" 
              action="${pageContext.request.contextPath}/customerRequest">             
            <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
            <label>Enter your Customer ID:</label><br>
            <input id="inputCustId" name="inputCustId"
                   value="${ID_ERROR_VAL}"
                   type="text" size="24" autocomplete="off"/>
            <p id="IdErrorMsg" class="errorStyle">${ID_ERROR_MESSAGE}</p>
            <input name="cmdName" type="submit" value="Login" />            
        </form>                                            
        <hr/>
        <form method="POST"
              action="${pageContext.request.contextPath}/customerRegister">
            <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
            <input name="cmdName" type="submit" value="Register" />   
            <input type="hidden"  name="inputCustId"  value="-1" />          
        </form> 
    </div><!--end dropdown -->
</c:if>           


            