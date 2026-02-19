/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import java.util.Collection;
import model.customer.Address;
import model.customer.City;
import model.customer.Country;
import model.customer.Customer;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

/**
 *
 * @author dinah
 */
@RunWith(Parameterized.class)
public class CheckConstraintsTest {
    
    private static Customer customer;
    
    private Class<?> clz ;
    private Object instance ;
    private BindingResult errors;
    private String[] fields;
    private String expected;
    
    public CheckConstraintsTest( Class<?> clz ,
     Object instance ,
     BindingResult errors,
     String[] fields,
     String expected) {
        this.clz = clz;
        this.instance = instance;
        this.errors = errors;
        this.fields = fields;
        this.expected = expected;
    }
    @Test
    public void checkViolationInErrors() {
        
        Throwable ex = assertThrows(IllegalStateException.class,
                () -> BeanUtil.throwEmptyCheckErrors(clz,instance, errors, fields));
        System.out.println(ex.getMessage());        
        assertTrue(ex.getMessage().contains(expected));
    }
    
     @Parameterized.Parameters
    public static Collection<Object[]> data() {
        
        customer = new Customer();
        
        Address address = new Address();
        
        customer.setAddressId(address);
        
        City city = new City();
        
        address.setCityId(city);
        
        Country country = new Country();
        
        city.setCountryId(country);
        
        BindingResult result = initAddressFldsBindingResult(customer);
        
        return Arrays.asList(new Object[][]{
            { Address.class, customer.getAddressId(), result, 
                  new String[] {"address1", "postalCode", "district", "phone" },
                 "district;phone"
            },
            { City.class, customer.getAddressId().getCityId(), result, new String[] {"cityId"},
               "cityId"                     
            },    
            { Country.class, customer.getAddressId().getCityId().getCountryId(),
                result, new String[] {"countryId"}, "countryId"                     
            }  
        });

    }//end method
    
    private static BindingResult initAddressFldsBindingResult(Object obj) {
        
        DataBinder binder = new DataBinder(obj);
        BindingResult result =  binder.getBindingResult();
        result.rejectValue("addressId.address1", null, "may not be empty");
        result.rejectValue("addressId.postalCode", null, "may not be empty");
        return result;
    }
    
   
} //end class
