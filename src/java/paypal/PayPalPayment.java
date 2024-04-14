/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

import com.cart.Cart;
import java.io.Serializable;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pp_payment.Link;
import pp_payment.Payment;
import pp_payment.TokenResponse;
import httpUtil.*;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import model.customer.PostalAddress;
import pp_payment.RedirectUrls;

/**
 *
 * Note: module-level Payment should not be initialized until a successful
 * upload, and return from the service.
 */
@Component
@Scope("session")
public class PayPalPayment implements Serializable{
    
    @Autowired
    private PayPalPaymentUtil paymentUtil;
   
    private String confirmUrl = "";
    private String cancelUrl = "";
    
    /*
     * Payment objects read by PaymentAttributes validation procedures
     */
    private TokenResponse tokenResponse = null;
    
    private GregorianCalendar tokenNow = null;
    
    private Payment payment = null;
    
    private String payerId; //returned from PayPal login site as a redirect parameter   

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public GregorianCalendar getTokenNow() {
        return tokenNow;
    }

    public Payment getPayment() {
        return payment;
    }   

    public void setTokenResponse(TokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

     public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
    

    public void setConfirmUrl(String confirmUrl) {
        this.confirmUrl = confirmUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }   
    
    public String processPayment(PostalAddress selectedAddr, Cart cart)
                  throws HttpClientException, HttpConnectException, PayPalExecuteException {        
        
        
        this.tokenResponse = paymentUtil.doAuthorize();
        
        this.tokenNow = new GregorianCalendar(TimeZone.getDefault());
       
        RedirectUrls urls = new RedirectUrls(this.confirmUrl, this.cancelUrl);
        
        this.payment = paymentUtil.doPaymentLogin(tokenResponse, cart, selectedAddr, urls);       
        
        String redirect = this.getRedirectUrl();
        
        System.out.println("PayPalPayment#processPayment:redirectUrl=" + redirect);
        
        return redirect;
      
    }
    
    public Payment doFinalExecute() throws HttpConnectException, HttpClientException,
                   PayPalExecuteException {
        
        Payment tpayment = paymentUtil.doFinalExecute(tokenResponse, payment, payerId);
        
        return tpayment;
    }
    
     /*
      * To do: Error check link for non-null and token
      * redirectUrl=https://www.sandbox.paypal.com/cgi-bin/webscr?
      * cmd=_express-checkout&token=EC-9KA84419PW545873G  
      */ 
     private String getRedirectUrl() {
         String redirect = "";
         ArrayList<Link> links = payment.getLinks();
         for(Link lnk : links)
             if(lnk.getMethod().equalsIgnoreCase("redirect"))
                 redirect = lnk.getHref();
         System.out.println("PayPalPayment#getRedirectUrl:href=" + redirect);
         return redirect;
     }
     
    
     
}//end class
