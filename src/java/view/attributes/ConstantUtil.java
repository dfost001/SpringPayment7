/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.attributes;

//import org.springframework.context.annotation.Scope;
import com.dvd.CheckoutButtonController;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author dinah
 */
@RequestScope
@Component
public class ConstantUtil {  
    
    
    /* 
     * Rendered by EL, accessed by JavaScript login.js 
     *
     */
    public final static String ID_ERROR_VAL = "ID_ERROR_VAL";
    
    public final static String ID_ERROR_MESSAGE = "ID_ERROR_MESSAGE"; 
    
    /*
     * Session keys accessed by controllers
     */
    public final static String CUST_BINDINGRESULT_KEY = "bindingResult";
    
    public final static String CUSTOMER_SESSION_KEY = "customer" ;
    
    public final static String CART = "cart" ;   
    
    public final static String CUSTOMER_UPDATED = "addressUpdated";
    
    public final static String SHIPADDRESS_UPDATED = "shipAddressUpdated";
    
    public final static String SHIPADDRESS_PREVIOUS_DELETION_MSG = "previousDeletion";
    
    /*
     * Moved to ShipAddressController
     */
    // public final static String SELECTED_POSTALADDRESS = "selectedShipAddress";
    
    // public final static String EDIT_SHIPADDRESS = "shipAddress";
    
    /*
     * Used by MyMethodNotSupported to return a view
     */
    public final static String ERR_NAVIGATION_VIEW = "navigation_error/errNavigation";
    public final static String ERR_CANCEL_VIEW = "navigation_error/errTransactionStartedPaymentOnly" ;
    public final static String ERR_TRANSACTION_STARTED_VIEW = "navigation_error/errTransactionStarted";
    
    /*
     * Property used by the EL to render the URL returning the view
     */
    
     public final static String CURRENT_URL_KEY = "currentUrlKey";
    
     private String currentUrl;  
    
    
     public String getCurrentUrlKey() {
        return CURRENT_URL_KEY;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }
    
    /*
     * Strings for title element of Customer view
    */
    public static final String TITLE_CUSTOMER = "Spring MVC Customer" ;
    public static final String ERR_TITLE_CUSTOMER = "Customer Error";
    
    private String customerTitle;      
   

    public String getCustomerTitle() {
        return customerTitle;
    }

    public void setCustomerTitle(String customerTitle) {
        this.customerTitle = customerTitle;
    }
    
    /*
     * For EL render of CheckoutButtonController request parameters
    */
    
    public final static String LOGIN_TIME_KEY = "loginTimeKey";
    
    public String getLoginTimeKey() {
        return LOGIN_TIME_KEY ;
    }
    
    public final static String CHECKOUT_COMMAND_NAME = "checkoutCommandName" ;
    
    public String getCommandName(){
        
        return CHECKOUT_COMMAND_NAME;
    }
    
    public String getRegisterValue() {
        
        return CheckoutButtonController.CheckoutCommandValue.REGISTER.name();
    }
    
    public String getSubmitValue() {
        
        return CheckoutButtonController.CheckoutCommandValue.SUBMIT.name();
    }
    
    public String getCheckoutValue() {
        
        return CheckoutButtonController.CheckoutCommandValue.CHECKOUT.name();
    }
    
    public static final String COMPARISON_CUST_ID = "compareCustomerId";
    
    public String getCompareCustKey() {
        
        return COMPARISON_CUST_ID ;
    }
    
    //private String idErrorVal;
    //private String idErrorMessage;
    
    /******Currently Not Used - Must be extracted at view handlers on redirect
     
    public void setIdErrorVal(String errVal) {
        this.idErrorVal = errVal;
    }
    
    public String getIdErrorVal(){
        return idErrorVal;
    }

    public String getIdErrorMessage() {
        return idErrorMessage;
    }

    public void setIdErrorMessage(String idErrorMessage) {
        this.idErrorMessage = idErrorMessage;
    }
    
    public String getIdErrorMessageKey() {
        return ID_ERROR_MESSAGE;
    }
    
    public String getIdErrorValKey() {
        return ID_ERROR_VAL;
    }
    *********************/
    
} //end class
