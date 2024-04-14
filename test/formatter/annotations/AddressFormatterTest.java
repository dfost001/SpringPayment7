/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dinah
 */
public class AddressFormatterTest {
    
    public AddressFormatterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        System.out.println("AddressFormatterTest#setUpClass");
    }
    
    @Before
    public void setUp() {
    }

    
    // @Test
     public void testMatchesBox3() {
         
         String[] input1 = {"pobox", "pobx", "pobo", "pox", "pbox", "pbx", "pbo"};
         
         String[] input = {"pobbox", "bxpo", "pppp", "xpo", "pbbx", "xxx", "bbo"};
         
         String[] input3 = {"pobboxac", "bxpod", "ppppg", "xpoa", "pbbxt", "xxxa", "bboa"};
         
         for(int i=0; i < input.length; i++) {
             
             boolean result = AddressFormatter.matchesBox3(input[i]);
             
             System.out.println(input[i] + " = " + result);
             
             assertEquals(input[i] + " is found", true, result);
         }
     }
     @Test
     public void testEvalBoxFormat2(){
         
         String[] input4 = {"PO Box 5676", "pobox555", "po bx123", "pobo 12", "p ox 4444", "56pbox56", "pbx21st", 
             "100 Main St", "Main St", "Box Lane", "bbbb", "POBox 5676",
             "PO Box5676"};
         
         String[] input1 = {"pobbox", "bxpo", "pppp", "xpo", "pbbx", "xxx", "bbo"};
         
         String[] input3 = {"pobboxac", "bxpod", "ppppg", "xpoa", "pbbxt", "xxxa", "bboa"};
         
         String[] input = {"pp1234 main st"};
         
         for(int i=0; i < input.length; i++) {
             
             String result = AddressFormatter.evalBoxFormat2(input[i]);
             
             System.out.println(input[i] + " = " + result);
         
         }
     }
}
