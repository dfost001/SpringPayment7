/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import exceptions.shipAddressController.UnequalHashTimeCurrentException;
import exceptions.shipAddressController.ExpiredEditViewRequest;
import exceptions.shipAddressController.LoginIdChangedException;
import dao.CustomerManager;
import dao.exception.CustomerNotFoundException;
import error_util.EhrLogger;
import exceptions.SelectedShipAddressCompareException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import model.customer.Address;
import model.customer.AddressTypeEnum;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import view.attributes.CustomerAttributes;

/**
 *
 * @author dinah
 * 
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION) //sessionScope required to inject CustomerAttributes
public class CompareAddressUtil2 implements Serializable {
    
     public static final String COMPARE_EXCEPTION = 
             "selectedShipAddressCompareException";
    
     @Autowired
     private CustomerManager custManager;
     
     @Autowired
     private CustomerAttributes customerAttrs;     
     
    // private HttpSession httpSession; 
     
     public List<String> generateHashCode(Customer customer, List<ShipAddress> shipAddressList) {          
          
          List<String> hash = new ArrayList<>();
          
          hash.add(customerHashCode(customer));
          
          for(PostalAddress addr : shipAddressList) {
              
              hash.add(customerHashCode(addr));
          }
          return hash;
      }
     
     public List<String> generateHashList(List<? super PostalAddress> list) {
         
          List<String> hash = new ArrayList<>();
          
          for(Object addr : list) {
              PostalAddress postal = (PostalAddress)addr;
              hash.add(customerHashCode(postal));
          }
          
          return hash;
     }
     
     public void compareSelectedPostalAddressToDb(HttpSession httpSession,
                          Customer modelCustomer, 
                          PostalAddress selectedShipAddress,
                          String invokingTitle)
                      throws IllegalArgumentException,
                      SelectedShipAddressCompareException,
                      CustomerNotFoundException {
         
        // this.httpSession = httpSession;        
        
         compareCustomerToDb(modelCustomer, invokingTitle);
         
         if(modelCustomer.getClass().isAssignableFrom(selectedShipAddress.getClass())) {
                 this.throwCompareModelCustomerToSelectedPostalAddress(modelCustomer,
                         selectedShipAddress, invokingTitle); //Throws if Id's not equal
                 return ; 
         }        
         
         this.throwCompareSessionShipAddressToDb(modelCustomer, 
                 (ShipAddress)selectedShipAddress,
                  invokingTitle);       
         
     }   
    
      public void compareCustomerToDb(Customer customer, String title)
              throws IllegalArgumentException,
              CustomerNotFoundException {
          
         String err = title + ": Customer fields are not equal to retrieved customer. ";  
         
         Customer dbCust = custManager.loadByEntityDef(Customer.class,customer.getCustomerId());
         
         String hashDb = customerHashCode(dbCust);
         
         String hashSession = customerHashCode(customer);
         
         if(hashSession.compareTo(hashDb) != 0)
             throw new IllegalArgumentException(
                     EhrLogger.doError(this.getClass().getCanonicalName(), "compareCustomerToDb", err));
         
         this.compareCustomerRelatedShipAddressToDb(customer.getShipAddressList(),
                 dbCust.getShipAddressList());             
     }
      
       private void compareCustomerRelatedShipAddressToDb(List<ShipAddress> sessionList, List<ShipAddress> dbList) {
         
         String title = "Comparing related ship addresses in Customer entity to Customer retrieved from storage: ";
         String sizeErr = "Lists of type ShipAddress do not have equal size." ;
         String hashErr = "ShipAddress entities do not have an equal hash";
         
         if(sessionList.size() != dbList.size()) {
             
             throw new IllegalArgumentException(
                     EhrLogger.doError(this.getClass().getCanonicalName(),
                             "compareCustomerRelatedShipAddressToDb", title + sizeErr));
             
         }
        
        System.out.println("CompareAddressUtil#compareCustomerRelatedShipAddressToDb:" + title);
        
        for(int i=0; i < sessionList.size(); i++) {
            
            String sessionHash = customerHashCode(sessionList.get(i));
            String dbHash = customerHashCode(dbList.get(i));
            
            /*String txt = MessageFormat.format("sessionHash={0} dbHash={1}", sessionHash, dbHash);
            System.out.println(txt);*/
           
            if(!sessionHash.equals(dbHash))
                throw new IllegalArgumentException(
                     EhrLogger.doError(this.getClass().getCanonicalName(),
                             "compareCustomerRelatedShipAddressToDb", 
                             title + hashErr + " at storage shipId="
                                     + dbList.get(i).getShipId()));
        } //end for         
     }
      
       public void throwCompareModelCustomerToSelectedPostalAddress
        (Customer modelAttr, PostalAddress selected, String invokingTitle)  
            throws 
                SelectedShipAddressCompareException {
            
          Customer selectedCustomer = (Customer) selected;
          
          if(!modelAttr.getCustomerId().equals(selectedCustomer.getCustomerId())) {
              
              String err = "Non-current customer in selected PostalAddress" ;
              
              this.throwIllegalArgumentException("throwCompareModelCustomerToSelectedPostalAddress", err);
          }
              
            
          String message = "Customer information selected as delivery address has changed.";
          
          String technical = "Customer hash in model and hash referenced by selectedShipAddress"
                  + " do not compare.";
          
          String modelAttrHash = customerHashCode(modelAttr);
          
          String selectedHash = customerHashCode(selected);
          
          if(!modelAttrHash.equals(selectedHash)) {              
              
              String updatedName = selected.getFirstName() + " "
                     +  selected.getLastName()
                     + " has changed. " ;
              
              throw this.initCompareException(message, technical, updatedName, invokingTitle);              
              
          }           
      }  
        
         public void throwCompareSessionShipAddressToDb(Customer customer, 
             ShipAddress selected, String invokingTitle)
                   throws SelectedShipAddressCompareException {
         
         String friendlyDeleted = "Selected delivery address has been deleted. Please reselect.";  
         
         String friendlyUpdated = "Delivery address has changed. Please reselect.";
         
         String technical = "ShipAddress may have been updated/deleted and "
                 + "not selected from selectShippingAddress.jsp";             
         
         ShipAddress shipAddressDb = this.findDbShipAddress(customer.getShipAddressList(), selected) ;
         
         this.checkInconsistentSelectedFlag(selected);
         
         if (shipAddressDb == null) {            
             
            if (!customerAttrs.isDeletedAddress(selected.getShipId())) {
               
                String err = "Selected ShipAddress is not in customer relationship. " +
                         "Not added to deleted array or deleted array was removed from session on CancelLogin?";                 
                 this.throwIllegalArgumentException("throwCompareSessionShipAddressToDb", err);                
                 
           } else if(!this.isRelated(customer, customerAttrs.getDeletedAddress(selected.getShipId()))) { //In deleted array and not related
               
               String err = "Selected ShipAddress is in the deleted array and not related to current Customer. " +
                         "Selection probably not removed from session when Login cancelled. ";                 
               this.throwIllegalArgumentException("throwCompareSessionShipAddressToDb", err);
               
           } else {

                 String updatedName = selected.getFirstName() + " "
                         + selected.getLastName()
                         + " has been deleted. ";                 
                 throw initCompareException(friendlyDeleted, technical,
                         updatedName, invokingTitle);
             }
         } //end null evaluation           
         
         String hashDb = customerHashCode(shipAddressDb);
         
         String hashSelected =customerHashCode(selected);       
         
         if(hashDb.compareTo(hashSelected) != 0) {
             String updatedName = selected.getFirstName() + " " 
                     + selected.getLastName() + " has changed. ";
             throw initCompareException(friendlyUpdated, technical, 
                     updatedName, invokingTitle);    
         }
     }  
     
      /*
       * Fix: Assumes non-null ShipAddress
       * Note: evalShipId throws if param does not retrieve
      */
      public boolean compareSelectedAddressHashToDbHash(String hash, Short id, AddressTypeEnum type)
            throws CustomerNotFoundException {          
          
          PostalAddress postal = null;
          
          if(type.equals(AddressTypeEnum.Customer))
              postal = custManager.loadByEntityDef(Customer.class, id);
          
          else if(type.equals(AddressTypeEnum.ShipAddress))
              postal = custManager.getShipAddressById(id);
          
         String dbHash = customerHashCode(postal);
         
         if(dbHash.equals(hash)){
             System.out.println("CompareAddressUtil#compareSelectedAddressHashToDbHash: "
                     + ": returning equal");
             return true;
         }
         System.out.println("CompareAddressUtil#compareSelectedAddressHashToDbHash: "
                     + ": returning not equal");
         return false;          
      }          
    
     /*
      * Invoked from ShippingAddressController#processEdit
      * Form parameter used to retrieve an entity.
      * If successful, and form parameter not equal to session model, 
      * throw LoginChangedException if form time not current
     */
     public void evalCustomerIdParam(Short paramCustomerId, 
             Customer modelCustomer, Long formTime, String hash) 
                throws UnequalHashTimeCurrentException,
                LoginIdChangedException, ExpiredEditViewRequest {
         
         boolean isCurrentTime = this.isCurrentFormTime(formTime,
                 customerAttrs.getFormTime(), "evalCustomerIdParam") ;
         
         Customer dbCustomer = this.custManager.customerById(paramCustomerId);
         
         String info = null;
         
         if(dbCustomer == null) {             
         
             throwIllegalArgumentException("evalCustomerIdParam", 
                     "'paramCustomerId' failed to retrieve a customer entity");
         }
         else if(!paramCustomerId.equals(modelCustomer.getCustomerId())){
             
             if(!isCurrentTime)
                 initLoginIdChangedException(dbCustomer, 
                         modelCustomer, AddressTypeEnum.Customer, ""); // Throws exception  
             throw new UnequalHashTimeCurrentException(
                    this.doError("evalCustomerIdParam", 
                    "Form request parameter 'customerId' not equal to @ModelAttribute customerId. ")) ;
             
         } else  {
             
             info = this.genUnequalHashMessage(dbCustomer, hash, formTime,
                     AddressTypeEnum.Customer, paramCustomerId);
             
             if(!isCurrentTime)
                 throw new ExpiredEditViewRequest(info);
             else if(info != null)  { //Form hash not equal to underlying and form time is current
                  info = this.doError("evalCustomerIdParam", info);
                  throw new UnequalHashTimeCurrentException(info);                  
             }                
         } 
     }   
     
     public void evalShipIdParam(Customer modelCustomer, Short paramShipId, 
             Long paramTime, String formHash) 
                       throws UnequalHashTimeCurrentException,
                              LoginIdChangedException, ExpiredEditViewRequest {
         
        boolean isCurrentTime = this.isCurrentFormTime(paramTime, 
                customerAttrs.getFormTime(), "evalShipIdParam");
         
        ShipAddress shipAddress = this.custManager.getShipAddressById(paramShipId);
        
        String info = null;
         
        if(shipAddress == null) {
            
           if(!this.customerAttrs.isDeletedAddress(paramShipId)) {  
               
               this.throwIllegalArgumentException("evalShipIdParam", 
                     "'paramShipId' failed to retrieve a ShipAddress entity "
                     + "and id '" + paramShipId + "' is not in deleted map.");
           
           } else {
               info = this.genUnequalHashMessage(shipAddress, formHash, paramTime, 
                       AddressTypeEnum.ShipAddress, paramShipId);
               if (!isCurrentTime)
                   throw new ExpiredEditViewRequest(info);
             
               else {
                    info = this.doError("evalShipIdParam", info);
                    throw new UnequalHashTimeCurrentException(info);
               }                        
        }
     }  else if(!isRelated(modelCustomer, shipAddress)) { 
           
            if(isCurrentTime)
            
                throw new UnequalHashTimeCurrentException(
                    this.doError("evalShipIdParam", 
                    "Form time is current and selected ShipAddress unrelated to session Customer. ")) ; 
            
            else this.initLoginIdChangedException(shipAddress.getCustomerId(), 
                    modelCustomer, AddressTypeEnum.ShipAddress, "");
            
      } else {
          info = this.genUnequalHashMessage(shipAddress, formHash, paramTime, 
              AddressTypeEnum.ShipAddress, paramShipId); 
          if(!isCurrentTime)
              throw new ExpiredEditViewRequest(info);
          else if(info != null) { //Form hash not equal to underlying and form time equal to underlying
              info = this.doError("evalShipIdParam", info) ;
              throw new UnequalHashTimeCurrentException(info);
          }
      }  
  }
    /*
     * We want to keep the message if time is not current and return null if current
     * Caller will throw ExpiredEditViewRequest whether or not message is empty.
     * IllegalArgument will only be thrown if message is null (not equal to underlying)
     * and time is current
     */
       private String genUnequalHashMessage(PostalAddress dbAddress, String formHash,
             Long formTime, AddressTypeEnum type, Short id)
                     throws UnequalHashTimeCurrentException  {
         
         boolean valid = true;
         
         boolean timeCurrent = isCurrentFormTime(formTime, customerAttrs.getFormTime(), 
                 "throwIfUnequalHashTimeCurrent");            
         
         String err = timeCurrent ? "Form time is equal to current time. Indicates development error. " :
               "Expired request. Indicates request from browser cache. "   ;
         
         if(type.equals(AddressTypeEnum.ShipAddress) && 
                 customerAttrs.isDeletedAddress(id)) {
                 
                 err += "Selected ShipAddress ID has been deleted. ";  
                 return err;                
                 
         } else if(!customerHashCode(dbAddress).equals(formHash)){
                 
                 valid = false;
         }         
         if(!valid) {
             err += "Information for "  + dbAddress.getFirstName() +
                       " " + dbAddress.getLastName() + " has changed. "; //User-friendly
         }            
         if (!timeCurrent){
             return err;
         } else if (!valid){
             return err;
         } else { //timeCurrent and valid
             err = null;
         }
         return err;
         
     } //end generate message 
     
     
 
     private boolean isRelated(Customer model, ShipAddress paramAddress) {  
         
        if(paramAddress == null || model == null)
           EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), 
                   "isRelated", "Unexpected null value for a parameter: ShipAddress or Customer");
         
        if(model.getCustomerId()
                   .equals(paramAddress.getCustomerId().getCustomerId())) 
            return true;
        
        return false;      
     
     } 
     
     
     private ShipAddress findDbShipAddress(List<ShipAddress> list, ShipAddress selected) {
         
         if(list == null)
             return null;
         
         for(ShipAddress addr : list){
             if(addr.getShipId().equals(selected.getShipId()))
                 return addr;
         }
         
         return null;
     }   
     
     private SelectedShipAddressCompareException 
          initCompareException(String message, String technical, 
                  String updatedName, String invokingTitle){
         
        SelectedShipAddressCompareException ex = 
                 new SelectedShipAddressCompareException(message, technical);
        
        ex.setUpdatedNameFld(updatedName);
        
        ex.setInvokingTitle(invokingTitle);   
         
        return ex;
     }
          
     private void initLoginIdChangedException(Customer prevLogin, 
             Customer currentLogin, AddressTypeEnum addressType, String detail) 
                 throws LoginIdChangedException {
         
         String prev = prevLogin.getFirstName() + " " + prevLogin.getLastName();
         
         String current = currentLogin.getFirstName() + " " + currentLogin.getLastName();       
        
         
         LoginIdChangedException ex = new LoginIdChangedException(addressType,
                    prev, current);
         
         ex.setDetail(detail);
         
         throw ex;
         
     }    
          
     public boolean isCurrentFormTime(Long paramTime, Long compareTime, String invoking) {
         
         if(compareTime == null)
             this.throwIllegalArgumentException(invoking + 
                     "->isCurrentFormTime", "Comparison time has not been initialized.");
         if(paramTime == null)
             this.throwIllegalArgumentException(invoking + 
                     "->isCurrentFormTime", "Time request parameter is null");
         int compare = paramTime.compareTo(compareTime);
         if(compare > 0)
           this.throwIllegalArgumentException(invoking + "->isCurrentFormTime", 
                   "Time request parameter is greater than system time");
         if(compare < 0)
               return false;
         return true;
         
     }   
     
     public boolean isCurrentFormTime(String formTime, Long compareTime, String invoking) {
       
         Long param = null;
         
         try {
             
             param = Long.parseLong(formTime);
             
         } catch (NumberFormatException e) {
             this.throwIllegalArgumentException(invoking + "->isCurrentFormTime", 
                  "Form time parameter failed to parse: " + e.getMessage());
         }
         
        return isCurrentFormTime(param, compareTime, invoking);
       
     }
     
     private void throwIllegalArgumentException(String method, String message) {
         
         throw new IllegalArgumentException(
                     EhrLogger.doError(this.getClass().getCanonicalName(),
                             method, message));
         
     }  
     
     private String doError(String method, String message){
         
         return EhrLogger.doError(this.getClass().getCanonicalName(),
                             method, message);
     }
     public void checkInconsistentSelectedFlag(PostalAddress selectedAddress) {
         
         boolean selected = customerAttrs.isShipAddressSelected();
         
         if(selectedAddress == null){
             if(selected)
                     this.throwIllegalArgumentException("Session SELECTED_POSTALADDRESS is null and CustomerAttrs#flag is set", 
                     "checkInconsistentSelectedFlag");
         }
         else if(!selected){
                       this.throwIllegalArgumentException("checkInconsistentSelectedFlag",
                       "Session SELECTED_POSTALADDRESS is NOT null and CustomerAttrs#flag is false");
         }        
     }
    
     public static String customerHashCode(PostalAddress addr){
         
        if(addr == null)
            throw new IllegalArgumentException(EhrLogger.doError(
                    "CompareAddressUtil", "customerHashCode", "PostalAddress.Address field is null")); 
        
        String email = addr.getEmail()==null ? "" : addr.getEmail();
        String firstName = addr.getFirstName() == null ? "" : addr.getFirstName();
        String lastName = addr.getLastName() == null ? "" : addr.getLastName();        
        
        String hash =  Integer.toString(email.hashCode()) + " " +
                    Integer.toString(firstName.hashCode()) + " " +
                    Integer.toString(lastName.hashCode()) + " " +
                    addressHashCode(addr.getAddressId());      
                
        return hash;             
    }
     
      private static String addressHashCode(Address addr) {
        
        if(addr == null)
            throw new IllegalArgumentException(EhrLogger.doError(
                    "CompareAddressUtil", "addressHashCode", "PostalAddress.Address field is null"));
        
        String addr2 = addr.getAddress2() == null ? "" : addr.getAddress2();          
        
        String hash = Integer.toString(addr.getAddress1().hashCode()) +
                   Integer.toString(addr2.hashCode()) +
                   Integer.toString(addr.getCityId().getCityId().hashCode()) +
                   Integer.toString(addr.getDistrict().hashCode()) +
                   Integer.toString(addr.getPhone().hashCode()) +
                   Integer.toString(addr.getPostalCode().hashCode());
        return hash;
    } 
    
       public static String addressSvcHash(Address addr) {
        
        if(addr == null) return "";
        
        String hash = Integer.toString(addr.getAddress1().hashCode()) +                  
                   Integer.toString(addr.getCityId().getCityId().hashCode()) +
                   Integer.toString(addr.getDistrict().hashCode()) +
                   Integer.toString(addr.getPostalCode().hashCode()) +
                   Integer.toString(addr.getCityId().getCountryId().getCountryId().hashCode()) ;
        return hash;
    }  
   
    
} //end class
