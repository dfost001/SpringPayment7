/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Note: A FieldError constructed with the rejected value is used, so that
 * the error field will retain its value, when form is re-presented.
 * 
 * @author Dinah
 */
public class MyDateEditor2 extends PropertyEditorSupport{
    
    private final BindingResult bindErrors;  
    private final String field;
    private final String objectName;
 
    public MyDateEditor2(BindingResult bindErrors, String field, String objectName){
        this.bindErrors = bindErrors;
        this.field = field;
        this.objectName = objectName;
    }
    
    private void addError(Object rejectedValue,
            String[] codes, Object[] arguments, String defaultMessage) {
        
        FieldError fieldError = new FieldError(objectName, field, rejectedValue,
            true, codes, arguments, defaultMessage);
        bindErrors.addError(fieldError);
    }
    
    private void addPartError(String rejectedValue, Object messageArg){
        
        Object[] args = {(Object)field, messageArg};
        
        addError((Object)rejectedValue, 
                new String[] {"date.invalid.datepart"},
                args,
                "Month or day-of-month for " + field + " has an invalid value.");
        
       /* bindErrors.rejectValue(
                field, 
                "date.invalid.datepart",
                 args,
                 "Month or day-of-month has an invalid value.");*/
                
    }
    
    private void addParseError(String rejectedValue){
        
        addError((Object)rejectedValue, 
                new String[] {"typeMismatch.java.util.Date"},
                new Object[] {(Object)field},
                "Please enter value for " + field + " as MM/DD/YYYY");
        
       /* bindErrors.rejectValue(
                field,
                "typeMismatch.java.util.Date",
                "Please enter values as MM/DD/YYYY");*/
    }    
    
    @Override
    public void setAsText(String value) { //into the bean
        
        //System.out.println("Into the bean: setAsText for " + field);
        
        SimpleDateFormat sdf = null;
        
        Date dt = null; 
        
        boolean isShortEntry = false;
        
        if(value == null || value.trim().isEmpty()){
           // super.setAsText(""); //required to prevent invalid property exception
            super.setValue(null);
            return; // let constraint validation handle the required rule
        }
        if(value.contains("/")){
               sdf = getShortFormatter() ;
               isShortEntry = true;
              
        }
        else  
               sdf = getFullFormatter();  //input in long format as printed     
        
        
        try {            
            dt = sdf.parse(value);
            
            if(isShortEntry)
                validateParts(value);
            
             super.setValue(dt);
            
        }
        catch(ParseException e){
            this.addParseError(value);
           // super.setValue(null);
           //throw new IllegalArgumentException(e.getMessage());
        }
        catch(Exception ex){
            this.addPartError(value,
            "Invalid value for " + ex.getMessage().toLowerCase() + ": '" + value + "'");
           // super.setValue(null);
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
        // String pattern = "MM/dd/yyyy";
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         return sdf;
    }
    
    private SimpleDateFormat getShortFormatter(){
         String pattern = "MM/dd/yyyy";
         SimpleDateFormat sdf = new SimpleDateFormat(pattern);
         return sdf;
    }  
  
    
    private void validateParts(String value) throws Exception{
        String [] parts = value.split("/");
        int month = Integer.valueOf(parts[0]) - 1;
        int day = Integer.valueOf(parts[1]);
        int year = Integer.valueOf(parts[2]);
        GregorianCalendar greg = new GregorianCalendar(
                year, month, day);
        try {
            greg.setLenient(false);
            greg.get(Calendar.MONTH);
            greg.get(Calendar.DATE);
            greg.get(Calendar.YEAR);
        }
        catch(Exception ex) {
            throw ex;
        }
        
    }
    
}//
