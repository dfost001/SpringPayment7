/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState;

import error_util.EhrLogger;
import java.util.List;
import org.springframework.validation.Errors;
import restCityState.client.LookupResponse;
import restCityState.client.Zipcodes;

/**
 *
 * @author dinah
 */
public class CityZipService {
    
    public static final String CITY_FLD_PATH = "addressId.cityId.cityId" ;
    
    public static final String ZIP_MESSAGE_PREFIX = "ZIP_LOOKUP"; //read by CustomerAttrsValidator
    
    public void processLookup(LookupResponse lookup, 
            String zipcode, String cityName, String stateCode, Errors errors) {
        
        if(this.validateCityStatus(lookup, cityName, stateCode, errors))
            this.validateCityZip(lookup.getZipcodes(), zipcode, cityName, errors);
        
    }
    /*
     * Bug: Recheck status since all except invalid_city are thrown as IllegalArg
     * OK: Only city/state are uploaded as query parameters
    */
    private boolean validateCityStatus(LookupResponse lookup, String city, String state, Errors errors) {
        
        LookupResponse.Status status = lookup.getStatus();
        
        if(status == null)
            return true;       
       
        
        switch (status) {
            case blank:  //Blank lookup (must provide a ZIP Code and/or City/State combination)    
                 this.throwIllegalArg("validCityStatus", lookup.getReason() + ": "
                      + "Must provide a ZIP Code and/or City/State combination. ");
                break;
            case invalid_state:  //Invalid State name or abbreviation 
                this.throwIllegalArg("validCityStatus", lookup.getReason() + ": "
                      + "Invalid State name or abbreviation. ");
                break;
                
            case invalid_zipcode: //Invalid ZIP Code
                this.throwIllegalArg("validCityStatus", lookup.getReason() + ": "
                      + "Zipcode is not evaluated as a parameter. ");
                break;
                
            case conflict: //Conflicting Code/City/State information.
                this.throwIllegalArg("validCityStatus", lookup.getReason() + ": "
                      + "Zipcode not sent as a parameter. ");
                break;
                
            case invalid_city:
                
                String issue = ZIP_MESSAGE_PREFIX + ": ";
                
                issue += city + " is not located in " + state +
                        ". Invalid city for state.";
                
                errors.rejectValue(CITY_FLD_PATH, null, issue);
               
                break;
                
            default:
                this.throwIllegalArg("validCityStatus", "Unknown value for LookupResponse#status");
                    
        } //end switch
        
        return false;
        
    } //end validate
    
    private void validateCityZip(List<Zipcodes> zipcodes, String zipIn, String city, Errors errors) {
        
        String srch = zipIn.length() > 5 ? zipIn.substring(0, 5) : zipIn ;
        
        for(Zipcodes zipcode : zipcodes) {
            
          if(srch.equals(zipcode.getZipcode()))
                return ;
        }
        String err = ZIP_MESSAGE_PREFIX + ": ";
        
        err += city + " has a zip between " + zipcodes.get(0).getZipcode()
                + " and "
                + zipcodes.get(zipcodes.size() - 1).getZipcode()
                + ". Your entry " + srch + " is not in the list of Zips for "
                + city;        
       
        
        errors.rejectValue(CITY_FLD_PATH, null, err); 
       
    }
    
    private void throwIllegalArg(String method, String message) {
        
        throw new IllegalArgumentException (EhrLogger.doError(
                this.getClass().getCanonicalName(), method, message));
    } 
}
