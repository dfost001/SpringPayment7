/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exception.CustomerNotFoundException;
import dao.exception.RecordNotFoundException;
import java.util.List;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.ShipAddress;
import model.customer.States;
import model.customer.Store;
import model.customer.validation.SupportedTld;
import org.springframework.dao.DataRetrievalFailureException;

/**
 *
 * @author Dinah
 */
public interface CustomerManager {
    public Customer loadByEntityDef(Class<Customer> clazz, Short id) 
            throws CustomerNotFoundException ;
    public Customer customerById(Short id);
    public List<City> getCities() ;
    public List<City> getCitiesError() ;
    public List<City> getCitiesByCountryId(Short countryId);   
    public City insertCity(String cityName, Short countryId);
    public List<States> getStatesList() ;
    public List<Country> getCountries () ;
    public Country findCountryById(Short id) throws DataRetrievalFailureException;        
    public Country getCountryByCityId(Short id);    
    public States findState(String code) ;
    public City findCityById(Short id) throws RecordNotFoundException;
    public List<Address> getAddresses() ;
    public void updateCustomer(Customer c) ;
    public void insertCustomer(Customer c) ;
    public void debugUpdate(Customer c) ;
    public Store getStoreById(Short id);
    public SupportedTld findSupportedTld(String tld);
    public City findCityByName(String cityName);
    public List<CustomerOrder> getOrderHistory(Short customerId, Integer firstIdx, Integer length);
    public List<ShipAddress> getShipAddressListByCustomer(Short customerId);
    public ShipAddress getShipAddressById(Short shipId);
    public void addShipAddress(Customer customer, ShipAddress shipAddress);
    public void modifyShipAddress(Customer customer, ShipAddress shipAddress);
    public Customer deleteShipAddress(ShipAddress shipAddress, Customer customer);
    public Customer retrieveUpdatedCustomer(Short id);
}
