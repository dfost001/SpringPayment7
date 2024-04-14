/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCustomerService;

import dao.CustomerDAO;
import error_util.EhrLogger;
import java.util.List;
import model.customer.City;
import model.customer.Country;
import model.customer.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dinah
 */

@Repository
public class CustomerServiceImpl implements ICustomerServiceRest{
    
    @Autowired
    private CustomerDAO customer;
    
    private boolean testCitiesByCountryRecoverable = false;

    @Override
    @Transactional
    public List<City> getCities() throws Exception {
        return customer.getCities();
    }
    /*
     * For testing Jackson2JsonView serialization on CustomerControllerREST
     */
    @Override
    @Transactional
    public List<City> getCitiesError() throws Exception {
        return customer.getCitiesError();
    }

    @Override
    @Transactional
    public List<Country> getCountries() throws Exception {
        return customer.getCountries();
    }    
   
    @Transactional
    @Override
    public List<City> getCitiesByCountryId(Short countryId) {
        
        if(this.testCitiesByCountryRecoverable) {
            this.testCitiesByCountryRecoverable = false;
            throw new RecoverableDataAccessException(EhrLogger.doError(
                    this.getClass().getCanonicalName(), "getCitiesByCountryId", 
                    "Testing Recoverable Data Access"));
        }
        
         return customer.getCitiesByCountryId(countryId);
    }   
    
    @Transactional
    @Override
    public List<States> getStatesList() throws Exception {
        return customer.getStatesList();
    }
    
    @Override
    @Transactional
    public List<Object[]> getCitiesWithDistrictByCountry(Short countryId) {
        
        System.out.println("CustomerServiceImpl#getCitiesWithDistrict: invoking CustomerDAO");
        
        List<Object[]> rows = customer.getCitiesWithDistrictByCountry(countryId);
        
        System.out.println("CustomerServiceImpl#getCitiesWithDistrict: rows="
            + rows.size());
        
        for(Object[] row : rows) {
            
            String line = Short.parseShort(row[0].toString()) + " "
                    + row[1].toString() + " "
                    + row[2].toString();
            
            System.out.println(line);
        }
        
        return rows;
    }
    
    
}
