/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import util.StringUtil; 

/**
 *
 * @author dinah
 */
public class StringUtilTest {
    
    public StringUtilTest() {
    }
    
    @Before
    public void setUp() {
    }

    
     @Test
    public void tokenizeAndCompareTest() {
        
        String[] compareTo = {"1      Main St   "};
        String[] entry = {"1   Main  St"};
        
        boolean equals = false;
        
        for(int i=0 ; i < compareTo.length; i++) {
            equals = StringUtil.tokenizeAndCompare(compareTo[i], entry[i], false);
            assertEquals(equals, true);
        }
    }
}
