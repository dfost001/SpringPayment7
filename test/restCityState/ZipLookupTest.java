/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCityState;

import httpUtil.HttpClientException;
import httpUtil.HttpException;
import java.io.IOException;
import model.customer.Customer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;

/**
 *
 * @author dinah
 */
public class ZipLookupTest {
    
    private static Errors errors;
    
    public ZipLookupTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        DataBinder binder = new DataBinder(new Customer());
        
        errors = binder.getBindingResult();
    }
    
    @Before
    public void setUp() {
    }

    
     @Test
     public void validateZip() throws IOException, HttpException{        
         
         String[][] params = {
             {"Oakland", "CA", "94612"},{"Savannah", "CA", "02130"},
             {"Savannah", "CA", "31405"},
             {"Savannah", "CA", "94612"},
             {"Los Angeles", "CA", "94612"}
         };         
         
         ZipLookupConnect connect = new ZipLookupConnect();
         for(int i=0; i <= 1; i++)
         try {
             
            connect.validateCityStateZip(params[i][0],params[i][1], params[i][2], errors);
            
         } catch (HttpClientException ex) {
             System.out.println(ex.getMessage());
         }
     }
}
