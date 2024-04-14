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
public class PaymentTimeException extends RuntimeException{
    
    private String technical;
    
    public PaymentTimeException(String technical) {
        super("Payment may have been completed or cancelled.");
        this.technical = technical;
    }
    
    public PaymentTimeException() {
        super("Payment may have been completed or cancelled.");
        this.technical = "Payment time parameter is less than PaymentAttributes#paymentTime";
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String supportMessage) {
        this.technical = supportMessage;
    }
    
    
    
}
