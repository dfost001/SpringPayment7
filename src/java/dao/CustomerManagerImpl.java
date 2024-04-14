/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import dao.exception.CustomerNotFoundException;
import dao.exception.RecordNotFoundException;
import error_util.EhrLogger;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;
import model.customer.Address;
import model.customer.AddressTypeEnum;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.ShipAddress;
import model.customer.States;
import model.customer.Store;
import model.customer.validation.SupportedTld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

/**
 *
 * @author Dinah
 */
@Service
public class CustomerManagerImpl implements CustomerManager{
    
    @Autowired
    private CustomerDAO customerDAO;    
    
    @Override
    @Transactional
     public Customer loadByEntityDef(Class<Customer> clazz, Short id) 
          throws CustomerNotFoundException {
         
         Table table = clazz.getAnnotation(Table.class);
         
         String tableName = table.name();
         String catalog = table.catalog();
         
         //stub error-checking
         if(!tableName.equalsIgnoreCase("customer") && 
                 !catalog.equalsIgnoreCase("sakila_2")) {
             
             String err = EhrLogger.doError(this.getClass().getCanonicalName(), 
                     "loadByEntityDef", "Table 'sakila_2.customer' expected. "
                     + "Actual annotation: " + catalog + "." + tableName);
             
             throw new IllegalArgumentException(err);
         }
                 
         Customer customer = customerDAO.loadCustomer(clazz, id);
         
         if(customer == null) {
             String err = EhrLogger.doError(this.getClass().getCanonicalName(),
                     "loadByEntityDef", "customerDAO#loadCustomer returned null. ");
             throw new CustomerNotFoundException(err);
         }
         
         return customer;            
         
     }
       
    @Transactional
    @Override
    public Customer customerById(Short id)  {
        return customerDAO.customerById(id);       
    }

    @Transactional
    @Override
    public List<City> getCities() throws RuntimeException{
          return customerDAO.getCities();
    }
    
    @Override
    @Transactional
    public List<City> getCitiesByCountryId(Short countryId) {
         return customerDAO.getCitiesByCountryId(countryId);
    }    
    
    @Transactional
    @Override
    public List<City> getCitiesError() throws RuntimeException{
        
          System.out.println("CustomerManagerImpl#getCitiesError: executing");
        
          return customerDAO.getCitiesError();
    }
    
    @Transactional
    @Override
    public List<Country> getCountries() {
        return customerDAO.getCountries();
    }
    
    @Transactional
    @Override
    public Country getCountryByCityId(Short cityId){
        return customerDAO.getCountryByCityId(cityId);
    }   
    
    @Transactional
    @Override
    public Country findCountryById(Short id) throws DataRetrievalFailureException {
        return customerDAO.findCountryById(id);
    }   
    
    @Transactional
    @Override
    public City insertCity(String cityName, Short countryId) {
        
        Country country = customerDAO.findCountryById(countryId);
        
        City city = new City();
        
        city.setCityName(cityName);
        city.setCountryId(country);
        city.setLastUpdate(new Date());
        
        customerDAO.insertCity(city);
        
        return city;
        
    }
    
    @Transactional
    @Override
    public List<States> getStatesList() throws RuntimeException {
        
        return customerDAO.getStatesList();
    }
    
    @Transactional
    @Override
    public List<Address> getAddresses() {
       return customerDAO.getAddresses();
    } 

    @Transactional
    @Override
    public void updateCustomer(Customer c) {
       customerDAO.updateCustomer(c);
    }

    @Transactional
    @Override
    public void insertCustomer(Customer c) {
        c.getAddressId().setLastUpdate(new Date());
        c.getAddressId().setAddressType(AddressTypeEnum.Customer);
        customerDAO.insertCustomer(c);
    }

    @Transactional
    @Override
    public void debugUpdate(Customer c)  {
         customerDAO.debugUpdate(c);
    }
    
    @Transactional
    @Override
    public Store getStoreById(Short id) {
        return customerDAO.getStoreById(id);
    }
    
    @Transactional
    @Override
    public States findState(String code) {
        return customerDAO.findState(code);
    } 
    
    @Transactional
    @Override
    public City findCityById(Short id) throws RecordNotFoundException {
        
        return customerDAO.findCityById(id);
    }

    @Transactional
    @Override
    public SupportedTld findSupportedTld(String tld) {
        
         return customerDAO.findSupportedTld(tld);
    }

    @Transactional
    @Override
    public City findCityByName(String name) {
        
        return customerDAO.findCityByName(name);
    }
    
    @Transactional
    @Override
    public List<CustomerOrder> getOrderHistory(Short customerId, Integer firstIndex, Integer length) {
        
        return customerDAO.getOrderHistory(customerId, firstIndex, length);
    }

    @Transactional
    @Override
    public List<ShipAddress> getShipAddressListByCustomer(Short customerId) {
        
        return customerDAO.shipListByCustomer(customerId);
       
    }

    @Transactional
    @Override
    public ShipAddress getShipAddressById(Short shipId) {
        
        return customerDAO.getShipAddressById(shipId);
        
    }
    
    /*
     * Does not work - update customer with ship address
     * Need to manually add to its collection field
    */

  /*  @Override
    @Transactional
    public Customer addShipAddress(Customer customer, ShipAddress shipAddress) {       
        
        shipAddress.setCustomerId(customer);
        shipAddress.getAddressId().setLastUpdate(new Date());
        shipAddress.getAddressId().setAddressType(AddressTypeEnum.ShipAddress);       
       customer.getShipAddressList().add(shipAddress);
       customerDAO.updateCustomer(customer);
       return customer;
               
    }*/
    
    /*
     * ShipAddressController does not define the edited/created ShipAddress
     * with @SessionAttributes. So the transactional method must assign 
     * unbound fields. These are: @ManyToOne Customer, and Address.lastUpdate
     */
    
    @Override
    @Transactional
    public void addShipAddress(Customer customer, ShipAddress shipAddress) {        
        
        shipAddress.setCustomerId(customer);
        shipAddress.getAddressId().setLastUpdate(new Date());
        shipAddress.getAddressId().setAddressType(AddressTypeEnum.ShipAddress);
        customerDAO.addShipAddress(shipAddress);
      //  updated = customerDAO.customerById(customer.getCustomerId());      
      
      // return updated;               
    }   
    

    @Override
    @Transactional
    public void modifyShipAddress(Customer customer, ShipAddress shipAddress) {
        
        shipAddress.setCustomerId(customer);
        shipAddress.getAddressId().setLastUpdate(new Date());
        customerDAO.modifyShipAddress(shipAddress);
        //customer = customerDAO.customerById(customer.getCustomerId());  
               
       
    }
    
    @Transactional
    @Override
    public Customer deleteShipAddress(ShipAddress shipAddress, Customer customer) {
        
      if((customerDAO.getShipAddressById(shipAddress.getShipId())) == null)        
      
            
            throw new IllegalArgumentException(
                    
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "deleteShipAddress", "CustomerDAO#getShipAddressById returned null")
            );
       
            
        shipAddress.getAddressId().setLastUpdate(new Date());
       
        // customer.removeShipAddress(shipAddress);  either here or in DAO is OK      
        
        Customer updated = customerDAO.deleteShipAddress2(shipAddress, customer);        
        
        //Customer updated = retrieveUpdatedCustomer(customer.getCustomerId());
        
        return updated;        
        
    } 
    
 /*   @Transactional
    @Override
    public Customer deleteShipAddress(ShipAddress shipAddress, Customer customer) {
        
        customer.removeShipAddress(shipAddress);
        
        customerDAO.updateCustomer(customer);
        
       // Customer updated = retrieveUpdatedCustomer(customer.getCustomerId());
        
        return customer;        
        
    } */
    
    @Transactional(propagation=Propagation.NESTED)
    @Override
    public Customer retrieveUpdatedCustomer(Short id) {
        
        Customer customer = customerDAO.customerById(id);
        
        return customer;
    }
}
