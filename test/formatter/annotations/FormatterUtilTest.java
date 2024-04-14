/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations; 

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import formatter.annotations.TextFormat.Format;

/**
 *
 * @author Dinah
 */
public class FormatterUtilTest {
    
    public FormatterUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of format method, of class FormatterUtil.
     */
    @Test
    public void testFormat() {
        System.out.println("format");
       // String value = "";
       // String expResult = "";
       // assertEquals(expResult, result);
      //testNameFormat();
     // testDefaultFormat();
        this.testEmailFormat();
    }
    
    private void testNameFormat() {
        
        FormatterUtil util = new FormatterUtil(new Format[] {Format.PROPER_NAME,Format.UPPER});
        
        String[] tests = {"dinah", "dinah2", "   dIn.ah"};
        String[] tests2 = {
            "2DIN2ah2", 
            " DINAH r$$$$",
            " Dinah**  -smith\\",
            "--Dinah",
            "Dinah--", 
            "Dinah-\\  ", 
            "dinah //// smith",
            "Dinah/Smith",
            "Dinah----Smith", 
            "   Dinah--   R    ",
            "4&!--/"};
       
        
        String result = "";
        
        for(String value : tests2) {
            
            result = util.format(value);
            
            System.out.println(value + " = '" + result + "'");
          
        }
    }
    
    private void testDefaultFormat() {
        
      /*  String[] tests = {
            "at&t/pg&e",
            "company a & company b",
            "ABC Plumbers& & Associates",
            "  &Fruit Wholesaler",
            "Fruit Wholesalers #& ",
            "pg&e/ #2",
            "PGE/ATT",
            "ABC Plumbers&/&Fruit Wholesale",
            "ABC plumbers##&/FruitWholesale",
            "#",
            "# 2",
            "at#$",
            "123 Main # 2",
            "123 Main #2",
            "123 main &# 2"
        };*/
        
      String[] tests = {
         "PG&E & Bell",
         "PG& E",
         "AT'&&T",
         "Research & Development",
         "AT &T",
         "& & AB&C  ",
         "& AB& //-c"
      };
      
         FormatterUtil util = new FormatterUtil(new Format[] {Format.DEFAULT, Format.PROPER});
        
         String result = "";
        
        for(String value : tests) {
            
            result = util.format(value);
            
            System.out.println(value + " = '" + result + "'");
          
        }
    }
    
    private void testEmailFormat() {
        
        String[] tests = {
            "dinah@"            
           // ".dinah.@.mail..com"
        };
          FormatterUtil util = new FormatterUtil(new Format[] {Format.EMAIL, Format.LOWER});
        
         String result = "";
        
        for(String value : tests) {
            
            result = util.format(value);
            
            System.out.println(value + " = '" + result + "'");
          
        }
    }
    
} //end class
