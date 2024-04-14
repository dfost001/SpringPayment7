/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import error_util.EhrLogger;
import java.io.Serializable;


import model.customer.PostalAddress;
//import model.customer.ShipAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
//import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * Fixed: If postal field is null or empty and not in Errors throw Runtime
 * 
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class AddressValidator implements Validator, Serializable{  
    
    @Autowired UiEvalUtil ui;
    
    @Autowired
    private AddressValidatorUtil validatorUtil;
    
    public AddressValidator(){
        //logger = LoggerResource.produceLogger(this.getClass().getCanonicalName());
    }     
   
    @Override
    public boolean supports(Class<?> type) {      
       
       boolean ok = PostalAddress.class.isAssignableFrom(type);
       
       return ok;
    }
    
    //To do: Assign formatted zip into object
    @Override
    public void validate(Object obj, Errors errors) {
        
        EhrLogger.printToConsole(this.getClass(), "validate", "executing"); 

        PostalAddress postal = (PostalAddress) obj;          
        
        ui.evalInterface(postal, errors);   
        
        if (!this.doValidation(errors)) {
            return;
        }   
          
        if (!ui.US_COUNTRY.equals(ui.getCountry().getCountryId())) {
            return;
        }       
        
        validatorUtil.validateAddressLineFormat(postal.getAddressId().getAddress1(), 
                "addressId.address1", errors);

        validatorUtil.validatePostalCode(postal, ui.getState(), errors);  
        
        EhrLogger.printToConsole(this.getClass(), "validate", "exiting"); 

    }
    
  
    
    private boolean doValidation(Errors errors){        
        
        for(FieldError err : errors.getFieldErrors()){
            if(err.getField().contains("addressId.phone"))
                continue; //does not affect postal address validation
            else if(err.getField().contains("addressId"))
                
                return false;
            
        }  
        System.out.println("AddresserValidator#doValidation: is returning true for no violations."); 
       
        return true;
    }   
  
    
} //end class
