<%-- 
    Document   : exceptionExamples
    Created on : May 29, 2019, 7:21:15 PM
    Author     : dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div style="padding:30px 20px 100px 20px;margin:auto;width:300px"> 
<form action="<c:url value="/exception/example/testRecoverableGet" />" method="get">    
    <h4>  <input type="submit" value="Data Access - Recoverable (HttpMethod: GET)" class="btn-link" /> </h4>
</form>    
 <form action="<c:url value="/exception/example/transient" />" method="post">    
    <h4>  <input type="submit" value="Data Access - Transient (HttpMethod: POST)" class="btn-link" /> </h4>
</form>   
<form action="<c:url value="/exception/example/nontransient" />" method="post">    
    <h4>  <input type="submit" value="Data Access - Non Recoverable - Invalid Query String" class="btn-link" /> </h4>
</form>   
<form action="<c:url value="/exception/example/nullpointer" />" method="post">    
    <h4>  <input type="submit" value="Runtime Exception - Null Pointer" class="btn-link" /> </h4>
</form>
<form action="<c:url value="/exception/example/nestedException" />" method="post">    
    <h4>  <input type="submit" value="isErrorPage - Mapped By web.xml" class="btn-link" /> </h4>
</form>
 <form action="<c:url value="/exception/example/invalidMethodParameter" />" method="post">    
    <h4>  <input type="submit" value="IllegalArgument - Invalid Method Parameter" class="btn-link" /> </h4>
</form> 
<form action="<c:url value="/exception/example/uninitializedProperty" />" method="post">    
    <h4>  <input type="submit" value="BeanUtil Test - Uninitialized Property" class="btn-link" /> </h4>
</form>
<form action="<c:url value="/product/request" />" method="post">
    <input type="hidden" name="id" value="1" />
    <h4>  <input type="submit" value="Spring Framework - Method Not Supported" class="btn-link" /> </h4>
</form> 
<form action="<c:url value="/exception/example/missingParameter" />" method="post">    
    <h4>  <input type="submit" value="Spring Framework - Missing Request Parameter" class="btn-link" /> </h4>
</form>  
<form action="<c:url value="/exception/example/testCities" />" method="get">    
    <h4>  <input type="submit" value="Test Cities By Country Id" class="btn-link" /> </h4>
</form>    
    
</div>        
    
