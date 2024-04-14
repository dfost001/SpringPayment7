/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dinah
 */
public class MaskUtilTest {
    
    private String phoneFormat = "(###) ###-####";
    
    public MaskUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testFormat(){
        
        String[] data = {"1231231234", ""};
        String formatted = "";
        for(int i=0; i<data.length; i++){
          formatted = MaskUtil.format(data[i], phoneFormat);
          System.out.println(formatted);
        }
    }
}
