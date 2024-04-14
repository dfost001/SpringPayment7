<%-- 
    Document   : viewCart
    Created on : Jan 20, 2020, 4:17:19 PM
    Author     : dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    li{ 
      font-size:10pt;
      font-weight:bolder
    }
      
</style>



<div style="width:800px;position:relative;margin-left:270px;">
    
   <p class="selectedCategoryTitle">Your Cart <br/>
    <span style="color:#999999; font-size:12pt"> Grand Total: &dollar; ${cart.formattedGrand}</span> </p>    
    
    <p>${viewCartController.emptyCartMessage}</p> 
    
    <c:if test="${not empty viewCartController.success}">
        <ul>  
            <c:forEach var="message" items="${viewCartController.success}">
                  <c:choose>
                      <c:when test="${fn:contains(message, updateCartUtility.warningKey)}">
                         <li style="color:#FF0D20">${message}</li>
                      </c:when>
                      <c:otherwise>
                         <li style="color:#00BB00">${message}</li>
                      </c:otherwise>     
                  </c:choose>
              </c:forEach>
        </ul>    
        
    </c:if>   
     
    <c:if test="${not empty viewCartController.errors}">
          <p style="color:#FF6600">${viewCartController.errors[0]}</p>          
    </c:if>
    
    
    <c:forEach var="item" items="${viewCartController.cartItems}" varStatus="status">        
       
            
            <form action="<c:url value="/viewCart/update"  />" method="post">
                
                <input type="hidden" name="filmId" value="${item.film.filmId}" />
                <input type="hidden" name="categoryId" 
                       value="${viewCartController.cartItemsCategory[status.index].categoryId}" />
                
                <table class="viewCartTable">
                    <tr>
                        <td> 
                        <img src="<c:url value="/resources/images/dvd-img-90.jpg" />"
                             width="90" height="90" alt="icon" />
                        </td>
                        <td style="width:200px">
                        ${item.film.title} <br/>
                        <label>Category:</label> ${viewCartController.cartItemsCategory[status.index].name}
                        </td>
                        <td style="width:160px">
                            <label>Id: </label><label>
                                &nbsp;${item.film.filmId}</label><br/>
                            
                            <label>Qty:</label><label>&nbsp;${item.qty}</label><br/>
                            
                            <label>Price:</label><label class="rightAlign">
                                &nbsp;&dollar;${item.film.rentalRate}</label><br/>
                            
                            <label>Ext. Price:</label><label class="rightAlign">
                                &nbsp;&dollar;${item.extPrice}</label><br/>
                            
                        </td>
                        <td style="width:200px;vertical-align:25px">
                            <button  type="button" data-toggle="collapse" 
                                     data-target="#editQty-${item.film.filmId}" class="btn btn-link"> 
                                Edit 
                                <span  class="glyphicon glyphicon-triangle-bottom"></span></button>
                            <div id="editQty-${item.film.filmId}" class="collapse">
                                <span style="font-size:10pt;font-family: Arial">
                                    Quantity:</span>
                                <input type="text" size="10" name="quantity" />
                                <input type="submit" class="btn btn-link" value="Submit" name="command" />
                            </div>
                        </td>
                        <td style="vertical-align: 25px">
                           <input type="submit" class="btn btn-link" value="Delete" name="command" />
                        </td>
                </tr>
                </table>    
            </form>
         </c:forEach>
          
       </div>   
          
       <div style="width:300px;margin-left:600px">
        <jsp:include page="../../jspIncludes/cartTotals.jsp" />
    </div> 
           

