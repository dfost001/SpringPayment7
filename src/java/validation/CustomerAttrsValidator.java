/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import com.cart.Cart;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import restCityState.CityZipService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.web.context.WebApplicationContext;
import view.attributes.PaymentAttributes;

/**
 *
 * @author Dinah
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class CustomerAttrsValidator {  
    
    
    @Autowired 
    private CustomerValidator customerValidator;  
    
    @Autowired
    private PaymentAttributes paymentAttrs;
   
    
     private static final String FRIENDLY_CUSTOMER_ERR = "Customer form may have errors. ";     
    
     private static final String FRIENDLY_CART_ERR = "Please add items to your cart. " ; 
     
     private static final String  COMPARISON_ERR = "MVC Errors and generated Errors do not compare. ";  
     
     private Errors validatorErrors = null; 
     
     private Boolean customerValid;
     
     private Boolean cartValid;
     
     private StringBuffer technical;
     
     private StringBuffer friendly; 
    
    
    
    public void evaluateConfirmCartState(Customer customer, Cart cart, BindingResult bindingResult)
            throws ConfirmCartException, IllegalStateException {
        
        System.out.println(this.getClass().getName() + " executing");
        
        technical =  new StringBuffer();
        
        friendly = new StringBuffer();
        
        evalNullPointerError(customer, cart, bindingResult);
        
        this.checkPaymentInitialization();
        
        cartValid = evalCartState(cart, technical, friendly);  //Do message for Cart before Customer
        
        this.evalCustomerBindingResult(customer, bindingResult);  //May throw Runtime                 
        
        customerValid = !bindingResult.hasErrors();  
       
        if(!cartValid || !customerValid)
            throw new ConfirmCartException(technical.toString(), friendly.toString());        
        
        System.out.println(this.getClass().getName() + " exiting.");
    } //end eval
    
    /*
     * validatePostalAddress throws IllegalArgumentException because unlike the
     * Customer entity, the ShipAddress entity should always be valid. It is not
     * stored in the session until the redirect to VerifySvcResultsController.
     * Note: If Customer has been selected as ShipAddress, and end-user re-edits 
     * to invalid values, the selected PostalAddress may be invalid. So a 
     * a ConfirmCartException is evaluated first, to avoid an ApplicationException view.
     */
    public void evaluateVerifySvcResults(Customer customer, Cart cart, BindingResult bindingResult,
               PostalAddress postalAddress) throws ConfirmCartException, IllegalArgumentException {
        
        String message = "";
        
        if(postalAddress == null) {
            
            message = EhrLogger.doError("CustomerAttrsValidator", "evaluateVerifySvcResults", 
                    "postalAddress is null or not defined as an @ModelAttribute.");
            throw new IllegalArgumentException(message);
        } 
        
        if(Customer.class.isAssignableFrom(postalAddress.getClass())) {
            
            evaluateConfirmCartState(customer, cart, bindingResult);
            
        } else if(ShipAddress.class.isAssignableFrom(postalAddress.getClass())){
            
           validatePostalAddress(postalAddress); //throws IllegalArgumentException
           
           evaluateConfirmCartState(customer, cart, bindingResult);
           
        } else {
            
            throw new IllegalArgumentException(
                EhrLogger.doError(this.getClass().getCanonicalName(), 
                        "evaluateVerifySvcResults", 
                        "Unexpected derived type in super-mapped PostalAddress"));
        }          
       
    }     
    
    
    /*
     * @ModelAttribute on Customer, BindingResult should throw 
     * org.springframework.web.HttpSessionRequired before the handler is executed.
     * Cart should be injected on entrance controllers
     */
    private void evalNullPointerError(Customer customer, Cart cart, BindingResult result) {
        
        String message = "";
        if(customer == null)
            message =  "Customer is null. " ;
        if(cart == null)
            message += "Cart is null.";
        if(result == null)
            message += "bindingResult is null";
        if(!message.isEmpty()){
            
            message = this.getClass().getCanonicalName() + "#evalNullPointerError: " + message;
            throw new IllegalStateException(message);
        }
            
    }
    
    private void checkPaymentInitialization() throws IllegalStateException {
        String message = paymentAttrs.invalidPaymentInitialization();
        if(message != null)
            throw new IllegalStateException (EhrLogger.doError(
                    this.getClass().getCanonicalName(),
                    "checkPaymentInitialization", message));
    }
    
   
    
    private boolean evalCartState(Cart cart, StringBuffer message, StringBuffer friendly) {      
        
        
        if (cart.getItems().isEmpty()) {
                message.append("Cart map is empty. ");
                friendly.append(FRIENDLY_CART_ERR);
                return false;
         }
        return true;        
        
    }  
    
    /*
     * Currently, only called for PostalAddress of type ShipAddress 
     * Possible for Customer to have errors
     */
    private void validatePostalAddress(PostalAddress address) throws IllegalArgumentException {
        
        DataBinder binder = new DataBinder(address);
        
        Errors errors = binder.getBindingResult();
        
        this.customerValidator.validate(address, errors);
        
        if(!errors.hasErrors())
            return;
        
        String type = Customer.class.isAssignableFrom(address.getClass()) ?
                "Customer" : "ShipAddress";
        String message = "selectedPostalAddress of type " + type +
                " has been assigned into session with errors.";
        
        for(FieldError err : errors.getFieldErrors())
            message += err.getField() + ": " + err.getDefaultMessage() + ". ";
        
        throw new IllegalArgumentException(EhrLogger.doError(
                this.getClass().getCanonicalName(), "validatePostalAddress", message));
        
    }  
    
    private void evalCustomerBindingResult(Customer customer, BindingResult mvcErrors) {
        
        validateCustomer(customer, technical, friendly);        
        
        if(!mvcErrors.hasErrors() && !validatorErrors.hasErrors()) {            
           return;           
        } 
        
        if(validatorErrors.hasErrors() && mvcErrors.hasErrors()) {
            
            StringBuffer dataBinderBuf = new StringBuffer();
                
            this.genFieldErrors("MVC BindingResult", dataBinderBuf, mvcErrors.getFieldErrors());
            
            compareBindingResultToValidatorResult(mvcErrors, this.validatorErrors,
                    dataBinderBuf, technical); //throws Runtime
            
            technical.append("MVC and Validator BindingResult are equal; ");
           
        } else if(validatorErrors.hasErrors()) {            
            
            throwIllegalArgument("evalCustomerBindingResult", 
                    COMPARISON_ERR + ": Generated result has errors. " 
                    + technical.toString());
            
        } else if(mvcErrors.hasErrors()){
            
            adjustZipLookup(mvcErrors, COMPARISON_ERR + "MVC BindingResult has errors. ") ;   
            
        }         
            
    }
    
     private boolean validateCustomer(Customer customer, StringBuffer technical,
            StringBuffer friendly) {

        
        DataBinder binder = new DataBinder(customer);
        validatorErrors = binder.getBindingResult();       

        customerValidator.validate(customer, validatorErrors);

        if (validatorErrors.hasErrors()) {
            
            friendly.append(FRIENDLY_CUSTOMER_ERR);

            this.genFieldErrors("CustomerAttrsValidator#validatorErrors", 
                    technical, validatorErrors.getFieldErrors()); //format error message
            
            technical.append("End-user may have navigated away from invalid form.");                
            
            return false;

        }
        return true;

    }  
    
    private void adjustZipLookup(BindingResult mvcErrors, String validatorEmptyMsg) {
        
        List<FieldError> fldErrors = new ArrayList<>();
        
        fldErrors.addAll(mvcErrors.getFieldErrors());
        
        this.removeMvcZipLookupError(fldErrors);
        
        if(fldErrors.size() > 0) {
            
             StringBuffer mvcBinderBuf = new StringBuffer();
            
             this.genFieldErrors("BindingResult#result", mvcBinderBuf, fldErrors);
            
             throwIllegalArgument("adjustZipLookup",
                     validatorEmptyMsg 
                    + mvcBinderBuf.toString()); 
        }
    }
    
    public void compareBindingResultToValidatorResult(BindingResult mvcBindingResult, 
            Errors validatorResult, StringBuffer binderErrBuf, StringBuffer validatorErrBuf)            
         throws  IllegalStateException {   
        
       String method = "compareBindingResultToValidatorResult";          
        
       String notEqualMsg = "MvcBindingResult#FieldErrors and ValidatorResult#FieldErrors "  
                + " do not compare at position(s): ";        
        
        String positions = "";
        
        String err = "";
       
        List<FieldError> mvcFieldErrors = new ArrayList<>();
        mvcFieldErrors.addAll(mvcBindingResult.getFieldErrors()); 
        
        List<FieldError> validatorFieldErrors = new ArrayList<>();
        validatorFieldErrors.addAll(validatorResult.getFieldErrors());     
       
        this.removeMvcZipLookupError(mvcFieldErrors);
        
        this.sortFieldErrors(mvcFieldErrors);
        this.sortFieldErrors(validatorFieldErrors); 

        int i = 0;        
        
        for(; i < mvcFieldErrors.size(); i++) {
            
            if(i == validatorFieldErrors.size()) {
                err = "MvcBindingResult#fieldErrors is greater than Validator#fieldErrors. ";
                break;
            }
            else if(!compareFieldError(mvcFieldErrors.get(i),
                    validatorFieldErrors.get(i)))
           
                positions += i + "; ";
        } //end for
        
        if(i < validatorFieldErrors.size())
            err = "MvcBindingResult#fieldErrors is less than Validator#fieldErrors. ";
        
        if(!positions.isEmpty())
           err +=  notEqualMsg + positions;
        
        if(!err.isEmpty()) {
            err +=  validatorErrBuf.toString() + binderErrBuf.toString();
            this.throwIllegalArgument(method, err);
        }
       
    } //end compare
    
    private void removeMvcZipLookupError(List<FieldError> mvcErrors) {
        
       Iterator<FieldError> iter = mvcErrors.iterator();
        
       while(iter.hasNext()) {
         
           FieldError err = iter.next();
           
           if(err.getField().equals(CityZipService.CITY_FLD_PATH)
                    && err.getDefaultMessage().contains(CityZipService.ZIP_MESSAGE_PREFIX)) {
               iter.remove();
               break;
           }
        }
    }
    
    private boolean compareFieldError(FieldError mvc, FieldError err) {  
      
        
         if(err.getDefaultMessage().contains(mvc.getDefaultMessage()) 
                        && err.getField().equals(mvc.getField())){
                    return true;
         }
         return false;
    } 
    
    /* If the field names are the same, sort on the messages else sort
     * by field name
    */
    private void sortFieldErrors(List<FieldError> list) {
        
        Collections.sort(list,
                (FieldError f1, FieldError f2) ->
                        f1.getField().compareTo(f2.getField()) != 0 ? 
                        f1.getField().compareTo(f2.getField()) :
                                f1.getDefaultMessage().compareTo(f2.getDefaultMessage())
        );
    }

   private void throwIllegalArgument(String method, String message) {
       
       throw new IllegalArgumentException(
               
               doErr(method, message)
       );
   }
    
    private String doErr(String method, String message) {
        return this.getClass().getCanonicalName() 
                + "#" + method + ": " + message;
    }
    
    public void genFieldErrors(String title, StringBuffer buf, List<FieldError> errors) {
         buf.append(title);
         buf.append(": ");
         for (FieldError f : errors) {
                buf.append(f.getField());
                buf.append(": ");
                buf.append(f.getDefaultMessage());
                buf.append("; ");
            }
    }
    
    private String formatUnequalMessage(FieldError feBinder, FieldError feValidator){
        
        String pattern = "Mvc BindingResult: field={0} default={1}" +
                " Validator: field={2} default={3}; ";
        
        String formatted = MessageFormat.format(pattern, 
                feBinder.getField(), feBinder.getDefaultMessage(),
                feValidator.getField(), feValidator.getDefaultMessage());
        
        return formatted;
    }
    
} //end class
