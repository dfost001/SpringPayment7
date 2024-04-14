/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addressService;

import restAddressService.addressService.ServiceConnect;
import restAddressService.addressService.SvcAnalysis;
import restAddressService.addressService.FieldEhr;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dinah
 */
public class ServiceConnectTest {
    
    public ServiceConnectTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }

   
    
  /*  @Test
    public void processAddressTest() {
        
        String[][] data = {
            
           // {"547 24th St Apt 15", "Oakland", "CA", "94612"},
           // {"547 24th St N Apt 15", "Oakland", "CA", "31405"}
            {"4603 Sussex Pl #2", "Savannah", "GA", "94612"}
            
        };
        
        ServiceConnect svc = new ServiceConnect();
        
        for(int row=0; row < data.length; row++){
            
            try {
                SvcAnalysis postal = svc.processAddress(data[row][0], 
                        data[row][1], data[row][2], data[row][3]);
                
                 System.out.println("Valid=" + postal.getValid());
		 System.out.println("MatchCode=" + postal.getMatchCode());
                 System.out.println("confirmRequired=" + postal.isConfirmRequired());
                // System.out.println("confirmLine=" + postal.getConfirmAddress());
		
		 System.out.println("found=" +
				        postal.getDeliveryLine() 
						+ " " + postal.getZipPlus4()
						+ " " + postal.getCity()
						+ " " + postal.getStateAbbrev());
		
		     printMessages(postal.getSvcMessages());
                     printErrors(postal.getErrors());
                
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
            
        } //end for
        
    } //test */
    
    private void printMessages(List<String> messages){
		
		for(String s : messages) {
			System.out.println(s);
		}
   }
    
    private void printErrors(List<FieldEhr> errors) {
        for(FieldEhr ehr : errors) {
            System.out.println(ehr.getField() + ": " + ehr.getMessage());
        }
    }
} //end class
