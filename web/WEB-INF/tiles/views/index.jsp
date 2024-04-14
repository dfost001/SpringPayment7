<%-- 
    Document   : index
    Created on : Oct 16, 2017, 12:20:12 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  
 
 <table style="border:none;margin:auto;padding:5px;width:40%"  id="paginationTbl">
                    <tr>
                        <c:choose>
                            <c:when test="${pageCalculator.pgNoStart gt 1}">
                                <td><a href="${pageContext.request.contextPath}/spring/previous">
                                  <img src="${pageContext.request.contextPath}/resources/images/previous1.png" 
                                       alt="Previous"/>
                                    </a></td>
                            </c:when>
                            <c:otherwise>
                                <td> &nbsp;</td>
                            </c:otherwise>
                        </c:choose>
                        <td>[</td>
                        <c:forEach var="itm" begin="${pageCalculator.pgNoStart}" end="${pageCalculator.pgNoEnd}">
                            <c:choose>
                                <c:when test="${pageCalculator.pgSelected eq itm}">
                                    <td><span style="color:black;font-weight:bold">${itm}</span></td>
                                </c:when>
                                <c:otherwise>    
                                     <td><a href="${pageContext.request.contextPath}/spring/page/${itm}">${itm}</a></td>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        <td>]</td>
                        <c:choose>
                            <c:when test="${pageCalculator.pgNoEnd lt pageCalculator.maxPageNo}">
                                <td><a href="${pageContext.request.contextPath}/spring/next">
                                  <img src="${pageContext.request.contextPath}/resources/images/next1.png" 
                                       alt="Next"/></a></td>
                            </c:when>
                            <c:otherwise>
                                <td>&nbsp;</td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </table>
                <br/>
                <table class="filmTable">
                    <thead>
                        <tr>
                            <th>ID</th>   
                            <th>Title</th>
                            <th>Description</th>
                            <th>Category</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${springDbController.filmList}" >
                            <tr>
                                <td><c:out value="${item.filmId}"/></td>
                                <td>                            
                                    <c:url var="itemUrl" value="/product/request">
                                        <c:param name="id" value="${item.filmId}" />
                                    </c:url>    
                                    <a href="${itemUrl}"><c:out value="${item.title}"/></a>
                                </td>

                                <td>
                                    <c:out value="${item.description}" />
                                </td>
                                <td>
                                    <select size="1" style="width:110px">
                                        <c:forEach var="filmCategory" items="${item.filmCategories}" >
                                            <option>${filmCategory.category.name}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>  
                            <tr>
                                <td colspan="2">&nbsp;</td>
                                <td> 20 of ${pageCalculator.recordCount} records.</td>
                                <td>&nbsp;</td>
                            </tr>    
                    </tfoot>
                </table>
                 
