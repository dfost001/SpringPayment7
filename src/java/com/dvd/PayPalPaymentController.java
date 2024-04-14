/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.dvd.redirect_control.PayPalRedirectController;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import javax.servlet.http.HttpServletResponse;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.RedirectView;
import paypal.PayPalExecuteException;
import paypal.PayPalPayment;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.PaymentAttributes;


/**
 *
 *
 */
@Controller
@Scope("session")
public class PayPalPaymentController implements Serializable{      
        
    @Autowired
    private Cart cart; 
    
    @Autowired
    private PaymentAttributes paymentAttrs;  
    
    @Autowired
    private CustomerAttrsValidator customerAttrsValidator;
    
    @Autowired
    private PayPalPayment paymentBean;   
    
    private PostalAddress selectedAddr;
    
   /*
    * Continue processing from the Payment button
    * Processing completes with a redirect to PayPal site
    */
    @RequestMapping(value="paypalLogin", method=RequestMethod.POST)
    public String doPayPalLogin(HttpServletRequest request, HttpServletResponse response)            
      throws HttpClientException, HttpConnectException, PayPalExecuteException {    
        
      
       selectedAddr = this.verify(request); 
        
        String confirmUrl = 
                "approvePayPal?"
                    + PaymentAttributes.PaymentTimeKey
                    + "="
                    + paymentAttrs.getPaymentTime();
                    
        
        String cancelUrl = "cancelPayPal?" 
                    + PaymentAttributes.PaymentTimeKey
                    + "="
                    + paymentAttrs.getPaymentTime();
        
       
           paymentBean.setCancelUrl(this.makeUrl(request,response, cancelUrl));
           paymentBean.setConfirmUrl(this.makeUrl(request, response, confirmUrl));
           
           String redirectUrl = paymentBean.processPayment(selectedAddr, cart);       
      
       
        return "redirect:" + redirectUrl;
    } 
    
    /**
     * Handler for the redirect from the PayPal site
     *
     * @param request Extracts id parameter from the PayPal redirect
     * @param model PaymentAttributes will use to append current system time value
     * @return PayPalRedirectController 
     * 
     * We will redirect so that request will not be accessible from browser history or 
     * navigation list. Since query will have an outdated payerId. 
     *  
     * @throws paypal.PayPalExecuteException
     */
    @RequestMapping(value="/approvePayPal", method=RequestMethod.GET)
    public RedirectView approveTransaction(HttpServletRequest request, ModelMap model)
               throws PayPalExecuteException {    
       
        System.out.println("PayPalPaymentController#approveTransaction executing");
        
       String payerId = request.getParameter("PayerID");  
      
      // String payerId = null; //test exception
        
        if(payerId == null || payerId.isEmpty()) { 
            
            throw new PayPalExecuteException("PayerID request parameter is null or empty", 
                this.getClass().getCanonicalName() + "#" + "approveTransaction");
        }         
        
        paymentBean.setPayerId(payerId);            
       
        RedirectView view =  paymentAttrs.payPalApprovalUrlWithTime(request, 
                PayPalRedirectController.redirectToAuthorizePath, model);       
        
        return view;       
    }//end approve   
    
    private String makeUrl(HttpServletRequest request, HttpServletResponse response,
            String mapping){
        String protocol = request.getScheme() + "://";
        String server = request.getServerName();
        int port = request.getServerPort();
        String context = request.getContextPath();
        String url = protocol + server + ":" + port + context + "/" + mapping;
        url = response.encodeRedirectURL(url);
        System.out.println("PayPalPaymentController#makeUrl:" + url);
        return url;
    }
    
    /*
     * Extract attributes from the session, since null parameters here will be
     * mapped as application not user-navigation errors.
    */
    private PostalAddress verify(HttpServletRequest request) {          
        
        PostalAddress selectedShipTo = (PostalAddress)request.getSession()
                .getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        
        Customer customer = (Customer)request.getSession().getAttribute(ConstantUtil.CUSTOMER_SESSION_KEY);
        
        BindingResult result = (BindingResult)request
                .getSession().getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);    
        
         try {
        
          customerAttrsValidator.evaluateVerifySvcResults(customer, 
                  cart, result, selectedShipTo);
        
        } catch(ConfirmCartException ex) {
            
            EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                    "checkNullPointers", ex.getTechnical());
        
        }
        return selectedShipTo;
        
    }
    
    
        
 }//end class
