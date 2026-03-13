/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import com.google.gson.JsonSyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import error_util.EhrLogger;
import httpUtil.ErrObjectHandlerException;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import httpUtil.HttpException;
import java.util.List;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import javax.xml.bind.JAXBException;
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
    
    private static final String RECOVERABLE_PATH = "recoverablePath";

    /*
     * Used internally on this component. 
    */
    private final String cancelRecoverablePath = "/cancelPayPal";   // Restart transaction 
    private final String transactionRecoverablePath = "/paypalAuthorizeRedirect"; //Resume
    /*
     * Model Attribute keys for PayPal defined Error object at paypalError.jsp
     */
    private static final String PAYPAL_ERROR = "payPalError"; //deserialized remote error object
    /*
     * Keys for Address Validation Message and Path
    */
    private static final String VALIDATION_ERROR_MESSAGE = "validationErrorMessage"; //error message
    private static final String VALIDATION_ERROR_PATH = "validationErrorPath" ; //key to select view    
    private final String redirectToSelect = "/shippingAddress/handlePayPalAddressError"; //value of select handler
    
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
         return null; //only PayPal connections - hard-coded path can be fixed by deriving HttpException
    
     if(!HttpException.class.isAssignableFrom(ex.getClass()))             
         return null;    
    
    String viewName = viewMapping.get(ex.getClass());
    
    ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName()); 
    
    this.evalException(mav, (HttpException)ex);   
    
    String path = (String)req.getSession().getAttribute(RECOVERABLE_PATH);
    
    if(path != null && path.contains(this.cancelRecoverablePath)) {
            paymentAttrs.onPaymentError(this.getClass());  //reset objects, update time    
                     
    }       
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
      
       HttpClientException clientEx = null;   
       HttpConnectException connectEx = null;
       
       if(ex instanceof HttpClientException) { //Not ConnectException
          if (this.evalDecodingError((HttpClientException)ex)) {
             paymentAttrs.onPaymentError(this.getClass()); //reset payment objects, update time
             return; 
          }
      }   
      // Probably dead code; unless status not really 200 or  unhandled error at deserialization method
      if(ex.getResponseCode() >= 200 && ex.getResponseCode() < 300) {
           String msg = EhrLogger.doError(this.getClass().getName(), "evalException", 
                   "Unknown problem deserializing Http content. Response status is OK. ");
           paymentAttrs.onPaymentError(this.getClass());
           throw new RuntimeException(msg);
        }      
    try {
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
    } catch (ClassCastException castEx) {
        EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), "evalException", 
                "Code at httpUtil.ResponseUtil.java has not assigned objects as expected. ", castEx);
    }
       String path = "";            
       
       if(ex.getResponseCode() >= 500 && ex.getResponseCode() < 600) {           
        
           path = this.makeRecoverableUrl();  
           mav.addObject(RECOVERABLE_PATH, path); 
           return;
       }       
       if(HttpConnectException.class.isAssignableFrom(ex.getClass()))  {
           
           connectEx = (HttpConnectException)ex;
           if(connectEx.getRecoverable().equals(Boolean.TRUE)){
               
               path = this.makeRecoverableUrl();
               mav.addObject(RECOVERABLE_PATH, path);               
           }          
       }  else EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
               "evalException", "Unknown derived HttpException. ");
         
  } //end eval
  
  private boolean evalDecodingError(HttpClientException clientEx) {
      
      Throwable ex = clientEx.getCause();
      
      if(ex == null)
          return false;
      
      if(ex instanceof CharacterCodingException ||
         ex instanceof JAXBException ||     
         ex instanceof JsonSyntaxException ||
         ex instanceof ClassCastException ||
         ex instanceof ErrObjectHandlerException )
             return true;
      return false;
  }
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

 