/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dinah
 */
public class CompareAddressUtil2Test {
    
    private  static Connection cn;
    
    private static ResultSet rsCustomer;
    
    private final static String CN_STRING = "jdbc:mysql://localhost:3306/sakila_2?useSSL=false";
    
    private final static String SQL = "Select * from Customer where customer_id = 628";
    
    public CompareAddressUtil2Test()  {
        
       
    }
    
    @BeforeClass
    public static void setUpClass() throws SQLException{
        
         cn = DriverManager.getConnection(CN_STRING, "root", "gw7749");
         
         Statement st = cn.createStatement();
         
         rsCustomer = st.executeQuery(SQL);
    }
    
    @Before
    public void setUp() {
        
        
    }

    
     @Test
     public void compareModelToPropertyToDbCustomer() {
         
         if(cn != null)
             System.out.println("Connection obtained: " + cn.toString());
     }
}
