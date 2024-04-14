/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exception.RecordNotFoundException;
import java.util.List;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.LineItem;
import model.customer.ShipAddress;
import model.customer.Store;
import model.customer.States;
import model.customer.validation.SupportedTld;
import org.springframework.dao.DataRetrievalFailureException;

/**
 *
 * @author Dinah
 */
public interface CustomerDAO {
    public Customer customerById(Short id);
    public Customer loadCustomer(Class<?> customer, Short id);
    public Customer customerByPhone(String phone);
    public Country getCountryByCityId(Short cityId);  
    public Country findCountryById(Short id) throws DataRetrievalFailureException;
    public List<City> getCitiesByCountryId(Short countryId);
    public List<Object[]> getCitiesWithDistrictByCountry(Short countryId);
    public List<City> getCities();
    public List<City> getCitiesError();
    public void insertCity(City city);
    public List<States> getStatesList() ;
    public List<Country> getCountries() ;
    public List<Address> getAddresses() ; 
    public void updateCustomer(Customer customer) ;
    public void insertCustomer(Customer customer);
    public void debugUpdate(Customer customer);
    public Store getStoreById(Short id) ;
    public States findState(String stCode) ;
    public City findCityById(Short id) throws RecordNotFoundException ;
    public void insertCustomerOrder(CustomerOrder order) ;
    public void insertLineItem(LineItem item); 
    public List<SupportedTld> getAllSupportedTld();
    public SupportedTld findSupportedTld(String tld); 
    public City findCityByName(String cityName);
    public List<CustomerOrder> getOrderHistory(Short customerId, Integer firstIdx, Integer length);
    public ShipAddress getShipAddressById(Short id);
    public void addShipAddress(ShipAddress shipAddress);
    public void modifyShipAddress(ShipAddress shipAddress);
    public void deleteShipAddress(ShipAddress shipAddress);
    public Customer deleteShipAddress2(ShipAddress shipAddress, Customer customer);
    public List<ShipAddress> shipListByCustomer(Short customerId);
}
