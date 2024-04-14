/* 
 * see doLists.js for getCityList(), getStateList(), showError()
 */
$(document).ready(
        function () {
            
            var process = processAddress(); //see addressVerify.js
            process.bindHandlers(); 
            
                      
            $("#phone").mask("(999) 999-9999");
            
            //$("#citySelect").chosen();
           
            $(":submit[disabled]").css("cursor","not-allowed");

            var cityList = [];                
            
            function attachPhoneHandlers() {
                $("#phone").on("focus", null, null, function(){
                   var el = $(this).get(0);                   
                   el.selectionEnd = el.value.length;
                });
           
                $("#phone").on("blur", null, null, function(){
                    $(this).selectionStart = 0;
                    $(this).selectionEnd = 0;
                }); 
            } //end attach
            
            function scrollToMessageList() {
                if(!$("#bindingResult").get(0)) return;
                var el = $("#bindingResult ul").children();
                var n = el.length;
               
                if(n === 0) return;
                var offset = $(".myPanel").offset();   
               //var offset = $(".custInfoTbl").offset();
               $("html, body").prop("scrollTop", offset.top);
                console.log("scrollToMessageList: offset.top=" + offset.top);
                
            }
           
            
            function showState(how){
                if(how === true) {
                    $("#state").fadeIn(500);
                    $("#state").prop("disabled", false);
                    $("#district").prop("readonly", true);
                    $("#district").css("background-color", "#DDDDDD");
                }
                else if(how === false){
                    $("#state").fadeOut(500);
                    $("#state").prop("disabled", true);
                    $("#district").prop("readonly", false);
                    $("#district").css("background-color", "#FFFFFF");
                }
                    
            }
            
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
            
            
            function findCountry(cityId){
                
                console.log("cityList length=" + cityList.length);
                
                for(var i=0; i < cityList.length; i++)
                    if(cityList[i].cityId === window.parseInt(cityId)){
                       
                        return cityList[i].countryId;
                    }
            } 
            
            function setStateSelected() {
              var stateSelect = document.getElementById("state");
              for(var i=0; i < stateSelect.options.length; i++) {
                  var option = stateSelect.options[i];
                  if(option.value === $("#district").val()){
                      option.selected = true;
                      break;
                  }
              }
              
            }
           

            $("#citySelect").change(function () {
                
                    clearMessage($(this));
                    clearAddressSvcMessages();
                
                    var val = $("#citySelect").val();
                    if(val === "") {
                         $("#country").val("");
                         $("#countryId").val("");
                         
                        showState(false);
                        
                        return;
                    }
                  
                    var country = findCountry(val);
                    $("#country").val(country.countryName);
                    $("#countryId").val(country.countryId);
                    $("#district").val("");
                    if($("#country").val() === "United States"){    
                        
                        showState(true);
                        
                         $("#state").val("");  
                       
                    }
                    else showState(false);                   
                                      
            });
            
            $("#state").change(function(){
                 var el = $("#district");
                clearMessage(el);
                var value = $("#state").val();               
                $("#district").val(value);
                clearAddressSvcMessages();
               
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
           
            scrollToMessageList();
            
            if($("#countryId").val() === "103") {
                showState(true);
                setStateSelected(); 
            }
            else {
                showState(false);
            }
            
            var promise = getCityList();  //doLists.js

            promise.then(
                    function (payload) {
                        
                        for (var i = 0; i < payload.length; i++) {
                            cityList.push(payload[i]);
                            
                        }            
                       
                    },
                    function (errPayload) {
                       doError(errPayload); //function in doLists.js
            }); //end .then     
                    
        } // end ready function parameter
);//end ready



