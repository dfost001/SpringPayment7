/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Dinah
 */
public class MySimpleDateEditor extends PropertyEditorSupport {
    
      @Override
    public void setAsText(String value) { //into the bean
        
        //System.out.println("Into the bean: setAsText for " + field);
        
        SimpleDateFormat sdf = null;
        
        Date dt = null;        
        
        if(value == null || value.trim().isEmpty()){
           // super.setAsText(""); //required to prevent invalid property exception
            super.setValue(null);
            return; // let constraint validation handle the required rule
        }
        value=value.trim();
        if(value.contains("/")){
               sdf = getShortFormatter();              
        }
        else { 
               sdf = getFullFormatter();  //input in long format as printed
        }
        
        
        try {            
            dt = sdf.parse(value);            
                        
             super.setValue(dt);
            
        }
        catch(ParseException e){
            
            super.setValue(null);
            throw new IllegalArgumentException("MySimpleDateEditor:" + e.getMessage());
        }
        
    }     
    
    @Override
    public String getAsText(){ //from the bean
       
        SimpleDateFormat sdf = getFullFormatter();
        
        String formatted = "";
        
        Date dt = (Date)super.getValue();
        
        if(dt==null)
            return "";    
        
        formatted = sdf.format(dt);
        return formatted;
    }     
    
    private SimpleDateFormat getFullFormatter() {
        String pattern = "EEE, d MMM, yyyy HH:mm a z"; //Wed, 7 Jul 2000 12:00pm PST
        // String pattern = "dd-MMM-yyyy HH:mm a z";
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         return sdf;
    }
    
    private SimpleDateFormat getShortFormatter(){
         String pattern = "MM/dd/yyyy";
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         sdf.setLenient(false);
         return sdf;
    }  
  
    
}
