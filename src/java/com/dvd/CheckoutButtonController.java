/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import exceptions.ExpiredLoginRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import validation.CompareAddressUtil2;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;


/**
 *
 * @author dinah
 */
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CheckoutButtonController implements Serializable{    
    
    public enum CheckoutCommandValue {
        SUBMIT, REGISTER, CHECKOUT
    }
    
    @Autowired
    private CustomerAttributes customerAttrs;
    
    @Autowired
    private CustomerAttrsValidator attrsValidator;
    
    @Autowired
    private CompareAddressUtil2 compareUtil;
    
    @Autowired
    private Cart cart;
    
    private final String customerEditUrl = "forward:/customerRequest";
    
    private final String customerInsertUrl = "forward:/customerRegister";
    
    private final String selectShipAddressUrl = "redirect:/shippingAddress/showSelect";
    
    private final String verifyResultsUrl = "redirect:/customer/verifySvcResults";
    
    @RequestMapping(value="/checkout/request/{debug}", method=RequestMethod.POST)
    public String checkoutRequest(          
            @RequestParam(ConstantUtil.LOGIN_TIME_KEY) Long loginTime,
            @RequestParam(ConstantUtil.CHECKOUT_COMMAND_NAME) String cmdValue,
            HttpSession session) throws ExpiredLoginRequest {
       
       this.evalInconsistentSessionState(session);
       
       this.evalExpiredLoginTime(loginTime);
       
       customerAttrs.updateLoginTime();
       
       this.throwEmptyCart(); 
       
       String url = "";
       
       switch(cmdValue){
           case "SUBMIT" :
           case "REGISTER" :  
               url = this.processFormRequest(cmdValue, session);
               break;
           case "CHECKOUT" :
               url = this.processButtonRequest(session);
               break;
           default: 
               this.throwIllegalArg("RequestParam is an unknown CheckoutCommandValue enum constant",
                       "checkoutRequest");
       }
       return url; 
    }
    private void evalInconsistentSessionState(HttpSession session) {
        
        Customer customer  = (Customer)session.getAttribute(ConstantUtil.CUSTOMER_SESSION_KEY) ;
        
        PostalAddress postal = (PostalAddress)session.getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS); 
        
        BindingResult result = (BindingResult)session.getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);
        
        String msg = "";
        
        if(customer == null) {
            if(postal != null)
                 msg = "Customer is null and Selected delivery address has a value. ";
            if(result != null)
                 msg += "Customer is null and BindingResult has a value. ";
        } else if(postal != null && result == null)
                 msg = "Delivery address selected and BindingResult is null";
        
        if(!msg.isEmpty()) {
           
                this.throwIllegalArg(msg, "evalInconsistentNullCustomerState");
            
        }          
    } 
    
    private void evalExpiredLoginTime(long loginTime) throws ExpiredLoginRequest {
        
       boolean current = compareUtil.isCurrentFormTime(loginTime, 
               customerAttrs.getLoginTime(), "CheckoutButtonController#checkoutRequest");
       
       if(!current) {
           String msg = MessageFormat.format("Parameter={0} : Session={1}", loginTime, 
                   customerAttrs.getLoginTime());
           customerAttrs.updateLoginTime();
           throw new ExpiredLoginRequest(msg);  
       }  
        
    }
    
    private String processFormRequest(String cmdValue, HttpSession session) {
        
         Customer customer = (Customer)session.getAttribute(ConstantUtil.CUSTOMER_SESSION_KEY);
         
         if(customer != null)
             this.throwIllegalArg(
                     "Login form submitted with Customer already in session. "
                      + " Check EL or JavaScript render of login form.",
                     "processFormRequest") ;         
         
         if(cmdValue.equals(CheckoutCommandValue.REGISTER.name()))
             return this.customerInsertUrl;
         
         return this.customerEditUrl;
        
    }
    
    
    private String processButtonRequest(HttpSession session) {
        
         Customer customer = (Customer)session.getAttribute(ConstantUtil.CUSTOMER_SESSION_KEY);    
         
        if(customer == null) {           
             
             this.throwIllegalArg(
                     "Appears that form not rendered and Customer is null. "
                     + "Problem with render of command button value on form?",
                     "processButtonRequest") ;   
         }        
         
         BindingResult bindingResult = (BindingResult)session.getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);     
         
         if(bindingResult == null) //Must evaluate before invoking CustomerAttrsValidator
             return this.customerEditUrl;           
         
         if(this.bindingResultHasErrors(bindingResult, customer))
             return this.customerEditUrl;
         
         compareUtil.compareCustomerToDb(customer, "CheckoutButtonController#processButtonRequest");
         
         PostalAddress postal = (PostalAddress)session.getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);   
         
         compareUtil.checkInconsistentSelectedFlag(postal);
                
         if(postal == null)        
              return this.selectShipAddressUrl;
         
         return this.verifyResultsUrl;
    }
    
    private boolean bindingResultHasErrors(BindingResult bindingResult, Customer customer) {
        
        try {
             
             attrsValidator.evaluateConfirmCartState(customer, cart, bindingResult);
             
         } catch (ConfirmCartException e) {
             
             return true;
         }
        
         return false;
    }    
    private void throwEmptyCart() {
         if(cart.mapAsList().isEmpty())
            this.throwIllegalArg("Checkout button rendered with empty cart", "throwEmptyCart");
    }
    
    private void throwIllegalArg(String message, String method) {
        
        throw new IllegalArgumentException(EhrLogger.doError(
                this.getClass().getCanonicalName(), method, message));
    }
}
