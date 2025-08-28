<%-- 
    Document   : newjsp
    Created on : Sep 11, 2015, 7:58:17 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


           
           <div class="productContent">
               <p class="productTitle">${selectedFilm.title}</p>
               <p class="productDesc">${selectedFilm.description}</p><br/>
               <p style="color:#009900;font-style:italic;font-size:9pt">
                   ${cartController.success}</p>
               <p style="color:#FF6600;font-weight:600">Category: ${cartController.category.name}</p>
               <form method="post" action="${pageContext.request.contextPath}/product/update">
                    <input type="hidden" name="selectedItem" value="${selectedFilm.filmId}" />
                    <input type="hidden" name="category" value="${cartController.category.categoryId}" />
                    <input type="submit" value="Update Cart" class="btn btn-success btn-sm" />                    
                    <select name="qty" id="qty">
                        <c:forEach begin="0" end="25" varStatus="st">
                            <option value="${st.index}"
                            <c:if test="${cart.items[selectedFilm.filmId].qty eq st.index}"> selected
                            </c:if>>${st.index}</option>
                        </c:forEach>
                    </select>
                </form>
               <br/>
                 <div style="float:left">
                   <table class="filmInfo_1">
                       <tbody>
                           <tr>
                               <td class="leftCol">Release Year:</td>
                               <td class="rightCol">${selectedFilm.releaseYear}</td>
                           </tr>
                           <tr>
                               <td class="leftCol">Language:</td>
                               <td class="rightCol">${selectedFilm.languageByLanguageId.name}</td>
                           </tr>
                           <tr>
                               <td class="leftCol">Film Length:</td>
                               <td class="rightCol">${selectedFilm.length} minutes</td>
                           </tr>
                           <tr>
                               <td class="leftCol">Rating:</td>
                               <td class="rightCol">${selectedFilm.rating}</td>
                           </tr>
                       </tbody>
                   </table>
                   <div class="actors">
                       <br/>
                       <h4>ACTORS</h4>                       
                           <c:forEach var="filmActors" items="${selectedFilm.filmActors}">
                               <div class="divActor">
                                   <c:out value="${filmActors.actor.firstName} ${filmActors.actor.lastName}" />
                               </div>                                 
                           </c:forEach>
                       
                   </div><br/><br/>
                 </div><!--end float left-->
                 <div style="float:right;margin:25px 75px auto auto">
                   <table class="filmInfo_2">
                       <tr>
                           <td class="leftCol">Rental Duration:</td>
                           <td class="rightCol">${selectedFilm.rentalDuration} days</td>
                       </tr>
                       <tr>
                           <td class="leftCol">Rental Rate:</td>
                           <td class="rightCol">$ ${selectedFilm.rentalRate}</td>
                       </tr>
                       <tr>
                           <td class="leftCol">Replacement Cost:</td>
                           <td class="rightCol">$ ${selectedFilm.replacementCost}</td>
                       </tr>
                   </table>
                   <div class="qtyDiv">
                     <form method="post" action="${pageContext.request.contextPath}/product/update">
                           <input type="hidden" name="selectedItem" value="${selectedFilm.filmId}" />
                           <input type="hidden" name="category" value="${cartController.category.categoryId}" />
                           <input type="submit" value="Update Cart" class="btn btn-success btn-sm"/>                          
                           <select name="qty" id="qty">
                               <c:forEach begin="0" end="25" varStatus="st">
                                  <option value="${st.index}"
                                    <c:if test="${cart.items[selectedFilm.filmId].qty eq st.index}"> selected
                                    </c:if>>${st.index}</option>
                               </c:forEach>
                           </select>
                       </form><br/><br/>
                       <form action="${pageContext.request.contextPath}/testBinding" method="POST">
                           <input type="submit" name="cmdSelected" value="Retrieve Model" class="btn btn-link"/>
                              &nbsp;
                            <input type="submit" name="cmdSelected" value="Create Model" class="btn btn-link"/>
                       </form><br/>  
                       
                       <form action="${pageContext.request.contextPath}/exception/example/view" method="GET">
                           <input type="submit" value="Exception Example" class="btn btn-link"/>                             
                        </form><br/>     
                       <form action="${pageContext.request.contextPath}/createBind" method="GET">
                           <input type="submit" value="Binding Example" class="btn btn-link"/>                             
                        </form><br/> 
                   </div> <!--end qtyDiv-->  
                 </div><!--end float right-->
                   <br/><br/>
               </div><!--end productContent-->
           <br style="clear:both" />
         
     