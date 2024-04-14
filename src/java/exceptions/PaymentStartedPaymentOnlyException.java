/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Dinah
 */
public class PaymentStartedPaymentOnlyException extends RuntimeException{
    
    private final String technical = "End-user has probably navigated away from PayPal site.";
    private static final String FRIENDLY = "You may have navigated away from the PayPal login.";
    
    public PaymentStartedPaymentOnlyException() {
        super(FRIENDLY);
       
    }

    public String getTechnical() {
        return technical;
    }  
    
} //end class
