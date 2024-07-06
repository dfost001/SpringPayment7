<%-- 
    Document   : selectedCategory
    Created on : Jan 5, 2018, 4:51:40 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

    
    <!-- script declared in body, so defer until DOM is created--> 
    <script src="${pageContext.request.contextPath}/resources/javascript/selectedCategory.js"
          defer="defer"></script> 
          
      <div class="categoryContent" >    
      <p class="selectedCategoryTitle">${categoryController.selectedCategory.name}</p>
      <div class="categoryMessages" style="padding-left: 10px">
      <c:if test="${not empty categoryController.successMessages}">
          <p style="color:#00BB00"><b>Success:</b></p>
          <ul>
              <c:forEach var="message" items="${categoryController.successMessages}">
                  <c:choose>
                      <c:when test="${fn:contains(message, updateCartUtility.warningKey)}">
                         <li style="color:#FF6600">${message}</li>
                      </c:when>
                      <c:otherwise>
                         <li style="color:#00BB00">${message}</li>
                      </c:otherwise>     
                  </c:choose>
              </c:forEach>
          </ul>
      </c:if>
      <c:if test="${not empty categoryController.errorMessages}">
          <p style="color:#FF0D20"><b>Errors:</b></p>
          <ul>
              <c:forEach var="message" items="${categoryController.errorMessages}">
                  <li style="color:#FF0D20">${message}</li>
              </c:forEach>
          </ul>
      </c:if>
      </div>    
      <form action='<c:url value="/category/updateSelected" />' method='post'>
          <input type="hidden" name="categoryId" value="${categoryController.selectedCategory.categoryId}" />
          
          <table class="table table-striped"> 

              <tr>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td><input type="submit" name="updateCmd" value="Update Cart" class="btn btn-info" /></td>

              </tr> 
              <tr>                  
                  <th>Title</th>
                  <th>Price</th>
                  <th>Description</th>
                  <th>Select</th>
              </tr>
              <tbody>
                  <c:forEach var="item" items="${categoryController.filmList}"> 
                      
                      <tr>
                          
                          <td>                            
                              <c:url var="itemUrl" value="/product/request">
                                  <c:param name="id" value="${item.filmId}" />
                              </c:url>    
                              <a href="${itemUrl}"><c:out value="${item.title}"/></a>
                          </td>
                          <td>
                              <fmt:formatNumber value="${item.rentalRate}" type="currency" 
                                                currencySymbol="$" />
                          </td>
                          <td>
                              <c:out value="${item.description}" />
                          </td>
                          <td><input type="checkbox" name="select"  
                              value="${item.filmId}"
                              <c:if test="${cart.items[item.filmId] ne null}"> checked </c:if> /></td>
                      </tr>
                      <tr class="rowHidden">
                          <td>&nbsp;</td>
                          <td>&nbsp;</td>
                          <td colspan="2"><label style='color:#ff6600'>Quantity:</label>&nbsp;
                              <input type="text" name="${item.filmId}"
                              value="${cart.items[item.filmId].qty}" /></td>
                      </tr>
                  </c:forEach>
              </tbody>
              <tr>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td><input type="submit" name="updateCmd" value="Update Cart" class="btn btn-info" /></td>
              </tr> 
          </table>
      </form>    
      </div><!--end categoryContent-->
