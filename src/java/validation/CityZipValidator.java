/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import dao.CustomerManager;
import dao.exception.RecordNotFoundException;
import httpUtil.HttpClientException;
import httpUtil.HttpException;
import model.customer.City;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;
import restCityState.ZipLookupConnect;

/**
 *
 * @author dinah
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class CityZipValidator implements org.springframework.validation.Validator {
    
    @Autowired
    private CustomerManager customerMngr;    

    @Autowired
    private ZipLookupConnect zipLookup;     
    
    private final Short US_COUNTRY = 103;
    
    private boolean testHttpEx = false;

    @Override
    public boolean supports(Class<?> type) {
        
         if(PostalAddress.class.isAssignableFrom(type)) 
             return true;
         
         return false;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        
        System.out.println("CityZipValidator#validate executing")  ;
        
        PostalAddress postal = (PostalAddress) obj;
        
        if(errors.hasErrors()) return;
        
        if(!isUS(postal))
            return;
        
        this.validateUSCityState(postal, errors);
        
    }
    
     private boolean isUS(PostalAddress postal) {
         
         Short countryId = postal.getAddressId()
                 .getCityId().getCountryId().getCountryId();
         
         if(countryId.equals(this.US_COUNTRY))
             return true;
         
         return false;
     }
    
      private void validateUSCityState(PostalAddress postal, Errors errors) {     
          
        if(testHttpEx) {
            testHttpEx = false;
            HttpClientException ex = new HttpClientException(null, "Testing HttpClientException", 
                    "Testing", "validateUSCityZip");
             String message = "Response Code: " + 500
                    + ": " + ex.getMessage() ;
            
            this.doError("validateUSCityState", message, ex);
        }  
         
        String cityName = this.retrieveCityName(postal.getAddressId().getCityId().getCityId());
        
        try {
            
        zipLookup.validateCityStateZip(cityName, 
                postal.getAddressId().getDistrict(), 
                postal.getAddressId().getPostalCode(), errors);
        
        } catch (HttpException ex) {
            
            String message = "Response Code: " + ex.getResponseCode()
                    + ": " + ex.getMessage() ;
            
            this.doError("validateUSCityState", message, ex);
        }
    }
      
     private String retrieveCityName(Short cityId) {
         
       City city = null;
        
        try {           
       
          city = this.customerMngr.findCityById(cityId);
          
        } catch(RecordNotFoundException e) {
            
           this.doError("retrieveCityByName", e.getMessage(), e);
        }
        
        return city.getCityName();
    } 
    
    
    private void doError(String method, String message, Exception cause) {
        
        String err = this.getClass().getCanonicalName() 
                + "#"
                + method
                + ": "
                + message;
        throw new IllegalArgumentException(err, cause);
    }  
    
}
