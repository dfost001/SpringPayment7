<%-- 
    Document   : main-layout
    Created on : Oct 15, 2017, 8:36:36 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html>
    <head>        
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />        
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link href="${pageContext.request.contextPath}/resources/css/default.css"  rel="stylesheet" type="text/css"/>
        <link href="${pageContext.request.contextPath}/resources/css/productPage.css"  rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/form.css" rel="stylesheet" />  
        <script src="${pageContext.request.contextPath}/resources/javascript/jquery-1.11.1.js"></script> 
        <script src="${pageContext.request.contextPath}/resources/javascript/bootstrap.min.js" type="text/javascript"></script>       
        <script src="${pageContext.request.contextPath}/resources/javascript/login.js"></script>
        <title><tiles:getAsString name="title" /></title>
    </head>
    <body>
        <div class="container-fluid">
            <div id="top">
                <tiles:insertAttribute name="logo" />
            </div><!--end top-->
            <div><!--bootstrap panel-->
                <tiles:insertAttribute name="navigation" />
            </div><!--end bootstrap panel-->
            <div id="left">
                <tiles:insertAttribute name="sidebar" />
            </div><!--end left-->
            <div class="center-content">
                <tiles:insertAttribute name="body" />
            </div><!--end center-content-->
            <div id="bottom">
                <tiles:insertAttribute name="footer" />
            </div><!--end bottom-->
            
        </div><!--end container-fluid-->       
    </body>
</html>
