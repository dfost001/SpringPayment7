/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    
    var cmd = $(":submit[name='updateCmd']");
    
    cmd.hide();
    
    /*
     * If a checkbox is checked onLoad, show the hidden quantity input
     */
    
    $(":checkbox[name='select']:checked").each(function(){
        
        var row = $(this).parent().parent().next();
        row.show();
        cmd.show();
        
    });
    
    $(":checkbox[name='select']").click(function(){
        
        var row = $(this).parent().parent().next();
        
        if($(this).prop("checked") == true){            
            
            row.fadeIn(600);
            
        }
        else row.fadeOut(600);
       
       var length = $(":checkbox[name='select']:checked").length;
             
       if(length === 0)
           cmd.fadeOut(500);
       else cmd.fadeIn(500);
       
            
    });
    
});
