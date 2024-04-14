/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

/**
 *
 * @author dinah
 */
@Component
public class DebugPrintUtil {
    
    public static void printSession(HttpSession session) {
        
        Enumeration<String> numer = session.getAttributeNames();
        
        System.out.println("DebugPrintUtility: Printing keys in the session:");
        
        while(numer.hasMoreElements()) {          
            
            System.out.println(numer.nextElement());
        }       
    } //end printSession
    
}//end class
