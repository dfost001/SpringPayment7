/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static junit.framework.Assert.assertEquals; 
import org.junit.Ignore;

/**
 *
 * @author Dinah
 */
public class PageCalculatorTest {
    
    public PageCalculatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of setRecordCount method, of class PageCalculator.
     */
    @Test
    public void testSetRecordCount() {
        
        Object[][] params = {
            {200,10},
            {203,11},
            {500,25},
            {575,29},
            {1000,50}
        };
        
        System.out.println("Testing calcMaxPageNo from setRecordCount using defaultPageSize 20");        
        PageCalculator calc = new PageCalculator();
        for(int i=0; i < params.length; i++){
            Object[] obj = params[i];
            int assertion = (int)obj[1];
            int param = (int)obj[0];
            calc.initialize(param);
            int count = calc.getMaxPageNo();
            System.out.println("records:" + param + " result:" + assertion);
            assertEquals(assertion, count);
            
        }
        
    }

    /**
     * Test of getRecordCount method, of class PageCalculator.
     */
    @Ignore
    @Test
    public void testGetRecordCount() {
    }

    /**
     * Test of getPageSize method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetPageSize() {
    }

    /**
     * Test of getCurrentEnd method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetCurrentEnd() {
    }

    /**
     * Test of getCurrentStart method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetCurrentStart() {
    }

    /**
     * Test of getPgNoStart method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetPgNoStart() {
    }

    /**
     * Test of getPgNoEnd method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetPgNoEnd() {
    }

    /**
     * Test of getMaxPageNo method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetMaxPageNo() {
    }

    /**
     * Test of getMaxNoIntervals method, of class PageCalculator.
     */
    @Test
    @Ignore
    public void testGetMaxNoIntervals() {
    }

    /**
     * Test of calcNext method, of class PageCalculator.
     */
    @Test
    public void testCalcNext() {
        System.out.println("testCalcNext with record count = 103");
        int[][] results = {
            {21,40},
            {41,60},
            {61,80},
            {81,100},
            {101,103}
        };
        PageCalculator calc = new PageCalculator();
        calc.initialize(103);
        for(int i=0; i < results.length; i++){
            calc.calcNext();
            int start = calc.getCurrentStart();
            int end = calc.getCurrentEnd();
            assertEquals(results[i][0], start);
            assertEquals(results[i][1], end);
        }
    }

    /**
     * Test of calcPrevious method, of class PageCalculator.
     */
    @Test
    public void testCalcPrevious() {
        System.out.println("testCalcPrevious");
    }

    /**
     * Test of calcFromPageNo method, of class PageCalculator.
     */
    @Test
    public void testCalcFromPageNo() {
    }

    /**
     * Test of calcPgNoEndPoints method, of class PageCalculator.
     */
    @Test
    public void testCalcPgNoEndPoints() {
    }
    
}
