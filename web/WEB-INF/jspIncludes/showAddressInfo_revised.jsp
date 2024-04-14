<%-- 
    Note: The form is shared by Customer, and ShipAddress. Both extend 
    @MappedSuperclass PostalAddress. ShipAddress#customerId is an object field.
    Customer#customerId is a primary field. To prevent a conversion error on
    ShipAddress update, customerId input is defined with disabled, so the browser
    will not include on submit.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="submitLabel" value="${updateShipAddressController.updateType}" />

<table   class="custInfoTbl">
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">
            <input type="submit" value="${not empty submitLabel ? submitLabel : 'Submit Changes'}" 
                   style="width:185px;color:white" class="btn btn-primary"/><br/>
        </td>
    </tr>
    <tr>  
    
    <input type="hidden"   name="addressTime"  value="${customerAttributes.addressUpdateTime}" />
        
        <td><label> Customer Id: </label></td>

        <td> <label>${customer.customerId}</label> </td>

        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>
            <label><span style="float:left">
                    First Name:</span><span class="asterick">*</span>
            </label>
        </td>
        <td>
            <form:input path="firstName" id="firstName" type="text"  required="true" />
            <div><form:errors path="firstName" cssClass="bindingErr"></form:errors></div>                               
            </td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>
                <label><span style="float:left">
                        Last Name:</span><span class="asterick">*</span>
                </label>
            </td>
            <td>
            <form:input path="lastName" type="text"  />
            <div><form:errors path="lastName" cssClass="bindingErr"></form:errors></div>
            </td>
            <td></td>
        </tr>
        <tr>
            <td>
                <label><span style="float:left">
                        E-Mail:</span><span class="asterick">*</span>
                </label>
            </td>
            <td>
            <form:input path="email" id="email" type="text" 
                        placeholder="youremail@mail.com" />
            <div><form:errors path="email" cssClass="bindingErr"></form:errors></div>
            </td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>
                <label><span style="float:left">Phone:</span><span class="asterick">*</span></label>
            </td>
            <td>
            <form:input path="addressId.phone"  id="phone" />
            <form:errors path="addressId.phone" cssClass="bindingErr"></form:errors>
            </td>
            <td>&nbsp;</td>
        </tr>       

        <tr>
            <td><label>Create Date:</label> </td>
            <td>
            <form:input path="createDate" readonly="true" /> 
            <form:errors path="createDate" cssClass="bindingErr" />
        </td>
        <td>&nbsp;</td>

    </tr> 
    <tr>
        <td><label>Last Update:</label></td>
        <td>
            <form:input path="lastUpdate" readonly="true" /> 
            <form:errors path="lastUpdate" cssClass="bindingErr" />
        </td>
        <td>&nbsp;</td>
    </tr>
    <tr id="addrInfoRow">
        <td colspan="3" style="color:#000088">Address Information:
            <span style="display:none" id="loading">
                <img src="${pageContext.request.contextPath}/resources/images/loading.gif" />
            </span>
            <div class="divAddrInfo"></div></td>
    </tr>

    <tr>
        <td>
            <label><span style="float:left">Address Line 1:</span>
                <span class="asterick">*</span></label>
        </td>
        <td>
            <form:input path="addressId.address1"  id="address1"/><br/>
            <form:errors path="addressId.address1" cssClass="bindingErr"></form:errors>
            </td>
            <td><span id="err_street" class="bindingErr"></span></td>
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
            <td><label><span style="float:left">City:</span>
                    <span class="asterick">*</span></label>          
                <br/>   
            </td>
            <td style="vertical-align: top">
            <form:select id="citySelect"  
                         path="addressId.cityId.cityId" style="clear:both"> 
                <form:option value="" label="Please select a city" />
                <form:options
                    items="${sessionScope.cityList}" itemValue="cityId" itemLabel="cityName" />                                   
            </form:select>

            <form:errors path="addressId.cityId.cityId" cssClass="bindingErr"></form:errors> 
            </td>
            <td><span id="err_city" class="bindingErr"></span></td>
        </tr>
        <tr>
            <td><label>Country:</label></td>
            <td>
            <form:input readonly="true" id="country" 
                        disabled="false"
                        path="addressId.cityId.countryId.countryName" />
            <form:input path="addressId.cityId.countryId.countryId" 
                        cssClass="countryId"
                        disabled="false"
                        readonly="true" 
                        id="countryId" />

        </td>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td>
            <label><span style="float:left">Region:</span>
                <span class="asterick">*</span></label>
        </td>
        <td>
            <form:input path="addressId.district"  id="district" />
            <form:errors path="addressId.district" cssClass="bindingErr"></form:errors>
        </td>
        <td>
            <select name="state" id="state" style="height:30px;margin-top:0px">
                <option value="">Please select a State</option>
                <c:forEach var="st" items="${sessionScope.statesList}">
                    <option value="${st.stCode}">${st.stName}</option>
                </c:forEach>          
            </select>
            <span id="err_state" class="bindingErr"></span>
        </td> 
    </tr>
    <tr>
        <td>
            <label><span style="float:left">Postal Code:</span>
                <span class="asterick">*</span></label>
        </td>
        <td>
            <form:input path="addressId.postalCode"
                        id="postalCode" />
            <form:errors path="addressId.postalCode" cssClass="bindingErr"></form:errors>
        </td>
        <td><span id="err_zipcode" class="bindingErr"></span></td>
    </tr>
    <tr>
        <td id="matchCode" style="color: #000088">&nbsp;</td>
        <td><input type="button" id="btnConfirm"
                   value="Confirm" 
                   class="btn btn-primary" style="width:185px"/><br/>
           
        </td>
        <td><button id="btnVerifyAddress" class="btn btn-success" style="width:185px">
                Verify Address </button></td>

    </tr>
   <tr>
        <td id="numberFoundDebug" style="color: #000088;font-size:9pt">&nbsp;</td>
        <td id="deliveryLineDebug" style="color: #000088;font-size:9pt">&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    
    <tr>
        <td style="width:305px"></td>
        <td style="width:290px"></td>
        <td>
            <input type="submit"  value="${not empty submitLabel ? submitLabel : 'Submit Changes'}"
                   class="btn btn-primary"
                   style="width:185px;color:white"/>

        </td>

    </tr>
</table>
