/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import java.util.Collection;
import model.customer.Customer;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author dinah
 */
@RunWith(Parameterized.class)
public class FullyInstantiatedTest {
    
    String expected;
    
    String info;
    
    String [] exclude;
    
    Object oinstance;
    
   // Class<?> cls;
    
    public FullyInstantiatedTest( Object oinstance,  String expected,
            String info, String[] exclude ) {
        
        this.expected = expected;
        this.info = info;
        this.exclude = exclude;
        this.oinstance = oinstance;
          
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }

   @ Test 
   public void fullyInstantiatedTest() {
       
       Throwable ex = assertThrows(IllegalArgumentException.class,
           () ->  BeanUtil.throwNotFullyInstantiated(oinstance, info, exclude));
       assertTrue(ex.getMessage().contains(expected));
       System.out.println(ex.getMessage());
   }
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        
        String info = "FullyInstantiatedTest" ;
        
        return Arrays.asList(new Object[][] {
            {null, "Container object is null", info, new String[] {"store"}},
            {new Customer(), "model.customer.Address", info, new String[] {"store"}},
            {initNullCity(), "model.customer.City", info, new String[] {"store"}},
            {initNullCountry(), "model.customer.Country", info, new String[] {"store"}}
              
        });
    }
    
    @Test
    public void fullyInstantiatedValidTest() {
        
       Customer customer = initFullyInstantiated();
       BeanUtil.throwNotFullyInstantiated(customer, "FullyIntantiatedTest",               
               new String [] {"store"});
        
    }
    
    private static Customer initNullCity() {
        
        Customer customer = new Customer();
        Address address = new Address();
        customer.setAddressId(address);
        return customer;
    }
    
    private static Customer initNullCountry() {
        
        Customer customer = initNullCity();
        City city = new City();
        Address address = customer.getAddressId();
        address.setCityId(city);
        return customer;
    }
    
    private static Customer initFullyInstantiated() {
        
        Customer customer = initNullCountry();
        City city = customer.getAddressId().getCityId();
        city.setCountryId(new Country());
        return customer;
    }
}
