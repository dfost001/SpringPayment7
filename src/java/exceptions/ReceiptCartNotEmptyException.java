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
public class ReceiptCartNotEmptyException extends Exception {
    
    private String technical = "";
    
    private static final String friendly = "Use the link below to view your most recent order.";
            
    
    public ReceiptCartNotEmptyException(String support) {
        
        super(friendly);
        
        this.technical = support;
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String support) {
        this.technical = support;
    }
    
    
    
}
