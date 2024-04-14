/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions.shipAddressController;

import model.customer.AddressTypeEnum;

/**
 *
 * @author dinah
 */
public class LoginIdChangedException extends Exception {
    
    private final static String TECHNICAL_MSG = "Expired ShipAddressController edit/delete action. "
            + "Requested address related to a previous login." ;
    
    private final static String MESSAGE = 
            "LOGIN CHANGED: Your login ID has changed";  
    
    private final AddressTypeEnum addressType;
    
    private String prevLogin;
    
    private String currentLogin;
    
    private String detail;
    
    public LoginIdChangedException(AddressTypeEnum addressType,
            String prevLoginInfo, String currentLoginInfo) {    
        
        super(MESSAGE);
        
        this.addressType = addressType;
        
        this.prevLogin = prevLoginInfo;
        
        this.currentLogin = currentLoginInfo;
    }

    public  String getTechnical() {
        return addressType.name() + ": " 
                + TECHNICAL_MSG + ". " + detail;
    }
    
    
    @Override
    public String getMessage() {
        
        String message = MESSAGE + ". From " + prevLogin + " to "
                + currentLogin
                + ". " +  addressType.name() + " expired.";
        
        return message;
    }
    
    

    public String getPrevLogin() {
        return prevLogin;
    }

    public void setPrevLogin(String prevLogin) {
        this.prevLogin = prevLogin;
    }

    public String getCurrentLogin() {
        return currentLogin;
    }

    public void setCurrentLogin(String currentLogin) {
        this.currentLogin = currentLogin;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    
}
