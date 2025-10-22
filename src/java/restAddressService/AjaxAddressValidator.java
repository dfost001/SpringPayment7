/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService;


import error_util.EhrLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import restAddressService.addressService.AddrSvcConstants;
import util.StringUtil;
import validation.AddressValidatorUtil;

/**
 *
 * @author Dinah
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class AjaxAddressValidator {
    
    @Autowired
    private AddressValidatorUtil addrValidator;   
    
    
       
    public void validate(AjaxRequest request, BindingResult errors)   
     throws IllegalArgumentException {   
        
        
        if(checkConstraints(request.getCountry(), request.getStreet(), errors))
                return; //Throws for not annotated or returns a boolean
        
        if(!isUnitedStates(request.getCountry(),errors))
            return;   
        
        String street = request.getStreet(); //formatted to empty string by deserializer    
         
        String zip = StringUtil.getValueOrEmpty(request.getZipcode());
        
        String city = StringUtil.getValueOrEmpty(request.getCity()); 
        
        String district = StringUtil.getValueOrEmpty(request.getState());
        
        addrValidator.validateAddressLineFormat(street, AddrSvcConstants.fldStreet, errors);       
        
        /*if(!zip.isEmpty()) {         
         
           String formatted = addrValidator.isValidPostalCode(zip, errors, AddrSvcConstants.fldZip);
          
           request.setZipcode(formatted);
        }*/   
        
        String zipFormatted = validateRequiredFields(city, zip, district, errors);   
        
       request.setZipcode(zipFormatted);
    }
    
   
    
    private Boolean checkConstraints(String country, String street, BindingResult errors){
        
        String message = "";
        Boolean hasErrors = false;
        
        String notAnnotatedEhr = "Empty field not handled by JSR constraint violation: ";
        String annotatedEhr = "Empty fields are annotated, " +
                "but should be handled by custom validation: city/state/zip. ";
        
        if(StringUtil.isNullOrEmpty(street)){
            if(!errors.hasFieldErrors(AddrSvcConstants.fldStreet))
              message = notAnnotatedEhr + "Empty street";
            else hasErrors = true;
        }
        if(StringUtil.isNullOrEmpty(country)) {
            if(!errors.hasFieldErrors(AddrSvcConstants.fldCountry))
               message = message.isEmpty() ? notAnnotatedEhr + "Empty country. " :
                       message + " and country. ";
            else hasErrors = true;
        }
        if(!message.isEmpty())
            EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), "checkConstraints", message);
        
        if(errors.hasFieldErrors(AddrSvcConstants.fldCity) ||
                errors.hasFieldErrors(AddrSvcConstants.fldState) ||
                errors.hasFieldErrors(AddrSvcConstants.fldZip))
            EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), "checkConstraints", annotatedEhr);
        
        return hasErrors;
    }
    
    private boolean isUnitedStates(String country, BindingResult errors){
        
        if(!country.equals("103")) {
           
           errors.reject(null,"Currently, only an United States address can be verified.");
           return false;
        }
        return true;
    }
    
   /* private boolean validateRequiredFields(String city, String zip,  String state, BindingResult errors) {
        
        System.out.println("AjaxAddressValidator#validateRequiredFields: processing");
        
        if(!zip.isEmpty())
            return true;
        else if(!state.isEmpty() && !city.isEmpty()) //zip is empty
            return true;       
        errors.reject(null,"Either a zipcode or a city and state is required.");
        return false;
    }*/   
    
     private String validateRequiredFields(String city, String zip,  
             String state, BindingResult errors) {
         
         String formatted = zip;
         
         boolean isMvc = false;
         
         if(state.isEmpty() || city.isEmpty()) {
             if(!zip.isEmpty())
                 formatted = this.addrValidator.isValidPostalCode
                                          (zip, errors, AddrSvcConstants.fldZip, isMvc);
             else errors.reject(null,"Either a zipcode or a city and state is required.");
         }
         return formatted;
     }
}
