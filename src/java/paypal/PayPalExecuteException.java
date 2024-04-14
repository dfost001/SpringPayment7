/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

/**
 *
 * @author Dinah
 */
public class PayPalExecuteException extends Exception {
    
    String method;
    
    String httpEntity;
    
    public PayPalExecuteException(String message, String method) {    
         super(message);
         this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    } 

    public String getHttpEntity() {
        return httpEntity;
    }

    public void setHttpEntity(String httpEntity) {
        this.httpEntity = httpEntity;
    }
    
    
    
}
