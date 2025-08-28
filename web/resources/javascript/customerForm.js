/* 
 * see doLists.js for getCityList(), getStateList(), showError()
 */
$(document).ready(
        function () {
            
            var process = processAddress(); //see addressVerify.js
            process.bindHandlers(); 
            
                      
            $("#phone").mask("(999) 999-9999");          
           
            $(":submit[disabled]").css("cursor","not-allowed");

            //var cityList = [];     
            
             function debug(value, title) {
                if(value === null) {
                   console.log(title + " is null") ;
                   return;
               }
                console.log(title + ": " + typeof(value));
                if(title === "payload") {
                    console.log("size=" + value.length);
                    console.log(value[0].cityName);
                }
                else console.log(title + ": " + value);
            }
            
            function scrollToMessageList() {
                if(!$("#bindingResult").get(0)) return;
                var el = $("#bindingResult ul").children();
                var n = el.length;
               
                if(n === 0) return 0;
                
                var offset = $(".myPanel").offset();   
              
               $("html, body").prop("scrollTop", offset.top);
                             
            }
            
            function scrollToForm() {
                if(scrollToMessageList() === 0) return;
                var offset = $(".custInfoTbl").offset();
                $("html, body").prop("scrollTop", offset.top);
            }
           
            
            function showState(how){
                if(how === true) {
                    $("#state").fadeIn(500);
                    $("#state").prop("disabled", false);
                    $("#district").prop("disabled", true);
                    $("#district").css("background-color", "#DDDDDD");
                }
                else if(how === false){
                    $("#state").fadeOut(500);
                    $("#state").prop("disabled", true);
                    $("#district").prop("disabled", false);
                    $("#district").css("background-color", "#FFFFFF");
                }
                    
            }
            /*
             * el is the jQuery element
             * Note: name HTML attribute is path attrib of tag
             * Error message is rendered within a span 
             */
            function clearMessage(el){
                
               var name = $(el).prop("name");
               var id = name + ".errors";               
               var node = document.getElementById(id);
               if(node && node.firstChild)
                     node.firstChild.nodeValue = "";                        
            } 
            
            function clearAddressSvcMessages() {
                
              //  $(".custInfoTbl span[id^='err_']").text(""); //clear all column errors
                
              //  $(".divAddrInfo").slideUp(500);               
                
                process.onChangeShowAlert(); //Alert to verify
            }         
            
            function setOptionSelected(selectBoxId, srcSelector) {                 
              
              var select = document.getElementById(selectBoxId);
              
              debug(select.options.length, "select.options.length");
              
              if(srcSelector === "") {
                  select.options[0].selected = true;
                  return;
              }
              
              for(var i=0; i < select.options.length; i++) {
                  var option = select.options[i];
                  if(option.value === $(srcSelector).val()){
                      option.selected = true;
                      break;
                  }
              }             
            }
            
            function fillCitiesList(data) {
               
               var citySelBox = $("#citySelect");
                
                citySelBox.empty();
                
                citySelBox.append("<option value=''>Please select a city</option>");
                
                for(var i = 0; i < data.length; i++) {
                    
                    citySelBox.append("<option value='" 
                            + data[i].cityId
                            + "'>" 
                            + data[i].cityName
                            + "</option>");                    
                }                
            }  //end  fillCities  
            
             /*To do: District value may be fullName not abbreviation 
              *So state may not be selected
              *Note: No need to reset City/State if not city not bound, done in Country change
              */
              function retrieveCitiesByCountryId() {
                
               var countryId = $("#countrySelect").val();
                
                if(countryId === "" )
                    return;
                
                var promise = getCityListByCountry(countryId); //doLists.js
                
                 promise.then(
                    function (payload) {      
                       
                       debug(payload, "payload");
                        
                       fillCitiesList(payload) ;   
                       
                       var jqCity = $("#cityId") ;
                       
                       if(jqCity.val() !== null && jqCity.val() !== "") { //Reset on country change event or insertion
                            setOptionSelected("citySelect", "#cityId"); //Set selected from bound input onLoad
                       }                    
                       if(countryId === "103") {
                           showState(true);                                                     
                       }
                       else showState(false);
                       
                    },
                    function (errPayload) {
                       var url = "/resources/cityListByCountry" ;
                       doError(errPayload, url); //function in doLists.js
                    }); //end then     
            }             
            
            $("#countrySelect").change(function(){
                
                 if($(this).val() === "") 
                     return;
                
                clearMessage($("#countrySelect"));
                
                $("#citySelect").val("");
                $("#state").val(""); 
                $("#district").val("");
                
                $("#cityId").val("");
                    
                             
                retrieveCitiesByCountryId(); 
                
              }) ;   //end change            
            
            /*
             * Not setting state or district, since these may be edited first.
             */
            $("#citySelect").change(function () {                           
                   
                    var citySelVal = $("#citySelect").val();
                    
                    $("#cityId").val(citySelVal); //assign bound hidden input                    
                    
                    clearMessage($("#cityId")); //<form:errors> is set to bound hidden control
                    
                    clearAddressSvcMessages();                    
            });
            
            $("#state").change(function(){                      
                 var value = $("#state").val();                   
                $("#district").val(value);
                clearAddressSvcMessages();
                clearMessage($("#district"));
               
            });
            
            $("#firstName, #lastName, #email, #phone").keyup(function(){
                
                clearMessage($(this));
                
            }); 
            
            /*
             * clearAddressSvcMessages will remove confirmHandler since confirmHandler
             * will overwrite edits
             */
            $("#address1, #postalCode").keyup(function(){
                
                clearMessage($(this));
                clearAddressSvcMessages();
                
            });          
            
           
            /*
             * Immediately executed code
             */            
            process.onChangeShowAlert(); //Show alert. See addressVerify.js          
            
            //scrollToForm();
            
            scrollToMessageList();
            
            if($("#countrySelect").val() === "") {
                $("#countrySelect").val(103); //Can set selected to String or Number
            }
              
            retrieveCitiesByCountryId(); //doSelected = true            
        } // end ready function parameter
);//end ready

  /*if($("#countrySelect").val() !== "") {
              
              retrieveCitiesByCountryId(); //doSelected = true            
            
            }  
            else {
                showState(false);
            }*/

