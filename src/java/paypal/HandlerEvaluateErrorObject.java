/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

import pp_payment.PayPalError;
import httpUtil.IValidateErrorObject;

/**
 *
 * To do: initialize/throw PayPalExecuteException if not 401
 */
public class HandlerEvaluateErrorObject implements IValidateErrorObject {

     @Override
    public String validate(Object deserializedErrObject, 
            Integer responseCode, 
            String httpEntity, 
            String invokingMethod) throws RuntimeException {
        
       if(responseCode == 401) 
           return "Permission Error: 401: PayPalError may not be initialized as expected. ";
        
       if(deserializedErrObject == null) 
          return  "Deserialized PayPalError is null";
       
       try {
           
           PayPalError.class.cast(deserializedErrObject);
           
       } catch(ClassCastException e) {
           return "Deserialized object does not cast to PayPalError.class";
       }
       
        
       return validPayPalError(deserializedErrObject);
    }
    
     private String validPayPalError(Object o) {
        
        PayPalError err = (PayPalError) o;
        
        String errMsg = "";
        
        if(err.getMessage() == null)
                errMsg = "PayPalError#message is empty. ";
        if(err.getName() == null)
                errMsg += "PayPalError#name is empty. ";
        if(err.getDetails() == null || err.getDetails().isEmpty())
                errMsg += "PayPalError#details: ArrayList<ErrDetail> is empty. ";
        else if(err.getDetails().get(0).getIssue() == null)
                errMsg += "ErrDetail#issue is null";
        
        return errMsg;
           
        
    } 
    
}
