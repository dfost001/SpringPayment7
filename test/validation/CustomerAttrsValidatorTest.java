/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import java.util.Date;
import model.customer.Address;
import model.customer.City;
import model.customer.Customer;
import model.customer.Store;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import view.attributes.CloneUtil;

/**
 *
 * @author Dinah
 */
public class CustomerAttrsValidatorTest {
    
    private CloneUtil cloneUtil;
    
    public CustomerAttrsValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        
    }
    
    @Before
    public void setUp() {
        
        cloneUtil = new CloneUtil();
    }
    //Method removed
    @Test
    public void isNewCustomerTest() {
        
        CustomerAttrsValidator validator = new CustomerAttrsValidator();
        
        Customer customer = new Customer();
        
        customer.setActive(true);
        
        customer.setCreateDate(new Date());
        
        Store store = new Store();
        
        customer.setStore(store);
        
        customer.setFirstName("");
        
        Address address = new Address();
        
        City city = new City();
        
        city.setCityId((short)1);
        
        address.setCityId(city);
        
        customer.setAddressId(address);       
        
        boolean empty = true;
        
        try {
        
             //empty = cloneUtil.isNewCustomer(customer,true); //evalNullOnly
        
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        System.out.println("Done: empty=" + empty);
        
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
