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
public class PaymentStartedException extends RuntimeException{
    
    String technical = "End-user has requested an update URL, and a payerId has been assigned.";
    
    
    public PaymentStartedException() {
        
        super("If a payment has started, details cannot be changed.");
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String technical) {
        this.technical = technical;
    }
    
    
    
}
