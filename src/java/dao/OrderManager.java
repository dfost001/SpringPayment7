/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.cart.Cart;
import model.customer.Customer;
import model.customer.CustomerOrder;




/**
 *
 * @author Dinah
 */

public interface OrderManager {
    
  public CustomerOrder placeOrder(Cart cart, Customer customer);
    
}
