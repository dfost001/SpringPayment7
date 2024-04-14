package view.attributes;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import model.customer.Address;
import model.customer.AddressTypeEnum;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.ShipAddress;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dinah
 */
public class CloneUtilTest {
    
    private Customer customer;
    
    public CloneUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
        customer = new Customer();
        
        Address address = new Address();
        address.setAddress1("1 Main St");
        address.setAddressId((short)5);
        address.setAddressType(AddressTypeEnum.Customer);
        address.setDistrict("GA");
        address.setPhone("1231231234");
        
        City city = new City();
        city.setCityName("Atlanta");
        
        Country country = new Country();
        country.setCountryName("United States");
        
        city.setCountryId(country);
        
        address.setCityId(city);
        
        customer.setAddressId(address);
        customer.setEmail("dinah@mail.com");
        customer.setFirstName("Dinah");
        
        List<ShipAddress> addressList = new ArrayList<>();
       
        ShipAddress shipAddress = new ShipAddress();
        
        shipAddress.setAddressId(address);
        shipAddress.setCustomerId(customer);
        shipAddress.setFirstName("Susan");
        shipAddress.setShipId((short)10);
        
        addressList.add(shipAddress);
       
        customer.setShipAddressList(addressList);
        
    }

    
     @Test
     public void cloneCustomerTest() {
         
         CloneUtil util = new CloneUtil();
         Object cloned = util.cloneCustomer(customer);
         Customer c = null;
         try {
          c = (Customer)cloned;
         } catch (Exception e) {
             e.printStackTrace();
         }
         if(c != null) {
             System.out.println(c.getAddressId().getAddress1());
         }
         
     }
}
