/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCustomerService;

import java.util.List;
import model.customer.City;
import model.customer.Country;
import model.customer.States;

/**
 *
 * @author Dinah
 */
public interface ICustomerServiceRest {
    public List<City> getCities() throws Exception;
    public List<City> getCitiesByCountryId(Short countryId);
    public List<City> getCitiesError() throws Exception;
    public List<Country> getCountries() throws Exception;
    public List<States> getStatesList() throws Exception;
    public List<Object[]> getCitiesWithDistrictByCountry(Short countryId);
    
}
