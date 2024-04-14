<%-- 
    Document   : bindModel
    Created on : Jul 21, 2016, 8:18:44 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bind Example</title>
    </head>
    <body>
        <div style="width:60%;margin:50px auto auto auto">
        <h1>Bind Model</h1>
        
        <c:out  value="${msg}" />
        <c:out value="${message}" /> 
        
        <div>
            <a href="<c:url value='/home' />">Return Home</a>
        </div>
        
        <form:form action="${pageContext.request.contextPath}/updateBindModel" 
                   method="post" commandName="bindModel">
            <table width="90%">
                <tr>
                    <td><label>Name:</label></td>
                    <td><form:input path="name" size="40"/></td>
                    <td><form:errors path="name" ></form:errors></td>
                </tr>
                <tr>
                    <td><label>Card No.</label></td>
                    <td><form:input path="card" size="40"/></td>
                    <td><form:errors path="card" ></form:errors></td>
                </tr>
                <tr>
                    <td><label>Expire Month</label></td>
                    <td><form:input path="month" size="40"/></td>
                     <td><form:errors path="month" ></form:errors></td>
                </tr>
                <tr>
                    <td><label>Expire Year</label></td>
                    <td><form:input path="year" size="40"/></td>
                    <td><form:errors path="year" ></form:errors></td>
                </tr>
                <tr>
                    <td><label>Card Type</label></td>
                    <td><form:input path="type" size="40"/></td>
                     <td><form:errors path="type" ></form:errors></td> 
                </tr>
                <tr>
                    <td><label>Amount:</label></td>
                    <td><form:input path="amount" size="40"/></td>
                     <td><form:errors path="amount" ></form:errors></td> 
                </tr>
                <tr>
                    <td><label>Start Date:</label></td>
                    <td><form:input path="startDate" size="40" /></td>
                    <td><form:errors path="startDate" /></td>
                </tr>
                 <tr>
                    <td><label>Birth Date:</label></td>
                    <td><form:input path="birthDate" size="40"/></td>
                    <td><form:errors path="birthDate" /></td>
                </tr>
                 <tr>
                    <td><label>Phone Number:</label></td>
                    <td><form:input path="phone" size="40"/></td>
                    <td><form:errors path="phone" /></td>
                </tr>
                <tr>
                    <td><label>Store Information:</label></td>
                    <td><form:checkbox path="storeInfo" /></td>
                    <td>&nbsp;</td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input type="submit" value="Submit" /></td>
                    <td>&nbsp;</td> 
                </tr>
            </table>
        </form:form>
        </div>
    </body>
</html>
