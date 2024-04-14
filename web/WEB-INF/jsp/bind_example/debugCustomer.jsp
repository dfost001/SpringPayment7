
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
    <head>
        <meta  charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/form.css" rel="stylesheet" />   
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>             
        
        <title>Spring MVC Customer</title>
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../../jspIncludes/navigation.jsp" />
           <!-- left content -->
           <div id="left">
              &nbsp;[left content]
            </div>    
            <div class="center_content" >
                
                <div>
                    <span class="customerCaption">Customer Information</span>
                </div>
               
                    <div class="myPanel">
                        <div class="divCommands">
                            <form action="${pageContext.request.contextPath}/home" method="GET">
                                <input type="submit" value="Continue Shopping" 
                                        class="btn btn-primary"
                                        title="Retain edits for checkout."/>
                            </form>
                        </div><!-- end cancel command --> 
                        
                        <div class="divCommands">
                            <c:if test="${not empty customer.customerId}">
                                <form action="${pageContext.request.contextPath}/reset" method="POST">                                
                                    <input type="submit" value="Reset" 
                                            class="btn btn-primary"/>                                                
                                </form>
                            </c:if> 
                            <c:if test="${empty customer.customerId}">
                                <input type="reset" value="Reset" class="btn btn-primary"/>
                            </c:if>
                        </div><!--end reset command-->
                        <div class="divCommands">
                        <form action="${pageContext.request.contextPath}/confirmCart" method="GET">
                                
                                <c:if test="${(customerAttributes.customerValid)}">
                                    <input type="submit" value="Continue Checkout" 
                                           class="btn btn-primary" name="continue"/>
                                </c:if> 

                                <c:if test="${(not customerAttributes.customerValid)}">
                                    <input type="submit" value="Continue Checkout" disabled
                                           name="continue" class="btn btn-primary"/> 
                                </c:if>                       
                        </form>
                        </div> 
                </div><!--end panel-->
              
                
                <form:form action="${pageContext.request.contextPath}/debugUpdateCustomer" commandName="customer">                  
                    
                    <table   id="custInfoTbl">
                        <tr>
                            <td>&nbsp;</td>
                            <td colspan="2">
                                <input type="submit" value="Submit Changes" style="width:185px"/><br/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label>
                                    First Name:
                                </label>
                            </td>
                            <td>
                                <form:input path="firstName" type="text"  required="true" />
                                <div><form:errors path="firstName" cssClass="bindingErr"></form:errors></div>                               
                                </td>
                                <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                    <label>
                                        Last Name:
                                    </label>
                            </td>
                            <td>
                                <form:input path="lastName" type="text" required="true"  />
                                <div><form:errors path="lastName" cssClass="bindingErr"></form:errors></div>
                            </td>
                            <td></td>
                        </tr>
                            <tr>
                                <td>
                                    <label>
                                        E-Mail:
                                    </label>
                                </td>
                                <td>
                                <form:input path="email" type="email" 
                                            placeholder="youremail@mail.com" 
                                            required="true"/>
                                <div><form:errors path="email" cssClass="bindingErr"></form:errors></div>
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td>
                                    <label>Active:</label>
                                </td>
                                <td>
                                <form:select path="active" >
                                   <form:option value="1" label="True" selected="true" />
                                   <form:option value="0" label="False" />
                                </form:select>
                             </td>
                             <td>&nbsp;</td>
                    </tr>

                        <tr>

                            <td><label> Customer Id: </label></td>

                            <td> <form:input path="customerId" readonly="true" disabled="true"
                                        style="width:25pt;" id="customerId" name="customerId"/>

                            <td>&nbsp;</td>
                        </tr>

                        <tr>

                            <td><label>Create Date:</label> </td>
                            <td>
                                <form:input path="createDate"  /> 
                                <form:errors path="createDate" cssClass="bindingErr" />
                            </td>
                            <td>&nbsp;</td>

                        </tr> 
                        <tr>

                            <td><label>Last Update:</label></td>
                            <td>
                                <form:input path="lastUpdate"  /> 
                                <form:errors path="lastUpdate" cssClass="bindingErr" />
                            </td>
                            <td>&nbsp;</td>
                        </tr> 
                        <tr>
                            <td>
                                <label>Address Line 1:</label>
                            </td>
                            <td>
                                <form:input path="addressId.address1"  required="true" id="address1"/><br/>
                                <form:errors path="addressId.address1" cssClass="bindingErr"></form:errors>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <label>Address Line 2:</label>
                            </td>
                            <td>
                                <form:input path="addressId.address2"  id="address2"/> <br/>
                                <form:errors path="addressId.address2" cssClass="bindingErr"></form:errors>
                            </td>
                            <td>&nbsp;</td>
                        </tr>

                        <tr>
                            <td><label>City:</label>          
                                <br/>   
                            </td>
                            <td style="vertical-align: top">
                                <form:select id="citySelect"  
                                             path="addressId.cityId.cityId"
                                             required="true" > 
                                    <form:option value="" label="Please select a city" />
                                    <form:options
                                        items="${cities}" itemValue="cityId" itemLabel="cityName" />                                   
                                </form:select>
                                <label id="cityIdLabel">${customer.addressId.cityId.cityId}</label><br/>                             
                                <form:errors path="addressId.cityId.cityId" cssClass="bindingErr"></form:errors> 
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        
                        <tr>
                            <td>
                                <label>Region:</label>
                            </td>
                            <td>
                                <form:input path="addressId.district"  id="district" required="true"/>
                                <form:errors path="addressId.district" cssClass="bindingErr"></form:errors>
                            </td>
                            <td>
                                <select name="state" id="state">
                                   
                                </select> 
                            </td> 
                                    
                        </tr>
                        <tr>
                            <td>
                                <label>Phone:</label>
                            </td>
                            <td>
                                <form:input path="addressId.phone"  id="phone" />
                                <form:errors path="addressId.phone" cssClass="bindingErr"></form:errors>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <label>Postal Code:</label>
                            </td>
                            <td>
                                <form:input path="addressId.postalCode"
                                            name="postalCode" 
                                            id="postalCode"                                             
                                            required="true" />
                                <form:errors path="addressId.postalCode" cssClass="bindingErr"></form:errors>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td style="width:200px"></td>
                            <td style="padding-left:50px">
                              
                             <input type="submit" style="width:185px;" value="Submit Changes"/>
                                
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                    </table>
                </form:form>
               
            </div><!-- end centerContent -->
            
           
        </div><!--end container -->
    </body>
</html>
