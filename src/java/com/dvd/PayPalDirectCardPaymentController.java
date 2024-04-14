/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.dvd.redirect_control.PayPalRedirectController;
import dao.OrderManager;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import httpUtil.HttpException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.RedirectView;
import paypal.OrderAttributesDebug;
import paypal.PayPalExecuteException;
import paypal.PayPalPaymentUtil;
import pp_payment.Payment;
import pp_payment.ShippingAddress;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.PaymentAttributes;

/**
 *
 * @author dinah
 */
@SessionAttributes({"customer", ShippingAddressController.SELECTED_POSTALADDRESS})
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class PayPalDirectCardPaymentController {
    
    @Autowired
    private Cart cart;
    
    @Autowired
    private CustomerAttrsValidator customerAttrsValidator;
    
    @Autowired
    private PaymentAttributes paymentAttrs;
    
    @Autowired
    private PayPalPaymentUtil paymentUtil;
    
    @Autowired
    private OrderManager orderManager;
    
    /*
     * Note: return value includes a time parameter
     * Receipt handler is intercepted for the PayPal login method, so time
     * parameter required, but is not relevant, and will be set to the
     * current system time
     */
    
    @RequestMapping(value="/payPal/processDirectPayment", method=RequestMethod.POST)
    public RedirectView processDirectPayment(HttpServletRequest request,
       @ModelAttribute("customer")Customer customer,
       @ModelAttribute(ShippingAddressController.SELECTED_POSTALADDRESS)PostalAddress deliveryAddress,
       ModelMap model)            
                     throws HttpException, PayPalExecuteException {
        
        this.checkNullOrInvalid(request, customer, deliveryAddress);        
        
        Payment payment = paymentUtil.doPaymentDirect(cart, customer, deliveryAddress);
        
        CustomerOrder order = orderManager.placeOrder(cart, customer); 
        
        OrderAttributesDebug debugAttrs = initDebugAttributes(payment);
        
        this.setSessionAttributes(request, order, debugAttrs);
        
        cart.removeAll();       
        
        return paymentAttrs.initializeRedirectViewWithTime(request, 
                PayPalRedirectController.redirectToReceiptPath, model);
        
    }
    
    
    private OrderAttributesDebug initDebugAttributes(Payment payment){
        String resourceId = payment.getId();
        String transId = payment.getTransactions()
                .get(0)
                .getRelatedResources()
                .get(0)
                .getSale()
                .getId();
        ShippingAddress address = payment.getTransactions()
                .get(0)
                .getItemList()
                .getShippingAddress();
        OrderAttributesDebug attrib = new OrderAttributesDebug();
        attrib.setResourceId(resourceId);
        attrib.setTransactionId(transId);
        attrib.setShippingAddress(address);
        return attrib;
    }
    
    
    
   private void setSessionAttributes(HttpServletRequest request,
           CustomerOrder order, OrderAttributesDebug debugAttrs) {
       
       HttpSession session = request.getSession();
       
       session.setAttribute("order", order); //Into the session to maintain after redirect
       session.setAttribute("paypalReceipt", debugAttrs);       
   }    
    
    private void checkNullOrInvalid(HttpServletRequest request,
            Customer customer,
            PostalAddress deliveryAddress) {
        
          BindingResult result = (BindingResult)request
                .getSession().getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);    
        
         try {
        
          customerAttrsValidator.evaluateVerifySvcResults(customer, 
                  cart, result, deliveryAddress);
        
        } catch(ConfirmCartException ex) {
            
            String message = EhrLogger.doError(
                            this.getClass().getCanonicalName(), 
                            "checkNullOrInvalid", 
                            "Nested exception: " + ex.getTechnical());
            
            throw new IllegalStateException(message, ex);
                    
                    
        
        }
        
    }
    
}
