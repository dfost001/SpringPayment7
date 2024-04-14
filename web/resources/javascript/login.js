/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * When user selects dropdown, dropdown disappears.
 * Fix: do not code mouseout, and provide a close
 */




$(document).ready(function(){
    
    
    /*
     * .dropdownLogin hidden by css display:none
     * so it does not show when form is rendered.
     */
    var positionLogin = function () {
        
        $(".dropdownLogin").show();
        
        var top = $(".panel-body").offset().top;
        
        top += $(".panel-body").innerHeight(false);       
        
        var left = $(".panel-body").width() - $(".dropdownLogin").width();
        
        $(".dropdownLogin").css({left:left, top:top});           
        
        $(".dropdownLogin").hide();
        
    };
    
    if($(".dropdownLogin").get(0)){   
        
        positionLogin();       
    }
    
    if($("#IdErrorMsg").text())
         $(".dropdownLogin").show();
  
    
    $("#linkCustId").click(function(event){
        
        var login = $(".dropdownLogin").get(0);
        if(login != null && login != undefined) {
             
             event.preventDefault();
             $(".dropdownLogin").slideToggle();
         }     
    }); 
    $("#loginCloseGlyph").click(function(){
        
        $(".dropdownLogin").css("display", "none");
        
    });
   
}); //end ready
