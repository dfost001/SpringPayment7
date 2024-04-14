/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService;

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
               
        addrValidator.validateAddressLineFormat(street, AddrSvcConstants.fldStreet, errors);
        
        String zip = StringUtil.getValueOrEmpty(request.getZipcode());
        
        if(!zip.isEmpty()) {
            zip = addrValidator.formatUsZip(zip);
            addrValidator.isValidPostalCode(errors, zip, AddrSvcConstants.fldZip);//Fix:Use less stringent
            request.setZipcode(zip);
        }
        this.validateRequiredFields(StringUtil.getValueOrEmpty(request.getCity()),
                zip,
                StringUtil.getValueOrEmpty(request.getState()), errors);              
              
    }
    
  
    
    private Boolean checkConstraints(String country, String street, BindingResult errors){
        
        String message = "";
        Boolean hasErrors = false;
        
        if(street == null || street.trim().isEmpty()){
            if(!errors.hasFieldErrors(AddrSvcConstants.fldStreet))
              message = "Empty street not caught by JSR bean constraint annotation";
            else hasErrors = true;
        }
        if(country == null || country.trim().isEmpty()) {
            if(!errors.hasFieldErrors(AddrSvcConstants.fldCountry))
               message += "Empty country not caught by JSR bean constraint annotation. ";  
            else hasErrors = true;
        }
        if(!message.isEmpty())
            throw new IllegalArgumentException(this.getClass().getName()
             + "#checkConstraints: " + message);
        
        if(errors.hasFieldErrors(AddrSvcConstants.fldCity) ||
                errors.hasFieldErrors(AddrSvcConstants.fldState))
             throw new IllegalArgumentException(this.getClass().getName()
             + "#checkConstraints: City and/or State are constraint annotated");
        
        return hasErrors;
    }
    
    private boolean isUnitedStates(String country, BindingResult errors){
        
        if(!country.equals("103")) {
           
           errors.reject(null,"Currently, only an United States address can be verified.");
           return false;
        }
        return true;
    }
    
    private boolean validateRequiredFields(String city, String zip,  String state, BindingResult errors) {
        
        System.out.println("AjaxAddressValidator#validateRequiredFields: processing");
        
        if(!zip.isEmpty())
            return true;
        else if(!state.isEmpty() && !city.isEmpty()) //zip is empty
            return true;       
        errors.reject(null,"Either a zipcode or a city and state is required.");
        return false;
    }
}
