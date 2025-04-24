
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
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery.min-3.3.1.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/resources/javascript/ajax.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/doLists.js" type="text/javascript"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery.maskedinput.js"></script>
        <script src="${pageContext.request.contextPath}/resources/javascript/addressVerify.js"></script>        
        <script src="${pageContext.request.contextPath}/resources/javascript/customerForm.js" type="text/javascript"></script> 
        <title>Shipping Address - ${updateShipAddressController.updateType}&nbsp; 
            ${updateShipAddressController.titleError}</title>
    </head>
    <body>
        <input type="hidden" name="addressTypeEnum" id="addressTypeEnum" value="${addressTypeEnum}" />
        <div class="container-fluid">
            <jsp:include page="../jspIncludes/navigation.jsp" />                     
             
            <div class="center_content" >
               
                <jsp:include page="../jspIncludes/handleAjaxError.jsp" />
                
                <div>
                   <span class="customerCaption">Delivery Address</span>  
                </div>
                <div style="width:400px;" class="alert alert-danger">
                   Pressing the browser refresh will display an error view.
                </div>
               
                    <div class="myPanel">
                        <div class="divCommands">
                            <form action="${pageContext.request.contextPath}/home" method="GET">
                                <input type="submit" value="Continue Shopping" 
                                        class="btn btn-primary"
                                        title="Cancel edit."/>
                            </form>
                        </div><!-- end cancel command --> 
                         <div class="divCommands">
                            <form action="${pageContext.request.contextPath}/updateShipAddress/cancel" 
                                  method="GET">
                                <input type="submit" value="Cancel Edit" 
                                       class="btn btn-primary"                                       
                                       title="Return to select address."/>
                            </form>
                        </div><!-- end cancel command -->  
                    <c:if test="${updateShipAddressController.updateType != 'DELETE'}">    
                        <div class="divCommands">
                            
                                <form action="${pageContext.request.contextPath}/updateShipAddress/reset"
                                      method="POST">
                                    <input type="hidden" 
                                           name="${updateShipAddressController.updateTypeKey}"
                                           value="${updateShipAddressController.updateType}" />
                                    <input type="hidden" name="shipId" value="${shipAddress.shipId}" />
                                    <input type="submit" value="Reset" 
                                            class="btn btn-primary"/>                                                
                                </form>       
                           
                        </div></c:if><!--end reset command-->                        
                </div><!--end panel-->
                <form:form 
                    action="${pageContext.request.contextPath}/updateShipAddress/submit" 
                    commandName="shipAddress">  
                <form:input type="hidden" path="shipId" value="${shipAddress.shipId}"  /> 
                <input type="hidden" name="${updateShipAddressController.updateTypeKey}" 
                       value="${updateShipAddressController.updateType}" />
                <div class="alert alert-success">
                   <c:if test="${empty shipAddress.shipId}">
                       Please create a delivery address. Then, submit.
                       <c:set var="alertLabel" value="Create" />
                   </c:if> 
                   <c:if test="${updateShipAddressController.updateType eq 'UPDATE'}">
                      Please edit address information. Then, submit.
                      <c:set var="alertLabel" value="Edit" />
                   </c:if>
                   <c:if test="${updateShipAddressController.updateType eq 'DELETE'}">
                       Please confirm address to delete. Then, submit.
                       <c:set var="alertLabel" value="Delete" />
                   </c:if>   
                      <input type="submit" value="${alertLabel}" class="alert-link" />     
                </div>   
               
                <c:if test="${not empty customerAttributes.shipAddressSuccessMsg}">
                   <div class="globalMessage successMsg">                 
                       ${customerAttributes.shipAddressSuccessMsg}  
                   </div>
                </c:if>
                <c:if test="${updateShipAddressController.shipAddressBindingResult.errorCount > 0}">
                   <div id="bindingResult" class="globalMessage bindingErr">
                    <ul>
                        <c:forEach var="err" items="${updateShipAddressController.shipAddressBindingResult.fieldErrors}">
                            <li>${err.field}: ${err.defaultMessage}</li>
                        </c:forEach>                       
                        <li>Questions? Please contact support at (123) 123-1234</li>                         
                    </ul>                  
                  </div><!--end globalMessage--> 
                </c:if>      
                
                <jsp:include page="../jspIncludes/showAddressInfo.jsp" />   
                 
                </form:form>
               
            </div><!-- end centerContent -->
            
           
        </div><!--end container -->
    </body>
</html>
