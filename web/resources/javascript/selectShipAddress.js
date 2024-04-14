/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function() {
    
    var checkBoxSelector = "input[name='checkedAddress']" ;
    
    var nodes = $(checkBoxSelector);
    
    var selectedValue;
    
    //var alert = null;
    
    console.log("nodes has length " + nodes.length);
    
    $(checkBoxSelector).click(function(){       
        
        console.log("props=" + $(this).prop("checked"));
        
        if($(this).prop("checked") === true) {
            selectedValue = $(this).prop("id");
            console.log("selectedValue=" + selectedValue);
        }
        
        for(var i=0; i < nodes.length; i++) {
            
            var id = nodes[i].id;
            
            if(id !== selectedValue)                
              nodes[i].checked = false;
        } 
       
        
    }); //end checkbox click
    

    
}); //end ready


