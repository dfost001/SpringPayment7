/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import dao.CustomerManager;
import dao.exception.RecordNotFoundException;
import error_util.EhrLogger;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import model.customer.States;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Component;


import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import util.BeanUtil;
import util.StringUtil;


/**
 *
 * @author dinah
 */
@Component
public class UiEvalUtil {
    
    @Autowired
    private CustomerManager customerMngr;  
    
    public final Short US_COUNTRY = 103;
    
    private Country country;
    
    private States state;

    public Country getCountry() {
        return country;
    }

    public States getState() {
        return state;
    }    
    
    
    public void evalInterface(PostalAddress postal, Errors errors) {
        
        this.checkFullyInstantiated(postal);

        Address address = postal.getAddressId();       

        this.checkConstraints(address, errors);
        
        Short tmpCountryId = address.getCityId().getCountryId().getCountryId();        
        
        validateCountryCityStateFill(tmpCountryId, address.getCityId().getCityId(), address.getDistrict());
        
        EhrLogger.printToConsole(this.getClass(), "evalInterface", "completed execution: countryId=" + tmpCountryId);
        
    }
    
     private void checkFullyInstantiated(PostalAddress postal) {
        
        String[] exclude = null;       
        
        if(postal == null) {
            String err = "PostalAddress is null. Possibly not annotated.";
            this.doError("checkFullyInstantiated", err, null);
        }
        
        if(postal.getClass().isAssignableFrom(Customer.class))
            exclude = new String[] {"store"};
        else if(postal.getClass().isAssignableFrom(ShipAddress.class))
            exclude = new String[] {"customerId"};
        
        String info = postal.getClass().getCanonicalName() +
                ": " + "Fields of the Address object are not bound: ";
        
        BeanUtil.throwNotFullyInstantiated(postal, info, exclude);
    }
     
    private void checkConstraints(Address address, Errors errors) {   
        
        this.debugPrintErrors(errors);
        
        BeanUtil.throwEmptyCheckErrors(Address.class, address, errors,
                "address1", "district", "postalCode", "phone");
        
        BeanUtil.throwEmptyCheckErrors(City.class, address.getCityId(), errors, "cityId");
        
        BeanUtil.throwEmptyCheckErrors(Country.class, 
                address.getCityId().getCountryId(), 
                errors, "countryId");
        
    } 
    /*
     * Note: If countryId is null, City may/may not have a value
     * depending on JavaScript code.
    */
    private void validateCountryCityStateFill(Short tmpCountryId, Short cityId, String districtValue) {
        
        if(tmpCountryId == null)
            return;
        
        this.country = validateCountryFill(tmpCountryId);
        
        if(cityId != null)
            validateCityFill(cityId, country.getCountryId());
        
        if(country.getCountryId().equals(this.US_COUNTRY) && !StringUtil.isNullOrEmpty(districtValue))
            this.processState(districtValue);
                
    }
     private Country validateCountryFill(Short id) {
         
        Country tcountry = null; 
        
        try {
            
            tcountry = customerMngr.findCountryById(id);
            
        } catch (DataRetrievalFailureException e) { //Rethrow as Runtime
            this.doError("validateCountryFill",
                    "Country selection box value failed to retrieve a record", e);
        }
        return tcountry;
    }
    
     private void validateCityFill(Short id, Short selectedCountryId) {  
         
         City city = null;
        
         try {
         
            city = customerMngr.findCityById(id);
            
         } catch (RecordNotFoundException e) {
             this.doError (
                     "validateCityFill",
                     "City select-box value fails to retrieve a City. ", e);
         } 
         if(!city.getCountryId().getCountryId().equals(selectedCountryId))
             this.doError(
                     "validateCityFill",
                     "CountryId in selected City record does not equal selected country.", null );
        
     } 
   
     private void processState(String districtValue) {         
         
         state = customerMngr.findState(districtValue);
         
         if(state == null)
             this.doError("processState", "District input does not retrieve a US state.", null);         
         
        
     }
    
    private void doError(String method, String message, Exception cause) {
        
        String err = this.getClass().getCanonicalName() 
                + "#"
                + method
                + ": "
                + message;
        
        if(cause != null)
              throw new IllegalArgumentException(err, cause);
        
        throw new IllegalArgumentException(err);
    }
    
    public void debugPrintErrors(Errors errors) {
        
        if(errors.hasErrors())
            System.out.println("UiEvalUtil#debugPrintErrors: BindingResult has errors.");
        else System.out.println("UiEvalUtil#debugPrintErrors: BindingResult has NO errors.");
        
        if(!errors.hasErrors()) return;
        
         for(FieldError err : errors.getFieldErrors()){
           System.out.println(err.getField() + ": " + err.getDefaultMessage());
            
        }  
    } 
    
} //end class
    
