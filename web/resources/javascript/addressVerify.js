

var processAddress = function(){    
    
    var request = {
        
        street: "",
        city: "",
        state: "",
        zipcode: "",
        country: ""
        
    };
    
    var response = {
          zip : "",
	  zipPlus4 : "",
	  city : "",
          cityId : 0,
	  stateAbbrev : "",
	  valid : true,
          allValidMessage: true,
          deliveryLine : "",  
          validatedStreetLineFormat : "",
	  matchCode : "",
          debugMatchCode : "",
          confirmRequired : false,
          cityInsertionRequired: false,
          errors : [],
          svcMessages : [],
          svcDetails : [],
          confirmPrompt: "",
          ajaxRequest: null,
          numberFound: 0
    };
    
    var fldEhr = {
        field: "",
        message: ""
    };
    
    var debugPrint = function() {
        
        console.log("confirmRequired=" + response.confirmRequired);
        console.log("matchcode=" + response.matchCode);
        console.log("valid=" + response.valid);
        console.log("validatedLineFormat=" + response.validatedStreetLineFormat);
        $.each(response.svcMessages, function(index, value){
            console.log(value);            
        });
        
    };
    
    var fillRequest = function(){
        
        request.street = $("#address1").val();
        request.city = $("#citySelect").val() === "" ? "" : $("#citySelect option:selected").text();        
        request.state = $("#state").val();
        request.zipcode = $("#postalCode").val();
        request.country = $("#countrySelect").val();
       // console.log("addressVerify#fillRequest:country=" + request.country);
        
    };
    
    var clearColMessages = function() {
        
         $(".custInfoTbl span[id^='err_']").text(""); //begins with selector
    };
    var escapeSelector = function(errId){
        
        var selector= $.escapeSelector(errId) ;
        $("#" + selector).text("");
    };
    var clearMvcMessages = function() {
       // $(".custInfoTbl span[id$='errors']").text(""); //ends with selector
      
      escapeSelector("addressId.address1.errors");
      escapeSelector("addressId.cityId.cityId.errors");
      escapeSelector("addressId.district.errors");
      escapeSelector("addressId.postalCode.errors"); 
      escapeSelector("addressId.cityId.countryId.countryId.errors");
    };
    
    var hideAlert = function() {
        if($(".divAddrInfo").css("display") === "none")
            return;
        //$(".divAddrInfo").animate({top:"-10px"},500);
        
        $(".divAddrInfo").fadeOut(500);
    };
    
   
    
    var processErrors = function() {
        
        if(!response.errors || !response.errors[0]) return;
        
        $.each(response.errors, function(idx, value){
            
            fldEhr = value;
            
            var selector = "#" + "err_" + fldEhr.field; //table column
            
            $(selector).text(fldEhr.message);
            
        });
        
    };
    
    var processMessages = function() {     
   
       if(!response.svcMessages || !response.svcMessages[0]) {
           $(".divAddrInfo").animate({top:"-10px"},500);
           return;
       } //May be empty if constraint violations returned
      
        var classVal ="";
        var removeClassVal = "";
        
        var html = "";   
        
        
        if(response.valid) {
             classVal = "alert alert-success";
             removeClassVal = "alert alert-danger";
            
         }
        else {
            classVal = "alert alert-danger";
            removeClassVal = "alert alert-success";
        }       
      
        $(".divAddrInfo").removeClass(removeClassVal).addClass(classVal);
        
        html += "<ul>";

        
             $.each(response.svcMessages, function(index, val){
                 html += "<li>" + val + "</li>";                 
             });                
       
        
        
         if(response.confirmRequired) {
             html += "<li>" + response.confirmPrompt;         
             html += "&nbsp;<a id='alertConfirm' href='#' class='alert-link'>";
             html += "Confirm </a></li>" ;
             
         }
      
        html += "</ul>";
        
       $(".divAddrInfo").html(html).fadeIn(600, function(){
           if(response.confirmRequired)
                $("#alertConfirm").on("click", null, null, confirmHandler);
            
       });     
       
    };
    
    var assignReformattedFields = function(){
        
        $("#address1").val(response.ajaxRequest.street);
        $("#postalCode").val(response.ajaxRequest.zipcode);
        
    };
    
    var assignDebugging = function() {
        
        $("#matchCode").html(response.debugMatchCode);
        
        var line = "";
        if(response.deliveryLine === null || response.deliveryLine === "")
            line = "None";
        else line = response.deliveryLine;
        
        $("#deliveryLineDebug").html("<i>Delivery Line:" + line + "</i>");
        
        $("#numberFoundDebug").html("<i>Number Found:" + response.numberFound + "</i>");
        
    };
    
    function processCityInsertionRequired() {
        
        if(!response.cityInsertionRequired) {
            return;            
        }   
       
        $("#citySelect").append("<option value='" 
                            + response.cityId
                            + "'>" 
                            + response.city
                            + "</option>");  
                    
         $("#citySelect").val(response.cityId);           
                    
         $("#cityId").val(response.cityId) ; //assign bound hidden control         
    }
    
    var processConfirmRequired = function(response) {    
        
        console.log("processConfirmRequired: allValid=" + response.allValid);
        
        if(response.confirmRequired)
            
            $("#btnConfirm").show(500);
        
        else if(response.allValidMessage)  {        
           $("#address1").val(response.validatedStreetLineFormat);
           $("#postalCode").val(response.zipPlus4);  
           $("#citySelect").val(response.cityId);
           $("#cityId").val(response.cityId); //Hidden input bound to backing-object
           $("#state").val(response.stateAbbrev);
        }        
    };   
    
   /*
    * Success handler code
    */
    
    var processAddressVerifySuccess = function(payload) {
       
        response = payload;
        
        debugPrint();
        assignReformattedFields(); //street, zip from Ajax request
        processErrors();
        processMessages();  
        processCityInsertionRequired();
        processConfirmRequired(response);
        
        assignDebugging();
        
    };
    
    var handleVerify = function (event) {       
            
            event.preventDefault();
            
            $("#loading").css("display", "inline");
            
            clearColMessages();
            
            clearMvcMessages();       
            
            hideAlert();
            
            fillRequest();
            
            var data = JSON.stringify(request);
            
            var addressType = $("#addressTypeEnum").val();
          
            var promise = verifyAddress(data, addressType); //doLists.js
            
            promise.then(
                    function (payload) {
                         $("#loading").css("display", "none");                        
                         processAddressVerifySuccess(payload);
                    },
                    function (ehr) {
                         $("#loading").css("display", "none");
                         
                         console.log(ehr.responseText);
                         
                         doError(ehr); //function in doLists.js
                    }
            );       
    };//end attach
    
    var confirmHandler = function(event){
        
        event.preventDefault();
            
            clearMvcMessages();   
            
           // $(".divAddrInfo").slideUp(500);
            
            if(response.validatedStreetLineFormat !== "")  
                   $("#address1").val(response.validatedStreetLineFormat);
               
            if(response.cityId !== null && response.cityId > 0)  {   
                  $("#citySelect").val(response.cityId);
                  $("#cityId").val(response.cityId);  //hidden input                
            }
              
            if(response.stateAbbrev !== null && response.stateAbbrev !== "")  {  
                 $("#state").val(response.stateAbbrev);             
                 $("#district").val(response.stateAbbrev);
             }
             
            if(response.zipPlus4 !== null && response.zipPlus4 !== "")            
                $("#postalCode").val(response.zipPlus4); //On server, no revisions unless zipPlus4
           
           
           $("#btnConfirm").hide(500); 
           
          
    };
    
    var attachConfirm = function() {        
        $("#btnConfirm").on("click", null, null, confirmHandler);
   };//end attach 
   
   var attachVerify = function() {
       
       $("#btnVerifyAddress").on("click", null, null, handleVerify);
       
   };
    
    var bindHandlers = function() {
        $("#btnConfirm").hide();
        attachConfirm();
        attachVerify();
        
    };
    
    //Invoked onload and from key press handlers on customerForm.js
     var onChangeShowAlert = function() {
         
        $("#btnConfirm").hide(500); //Will overwrite edits          
        
        var html = "Please verify after edits are complete. ";
        
        var selectorAlert = ".divAddrInfo";
        
        if($(selectorAlert).hasClass("alert-success")) {
            $(selectorAlert).removeClass("alert alert-success");          
        }
        $(selectorAlert).addClass("alert alert-danger");
        
        html +=  "&nbsp;&nbsp;<a id='alertVerify' href='#' class='alert-link'>";
        html += "Verify </a>" ;
             
        $(selectorAlert).fadeIn(600).html(html).css("top", "0px");
        
        $("#alertVerify").on("click", null, null, handleVerify);
    };
    
    return {
        bindHandlers: bindHandlers, 
        onChangeShowAlert: onChangeShowAlert
    };
};
