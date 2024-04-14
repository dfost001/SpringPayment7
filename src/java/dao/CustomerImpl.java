/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;



import dao.exception.RecordNotFoundException;
import error_util.EhrLogger;
import java.util.List;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.LineItem;
import model.customer.ShipAddress;
import model.customer.States;
import model.customer.Store;
import model.customer.validation.SupportedTld;
import org.hibernate.FlushMode;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
//import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Repository;
//import org.springframework.stereotype.Service;

/**
 *
 * @author Dinah
 */
//@Service
@Repository
public class CustomerImpl implements CustomerDAO{
    
    @Autowired
    private SessionFactory sessionFactory;

   @Override
    public Customer customerById(Short id) {
        
        String sql = "from Customer where customerId = " + id;
        //customer = (Customer)sessionFactory.getCurrentSession().load(Customer.class, id);
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<Customer> list = q.list();
        if(list == null || list.isEmpty())
            return null;
        return list.get(0);
    }
    
    @Override
    public Customer loadCustomer(Class<?> clazz, Short id) {
        
        IdentifierLoadAccess identifierAccess = sessionFactory
                .getCurrentSession().byId(clazz); //otherwise, entity loaded lazily
        
        Customer customer = (Customer)identifierAccess.load(id);
         
        return customer;
    }
    
    @Override
    public Customer customerByPhone(String phone){
        List<Customer> list = null;
        String sql = "from Customer c where c.addressId.phone = :phone";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        q.setParameter("phone", phone);
        list = q.list();
        if(list == null || list.isEmpty())
            return null;
        return list.get(0);
    }
    
    @Override
    public Country getCountryByCityId(Short cityId) {
        
        String sql = "from City c where c.cityId = " + cityId;
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<City>list = (List<City>)q.list();
        if(list == null || list.isEmpty())
            return null;
        City city = list.get(0);
        return city.getCountryId();
    }
    @Override
    public List<City> getCitiesByCountryId(Short id) {
        
        String sql = "from City c where c.countryId.countryId = " + id
                + " order by c.cityName asc";
         Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<City>list = (List<City>)q.list();
        if(list == null || list.isEmpty()) {
            String err = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "getCitiesByCountryId", "Cities by CountryId returned a null or empty list");
            throw new DataRetrievalFailureException(err);
        }
        return list;
    }
    
    @Override
    public Country findCountryById(Short id) throws DataRetrievalFailureException {
        
        //System.out.println("CustomerImpl#findCountryById: " + id);
        
         IdentifierLoadAccess identifierAccess = sessionFactory
                .getCurrentSession().byId(Country.class); //otherwise, entity loaded lazily
        
        Country country = (Country)identifierAccess.load(id);
         
        if(country == null)
           throw new DataRetrievalFailureException(EhrLogger.doError(this.getClass().getCanonicalName(),
                   "findCountryById", "Retrieval by Id returned null"));
        
        return country;          
         
    }   
   
    @Override
   public void insertCity(City city) {
        
       sessionFactory.getCurrentSession().persist(city);
    }
    
   /* @Override
    public List<Object[]> getCitiesWithDistrictByCountry(Short countryId) {
        
        String nativeSql = "Select DISTINCT c.cityId, c.cityName, a.district " +
                "FROM City c LEFT JOIN Address a ON c.cityId = a.cityId.cityId " +
                "WHERE c.countryId.countryId = " + countryId;
        
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(nativeSql);
        
        List<Object[]> rows = query.list();
        
        if(rows == null || rows.isEmpty()) {
            String err = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "getCitiesWithDistrictByCountry", "Native query returned no rows.");
            throw new DataRetrievalFailureException(err);
        }
        
        return rows;
    } */
    
    @Override
    public List<Object[]> getCitiesWithDistrictByCountry(Short countryId) {
        
        String nativeSql = "Select DISTINCT c.city_id, c.city_name, addr.district " +
                "FROM City AS c INNER JOIN address AS addr ON c.city_id = addr.city_id " +
                "WHERE c.country_id = " + countryId + " ORDER BY c.city_name";
        
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(nativeSql);
        
        if(query == null) {
            String err = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "getCitiesWithDistrictByCountry", "SQLQuery object is null");
            throw new DataRetrievalFailureException(err);
        }
        
        List<Object[]> rows = query.list();
        
        if(rows == null || rows.isEmpty()) {
            String err = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "getCitiesWithDistrictByCountry", "Native query returned no rows.");
            throw new DataRetrievalFailureException(err);
        }
        
        return rows;
    } 

    @Override
    public List<City> getCities()  {
          List<City> cityList = null;
          String sql = "from City c order by c.cityName";
          Query q = sessionFactory.getCurrentSession().createQuery(sql);
          cityList = (List<City>)q.list();          
          return cityList;       
    }
    
    @Override
    public List<City> getCitiesError() throws RuntimeException {    
        
        List<City> cityList = null;
        
        System.out.println("CustomerImpl#getCitiesError: executing");
      
        try {
         
          String sql = "from Citys c order by c.cityName";
          Query q = sessionFactory.getCurrentSession().createQuery(sql);
          cityList = (List<City>)q.list();          
          
        } catch (RuntimeException ex)  {
            System.out.println("CustomerImpl#getCitiesError: RuntimeException: "
                + ex.getMessage());
            throw ex; 
        }
        
        return cityList;
    }
    
    @Override
    public List<States> getStatesList() {
        
        List<States> list = null;
        String sql = "from States s order by s.stName";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        list = q.list();
        return list;
        
    }
    
    @Override
    public List<Address> getAddresses() {
        List<Address>addrList = null;
        String sql = "from Address";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        addrList = q.list();
        return addrList;
    }
    @Override
    public void updateCustomer(Customer customer) {
        
       sessionFactory.getCurrentSession().update(customer);           
       
    }
    
    @Override 
    public List<Country> getCountries() {
        String sql = "from Country";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        return (List<Country>)q.list();
        
    }
    
     @Override
    public void debugUpdate(Customer customer)  {
       
            sessionFactory.getCurrentSession().update(customer);
       
        
    }

    @Override
    public void insertCustomer(Customer customer) {
      
           sessionFactory.getCurrentSession().persist(customer);           
       
    }
    /*
     * Possible null pointer exception
     */
    @Override
    public Store getStoreById(Short id)  {
            String sql = "from Store where storeId = " + id;
            Query q = sessionFactory.getCurrentSession().createQuery(sql);
            return (Store)q.list().get(0);
    }
    
    @Override
    public States findState(String stCode) {
        
        String sql = "from States s where s.stCode = '" + stCode + "'";
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<States> list = q.list();
        if(list == null || list.isEmpty())
            return null;
        States state = list.get(0);
        return state;
    }
    
    public States findStateByCodeOrName(String value) {
        
        String sql = "from States s where s.stCode = :value OR "
                + "s.stName = :value";
                
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        q.setParameter("value", value);
        
        List<States> list = q.list();
        
        if(list == null || list.isEmpty())
            return null;
        States state = list.get(0);
        return state;
    }
    
    @Override
    public City findCityById(Short id) throws RecordNotFoundException {
        
        String sql = "from City c where c.cityId = " + id ;
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        List<City> list = q.list();
        
        if(list == null || list.isEmpty())
           throw new RecordNotFoundException("City", "cityId", id.toString());
        
        City city = list.get(0);
        
        return city;
        
    }
    
    @Override
    public void insertCustomerOrder(CustomerOrder order) {
        
        sessionFactory.getCurrentSession().persist(order);
       
       //System.out.println("inside insert CustomerOrder");
       
    }
    
    @Override
    public void insertLineItem(LineItem item) {
        
        sessionFactory.getCurrentSession().persist(item);
       // System.out.println("inside insertLineItem");
       //throw new UnsupportedOperationException();
    }
    
    @Override
    public List<CustomerOrder> getOrderHistory(Short customerId, Integer firstIdx, Integer length){
        
        String sql = "Select o from CustomerOrder o where o.customer.customerId = "
                + customerId + " order by o.orderDate desc" ;
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        q.setFirstResult(0);
        q.setMaxResults(length);
        List<CustomerOrder> orders = q.list();
        return orders;
        
    }
    
    
    
    @Override
    public List<SupportedTld> getAllSupportedTld() {
        
        List<SupportedTld> list = null;
        
        String sql = "from SupportedTld" ;
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        list = q.list();
        
        if(list == null || list.isEmpty())
            throw new IllegalArgumentException(this.getClass().getCanonicalName()
               + "#getAllSupportedTld: list is null");
        
        return list;
    }

    @Override
    public SupportedTld findSupportedTld(String tld) {
        
        //System.out.println("CustomerImpl#findSupportedTld: param=" + tld );
        
        /*String sql = "from SupportedTld t where "
                + "TRIM(LEADING '.' FROM t.tld) = '" 
                + tld + "'";*/
        
        if(!tld.startsWith("."))
            tld = "." + tld;
        
        String sql = "from SupportedTld s where s.tld = '" + tld + "'";
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        List<SupportedTld> list = null;
        
        list = q.list();
        
        if(list != null && !list.isEmpty()) {
            //System.out.println("CustomerImpl#findSupportedTld: " + list.get(0).getTld());
            return (SupportedTld)list.get(0);
        }
        //System.out.println("CustomerImpl#findSupportedTld: returning null");
        return null;
        
    }
     
    @Override
    public City findCityByName(String name){
        
        String sql = "from City c where lower(c.cityName) = '"
                +  name.toLowerCase() + "'";
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        List<City> list = q.list();
        
        if(list != null && !list.isEmpty())
            return list.get(0);
        
        return null;
    }

    @Override
    public ShipAddress getShipAddressById(Short id) {
        
        String sql = "Select s from ShipAddress s where s.shipId = " + id;
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        List<ShipAddress> list = q.list();
        
        if(list == null || list.isEmpty())
            return null;
        return list.get(0);
    }

    @Override
    public void addShipAddress(ShipAddress shipAddress) {
        
        Session session = sessionFactory.getCurrentSession();
        session.persist(shipAddress);
        session.flush();
        
    }

    @Override
    public void modifyShipAddress(ShipAddress shipAddress) {
        
        sessionFactory.getCurrentSession().merge(shipAddress);
       
    }
    
    @Override
    public List<ShipAddress> shipListByCustomer(Short customerId) {
        
        String sql = "from ShipAddress s where s.customerId.customerId=" + customerId;
        
        Query q = sessionFactory.getCurrentSession().createQuery(sql);
        
        List<ShipAddress> list = q.list();
        
        if(list == null || list.isEmpty())
            
            throw new IllegalArgumentException(EhrLogger.doError(
                    this.getClass().getCanonicalName(), 
                    "shipListByCustomer", 
                    "ShipAddress records with customer key = '"
                            + customerId + "' cannot be found."));
        return list;
    }
    /* Generating exception 
     * org.hibernate.ObjectDeletedException: deleted object would be re-saved by cascade 
     * (remove deleted object from associations): [model.customer.ShipAddress#693]
     */
    @Override
    public void deleteShipAddress(ShipAddress shipAddress) {
        
        Session session = sessionFactory.getCurrentSession();
        
        session.setFlushMode(FlushMode.ALWAYS);
        
        shipAddress = (ShipAddress)session.merge(shipAddress);
        
        shipAddress.setAddressId(null);
        
        session.delete(shipAddress);
        
        session.flush();
        
        
        
    }
    
    @Override
    public Customer deleteShipAddress2(ShipAddress shipAddress, Customer customer) {
        
         Session session = sessionFactory.getCurrentSession();         
                  
         customer.removeShipAddress(shipAddress);
         
         shipAddress = (ShipAddress)session.merge(shipAddress);
        
         shipAddress.setCustomerId(null);
        
         session.delete(shipAddress);
        
         Customer updated = (Customer)session.merge(customer);
         
         session.flush();
         
         return updated;
        
        
    }
    
}
