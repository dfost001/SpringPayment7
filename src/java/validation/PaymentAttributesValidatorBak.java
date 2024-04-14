/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import com.cart.Cart;
import error_util.EhrLogger;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import paypal.PayPalPayment;
import pp_payment.TokenResponse;
import util.StringUtil;

/**
 *
 * Note: Exposed procedures will be invoked from PaymentAttributes evaluation
 * method by UpdatedNotSupported interceptor before the payment flow has been initiated.
 * So an IllegalArgumentException can NOT be thrown for invalid customer values.
 * Only for invalid Payment object values if the Payment object is NOT null.
 */
//@Scope("session")
//@Component
public class PaymentAttributesValidatorBak implements Serializable {
    
   @Autowired
   private PayPalPayment paypalPayment;
   
   @Autowired
   private CustomerValidator validator;
   
   @Autowired
   private CustomerAttrsValidator customerAttrsValidator;
  
    
   
    public String validatePaymentStartedWithPayerId(Cart cart, 
            PostalAddress postal, Customer customer, BindingResult bindingResult) {
        
        EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedWithPayerId", "executing");
        
        String msgPaymentOnly = validatePaymentStartedPaymentOnly(cart, postal, customer, bindingResult);
        
        String msgPayerId = StringUtil.isNullOrEmpty(this.paypalPayment.getPayerId()) ?
         
                        " payerId is null or empty;" : "";
        
        //Throw an error if a PayerId has been obtained, and the Payment object is null or invalid
        if(!msgPaymentOnly.isEmpty() && msgPayerId.isEmpty())             
            throw new IllegalArgumentException(
                    
               EhrLogger.doError(this.getClass().getCanonicalName(),
                       "invalidPaymentStartedWithPayerId",
                       "Invalid payment object or session attributes, and PayerId has a value")); 
        
        EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedWithPayerId", msgPaymentOnly
                                + msgPayerId);
        
        return msgPaymentOnly + msgPayerId;
        
    }
    
    /*
     * 
     */
    public String validatePaymentStartedPaymentOnly(Cart cart, 
            PostalAddress address, Customer customer, BindingResult result) {  
        
        EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedPaymentOnly", "executing");
        
        String msgPayment = this.evaluatePayment(); //throws IllegalArgument for invalid initialization
        
       /* Alternate */
       /* if(msgPayment.isEmpty())
            throwIfInvalidValues(cart, address, customer,result); */
       
       String errValidation = evalInvalidValues(cart,address, customer, result);          
       
       if(msgPayment.isEmpty()) {
         if(!errValidation.isEmpty())
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(),
                   "validatePaymentStartedPaymentOnly", 
                   "Payment object initialized with invalid entity or cart values: " + errValidation);
       }
       else msgPayment += " " + errValidation;  //Concat validation to message for information      
           
       return msgPayment;
    }
  
    /* 
     * If the Payment object is not null and has not been initialized a Runtime exception
     * is thrown.
    */
    
    private String evaluatePayment() {
        
        String tokenNullMsg = "";
        String paymentNullMsg = "";
        String stateMsg = "";
        
        if(this.paypalPayment.getTokenResponse() == null)
                tokenNullMsg = "TokenResponse is null;";
        else if(this.paypalPayment.getTokenResponse().getAccessToken().isEmpty())
                stateMsg += "AccessToken field of TokenResponse is empty;";               
        
        if(this.paypalPayment.getPayment() == null)
                paymentNullMsg += "PayPal Payment is null;";
        else if(StringUtil.isNullOrEmpty(paypalPayment.getPayment().getId()))          
                stateMsg += "PayPal resource id is empty;";   
        
        if(!stateMsg.isEmpty())
            throw new IllegalArgumentException (
            EhrLogger.doError(this.getClass().getCanonicalName(),
                       "evaluatePayment",
                       stateMsg));        
        
        if(paymentNullMsg.isEmpty())
            if(!tokenNullMsg.isEmpty() || this.tokenExpired(paypalPayment.getTokenResponse()))
               throw new IllegalArgumentException(
               EhrLogger.doError(this.getClass().getCanonicalName(),
                       "evaluatePayment",
                       "Payment is  not null and the token is null or expired"));      
       
       return paymentNullMsg;
    }
    
    public boolean tokenExpired(TokenResponse token) {
        
        GregorianCalendar tokenReceivedAt = paypalPayment.getTokenNow();
                
        tokenReceivedAt.add(Calendar.SECOND, token.getExpiresIn());
        
        GregorianCalendar now = new GregorianCalendar(TimeZone.getDefault());
        
        if(now.compareTo(tokenReceivedAt) > 0)
            return true;
        return false;
    }
    
    
    public String evalInvalidValues(Cart cart, PostalAddress address,
            Customer customer, BindingResult result){
        
        String msg = "";      
        
        if(customer == null)
                msg += "Session Customer is null; ";
        else if(customer.getCustomerId() == null)
                msg += "customer#getId returned null; ";
        else msg += this.validatePostalAddress(customer, result);
        
        if(address == null)
                msg += "Session selectedShipAddress is null; "; 
        else if(ShipAddress.class.isAssignableFrom(address.getClass()))
               msg += this.validatePostalAddress(address, null);
        
        if(cart == null || cart.mapAsList().isEmpty())
                msg += "Cart is null or Cart#mapAsList is empty; ";          
     
       String debug = msg.isEmpty() ? "No null or validation errors. "
               : "Null or Validation errors detected. " ;
       
       EhrLogger.printToConsole(this.getClass(), "evalInvalidValues", debug + msg); 
       
       return msg;        
    } 
    
    /*
     * Note: Only invoked with non-null PostalAddress
    */
    private String validatePostalAddress(PostalAddress address, BindingResult mvcResult) {
        
        String type = Customer.class.isAssignableFrom(address.getClass()) ?
                "Customer" : "ShipAddress";
        
        DataBinder binder = new DataBinder(address);
        
        Errors validatorResult = binder.getBindingResult();
        
        this.validator.validate(address, validatorResult);             
        
        String message =  "";
        
        for(FieldError err : validatorResult.getFieldErrors())
            message += err.getField() + ": " + err.getDefaultMessage() + ". ";
        
        if(type.equals("ShipAddress")) {
            if(!message.isEmpty()) {
                
                message = "ShipAddress should either be valid or not in the session. " + message;   
                EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(),
                        "validatePostalAddress", message);
            }  
            return "";            
        }  
        
        if(mvcResult == null) {    
          message += ": MVC BindingResult is not in the session. ";
          return message;
        }
        //optional
        this.compareMvcBindingToValidator(message, mvcResult, validatorResult);
        
         message = message.isEmpty() ? "" : type + ": " + message;
         
         return message;
        
    }
    
    private void compareMvcBindingToValidator(String validatorErrMsg,
            BindingResult mvcResult, Errors validatorResult) {          
        
        if(!mvcResult.hasErrors() && !validatorResult.hasErrors())
            return;
        StringBuffer validatorMsgBuffer = new StringBuffer(validatorErrMsg);
        
        StringBuffer mvcMsgBuffer = new StringBuffer();
        
        if(mvcResult.hasErrors() && validatorResult.hasErrors()) {
        
              customerAttrsValidator.genFieldErrors("PaymentAttributesValidator#Customer", 
                mvcMsgBuffer, mvcResult.getFieldErrors());
        
              customerAttrsValidator.compareBindingResultToValidatorResult(mvcResult, validatorResult, 
                 mvcMsgBuffer, validatorMsgBuffer); //throws IllegalArgument
        }
        else EhrLogger.throwIllegalArg("PaymentAttibutesValidator",
                    "compareMvcBindingToValidator",
                    "MVC BindingResult and programmatic BindingResult do not compare.");   
    }
    
}
