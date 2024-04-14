/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dinah
 */
public class BeanUtilTest {
    
    public BeanUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void throwNotFullyInstantiatedTest() {
        
        String[] exclude = {"store", "customerId"};
        
        PostalAddress postal = null;
        
        List<PostalAddress> list = new ArrayList();
        
        postal = initValidPostalAddress(new Customer());
        
        list.add(postal);
        
        postal = initValidPostalAddress(new ShipAddress());
        
        list.add(postal);
        
        postal = initInvalidAddress(new ShipAddress());
        
        list.add(postal);
        
        invokeTest(list,exclude);
        
       
        
        
    }
    
    private void invokeTest(List<PostalAddress> postalList,  String[] exclude) {
        
        try {

            for (PostalAddress postal : postalList) {

                String info = postal.getClass().getSimpleName();
                
                BeanUtil.throwNotFullyInstantiated(postal, info, exclude);
                
                System.out.println(info + " fully instantiated.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
   // @Test
  /*  public void isFullyInstantiatedTest() throws IllegalAccessException, 
            NoSuchMethodException, InvocationTargetException {
        
       // PostalAddress postal = initValidPostalAddress(new Customer());
       
       // PostalAddress postal = initInvalidAddress(new Customer());
       
       PostalAddress postal = new Customer();
        
        List<String> exclude = Arrays.asList("store");       
        
        String err = BeanUtil.isFullyInstantiated(postal.getClass(), 
                postal, exclude);
        
        System.out.println("Error returned = " + err);
        
    }*/
    

    //@Test
  /*  public void evalFullyInstantiatedTest()
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        
      PostalAddress postal =  initValidPostalAddress(new Customer());
        
      List<String> list = Arrays.asList("Store");
        
      String err = BeanUtil.evalFullyInstantiated(postal.getClass(),postal,list );
       
      System.out.println("Error returned = " + err);
        
    }*/
    
    private PostalAddress initValidPostalAddress(PostalAddress postal)  {   
            
        
        Address address = new Address();
        
        City city = new City();
        
        Country country = new Country();     
        
        city.setCountryId(country);
        
        address.setCityId(city);
        
        postal.setAddressId(address);
        
        return postal;
       
        
    }
    
    private PostalAddress initInvalidAddress(PostalAddress postal) {
        
        Address address = new Address();
        
        City city = new City();
        
        //Country country = new Country();     
        
       // city.setCountryId(country);
        
        address.setCityId(city);
        
        postal.setAddressId(address);
        
        return postal;
        
    }
}
