/**
 * 
 */
var doSetup = function () {

  $.ajaxSetup(
	{
	   headers: {
               Accept: "application/xml,application/json,text/plain,text/html,image/gif,image/jpeg,*/*;q=.2"
           },
	   jsonp: false
	}
   );//end ajaxSetup
}; //end doSetup

var doGet = function (url) {
    doSetup();
    var request = $.ajax({
        type: "GET",
        //complete: doComplete,
        url: url,
        dataType: "json"
    });
    return request;
};

var doData = function (url, myData, method) {
    doSetup();
    alert(myData.city + " " + myData.state + " " + myData.addrLine);
    var request = $.ajax(
            {
                type: method,
                url: url,
                data: myData,
                dataType: "json"

            }
    ); //ajax
    return request;
}; //end doData
/*
 * dataType: "json" Evaluates the response as JSON and returns a JavaScript object.
 * Also used as the Accept header value
 */                
var doData2 = function (relUrl, data, method) {
    
    console.log("Testing cache control");
    
    doSetup();
    var request = $.ajax({
        type: method,
        url: getBaseUrl() + "/" + relUrl,
        data: data,
        processData: false,
        dataType: "json",
        contentType: "application/json"

    });
    return request;
};
                
                var getBaseUrl = function() {
                    
                    var href = window.location.href;
                    
                    var startPos = (location.protocol + "://").length;
                    
                    var contextPosition = href.indexOf("/", startPos);
                    
                   // console.log("contextPosition=" + contextPosition);
                    
                    var requestPathPosition = href.indexOf("/", contextPosition + 1);
                    
                   // console.log("requestPathPos=" + requestPathPosition);
                    
                    var baseUrl = href.substring(0, requestPathPosition); //does not include trailing '/'
                    
                   // console.log("ajax.js#getBaseUrl:" + baseUrl);
                    
                    return baseUrl;
                    
                };

  
	