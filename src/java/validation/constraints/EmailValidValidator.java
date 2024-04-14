/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation.constraints;

import dao.CustomerManager;
import error_util.EhrLogger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import model.customer.validation.SupportedTld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** 
 * 
 * Note: For transaction to work, Hibernate property javax.persistence.validation.mode
 * must be set to NONE. Otherwise, use a custom Spring validator for retrieval.
 *  
 * To do: Initialize formatMessage with a bundle key
 * 
 * Algorithm incorrect: If more than two domain components, need to message tld found
 */
@Component
public class EmailValidValidator implements ConstraintValidator<EmailValid, String> {
    
    @Autowired
    private CustomerManager customerManager;   
  
    private final String missingLocal = "{EmailValid.MissingLocal}" ;
    private final String missingHost = "{EmailValid.MissingHost}";
    private final String missingTld = "{EmailValid.MissingTld}";
    private final String missingDelimiter = "{EmailValid.MissingDelimiter}";
    private final String multipleDelimiter = "{EmailValid.MultipleDelimiter}";
    private final String formatErrDelimiter = "{EmailValid.FormatDelimiter}";
    private final String symbolBeforeDelimiter = "{EmailValid.SymbolBeforeDelimiter}";
    private final String missingPeriod = "{EmailValid.MissingPeriod}";
    private final String invalidSymbolSequence = "{EmailValid.InvalidSymbolSequence}";
    private final String terminalPeriod = "{EmailValid.TerminalPeriod}"; //Formatter should remove
    private final String terminalDelimiter = "{EmailValid.TerminalDelimiter}" ;
    
    @Override
    public void initialize(EmailValid constraintAnnotation) {
        
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
      EhrLogger.printToConsole(this.getClass(),"isValid", 
              "JSR Bean Validation custom constraint executing: " + value);
        
       if(value == null || value.trim().isEmpty()) 
           return false;  //should be trapped by composite annotation 
       
       if(!isValidLocalDelimiter(context,value)){ // '@' missing, trailing, more than one
             
          return false;   //ensure some length after the '@'
       }
      /* else  if(isMissingLocal(context, value)) {  
           return false;
       }*/
       else if(isDelimiterFormatError(context,value)){ //Invalid punctuation following '@'
           
           return false;
       }
       else if(isMissingHost(context, value)) {   //Substring following '@' found as TLD      
                 
          return false;   
       }
       else if(isFormatError(context, value)) { //No period or Invalid punctuation preceding/following
           
           return false; 
       }
       else if(findDomain(context,value.substring(value.indexOf("@")+1)) == null) { //Top-level domain not found
          
           return false; 
       }
      
        return true;
    }    
   
    /*
     * Evaluation of '@' delimiter
     * Formatter will remove all trailing symbols,and reconcat an '@' on empty
     */
    private boolean isValidLocalDelimiter(ConstraintValidatorContext ctx,
            String value){
        
       //System.out.println("EmailValidator#isValidLocalDelimiter: " + value) ;
       
       int position = value.indexOf("@"); 
       
       if(position == -1) {
           this.addConstraintViolation(ctx, missingDelimiter);
           return false;
       }
      
       if(position == value.length() - 1){
           this.addConstraintViolation(ctx, this.terminalDelimiter);
           return false;
       }
       
       int another = value.indexOf('@',position+1);    
       
       if(another > -1) {
           this.addConstraintViolation(ctx, this.multipleDelimiter);
           return false;
       }
       if(position == 0) {
           this.addConstraintViolation(ctx, this.missingLocal);
           return false; 
       }
       
       if(!Character.isDigit(value.charAt(position - 1))
          && !Character.isAlphabetic(value.charAt(position - 1))) {       
           this.addConstraintViolation(ctx, this.symbolBeforeDelimiter);
           return false;
        }
       return true;
    }
    private boolean isMissingLocal(ConstraintValidatorContext ctx, String value) {
        
        String local = value.substring(0, value.indexOf("@"));
        
        if(local == null || local.isEmpty()) {            
        
              this.addConstraintViolation(ctx, this.missingLocal);
              
              return true;
        }      
        
        return false;
        
    }
    /*
     * Evaluate punctuation following the '@' - not a character or digit following the '@'
     * Possible invalid period after the '@'. Formatter will remove hyphen.
     * There is no evaluation of user-name string preceding the '@'
     */
    private boolean isDelimiterFormatError(ConstraintValidatorContext ctx, String value) {
        
        int position = value.indexOf("@");
        
        char c = value.charAt(position+1); //length already evaluated
        
        if(Character.isDigit(c) || Character.isAlphabetic(c)){
           
            return false;
        }
        this.addConstraintViolation(ctx, this.formatErrDelimiter);
        return true;
    }
    
    /*
     * Return if no period delimiter else call helper to recurse through substrings delimited
     * by a period
     * 
     */
    private boolean isFormatError(ConstraintValidatorContext ctx, String value) {
        
        String email = value.substring(value.indexOf("@")+1); //terminal @ already evaluated
        
        int posPeriod = email.indexOf((char)46);
        
        if(posPeriod == -1){
            this.addConstraintViolation(ctx, this.missingPeriod);
            return true;
        }        
        return isInvalidSequence(ctx, email);        
    }
    /*
     * Note: A period following the '@' has already been evaluated
     * Note: Formatter should remove consecutive.
     * Note: Terminal period should be removed
     * These will cause an index out of range exception
     *
     * Recursive function that returns after the first invalid sequence is found
     * 
    */
    private boolean isInvalidSequence(ConstraintValidatorContext ctx, String value) {
        
        int posPeriod = value.indexOf((char)46);
        if(posPeriod == -1)
            return false; //Recursion terminated. Already evaluated no period error.
        if(posPeriod == 0) { //Period following @ or consecutive period
            this.addConstraintViolation(ctx, this.invalidSymbolSequence);
            return true;
        }
        char prev = value.charAt(posPeriod-1) ;          
        if(!Character.isAlphabetic(prev) && !Character.isDigit(prev)) {
            this.addConstraintViolation(ctx, this.invalidSymbolSequence);
            return true;
        } else if(posPeriod == value.length() - 1) {
            this.addConstraintViolation(ctx, this.terminalPeriod);
            return true;
        } else { //Formatter should already remove symbol following period
            char next = value.charAt(posPeriod + 1);
            if(!Character.isAlphabetic(next) && !Character.isDigit(next)) {
               this.addConstraintViolation(ctx, this.invalidSymbolSequence);
               return true;
            }
        }
        return isInvalidSequence(ctx, value.substring(posPeriod+1)); //recurse with remaining substring
    }
    
    private boolean isMissingHost(ConstraintValidatorContext ctx, String value) {       
        
        int position = value.indexOf("@");
        String domain = value.substring(position+1);
        
        SupportedTld tld = customerManager.findSupportedTld(domain);        
        
        if(tld == null)
            return false;
        
        this.addConstraintViolation(ctx, this.missingHost);
        return true;  //entire string after @ is a domain name     
        
    }
    
    /*
     * Assumes no trailing punctuation
     * Recursively parses the substring following each period until a
     * supported TLD is found or none
     */
    private SupportedTld findDomain(ConstraintValidatorContext ctx, String value){
        
        //System.out.println("EmailValidValidator#findDomain: value=" + value);      
        
        int pos = value.indexOf((char)46);
        
        if(pos == -1) { //at end of string and recursion has not returned a tld
            this.addConstraintViolation(ctx, this.missingTld);
            return null;
        }
        
        String nextVal = value.substring(pos + 1);
        
        SupportedTld tld = null;
        
        tld = customerManager.findSupportedTld(nextVal);
        
        if(tld == null)
            return findDomain(ctx,nextVal);
        
        return tld;
    }
    
    private void addConstraintViolation(ConstraintValidatorContext ctx, String template) {
        
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(template).addConstraintViolation();
    }
    
    private String symbolToText(char symbol) {
        
        String text = "";
        
        switch(symbol) {
            case '-' :
                text = "dash";
                break;
            default :
                text = "symbol"; 
                break;
        }
        return text;
    }
    
} //end class
