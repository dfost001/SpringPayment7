<%-- 
    Document   : handleAjaxError
    Created on : Oct 17, 2018, 1:25:32 PM
    Author     : Dinah
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

 <form action='<c:url value="/handleAjaxError"/>' method="Post" >
                    <div class="alert alert-danger" style="display:none" id="alert">
                        An error occurred.<br/>
                        Please make sure you are connected to the Internet.<br/>
                        Then, click this link to continue. 
                        <input type="submit" value="Continue" class="btn alert-link" />
                    </div>
                    <input type="hidden" name="status" id="status" value="-1" />
                    <input type="hidden" name="xhrStatus" id="xhrStatus" value="-1" />
                    <input type="hidden" name="trace" id="trace" value="none" />
                    <input type="hidden" name="url" id="url" value="none" />
                    <input type="hidden" name="messages" id="messages" value="none" />
                    <input type="hidden" name="recoverable" id="recoverable" value="false" />
                    <input type="hidden" name="exceptionName" id="exceptionName" value="none" />
                    <input type="hidden" name="errAddressType" id="errAddressType" value="none" />
</form>
