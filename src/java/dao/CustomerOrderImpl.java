/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.cart.Cart;
import com.cart.CartItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.LineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dinah
 */
@Service
public class CustomerOrderImpl implements OrderManager{
    
    @Autowired
    private CustomerDAO customerDAO;

    @Override
    @Transactional
    public CustomerOrder placeOrder(Cart cart, Customer customer) {     
        
       boolean testTransient = false;
       
       if(testTransient)
           throw new RecoverableDataAccessException(this.getClass().getCanonicalName()
               + ": testing DataAccessExceptionResolver");
       
       CustomerOrder order = this.insertOrder(customer, cart);        
      
       return order;
       
    }
    
    private CustomerOrder insertOrder(Customer customer, Cart cart) {
        
        CustomerOrder order = new CustomerOrder();
        
        order.setCustomer(customer);
        
        BigDecimal amount = new BigDecimal(cart.getFormattedGrand());
        
        order.setOrderAmount(amount);
        
        System.out.println("Inside CustomerOrderImpl:" + cart.getGrandTotal().toString());
                
        order.setOrderDate(new Date());
      
        this.insertItems(order, cart);
        try {
           customerDAO.insertCustomerOrder(order);
           debugPrint(order);
        }
        catch (DataAccessException e){
            order.setOrderId(Integer.MIN_VALUE);
            System.out.println(this.getClass().getName() +
                    "#insertOrder: DataAccessException occurred " + e.getMessage());
           // throw e;
        }
        return order;
    }
    
    private void insertItems(CustomerOrder order, Cart cart) {
        
        List<CartItem> items = cart.mapAsList();
        
        System.out.println("CustomerOrderImpl#insertLineItems:" + items.size());
        
        for(CartItem it : items){
            
            LineItem lineItem = new LineItem();
            
            lineItem.setFilm(it.getFilm());
            
            lineItem.setQuantity(it.getQty());
            
            order.getLineItems().add(lineItem);
            
            //lineItem.setCustomerOrder(order); --BUG, do not set order until all items added
                   
        }
        for(LineItem itm : order.getLineItems())
            itm.setCustomerOrder(order);
    }
    
    private void debugPrint(CustomerOrder order) {
        System.out.println(this.getClass().getCanonicalName() + ":debugPrint");
        System.out.println("orderId=" + order.getOrderId());
        for(LineItem it : order.getLineItems()){
            System.out.println("Item=" + it.getFilm().getTitle()
             + " id=" + it.getId());
        }
    }
    
}
