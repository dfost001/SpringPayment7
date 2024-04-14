/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.redirect_control;

import com.cart.Cart;
import com.dvd.ShippingAddressController;
import exceptions.ReceiptCartNotEmptyException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import paypal.OrderAttributesDebug;
import paypal.PayPalPayment;
import view.attributes.ConstantUtil;
import view.attributes.PaymentAttributes;



/**
 *
 * @author Dinah
 * If receipt can be removed from history list then the navigation
 * error can be removed.
 */
@Controller
@Scope("request")
public class PayPalRedirectController implements Serializable{   

    public static final String redirectToAuthorizePath = "/paypalAuthorizeRedirect" ;
    public static final String redirectToReceiptPath = "/paypalReceiptRedirect" ;    
    
    @Autowired 
    private PayPalPayment paymentBean;   
    
    @Autowired
    private PaymentAttributes paymentAttrib;
    
    @Autowired
    private Cart cart;
     
     /*
      * Returned from PayPalPaymentController#approvalURL
      * Error-handling is necessary to protect the execute command, if end-user 
      * navigates with browser lists,
      * and to ensure that the payment object has been initialized in case the
      * execute fails.
      * Revision: Removed setTransaction() to beginning of flow since it is an
      * error to set if there is no transaction in progress.
      */
    
     @RequestMapping(value="/paypalAuthorizeRedirect", method=RequestMethod.GET)
     public String redirectToAuthorize(HttpServletRequest request, ModelMap map)
         throws IllegalStateException  {   
         
        PostalAddress selectedShipAddr = (PostalAddress)request.getSession()
                .getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS); 

        Customer customer = (Customer)request.getSession().getAttribute("customer") ;  
        
        BindingResult result = (BindingResult)request.getSession()
                .getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);
        
        paymentAttrib.invalidPaymentStartedState(selectedShipAddr, cart, customer, result);  
       
      
        map.addAttribute(ShippingAddressController.SELECTED_POSTALADDRESS, selectedShipAddr);
        map.addAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer);
        map.addAttribute(ConstantUtil.CART, cart);                
        //debug purposes only
        map.addAttribute("payerId", paymentBean.getPayerId());
        map.addAttribute("tokenResponse", paymentBean.getTokenResponse()); 
        map.addAttribute("payment", paymentBean.getPayment()); 
        map.addAttribute("init", paymentBean.getPayment()
            .getTransactions().get(0)
            .getItemList().getItems().get(0).getName());
        
        return "authorizePayPal";
     } //
     
     
      /*
      * Returned from PayPalExecuteController
      * Receipt will be current until another payment is started.
      * Rethrow IllegalStateException as user-navigation error in case receipt
      * is requested after items added to cart, but before payment button used.
      * 
      */
     @RequestMapping(value="/paypalReceiptRedirect", method=RequestMethod.GET)
     public String redirectToReceipt( 
             HttpSession session, ModelMap map)
             throws IllegalStateException, ReceiptCartNotEmptyException {       
         
         CustomerOrder order = (CustomerOrder)session.getAttribute("order");
            
         OrderAttributesDebug orderPayPal = (OrderAttributesDebug)session.getAttribute("paypalReceipt");         
                
         paymentAttrib.invalidPaymentCompletedState(order, orderPayPal, cart);         
         
         map.addAttribute("paypalReceipt",orderPayPal);
         
         map.addAttribute("order", order);        
         
         return "paypalReceipt";
     }
    
}//end controller

