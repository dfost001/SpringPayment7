/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;


import error_util.EhrLogger;
import java.util.Arrays;
import java.util.List;
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
    
    public  void validatePostalCode(PostalAddress postalEntity, States state, Errors errors){
        
        String zip = this.formatUsZip(postalEntity.getAddressId().getPostalCode()) ;
        
        postalEntity.getAddressId().setPostalCode(zip);
        
        if(isValidPostalCode(errors, zip, "addressId.postalCode"))
            validateZipRange(errors, zip, state, "addressId.postalCode");
    }
    
     public String formatUsZip(String value) {
        
        if(StringUtil.isNullOrEmpty(value))
           return value;
        
        String alnum = StringUtil.removePunctuation(value);
        
        if(alnum.length() <= 5)
            return alnum;
        
        return alnum.substring(0,5) + "-" + alnum.substring(5);        
          
    }
    
    public boolean isValidPostalCode(Errors errors, String zip, String fld) {
        
       // System.out.println("AddressValidator#validatePostalCode:zip length=" + zip.length());
       
        if(zip == null)
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(),
                   "isValidPostalCode", "Zip String Parameter is Null.");
        
        if (zip.length() == 5) {
            if (!allDigits(zip)) {
                errors.rejectValue(fld, "", "Only digits, please.");
                return false;
            } else {
                return true;
            }
        } else if (zip.length() == 10) {
            if (!allDigits(zip.substring(0, 5))
                    || zip.charAt(5) != '-'
                    || !allDigits(zip.substring(6))) {
                errors.rejectValue(fld, "", "5 plus 4 digits delimited by a dash required.");
                return false;
            } else {
                return true;
            }
        } else {
            errors.rejectValue(fld, "", "5 or 5 plus 4 digits required.");
            return false;
        }
        
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
