


var getCityList = function () {

    var baseUrl = getBaseUrl(); //in ajax.js
    var url = baseUrl + "/resources/cityList?media=json";

    var deferred = $.Deferred();
    var request = doGet(url); //ajax.js
    request.done(
            function (data) {
                var temp = [];

                for (var i = 0; i < data.length; i++) {
                    temp.push(data[i]);
                    //console.log("Resolved " +  i + " " + temp[i].text);

                }
                deferred.resolve(
                        temp
                        );
            }//end function parameter
    ) //end done
            .fail(function (xhr, textStatus, errorThrown) {
                
                console.log("content-type=" + xhr.getResponseHeader("Content-Type"));
                console.log("status=" + xhr.status);
                console.log("textStatus=" + textStatus);
                console.log("errorThrown=" + errorThrown);  
                
                deferred.reject(xhr);
            }); //end fail
    return deferred.promise();
};

var getCityListByCountry = function (countryId) {

    var baseUrl = getBaseUrl(); //in ajax.js
    
   var url = baseUrl + "/resources/cityListByCountry/" +
            countryId + "?media=json"; 
    
    /*var url = baseUrl + "/resources/cityListByCountry/" +
            countryId + "?media=xml";*/

    var deferred = $.Deferred();
    var request = doGet(url); //ajax.js
    request.done(
            function (data) {
                
                console.log("getCityListByCountry completed");
                
                var temp = [];

                for (var i = 0; i < data.length; i++) {
                    temp.push(data[i]);
                }
                deferred.resolve(temp);
            }//end function parameter
    ) //end done
            .fail(function (xhr, textStatus, errorThrown) {
                
                console.log("content-type=" + xhr.getResponseHeader("Content-Type"));
                console.log("status=" + xhr.status);
                console.log("textStatus=" + textStatus);
                console.log("errorThrown=" + errorThrown);
                console.log("response=" + xhr.responseText);
                
                deferred.reject(xhr);
                
            }); //end fail
    return deferred.promise();
};
/*
 * Not invoked - rendered on server
 */
var getStatesList = function(){
    
     
     var url = getBaseUrl() + "/resources/statesList" ;
   
    var deferred = $.Deferred();
    var request = doGet(url); //ajax.js
        request.done(
            function (data) {
                var temp = [];

                for (var i = 0; i < data.length; i++) {
                    temp.push(data[i]);
                    //console.log("Resolved " +  i + " " + temp[i].text);

                }
                deferred.resolve(
                    temp
                );
            }//end function parameter
        ) //end done
        .fail(function(xhr,textStatus,errorThrown) {
            
            deferred.reject (xhr);
            
        }); //end fail
    return deferred.promise();
    };
    
    //jQuery returns a Promise object
    var verifyAddress = function(data,addressType){
        
        var deferred = $.Deferred();
        
       // var query = "?media=xml";
       
       var query = "?media=json";
        
        var url = "resources/verifyAddress/" + addressType + query;
        
        //alert("verifyAddress: url=" + url);
       
        var request = doData2(url, data, "post");
       
        request.done(function(payload){
            
            deferred.resolve(payload);
            
        })
        .fail(function(xhr){
            deferred.reject(xhr);                    
        });
        
        return deferred.promise();
        
    };//end verifyAddress
    
  var scrollToTop = function(){
      var offset = $(".alert").offset();
      $("html, body").animate({
          scrollTop: offset.top,
          scrollLeft: 0});
      
  } ; 
/*
 * 
 * To do: If xhr.responseText is HTML, text requires encoding if uploaded as a form parameter
 */
var doError = function (xhr) {   
    
    if(xhr.getResponseHeader("Content-Type").includes("json")) {
           var obj = JSON.parse(xhr.responseText);
           console.log("obj=" + xhr.responseText);
    }   

    /* Set hidden parameters uploaded to AjaxErrorController */
    if (obj && obj.exceptionName) {
        $.each(obj, function (key, value) {
            var selector = "#" + key;
            $(selector).val(value);
            console.log(key + "=" + value);
        });
    } else {
       // $("#messages").val(xhr.responseText); //HTML requires unescaping
        console.log("responseText=" + xhr.responseText); //To do
    }

    $("#xhrStatus").val(xhr.status);
    
    if(xhr.status === 406) {
        $("#exceptionName").val("org.springframework.web.HttpMediaTypeNotAcceptableException") ;
        $("#recoverable").val("false");
        $("#trace").val("Not Available");
        $("#url").val(xhr.responseURL); //empty
        $("#url").val("resources/verifyAddress/" + "addressType?media=");
        $("#messages").val("Developer error: Please set Accept request-header to application/json");
    }
    
    var addressTypeEnum = $("#addressTypeEnum").val();  
    
    $("#errAddressType").val(addressTypeEnum);
    
    if(addressTypeEnum === "Customer")
        selector = "form[id='customer']";
    else if(addressTypeEnum === "ShipAddress")
        selector = "form[id='shipAddress']";   

    $(selector).prop("disabled", true);

    $(selector).fadeTo(500, .4);
    
    $("#alert").fadeIn(500);    
    
    scrollToTop();

};


