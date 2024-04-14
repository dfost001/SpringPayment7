/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.dvd.redirect_control.PayPalRedirectController;
import dao.OrderManager;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.CustomerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import model.customer.PostalAddress;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.RedirectView;
import paypal.OrderAttributesDebug;
import paypal.PayPalExecuteException;
import paypal.PayPalPayment;
import paypal.PayPalPaymentUtil;
import pp_payment.Payment;
import pp_payment.ShippingAddress;
import view.attributes.PaymentAttributes;

@Controller
@Scope("request")
public class PayPalExecuteController implements Serializable{
    
    @Autowired
    private PayPalPayment ppPayment;
    
    @Autowired
    private Cart cart;       
    
    @Autowired
    private PaymentAttributes paymentAttrib;
    
    @Autowired
    private PayPalPaymentUtil paymentUtil;   
    
    @Autowired
    private OrderManager orderManager; 
    
    private Payment paymentResponse;
    
    private CustomerOrder order;  
  
    
    public CustomerOrder getOrder() {
        return this.order;
    } 
    
    /*
     * Read by PayPalCancelController
     */
    public Payment getPaymentResponse(){
        return this.paymentResponse;
    }
    
    @RequestMapping(value="/paypal/cancelAuthorize", method=RequestMethod.POST)
    public String cancelAuthorize(HttpSession session) {        
      
      return "redirect:/cancelPayPal";
        
    }    
   
    @RequestMapping(value="/paypal/execute", method=RequestMethod.POST)
    public RedirectView executeTransaction(HttpServletRequest request)
                throws HttpConnectException, HttpClientException,
                       PayPalExecuteException, IllegalStateException {    
               
        this.evalPaymentStartedState(request);
        
        /*this.paymentResponse = paymentUtil.doFinalExecute(ppPayment.getTokenResponse(),
                ppPayment.getPayment(),
                ppPayment.getPayerId());*/
        
        this.paymentResponse = ppPayment.doFinalExecute();
                
        order = orderManager.placeOrder(cart, 
                (Customer)request.getSession().getAttribute("customer"));         
        
        this.setSessionAttributes(request, order, this.initDebugAttributes());
        
        cart.removeAll();
        
        /* set payment objects to null, and concat updated paymentTime request parameter */
        RedirectView view = paymentAttrib.payPalExecuteSuccessWithTime(request, 
                PayPalRedirectController.redirectToReceiptPath); 
        
        return view;
    } //end execute
    
    private void evalPaymentStartedState(HttpServletRequest request) {
        
        HttpSession session = request.getSession();             
        
        Customer customer = (Customer)session.getAttribute("customer");
        
        PostalAddress selectedShipAddress =
                (PostalAddress)session.getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        
        BindingResult result = (BindingResult)session.getAttribute("bindingResult");
        
        paymentAttrib.invalidPaymentStartedState(
                selectedShipAddress, cart, customer, result);  //throws InvalidStateException 
    }  
    
    private OrderAttributesDebug initDebugAttributes(){
        String resourceId = this.paymentResponse.getId();
        String transId = paymentResponse.getTransactions()
                .get(0)
                .getRelatedResources()
                .get(0)
                .getSale()
                .getId();
        ShippingAddress address = paymentResponse.getTransactions()
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
       
   } //end set session
  
} //end class
