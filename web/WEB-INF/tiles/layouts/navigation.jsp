<%-- 
    Document   : navigation
    Created on : Oct 16, 2017, 11:08:49 AM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

 
<div class="panel panel-primary">
    <div class="panel-body">
        <form action="<c:url value='/cancelLogin' />" method="POST">  
         <ul class="nav nav-pills" role="tablist">
             
             <li><a href="${pageContext.request.contextPath}/home">Home</a></li> 
             
             <li> 
                <c:url var="searchUrl" value='/search/request' />                        
          
               <a href="${searchUrl}">
                   <span class="glyphicon glyphicon-search"></span>
                   Search Title</a>
             </li>
             
             <li>
                <c:url var="orderUrl" value='/orderHistory/login' >
                   <c:param name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
                </c:url> 
            
               <a href="${orderUrl}">Order History</a>
             </li>
             
             <li>
                <a href="<c:url value='/viewCart/requestView' />"> 
                  <img src="${pageContext.request.contextPath}/resources/images/cart.gif" alt="View Cart"/>
                   ${cart.count} item(s)&nbsp;
                </a>
             </li>
             <c:if test="${sessionScope.customer ne null}">
             <li>
                 <input type="submit" value="Cancel Login" 
                        class="btn btn-link" style="color: #045491; font-weight : bold; margin-top: 5px" />
             </li>
             </c:if>
             <c:if test="${sessionScope.customer eq null}">
             <li>
                 <input type="submit" value="Cancel Login" disabled
                    class="btn btn-link" 
                    style="color: #045491;font-weight: bold; margin-top: 5px; cursor: not-allowed" />
             </li>
             </c:if>
         </ul>
        </form>
           <form action="<c:url value="/checkout/request/completed"/>" method="POST"> 
                <div style="float:right">
                  <c:if test="${(cart.count > 0)}">                
                       
                       <input type="hidden" name="${constantUtil.loginTimeKey}"
                              value="${customerAttributes.loginTime}" />
                 
                      <input type="submit" id="linkCustId"
                             value="${constantUtil.checkoutValue}" 
                             name="${constantUtil.commandName}"
                             class="btn btn-link" 
                             style="color: #045491; font-weight : bold;"/> 
                    </form>
                  </c:if>   
                 <span class="glyphicon glyphicon-user"></span>
                 &nbsp; ${cookie.customerName.value}  
                </div>          
           </form>
    </div><!--end panel body-->     
   </div><!--end panel-->

 <!--Javascript dropdown login-->                
<c:if test="${(sessionScope.customer eq null) and (cart.count gt 0)}">    
    <div class="dropdownLogin" >
        <div id="loginCloseGlyph" style="float:right;margin-right:20px">
            <a href="#"><b><span class="glyphicon glyphicon-remove-sign"></span></b></a></div>
        <form method="POST" 
              action="${pageContext.request.contextPath}/checkout/request/dropdown" > 
            
            <input type="hidden" name="${constantUtil.currentUrlKey}" value="${constantUtil.currentUrl}" />
            
            <input type="hidden" name="${constantUtil.loginTimeKey}"  value="${customerAttributes.loginTime}" />
            
            <label>Enter your Customer ID:</label><br>
            
            <input id="inputCustId" name="inputCustId"
                   value="${ID_ERROR_VAL}"
                   type="text" size="24" autocomplete="off" />                   
            
            <p id="IdErrorMsg" class="errorStyle">${ID_ERROR_MESSAGE}</p>
            <div style="float:left">
                <input name="${constantUtil.commandName}" class="btn btn-primary btn-sm"
                   type="submit" value="${constantUtil.submitValue}" /> 
            </div>             
            <div style="float:left;padding-left:12px">
                 <input name="${constantUtil.commandName}" class="btn btn-success btn-sm"
                   type="submit" value="${constantUtil.registerValue}" />
            </div>
        </form> 
    </div><!--end dropdown -->
</c:if>           


            