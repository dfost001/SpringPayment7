/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions.shipAddressController;

/**
 *
 * @author dinah
 */
public class ExpiredEditViewRequest extends Exception {          
   
    public ExpiredEditViewRequest(String info) {
         super(info);
       
    }   
}
