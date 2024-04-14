/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import exceptions.ConfirmCartException;
import exceptions.SelectedShipAddressCompareException;
import javax.servlet.http.HttpServletRequest;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import validation.CompareAddressUtil2;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;

/**
 *
 * @author dinah
 */
@Controller
@Scope("request")
@SessionAttributes({"customer", ConstantUtil.CUST_BINDINGRESULT_KEY, ShippingAddressController.SELECTED_POSTALADDRESS})
public class PayPalCancelUrlController {
    
    @Autowired
    private PaymentAttributes paymentAttrs;
    
    @Autowired
    private CustomerAttrsValidator attrsValidator;
    
    @Autowired
    private Cart cart;
    
    @Autowired
    private CustomerAttributes customerAttrs;  
    
    @Autowired
    private CompareAddressUtil2 compareUtil;
    
    private final String IS_CANCELLED = "IS_CANCELLED";
   
    
   
    /* 
     * Reset payment variables to make application available. Update time to
     * cause a PaymentAccessException by PayPalApprovalUrlInterceptor if user 
     * resumes payment via browser navigation. The PayPal view may be accessed
     * from browser cache.
     *
     * To do: Define as a PaymentTimeInterceptor path
     * Bug: PaymentTime parameter missing if entrance from other than PayPal login
     *
     * Note: cannot redirect to confirmCart view handler since its method is POST
     */
    @RequestMapping(value="/cancelPayPal", method=RequestMethod.GET)
    public String cancelPayPal(@ModelAttribute("customer") Customer customer,
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult,
            @ModelAttribute(ShippingAddressController.SELECTED_POSTALADDRESS) PostalAddress shippingAddress,
            ModelMap map, HttpServletRequest request) throws 
                ConfirmCartException, 
                SelectedShipAddressCompareException {              
           
           paymentAttrs.resetPayPalCancelUrl();//reset Payment always, make application available  
          
           attrsValidator.evaluateVerifySvcResults(customer, cart, bindingResult, shippingAddress);  

           compareUtil.compareSelectedPostalAddressToDb(request.getSession(), customer,
                shippingAddress, "Confirm Cart Compare");             
         
           map.addAttribute(IS_CANCELLED, true);
           
           this.addModelAttributes(map,customer,shippingAddress);
           
           return "confirmCart" ;
    }
    
     private void addModelAttributes(ModelMap map, Customer customer, PostalAddress selectedShipTo) {     
        
     map.addAttribute("cart",cart);  
     
     map.addAttribute("customer", customer);   
     
     map.addAttribute("customerAttributes", customerAttrs); //successMessage
     
     map.addAttribute(ShippingAddressController.SELECTED_POSTALADDRESS,selectedShipTo);
    
    }   
     
    
} //end class
