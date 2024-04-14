/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCustomerService;


import error_util.EhrLogger;
import java.util.List;
import model.customer.City;
import model.customer.Country;
import model.customer.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author Dinah
 */
@RestController
@RequestMapping(value="/resources")
public class CustomerControllerREST {
    
    @Autowired
    private ICustomerServiceRest customer;
    
    @RequestMapping(value="/cityList", method=RequestMethod.GET, 
            produces={"application/json", "text/plain"})
    public List<City> getCities() throws Exception{
        
        @SuppressWarnings("UnusedAssignment")
        List<City> list = null;             
       
       list=customer.getCities();
       // list = customer.getCitiesError();
       
       if(list == null)
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                   "getCities", "Transaction returned null. ");
        
       return list;
    }
    
    @RequestMapping(value="/cityListByCountry/{countryId}", method=RequestMethod.GET, 
            produces={"application/json", "text/plain"})
    public List<City> getCitiesByCountry(@PathVariable("countryId") Short countryId) throws Exception{
        
        @SuppressWarnings("UnusedAssignment")
        List<City> list = null;    
        
       if(countryId == null) 
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                   "getCitiesByCountry", "Missing path parameter countryId");
           
       
       list=customer.getCitiesByCountryId(countryId);     
       
       if(list == null)
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                   "getCitiesByCountry", "Transaction returned null. ");
        
       return list;
    }
    
    /*
     * Request removed. List rendered on server.
     */
    @RequestMapping(value="/statesList", method=RequestMethod.GET, produces="application/json")
    public List<States> getStates() throws Exception {
        
        List<States> list = null;
       
        list = customer.getStatesList();
        
       return list;
    }
    
     /*
      * Request not used.
      */
     @RequestMapping(value="/countryList", method=RequestMethod.GET, produces="application/json")
    public List<Country> getCountries() throws Exception{
        List<Country> list = null;
        try {
            list = customer.getCountries();
        }
        catch(Exception ex) {
            throw new Exception("Error retrieving countries:" + ex.getMessage());
        }
        return list;
    }
    
    
    
}
