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
public class ConfirmCartException extends Exception {
    
    private String friendly;
    
    private String technical;
    
    public ConfirmCartException(String technical, String friendly) {
        
        super(friendly, null, true, true); //message,cause,enableSuppression, writeableStackTrace
        
        this.friendly = friendly;
        
        this.technical = technical;
    }
   
    public String getFriendly() {
        return friendly;
    }

    public void setFriendly(String friendly) {
        this.friendly = friendly;
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String technical) {
        this.technical = technical;
    }
    
    
    
}
