/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import error_util.EhrLogger;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import httpUtil.HttpException;
import java.util.List;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import pp_payment.ErrDetail;

import pp_payment.PayPalError;
import view.attributes.PaymentAttributes;
import view.attributes.PaymentAttributes.TransactionState;

/**
 * 
 * Handles HttpException thrown from PayPal REST client components
 * 
 *
 */

public class PayPalHttpExceptionMappingResolver extends AbstractHandlerExceptionResolver {
    
    /*
     * Model Attributes used by EL on paypalError.jsp, connectionError.jsp
     */
    private static final String IS_RECOVERABLE_KEY = "recoverableKey"; // boolean attribute
    private static final String RECOVERABLE_PATH_KEY= "recoverablePathKey"; //path if recoverable
    private static final String IS_PAYMENT_RESET = "isPaymentReset" ; //Show home prompt
    private final String cancelRecoverablePath = "/cancelPayPal";   // Restart transaction 
    private final String transactionRecoverablePath = "/paypalAuthorizeRedirect"; //Resume
    /*
     * Model Attribute keys for PayPal defined Error object at paypalError.jsp
     */
    private static final String PAYPAL_ERROR = "payPalError"; //deserialized remote error object
    /*
     * Keys for Address Validation Message and Path
    */
    private static final String VALIDATION_ERROR_MESSAGE = "validationErrorAddress"; //address error message
    private static final String VALIDATION_ERROR_PATH = "validationErrorPath" ; //redirect path to select view    
    private final String redirectToSelect = "/shippingAddress/handlePayPalAddressError";
    
    private PaymentAttributes paymentAttrs;   
    
    private HttpServletRequest request;
    
    private HashMap<Class<?>,String> viewMapping;
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrs){
        this.paymentAttrs = attrs;
    }
    
    @Required
    public void setViewMapping(HashMap<Class<?>, String> viewMapping){
        this.viewMapping = viewMapping;
    } 
    
    @Override
     protected void logException(Exception ex, HttpServletRequest request) {
         
         if(!request.getRequestURI().toLowerCase().contains("paypal"))
            return ; //only PayPal connections - hard-coded path can be fixed
    
         if(!HttpException.class.isAssignableFrom(ex.getClass()))             
            return ;  
         if(HttpClientException.class.isAssignableFrom(ex.getClass()))
               EhrLogger.logHttpClientException((HttpClientException)ex, request, this.getClass());
         else EhrLogger.logException(ex, request, this.getClass());
    }
    
  @Override
  protected ModelAndView doResolveException(HttpServletRequest req,
        HttpServletResponse resp, Object handler, Exception ex) {
      
     this.request = req;
     
     String url = req.getRequestURL().toString();
     
     if(!url.toLowerCase().contains("paypal"))
         return null; //only PayPal connections - hard-coded path can be fixed
    
     if(!HttpException.class.isAssignableFrom(ex.getClass()))             
         return null;    
    
    String viewName = viewMapping.get(ex.getClass());
    
    ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName()); 
    
    this.evalException(mav, (HttpException)ex);    
    
    return mav;
  }
  
  /*protected String buildLogMessage(Exception e, HttpServletRequest request){
      
  }*/
  
 /*
  * Assumption: If response code is 500, the payment was not processed, and
  * a retry is possible without duplicate processing on remote service.
  */
  private void evalException(ModelAndView mav, 
          HttpException ex) {
      
      boolean isRecoverableKey = false;
      
       HttpClientException clientEx = null;   
       HttpConnectException connectEx = null;
       
       mav.addObject(IS_RECOVERABLE_KEY, isRecoverableKey);
       
       /* CharacterCodingException */
       if(ex.getCause() != null && ex.getCause().getClass().equals(CharacterCodingException.class)){
           paymentAttrs.onPaymentError(this.getClass()); //reset payment objects
           return;
       }
       
      /* JsonSyntax problem */
       if(ex.getResponseCode() >= 200 && ex.getResponseCode() < 300) { 
           paymentAttrs.onPaymentError(this.getClass()); //reset payment objects, update time
           return;
       } 
       
       if(ex.getResponseCode() >= 400 && ex.getResponseCode() < 500) {             
                    
          paymentAttrs.onPaymentError(this.getClass()); //reset payment objects, update time
           
           clientEx = (HttpClientException)ex; 
           PayPalError err = (PayPalError)clientEx.getErrObj();
           if(err != null) {
              
               evalPayPalError(err,clientEx, mav);
               mav.addObject(PAYPAL_ERROR, err);
           }
           return;
       }          
       
       String path = "";
       
       if(ex.getResponseCode() >= 500 && ex.getResponseCode() < 600) {
           
           mav.addObject(IS_RECOVERABLE_KEY, true);
           path = this.makeRecoverableUrl();  
           mav.addObject(RECOVERABLE_PATH_KEY, path); 
       }       
       if(HttpConnectException.class.isAssignableFrom(ex.getClass()))  {
           
           connectEx = (HttpConnectException)ex;
           if(connectEx.getRecoverable().equals(Boolean.TRUE)){
               mav.addObject(IS_RECOVERABLE_KEY, true);
               path = this.makeRecoverableUrl();
               mav.addObject(RECOVERABLE_PATH_KEY, path);               
           }          
       }        
       if(path.contains(this.cancelRecoverablePath)) {
            paymentAttrs.onPaymentError(this.getClass());  //reset objects, update time    
            mav.addObject(IS_PAYMENT_RESET, true);            
       }
       else mav.addObject(IS_PAYMENT_RESET, false); 
       
  } //end eval
  
  private void evalPayPalError(PayPalError err, HttpClientException ex, ModelAndView mav) {     
      
      if(err.getName().equals("PAYMENT_ALREADY_DONE")){
           
            ex.setFriendly("Payment Already Completed");
            return;
      }
      
      List<ErrDetail>errDetails = null;
      
      String addressMsg = "Please re-verify city, state, zip entries.";
      String friendlyMsg = "An address validation error has occurred.";
      
      if(err.getName().equals("VALIDATION_ERROR")){
         
         errDetails = err.getDetails();
      }
      for(ErrDetail e : errDetails) {
          if(e.getField().equalsIgnoreCase("city")
                  || e.getField().equalsIgnoreCase("state")
                  || e.getField().equalsIgnoreCase("zip")) {
              ex.setFriendly(friendlyMsg);
              mav.addObject(VALIDATION_ERROR_MESSAGE, addressMsg);
              mav.addObject(VALIDATION_ERROR_PATH, this.redirectToSelect);
              break;
          }
      }  //end for      
  } // evaluate  
 
  
    public String makeRecoverableUrl(){         
         
       TransactionState transState = paymentAttrs.evalTransactionState(this.request);
       
       if(TransactionState.PAYER_ID.equals(transState)) {
            
             return this.transactionRecoverablePath + "?" 
                 + PaymentAttributes.PaymentTimeKey 
                 + "="
                 + paymentAttrs.getPaymentTime();
       }
       
       return this.cancelRecoverablePath;
                 
     }
    
} //end resolver

 