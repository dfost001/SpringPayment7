
<%--form:form encloses data table only--%>


<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
        <script src="${pageContext.request.contextPath}/resources/javascript/chosen/chosen.jquery.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/javascript/addressVerify.js"></script>        
        <script src="${pageContext.request.contextPath}/resources/javascript/customerForm.js" type="text/javascript"></script> 
        <title>${constantUtil.customerTitle}</title>
    </head>
    <body>
        <input type="hidden" value="${addressTypeEnum}" name="addressTypeEnum" id="addressTypeEnum"/>
        <div class="container-fluid">
            <jsp:include page="../jspIncludes/navigation.jsp" />
           
            <c:if test="${empty customer.customerId}">
                <c:set var="alertMessage" value="Please create your customer information. Then, submit." />
            </c:if> 
            <c:if test="${not empty customer.customerId}">
                <c:set var="alertMessage"
                        value="Please review your information. You may need to edit or press 'Submit'." />
            </c:if>                      
             
            <div class="center_content" >
               
                <jsp:include page="../jspIncludes/handleAjaxError.jsp" />
                
                <div>
                   <span class="customerCaption">Customer Information</span>  
                </div>
                <div>${pageContext.request.requestURL}<br/>
                    ${customerAttributes.cancelCustEditUrl}
                </div>
               
                    <div class="myPanel">
                        <div class="divCommands">
                            <form action="${pageContext.request.contextPath}/home" method="GET">
                                
                                <input type="submit" value="Continue Shopping" 
                                        class="btn btn-primary"
                                        title="Retain edits and return."/>
                            </form>
                        </div><!-- end cancel command --> 
                        <c:if test="${fn:contains(customerAttributes.cancelCustEditUrl,'shipping')}">
                         <div class="divCommands">                             
                          <form action="${pageContext.request.contextPath}/customer/cancelEdit" method="POST">                      
                              
                                                              
                               <input type="submit" value="Cancel Edit" 
                                    class="btn btn-primary"
                                    title="Reset info and exit form."/>                                 
                                                     
                         </form>       
                        </div><!--end reset command--> 
                        </c:if>
                         <div class="divCommands">
                            <form action="${pageContext.request.contextPath}/cancelLogin" method="POST">
                                
                                <input type="submit" value="Cancel Login" 
                                       class="btn btn-primary"                                       
                                       title="Login again."/>
                            </form>
                        </div><!-- end cancel command -->                                     
                        <div class="divCommands">
                          <form action="${pageContext.request.contextPath}/reset" method="POST">
                              
                            <c:if test="${not empty customer.customerId}">
                                                              
                                    <input type="submit" value="Reset" 
                                            class="btn btn-primary"
                                            title="Restore and re-edit."/>                                                
                                
                            </c:if>                            
                         </form>       
                        </div><!--end reset command-->                        
                </div><!--end panel-->
                <form:form action="${pageContext.request.contextPath}/updateCustomer" commandName="customer"> 
                    
                <input type="hidden"   name="addressTime"  value="${customerAttributes.addressUpdateTime}" />
                    
                <div class="alert alert-success">
                    ${pageScope.alertMessage}    
                     <input type="submit" value="Submit" class="alert-link" />    
                </div> 
               
                <c:if test="${not empty customerAttributes.successMessage}">
                   <div class="globalMessage successMsg"> 
                
                    ${customerAttributes.successMessage}  
                   </div>
                </c:if>
                <c:if test="${bindingResult.errorCount > 0}">
                     
                   <div id="bindingResult" class="globalMessage bindingErr">
                    <ul>
                        <c:forEach var="err" items="${bindingResult.fieldErrors}">
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
