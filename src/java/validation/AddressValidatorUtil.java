/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;


import error_util.EhrLogger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import model.customer.PostalAddress;
import model.customer.States;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import util.StringUtil;

/**
 *
 * @author dinah
 */
@Component
public class AddressValidatorUtil {
    
   
    
    public void validateAddressLineFormat(String addrLine, String fldName, Errors errors) {
        
        if(isPostalLine(addrLine))
            
            validatePostalLine(addrLine, fldName, errors);
        
        else validateBuildingNumber(addrLine, fldName, errors);
        
    }
    
    
    public  Boolean isPostalLine(String addressLine) {
        
         String[] formats = {"PO", "RR", "UNIT"} ;
         
         String token[] = addressLine.split("\\s");
         
         for(int i = 0; i < formats.length; i++)
             if(token[0].equalsIgnoreCase(formats[i]))
                     return true;
         
         return false;
        
    }
    
    public void validatePostalLine(String addressLine, String fldName, Errors errors) {
        
        String[] token = addressLine.split("\\s");       
        
        int count = token.length;
        
        String designator = token[0].toUpperCase() ;
        
        boolean valid = true;

        String err = "";        
        
                switch(designator) {
                    case "PO":    
                                                
                            if(count != 3){   
                                err = "Expected format is 'PO Box 9999'";
                                valid = false;
                            }                           
                            
                            else if(!token[1].equalsIgnoreCase("Box"))  { 
                                err = "'Box' is expected after 'PO'" ;
                                valid = false;
                            }
                            
                          /*  else if(!StringUtil.allAlphaDigits(token[2])) {
                                err = "PO Box number contains an invalid symbol(s)";
                                valid = false;
                            }   */             
                            
                            break;
                    case "RR" :                         
                         
                         if(count != 2) {
                             err = "Expected Rural Route format is 'RR 99'";
                             valid = false;
                         }
                         else if(!StringUtil.allAlphaDigits(token[1])) {
                                err = "Rural Route number contains an invalid symbol(s)";
                                valid = false;
                         }
                         
                         break;
                         
                    case "UNIT" :  
                        
                         err = "Expected Military Unit format is 'Unit 999'";
                         
                         if(count != 2)
                             valid = false;
                         
                        else if(!StringUtil.allAlphaDigits(token[1])) {
                                err = "Military Unit number contains an invalid symbol(s)";
                                valid = false;
                         }
                         break;
                   
                }//end switch 
                
                if(!valid)
                     errors.rejectValue(fldName, "", err);              
        
    }
    
    public void validateBuildingNumber(String addrLine, String fldName, Errors errors) {
        
        String[] token = addrLine.split("\\s");

        int count = token.length;

        String err = "";

        if (count < 2) {
            
            err = "Line must have at least a building number and street-name. ";
            errors.rejectValue(fldName, "", err);
            
        } else if (!StringUtil.allDigits(token[0])) {
            
                err = "Line must begin with an all digits building number or 'PO Box 99'. ";
                errors.rejectValue(fldName, "", err);
                
        } else if (isStreetSuffix(token[1])) {
            
                err = "Street-name following building number cannot be a suffix. ";
                errors.rejectValue(fldName, "", err);
                
        } 
        else if (StringUtil.allDigits(token[1])) {
            
                err = "Street-name cannot be all digits. "
                        + "Is the building number spaced correctly?";
                errors.rejectValue(fldName, "", err);
        }
        
    }
    /*
     * Invoked from AddressFormatter.java
     */
    public static boolean isStreetSuffix(String value) {
        
        String[] suffixList = {"St", "Ave", "Blvd", "Pl"};
        
        for(int i=0; i < suffixList.length; i++)
            if(value.equalsIgnoreCase(suffixList[i]))
                return true;
        
        return false;
    }
    
    public  void validatePostalCodeMvc(PostalAddress postalEntity, Errors errors, Optional<States> stateEntity){
        
       String formatted = isValidPostalCode(postalEntity.getAddressId().getPostalCode(), 
               errors, "addressId.postalCode", true);
       
      // EhrLogger.printToConsole(this.getClass(), "validatePostalCodeMvc", "edited = " + formatted);
       
       postalEntity.getAddressId().setPostalCode(formatted);
                
       if(errors.getFieldError("addressId.postalCode") == null && stateEntity.isPresent())  {    
            validateZipRange(errors, formatted, stateEntity.get(), "addressId.postalCode");
       }       
    }  
    /*
     * Currently not used - reformatting outside of an event
    */
     public String formatUsZip(String value) {
        
        if(StringUtil.isNullOrEmpty(value))
           return value;
        
        String alnum = StringUtil.removePunctuation(value); //Retain alphabetic and digits only
        
        if(alnum.length() <= 5)
            return alnum;
        
        return alnum.substring(0,5) + "-" + alnum.substring(5);              
    }  
    /*
     * Need to make sure hyphen is not leading/trailing - 
     * Will not be able to reformat once assigned, if in the Errors collection
     * Does formatter remove leading/trailing hyphens: YES
     */
    public String isValidPostalCode(String value, Errors errors, String name, boolean isMvc) { 
        
        if(StringUtil.isNullOrEmpty(value))
            return value;
        
        boolean valid = false;
        boolean truncate = false;        
        
        int pos = value.indexOf("-");        
        
        if(pos > -1) {         
            valid = validateZip5(value.substring(0,pos), errors, name);
            if(isMvc && valid && !errors.hasErrors()){                
                 truncate = validatePlusFour(value.substring(pos+1));
            }
            else if(!isMvc) {
                truncate = validatePlusFour(value.substring(pos+1));
            }             
        } else {
            validateZip5(value, errors, name);            
        }        
        if(truncate)
           return value.substring(0, pos);
        return value;
    }
    
    private boolean validateZip5(String zipSub, Errors errors, String name) {
        
        boolean valid = false;
        
        if( zipSub.length() != 5 && !StringUtil.allDigits(zipSub)) {
                 errors.rejectValue(name, "", "All digits and a length of 5 for the main zip. ");                
        }
        else if(zipSub.length() != 5) {
            errors.rejectValue(name, "", "A length of 5 for the main zip, please. ");
        }
        else if(!StringUtil.allDigits(zipSub)){
             errors.rejectValue(name, "", "All digits for the main zip, please. ");
        }
        else valid = true;
        return valid;
    }
    
    private boolean validatePlusFour(String plusFour) {
        boolean truncate = false;
        if(plusFour.length() != 4 || !StringUtil.allDigits(plusFour))
            truncate = true;
        return truncate;
    }
   
    public  boolean validateZipRange(Errors errors, String zip, States state, String fld) {        
       
        
         int code = Integer.parseInt(zip.substring(0,5));
         
         //Temporary fix for corrupt data in record at lastZip field
         int lastZip = Integer.parseInt(state.getLastZip().substring(0,5));  
        
         int firstZip = Integer.parseInt(state.getFirstZip().substring(0,5));
       
        
        if(code >= firstZip && code <= lastZip) {
            //System.out.println("AddressValidator# first=" + firstZip + " to " + lastZip);
            return true;
        }
         
        String msg = state.getStName() + " has a postal code between " +
                firstZip + " and " + lastZip;
        
       errors.rejectValue(fld, "", msg) ;    
       
       //System.out.println("AddressValidator#returning with error " + msg); 
       
       return false;
    }    
    
    private  boolean allDigits(String entry){
        for(int i=0; i < entry.length(); i++)
            if(!Character.isDigit(entry.charAt(i)))
                   return false;
        return true;
    }
    
    private  int suffixPosition(String[] tokens) {
        
        String[] suffixTest = {"St", "Ave", "Blvd"};
        
        List<String> suffixList = Arrays.asList(suffixTest);
        
        for(int i = 0; i < tokens.length; i++)
            if(suffixList.contains(tokens[i]))
                return i;
        
        return -1;
            
    }
    
} //end class
