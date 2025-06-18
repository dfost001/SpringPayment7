/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.attributes;


import error_util.EhrLogger;
import exception_handler.LoggerResource;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import restAddressService.AddressControllerRest;
import restAddressService.addressService.SvcAnalysis;
import validation.CompareAddressUtil2;

/**
 *
 * Read/Written by CustomerRequestController, CustomerController
 *  
 */
@Scope("session")
@Component
public class CustomerAttributes implements Serializable{
   
    public enum MessageType {CREATED, 
    UPDATED, DELETED, RESET, SUBMITTED, CANCELLED};
    
    private final String pathToLogFile = "C:\\Users\\dinah\\myLogs\\Spring7\\is_equal_logger.txt";
    
    @Autowired
    private CloneUtil cloneUtil;
    
    private Customer prevCustomer = null;      
    
    private String successMessage = "";
    
    private String shipAddressSuccessMsg = "";
    
    private boolean shipAddressSelected = false;
    
    private final int cookieAge = 24 * 60 * 60 * 30; //30 days in seconds  

    private final HashMap<Short,ShipAddress> deletedAddressMap  = new HashMap<>(); 
    
    private final HashMap<Short, SvcAnalysis> addressSvcMap = new HashMap<>();  
    
     /*
     * Rendered as hidden input on selectShipAddress.jsp
     * Initialized by ShipAddressController when form returned.
     */
    private Long formTime;    
  
    public synchronized Long getFormTime() {
        return formTime;
    }

    public synchronized void setFormTime() {
        //this.formTime = System.currentTimeMillis();
        this.formTime = Instant.now().toEpochMilli(); 
                      //Calculates using highest precision system clock and time-zone offset
    }
    
    /*
     * Rendered as hidden input on drop-down login form and on checkout button.
     * See tiles/template/navigation.jsp
     * Requires an initial value. See CheckoutbuttonControlleer#evalExpiredLoginTime
     */
    private Long loginTime = System.currentTimeMillis();

    public synchronized Long getLoginTime() {
        return loginTime;
    }
    
    public synchronized void updateLoginTime() {
        
        this.loginTime = System.currentTimeMillis();
    }
    
    /*
     * Rendered on the Customer and ShipAddress edit forms
     * Evaluated by interceptors.NonCurrentUpdateInterceptor
     * 
     */
    
    private Long addressUpdateTime = System.currentTimeMillis();
    
    public synchronized Long getAddressUpdateTime() {
        
        return this.addressUpdateTime;
    }
    
    public synchronized void setAddressUpdateTime() {
        this.addressUpdateTime = System.currentTimeMillis();
    }
    
    /*
     * Set in ShippingAddressController before redirect to CustomerController
     * Read and reset by CustomerController#cancelAndReset 
     * See cancelLogin on this component
     */
    private String cancelCustEditUrl;

    public String getCancelCustEditUrl() {
        return cancelCustEditUrl;
    }

    public void setCancelCustEditUrl(String cancelCustEditUrl) {
        this.cancelCustEditUrl = cancelCustEditUrl;
    }  

    public void setPrevCustomer(Customer customer) {
        
        this.evalNullorInvalidCustomer(customer, "setPrevCustomer");
                
        Customer cloned = (Customer)cloneUtil.cloneCustomer(customer);
        
        this.prevCustomer = cloned;
        
        debugPrint("setPrevCustomer", "customer cloned " + prevCustomer.getFirstName());
    }    
    
    public Customer getPrevCustomer() {
        return prevCustomer;
    }    

    public boolean isShipAddressSelected() {
        return shipAddressSelected;
    }

    public void setShipAddressSelected(boolean isShipAddressSelected) {
        this.shipAddressSelected = isShipAddressSelected;
    }   

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    } 

    public String getShipAddressSuccessMsg() {
        return shipAddressSuccessMsg;
    }

    public void setShipAddressSuccessMsg(String shipAddressSuccessMsg) {
        this.shipAddressSuccessMsg = shipAddressSuccessMsg;
    }     

    public HashMap<Short, SvcAnalysis> getAddressSvcMap() {
        return addressSvcMap;
    }  
    
    public void doUpdateCancelledMessage(Class<?> clazz) {
        
        String title = "";
        String message = "";
        
        if(Customer.class.isAssignableFrom(clazz)) {
            title = "Customer" ;
            message = "Your " + title + " edit has been cancelled";
            this.successMessage = message;
        } 
        else if(ShipAddress.class.isAssignableFrom(clazz)) {
            title = "Shipping Address";
            message = "Your " + title + " edit has been cancelled";
            this.shipAddressSuccessMsg = message;
        }
    }

    public void doAddressUpdatedMessage(PostalAddress address, 
            MessageType type, String details) {
        
        String title = "";
        String action = "";
        
        if(Customer.class.isAssignableFrom(address.getClass()))
            title = "Customer: " ;
        else if(ShipAddress.class.isAssignableFrom(address.getClass())) {
            title = "Shipping Address: ";             
        }
        
        action = type.name();
        
        String message = title + address.getFirstName() + " "
                + address.getLastName() + " at '" + address.getEmail()
                + "' " + action;   
        
        message += ". " + details;
        
        if(type.equals(MessageType.CREATED) ||
                type.equals(MessageType.UPDATED))
            message += ". Please review re-formatted data.";
        
        if(title.contains("Customer"))
            this.successMessage = message;
        else if(title.contains("Shipping"))
            this.shipAddressSuccessMsg = message;
    }  //end   
  
    /*
     * Code to delete cookie not working. May need to set value to null. Yes.
     */
    public void cancelLogin(HttpServletRequest request, HttpServletResponse response) {
        
        this.prevCustomer = null;     
        
        this.successMessage = new String();
        
        this.shipAddressSuccessMsg = new String();
        
        this.shipAddressSelected = false;   
        
        this.setFormTime(); //ShippingAddressController
        
        this.cancelCustEditUrl = null;
        
        this.updateLoginTime(); //CheckoutButtonController
        
        this.setAddressUpdateTime(); //NonCurrentUpdateInterceptor
        
        this.addressSvcMap.remove((short)0); //May login again with a different customer. 
       
      // BUG if reset. An edit from a previous login may be in the cache.  
      //see CompareAddressUtil2#evalShipIdParameter invoked from ShipAddressController
      
      //  this.deletedAddressMap.clear(); 
             
        this.deleteNameCookie(request, response);         
       
    }
   
    
    public void addNameCookie(Customer customer, HttpServletRequest request,HttpServletResponse response) {
        
        String name = customer.getFirstName() + " " + customer.getLastName();
        
        Cookie cookie = new Cookie("customerName", name );     
        
        System.out.println("CustomerAttributes#addNameCookie:localAddr=" + request.getLocalAddr());
        
        String path =  request.getContextPath();
        
        cookie.setPath(path);
        
        cookie.setMaxAge(cookieAge);
        
        response.addCookie(cookie);        
        
    }
    /* Fixed?
     * Not working: try setting value, path and domain to empty, age to a negative value
     * Or possible that cookie is deleted after the window is closed?
     */
    public void deleteNameCookie(HttpServletRequest request, HttpServletResponse response) {
        
        String name = "";
        
        Cookie cookie = new Cookie("customerName", name );     
        
        System.out.println("CustomerAttributes#addNameCookie:localAddr=" + request.getLocalAddr());
        
        String path =  request.getContextPath();
        
        cookie.setPath(path);
        
        cookie.setMaxAge(0);
        
        response.addCookie(cookie);        
    }
    
  
    
    /* Error-checking ensures that inserting a new customer is not dependent on customerChanged.
     * More error-checking is required to ensure that a partially validated object
     * is not passed to customerChanged, since Null fields are converted to empty strings.
     * Assumed: Method not called until Customer passes validation
     */
    public boolean customerChanged(Customer current) {
        
        this.evalNullorInvalidCustomer(current, "customerChanged");
       
        if(prevCustomer == null)
            return true;
        
        String hashPrev = CompareAddressUtil2.customerHashCode(this.prevCustomer);
        
        String hashCurrent = CompareAddressUtil2.customerHashCode(current);
        
        System.out.println("customerAttributes#customerChanged:hashPrev=" + hashPrev);
        System.out.println("customerAttributes#customerChanged:hashCurrent=" + hashCurrent);
        
        if(hashPrev.equals(hashCurrent))
             return false;
        return true;
       
     /*  Logger logger = LoggerResource.createFileHandler(pathToLogFile, this.getClass());
       
       if(current.equals(prevCustomer)) {
           
           logger.info("CustomerAttributes#customerChanged: returning false for customerChanged");
           LoggerResource.flush(logger);
           return false;
       }
        logger.info("CustomerAttributes#customerChanged: returning false for customerChanged");
        LoggerResource.flush(logger);
        return true; */
    } 
    
    
    /*
     * Invoked from view.attributes.AddressSvcResult#createMessageCustomer
     * Note: Changes in address2 do not effect the comparison
     * Not Used
    */
    public boolean addrSvcAddressChanged(Customer current){
        
        this.evalNullorInvalidCustomer(current, "addrSvcAddressChanged");
        
        if(this.prevCustomer == null)
            return true;
        
        String hash1 = CompareAddressUtil2.addressSvcHash(current.getAddressId());
        String hash2 = CompareAddressUtil2.addressSvcHash(this.prevCustomer.getAddressId());
        boolean changed = !hash1.equals(hash2);
        return changed;   
        
    }

    public void addDeletedAddress(ShipAddress shipAddressCloned)  {
        
        
        this.deletedAddressMap.put(shipAddressCloned.getShipId(), shipAddressCloned);
        
        this.addressSvcMap.remove(shipAddressCloned.getShipId());
    } 
    
      public boolean isDeletedAddress(Short shipId) {
        
        if(this.deletedAddressMap.containsKey(shipId))
            return true;
        return false;
    }
      public ShipAddress getDeletedAddress(Short shipId) {
          if(isDeletedAddress(shipId))
              return this.deletedAddressMap.get(shipId);
          return null;
      }
    /*
     * To do: Procedure to validate type of derived in PostalAddress
     */
    public void addSvcAnalysisIfPresent(PostalAddress postalAddress, HttpSession session) {
        
        if(Customer.class.isAssignableFrom(postalAddress.getClass())) {
            
            SvcAnalysis analysis = (SvcAnalysis)session.getAttribute(AddressControllerRest.ANALYSIS_KEY);
            
            if(analysis != null) {            
               this.addressSvcMap.put((short)0, analysis);  
            }
                       
            session.removeAttribute(AddressControllerRest.ANALYSIS_KEY);
           
        }
        else if(ShipAddress.class.isAssignableFrom(postalAddress.getClass())) {
            
            SvcAnalysis analysis = 
                (SvcAnalysis)session.getAttribute(AddressControllerRest.ANALYSIS_KEY_SHIP_ADDRESS);
            
            if(analysis != null) {            
               ShipAddress ship = (ShipAddress)postalAddress;
               this.addressSvcMap.put(ship.getShipId(), analysis);
            }
            
            session.removeAttribute(AddressControllerRest.ANALYSIS_KEY_SHIP_ADDRESS);
        }
    }
    
    public SvcAnalysis getSvcAnalysis(PostalAddress postalAddress) {
        
        SvcAnalysis analysis = null;
        
        if(Customer.class.isAssignableFrom(postalAddress.getClass()))
             analysis = this.addressSvcMap.get((short)0);
        
        else if(ShipAddress.class.isAssignableFrom(postalAddress.getClass())) {
            
            ShipAddress ship = (ShipAddress)postalAddress;
            analysis = this.addressSvcMap.get(ship.getShipId());
        }
        
        return analysis;
    }
    
    public void removeSvcAnalysis(PostalAddress postalAddress){
        
         if(Customer.class.isAssignableFrom(postalAddress.getClass()))
             this.addressSvcMap.remove((short)0);
         
         else if(ShipAddress.class.isAssignableFrom(postalAddress.getClass())) {
            ShipAddress ship = (ShipAddress)postalAddress;
            this.addressSvcMap.remove(ship.getShipId());
         }
    }
   
    public String doPreviousDeletionMessage(Short shipId, String detail){
        
        ShipAddress address = this.deletedAddressMap.get(shipId);     
                    
        
        if(address == null)
            throw new IllegalArgumentException(
                    
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "doDeletionMessage", "deletedAddressMap does not contain key " + shipId)
            );
        
        this.doAddressUpdatedMessage(address, MessageType.DELETED, detail);
        
        String msg = this.shipAddressSuccessMsg;
        
        shipAddressSuccessMsg = "";
        
        return msg;
    }
    /*
     * Stub for invoking CustomerAttrsValidator
    */
    private void evalNullorInvalidCustomer(Customer customer, String method) {
        
        String message = "";
        
        
        if(customer == null)
            message = " Current customer is null";
        if(customer.getCustomerId() == null || customer.getCustomerId() == 0)
            message = " customerId is empty";
        if(customer.getAddressId() == null
                || customer.getAddressId().getAddressId() == null
                || customer.getAddressId().getAddressId() == 0)
            message = "addressId is empty"; 
        
        if(message.isEmpty())
            return;
        
        throw new IllegalArgumentException("customerAttributes#" + method
                + ": "  + message);
        
    }
    
    private void debugPrint(String method, String message) {
        
        String line = this.getClass().getCanonicalName() +
                "#" + method + ": " + message;
        
        System.out.println(line);
    }
    
}//end class
