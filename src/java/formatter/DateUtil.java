/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author Dinah
 */
public class DateUtil {
    
    public synchronized static Date setDate(String value) { //into the bean
        
        //System.out.println("Into the bean: setAsText for " + field);
        
        SimpleDateFormat sdf = null;
        
        Date dt = null;        
        
        if(value == null || value.trim().isEmpty()){
           return null;
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
            
        }
        catch(ParseException e){           
            throw new IllegalArgumentException(e.getMessage());
        }
        return dt;
    }     
    
    
    public synchronized static String getAsText(Date dt){ //from the bean
       
        SimpleDateFormat sdf = getFullFormatter();
        
        String formatted = "";       
        
        if(dt==null)
            return "";    
        
        formatted = sdf.format(dt);
        return formatted;
    }     
    
    private static synchronized SimpleDateFormat getFullFormatter() {
        
        //String pattern = "EEE, d MMM, yyyy HH:mm a z"; //Wed, 7 Jul 2000 12:00pm PST
        String pattern = "MM-dd-yyyy HH:mm a z";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf;
    }
    
    private static synchronized SimpleDateFormat getShortFormatter(){
         String pattern = "MM/dd/yyyy";
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         sdf.setLenient(false);
         return sdf;
    }  
  
    
}
