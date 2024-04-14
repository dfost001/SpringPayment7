/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.cart.Cart;
import com.cart.CartItem;
import java.util.Date;
import java.util.List;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dinah
 */
//@Service
public class CustomerOrderImpl_bak2 implements OrderManager{
    
    @Autowired
    private CustomerDAO customerDAO;

    @Override
    @Transactional
    public CustomerOrder placeOrder(Cart cart, Customer customer) {     
       
       CustomerOrder order = this.insertOrder(customer, cart);   
       
       this.insertItems(order, cart);
      
       return order;
       
    }
    
    private CustomerOrder insertOrder(Customer customer, Cart cart) {
        
        CustomerOrder order = new CustomerOrder();
        
        order.setCustomer(customer);
        
        order.setOrderAmount(cart.getGrandTotal());
        
        System.out.println("Inside CustomerOrderImpl:" + cart.getGrandTotal().toString());
                
        order.setOrderDate(new Date());      
      
        try {
           customerDAO.insertCustomerOrder(order);
        }
        catch (DataAccessException e){
            
           // order.setOrderId(Integer.MIN_VALUE);
           
            throw e;
        }
        return order;
    }
    
    private void insertItems(CustomerOrder order, Cart cart) {
        
        List<CartItem> items = cart.mapAsList();
        
        System.out.println("CustomerOrderImpl#insertLineItems:" + items.size());
        System.out.println("CustomerOrderImpl#insertLineItems: orderId=" + order.getOrderId());
        
        for(CartItem it : items){
            LineItem lineItem = new LineItem();
            lineItem.setFilm(it.getFilm());
            lineItem.setQuantity(it.getQty());            
            lineItem.setCustomerOrder(order);
            
            customerDAO.insertLineItem(lineItem);
            
            System.out.println("CustomerOrderImpl#insertLineItems:"
             + "film=" + it.getFilm().getTitle() + " quanity=" + it.getQty());
            
            System.out.println("CustomerOrderImpl#insertLineItems: id=" + lineItem.getId());
            
        }
    }
    
   
    
}
