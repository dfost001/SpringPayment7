/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import com.cart.Cart;
import error_util.EhrLogger;
import java.io.Serializable;
import java.text.MessageFormat;
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
@Scope("session")
@Component
public class PaymentAttributesValidator implements Serializable {
    
   @Autowired
   private PayPalPayment paypalPayment;
   
   @Autowired
   private CustomerValidator validator;
   
   @Autowired
   private CustomerAttrsValidator customerAttrsValidator;
  
   public enum ValidatorKey {With_PayerId, Payment_Only, Trans_None} ; 
   
    public String validatePaymentStartedWithPayerId(Cart cart, 
            PostalAddress postal, Customer customer, BindingResult bindingResult) {
        
        EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedWithPayerId", "executing");
        
        String msgPaymentOnly = validatePaymentStartedPaymentOnly(cart, postal, customer, bindingResult);
        
        String msgPayerId = StringUtil.isNullOrEmpty(this.paypalPayment.getPayerId()) ?
         
                        " payerId is null or empty;" : new String();
        
         String message = msgPaymentOnly + msgPayerId;
        
        /* PayerId has been obtained, and the Payment object is null or invalid */
        if(!msgPaymentOnly.isEmpty() && msgPayerId.isEmpty())   
            
            EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                    "validatePaymentStartedWithPayerId", 
                    "Inconsistent State: Payment object is null and PayerId has a value");            
           
        else if(!msgPaymentOnly.isEmpty()&& !msgPayerId.isEmpty())
            message = ValidatorKey.Trans_None.name() + ": " + message;
        
        else if(msgPaymentOnly.isEmpty() && msgPayerId.isEmpty())
            message = ValidatorKey.With_PayerId.name() ;
        
        else if(msgPaymentOnly.isEmpty() && !msgPayerId.isEmpty())
            message = ValidatorKey.Payment_Only.name() + ": " + message;
        
        EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedWithPayerId", message);
        
        return message;
        
    }
    
    /*
     * 
     */
    public String validatePaymentStartedPaymentOnly(Cart cart, 
            PostalAddress address, Customer customer, BindingResult result) {  
        
       EhrLogger.printToConsole(this.getClass(), "validatePaymentStartedPaymentOnly", "executing");
        
       String msgPayment = this.evaluatePayment(); //throws IllegalArgument for invalid initialization     
       
       String errValidation = "";          
       
       if(msgPayment.isEmpty()) {
         errValidation = evalInvalidValues(cart,address, customer, result);          
       }
       
       if(!errValidation.isEmpty()) {
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(),
                   "validatePaymentStartedPaymentOnly", 
                   "Payment object is initialized and customer or cart is invalid: " + errValidation);
       }
       
      // else msgPayment += " " + errValidation;  //Concat validation to message for information      
           
       return msgPayment;
    }
  
    /* 
     * If the Payment object is not null and has not been initialized a Runtime exception
     * is thrown.
    */
    
    private String evaluatePayment() {
        
        String tokenNullMsg = "";
        String paymentNullMsg = "";
        String stateMsg = ""; //inconsistent state message
        
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
        
        this.debugPrintPostalAddress(address);
        
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
          message += ": MVC BindingResult is not in the session (not comparing). ";
          return message;
        }
        //optional
        this.compareMvcBindingToValidator(message, mvcResult, validatorResult);
        
         message = message.isEmpty() ? "" : type + ": " + message;
         
         return message;
        
    }
    
    private void compareMvcBindingToValidator(String validatorErrMsg,
            BindingResult mvcResult, Errors validatorResult) {  

        EhrLogger.printToConsole(this.getClass(), 
                "compareMvcBindingToValidator: attrsResult=", validatorErrMsg);       
       
        if(!mvcResult.hasErrors() && !validatorResult.hasErrors())
            return;
        
        StringBuffer validatorMsgBuffer = new StringBuffer(validatorErrMsg);
        
        StringBuffer mvcMsgBuffer = new StringBuffer();
        
        if(mvcResult.hasErrors() && validatorResult.hasErrors()) {
        
              customerAttrsValidator.genFieldErrors("PaymentAttributesValidator#MvcResult", 
                mvcMsgBuffer, mvcResult.getFieldErrors());
              
              debugPrintBuffers(mvcMsgBuffer, validatorMsgBuffer);
        
              customerAttrsValidator.compareBindingResultToValidatorResult(mvcResult, validatorResult, 
                 mvcMsgBuffer, validatorMsgBuffer); //throws IllegalArgument
        }
        else {
            
            customerAttrsValidator.genFieldErrors("PaymentAttributesValidator#MvcResult", 
                mvcMsgBuffer, mvcResult.getFieldErrors());
            
            String mvcErrors = mvcMsgBuffer.length() == 0 ? "Mvc has no errors. " : 
                "Mvc BindingResult=" + mvcMsgBuffer.toString();
        
            String validatorErrors = validatorMsgBuffer.length() == 0 ? " Validator has no errors. " : 
                " Validator BindingResult=" + validatorMsgBuffer.toString();
            
            EhrLogger.throwIllegalArg("PaymentAttibutesValidator",
                    "compareMvcBindingToValidator", mvcErrors + validatorErrors);  
        }
    }
    
    private void debugPrintBuffers(StringBuffer mvcBuffer, StringBuffer validatorBuffer) {
        
        String mvc = mvcBuffer.length() == 0 ? "Mvc has no errors. " : 
                "Mvc BindingResult=" + mvcBuffer.toString();
        
        String attrs = validatorBuffer.length() == 0 ? "Validator has no errors. " : 
                "Validator BindingResult=" + validatorBuffer.toString();
        
        EhrLogger.printToConsole(this.getClass(), "compareMvcBindertoValidator", mvc);
        
        EhrLogger.printToConsole(this.getClass(), "compareMvcBindertoValidator", attrs);
    }
    
    private void debugPrintPostalAddress(PostalAddress addr){
        
        String line = addr.getAddressId().getAddress1();
        String zip = addr.getAddressId().getPostalCode();
        String city = addr.getAddressId().getCityId().getCityName();
        String state = addr.getAddressId().getDistrict();
        
        String formatted = MessageFormat.format("line={0} zip={1} city={2} state={3}", 
                line,zip,city, state);
        
        EhrLogger.printToConsole(this.getClass(), "validatePostalAddress", formatted);
        
    }
    
}
