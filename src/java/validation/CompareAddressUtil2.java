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
         
              
         compareCustomerToDb(modelCustomer, invokingTitle);
         
         if(modelCustomer.getClass().isAssignableFrom(selectedShipAddress.getClass())) {
                 this.throwCompareModelCustomerToSelectedPostalAddress(modelCustomer,
                         selectedShipAddress, invokingTitle); //Throws if Id's not equal
                 return ; 
         }   
         this.throwCompareDbToSelectedShipAddress((ShipAddress)selectedShipAddress, 
                 modelCustomer, invokingTitle);        
         
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
        
       /* if(!customer.equals(dbCust))
            throw new IllegalArgumentException(
                     EhrLogger.doError(this.getClass().getCanonicalName(), "compareCustomerToDb", err)); */
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
        public void throwCompareDbToSelectedShipAddress(ShipAddress selected, Customer modelCustomer, 
                String invokingTitle) throws SelectedShipAddressCompareException {
            
             this.throwInconsistentSessionToDbDeletion(modelCustomer, selected);
             this.throwCompareDbToSelectedHash(modelCustomer, 
                 selected, invokingTitle);     
        }
        
         public void throwCompareDbToSelectedHash(Customer customer, 
             ShipAddress selected, String invokingTitle)
                   throws SelectedShipAddressCompareException {
             
         String previousName = selected.getFirstName() + " " + selected.getLastName() + ": ";           
         
         String friendlyUpdated = "Delivery address has changed. Please reselect.";
         
         String technicalUpdated = "ShipAddress " + selected.getShipId() +
                 " may have been updated and "
                 + "not reselected from selectShippingAddress.jsp. ";        
         
           ShipAddress shipAddressDb = this.findDbShipAddress(customer.getShipAddressList(), selected) ;
         
           String hashDb = customerHashCode(shipAddressDb);
         
           String hashSelected =customerHashCode(selected);       
         
           if(hashDb.compareTo(hashSelected) != 0) {
             
             throw initCompareException(previousName + " " + friendlyUpdated, 
                     previousName + " " + technicalUpdated, 
                     previousName, invokingTitle);    
           }
        } 
        /* If selected, ShipAddress should be found. A runtime is thrown if it cannot be found in the
         * customer-relation. 
         * On deletion, shipAddress is removed from session and added to CustomerAttributes#deletedMap
         */
         public void throwInconsistentSessionToDbDeletion(Customer customer, ShipAddress selected) {          
            
            this.checkInconsistentSelectedFlag(selected);          
         
            ShipAddress shipAddressDb = this.findDbShipAddress(customer.getShipAddressList(), selected) ;           
         
            if (shipAddressDb == null) {    
             
              String msg = ""; 
             
              if (!customerAttrs.isDeletedAddress(selected.getShipId())) {
               
                msg = "Selected ShipAddress is not in customer relationship. " +
                         "Not added to deleted map, not removed from the session, flag not reset. ";                  
              } else {
                msg = "Selected is in deleted map, but not removed from the session on delete, flag not reset. " ;
              }
              
            String technicalDeleted = "Selected delivery address " 
                 + selected.getShipId() + " cannot be retrieved. ";  
            
            String previousName = selected.getFirstName() + " " + selected.getLastName();   
              
            String err = previousName + " " + technicalDeleted + msg ;
           
            this.throwIllegalArgumentException("throwInconsistentSessionToDbDeletion", err);
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
             
             this.genUnequalHashMessage(dbCustomer, hash, formTime,
                     AddressTypeEnum.Customer, paramCustomerId);           
         }        
     }   
     
     public void evalShipIdParam(Customer modelCustomer, Short paramShipId, 
             Long paramTime, String formHash) 
                       throws UnequalHashTimeCurrentException,
                              LoginIdChangedException, ExpiredEditViewRequest {
         
        boolean isCurrentTime = this.isCurrentFormTime(paramTime, 
                customerAttrs.getFormTime(), "evalShipIdParam");
         
        ShipAddress shipAddress = this.custManager.getShipAddressById(paramShipId);       
         
        if(shipAddress == null) {
            
           if(!this.customerAttrs.isDeletedAddress(paramShipId)) {  
               
               this.throwIllegalArgumentException("evalShipIdParam", 
                     "'paramShipId' failed to retrieve a ShipAddress entity "
                     + "and id '" + paramShipId + "' is not in deleted map.");
           
           } else {
               this.genUnequalHashMessage(shipAddress, formHash, paramTime, 
                       AddressTypeEnum.ShipAddress, paramShipId);                                
            }
        } else if(!isRelated(modelCustomer, shipAddress)) { 
           
            if(isCurrentTime)
            
                throw new UnequalHashTimeCurrentException(
                    this.doError("evalShipIdParam", 
                    "Form time is current and selected ShipAddress unrelated to session Customer. ")) ; 
            
            else this.initLoginIdChangedException(shipAddress.getCustomerId(), 
                    modelCustomer, AddressTypeEnum.ShipAddress, "");
            
       } else {
          this.genUnequalHashMessage(shipAddress, formHash, paramTime, 
              AddressTypeEnum.ShipAddress, paramShipId);          
        }  
    }
   
      private String genUnequalHashMessage(PostalAddress dbAddress, String formHash,
             Long formTime, AddressTypeEnum type, Short id)
                     throws UnequalHashTimeCurrentException, ExpiredEditViewRequest  {          
         
         boolean timeCurrent = isCurrentFormTime(formTime, customerAttrs.getFormTime(), 
                 "genUnequalHashMessage");            
         
         String err = timeCurrent ? "Form time is equal to current time. Indicates development error. " :
               "Expired request. Indicates request from browser cache. "   ;
         
         if(type.equals(AddressTypeEnum.ShipAddress) && 
                 customerAttrs.isDeletedAddress(id)) {
                 
              err += "Selected ShipAddress ID has been deleted. ";  
              if (!timeCurrent)
                   throw new ExpiredEditViewRequest(err);
             
               else {
                    err = this.doError(type.name(), err);
                    throw new UnequalHashTimeCurrentException(err);
               }                         
                 
         } 
         
         boolean dataChanged = !customerHashCode(dbAddress).equals(formHash);  
         
         EhrLogger.printToConsole(this.getClass(), "genUnequalHashMessage",
                 "dbHash=" + customerHashCode(dbAddress));
         
          EhrLogger.printToConsole(this.getClass(), "genUnequalHashMessage",
                 "formHash=" + formHash);
                  
         if(dataChanged) {
             err += "Information for "  + dbAddress.getFirstName() +
                       " " + dbAddress.getLastName() + " has changed. "; //User-friendly
             if(timeCurrent) {
                 err = this.doError(type.name(), err);
                 throw new UnequalHashTimeCurrentException(err);   
             } else {
                 throw new ExpiredEditViewRequest(err);
             }
         } else if(!dataChanged && !timeCurrent) {   
                 throw new ExpiredEditViewRequest(err);
         } 
         return null;
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
                    "CompareAddressUtil", "customerHashCode", "PostalAddress is null")); 
        
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
