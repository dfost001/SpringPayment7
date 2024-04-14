/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author dinah
 */
public class SelectedShipAddressCompareException extends Exception {
    
    private String technical; 
    
    private String updatedNameFld;
    
    private String invokingTitle;
    
    public SelectedShipAddressCompareException(String message, String technical) {
        super(message);
    }

    public String getTechnical() {
        return technical;
    } 

    public String getUpdatedNameFld() {
        return updatedNameFld;
    }

    public void setUpdatedNameFld(String updatedNameFld) {
        this.updatedNameFld = updatedNameFld;
    }

    public String getInvokingTitle() {
        return invokingTitle;
    }

    public void setInvokingTitle(String invoker) {
        this.invokingTitle = invoker;
    }
    
    
    
}
