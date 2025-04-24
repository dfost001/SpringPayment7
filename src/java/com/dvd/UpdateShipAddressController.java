/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import dao.CustomerManager;
import dao.exception.RecordNotFoundException;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import httpUtil.HttpException;
import java.util.Date;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import model.customer.AddressTypeEnum;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restAddressService.AddressControllerRest;
import validation.AddressValidator;
import validation.CityZipValidator;
import validation.CustomerAttrsValidator;
import view.attributes.CloneUtil;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;

/**
 *
 * @author dinah
 * 
 * Note: ShippingAddressController.EDIT_SHIPADDRESS is NOT returned in the session
 * between possible error updates
 */
@Controller
@Scope("request")
@SessionAttributes({ConstantUtil.CUSTOMER_SESSION_KEY,
                    ConstantUtil.CUST_BINDINGRESULT_KEY})
public class UpdateShipAddressController {   
    
    
    public static final String PREVIOUS_DELETED_URL = "/shippingAddress/handlePreviousDeletionUpdate";
    
    /*
     * Read by update procedure to determine the type of CRUD
     * Put into the model by ShipAddressController and by this update handler on error
     * Fix: move handler that configures edit request to this controller
     * Fix: get selected address from session, and remove on success
     */
    public enum ShipAddressUpdateType {CREATE,UPDATE,DELETE};
    
    private final String updateTypeKey = "updateTypeKey"; //Used by hidden control on submit/reset
    
    private String updateType;
    
    private BindingResult shipAddressBindingResult;  // used to display global view errors 
    
    private String titleError;
    
    private HttpSession session;
    
    @Autowired
    private CustomerManager customerManager;
     
    @Autowired
    private CustomerAttrsValidator attrsValidator;
    
    @Autowired
    private CustomerAttributes customerAttrs;
    
    @Autowired
    private AddressValidator addressValidator;
    
    @Autowired
    private Cart cart;
    
    @Autowired
    private CityZipValidator zipValidator;
    
    @Autowired
    private ShippingAddressController shipAddressController;
    
    @Autowired
    private ConstantUtil constantUtil;
    
    @InitBinder(ShippingAddressController.EDIT_SHIPADDRESS)
    public void initBinder(WebDataBinder dataBinder) {
        
        dataBinder.addValidators(addressValidator,zipValidator);
      
    } 

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }   

    public String getUpdateType() {
        return updateType;
    }

    public String getUpdateTypeKey() {
        return updateTypeKey;
    }    
    
    public BindingResult getShipAddressBindingResult() {
        return shipAddressBindingResult;
    } 
    
    public String getTitleError() {
        return this.titleError;
    }
    
    @RequestMapping(value="/updateShipAddress/cancel", method=RequestMethod.GET)
    public String cancelEdit(RedirectAttributes redirectAttrs, HttpSession session) {
        
        customerAttrs.doUpdateCancelledMessage(ShipAddress.class);
        redirectAttrs.addFlashAttribute(ConstantUtil.SHIPADDRESS_UPDATED, true);
        return "redirect:/shippingAddress/showSelect";
    }
    
    @RequestMapping(value="/updateShipAddress/create", method=RequestMethod.POST)
    public String requestShipAddressNew(@ModelAttribute("customer") Customer customer,
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult customerResult, ModelMap map)
                      throws ConfirmCartException {
        
        attrsValidator.evaluateConfirmCartState(customer, cart, customerResult); 
        
        customerAttrs.setShipAddressSuccessMsg("");
        
        ShipAddress shipAddress = new ShipAddress();
        shipAddress.setCreateDate(new Date());
        shipAddress.setLastUpdate(new Date());         
        
        updateType = ShipAddressUpdateType.CREATE.name(); //Accessed by EL by property to render title and value of hidden control      
        
        this.addUpdateModelAttributes(map, customer, shipAddress);
        
        return "editShipAddress";
        
    }
    /*
     * This is the forward target from the edit request on ShippingAddressController
     * Missing or invalid Customer and BindingResult session attributes are evaluated
     * on source handler
     */ 
    @RequestMapping(value="updateShipAddress/requestUpdateView", method = RequestMethod.POST)
    public String requestShipAddressUpdateView(@ModelAttribute("customer") Customer customer, 
            HttpSession session, ModelMap model) {
        
        System.out.println("ShipAddressUpdate#requestView: customerId=" + customer.getCustomerId());
        
        ShipAddress shipAddress = (ShipAddress)session.getAttribute(ShippingAddressController.EDIT_SHIPADDRESS);
        
        if(shipAddress == null)
            throw new IllegalArgumentException(
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "requestShipAddressUpdateView", "EDIT_SHIPADDRESS  is NULL in the session"));
        
        session.removeAttribute(ShippingAddressController.EDIT_SHIPADDRESS);
        
        customerAttrs.setShipAddressSuccessMsg("");
        
        ShippingAddressController.SelectShipAction action = shipAddressController.getSelectShipAction();
        
         if(ShippingAddressController.SelectShipAction.Edit.equals(action))
             this.updateType = ShipAddressUpdateType.UPDATE.name();
                
        else if(ShippingAddressController.SelectShipAction.Delete.equals(action))
             this.updateType = ShipAddressUpdateType.DELETE.name();
         
        this.addUpdateModelAttributes(model, customer, shipAddress);
        
        return "editShipAddress" ;
    }
    
     /*
     * shipId is bound as <form:input type="hidden" path="shipId>
     * see editShipAddress.jsp
     */
    @RequestMapping(value="/updateShipAddress/submit", method=RequestMethod.POST)
    public String updateShipAddress(@Valid 
            @ModelAttribute(ShippingAddressController.EDIT_SHIPADDRESS) ShipAddress shipAddress,            
            BindingResult shipResult, 
            @RequestParam("updateTypeKey") String updateTypeRequest,
            ModelMap map, 
            HttpSession session,
            RedirectAttributes redirectAttrs)
                throws ConfirmCartException, RecordNotFoundException {     
        
        this.session = session;
       
        Customer customer = this.checkNullOrInvalidCustomer(session); 
        
        customerAttrs.setShipAddressSuccessMsg(""); //clear reset message
        
        if(updateTypeRequest.equals(ShipAddressUpdateType.DELETE.name()))
            return this.doDelete(shipAddress, customer, redirectAttrs);
        
        Short shipId = shipAddress.getShipId();
        
        ShipAddress checkAddress = null; //use another variable to keep user edits
        
        if(updateTypeRequest.equals(ShipAddressUpdateType.UPDATE.name()))
              checkAddress = this.processPreviousDeletion(shipId);
        else if(updateTypeRequest.equals(ShipAddressUpdateType.CREATE.name()))
              checkAddress = shipAddress;
        
        if(checkAddress == null) {
            
            String redirectUrl = this.processRedirectToPreviousDeleted(redirectAttrs, shipId);
            
            return redirectUrl;
        }          
        
        if(shipResult.hasErrors()) {
            
            this.assignErrorAttributes(updateTypeRequest, shipResult); //Into EL properties
            
            this.addUpdateModelAttributes(map, customer, shipAddress);
            
            return "editShipAddress";
        }
   
        
        CustomerAttributes.MessageType messageType = null;
        
        if(shipAddress.getShipId() == null) {
            
             customerManager.addShipAddress(customer, shipAddress); 
             
             messageType = CustomerAttributes.MessageType.CREATED;
        }
        
        else {
            customerManager.modifyShipAddress(customer, shipAddress);
            
            messageType = CustomerAttributes.MessageType.UPDATED;
        }          
        
        customerAttrs.addSvcAnalysisIfPresent(shipAddress, session);
         
        customerAttrs.doAddressUpdatedMessage(shipAddress,
                messageType, "Successful update.");
        
        customer = this.customerManager.customerById(customer.getCustomerId()); //refresh   
        
        redirectAttrs.addFlashAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer); //replace customer with updated shipAddress list  
        
        redirectAttrs.addFlashAttribute(ConstantUtil.SHIPADDRESS_UPDATED, true);
       
        return "redirect:/shippingAddress/showSelect";
    }
    
    @RequestMapping(value="/updateShipAddress/reset", method = RequestMethod.POST)
    public String resetShipAddress(@ModelAttribute("customer") Customer customer,
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult customerResult,
            @RequestParam("shipId") Short shipId, ModelMap map,
            @RequestParam("updateTypeKey") String updateRequestType,
            RedirectAttributes redirectAttrs,
            HttpSession session) throws ConfirmCartException {  
        
        if(updateRequestType.equals(ShipAddressUpdateType.CREATE.name()))
            return this.requestShipAddressNew(customer, customerResult, map);      
        
        this.checkNullOrInvalidCustomer(session);
        
        customerAttrs.setShipAddressSuccessMsg("");
        
        ShipAddress shipAddress = this.processPreviousDeletion(shipId); //throws IllegalArgument if shipId invalid
        
        if(shipAddress == null) {
            
            String redirectUrl = this.processRedirectToPreviousDeleted(redirectAttrs, shipId);
            
            return redirectUrl;
        }         
        
        customerAttrs.addSvcAnalysisIfPresent(shipAddress, session);
        
        this.updateType = ShipAddressUpdateType.UPDATE.name(); //reset
        
        this.addUpdateModelAttributes(map, customer, shipAddress);
        
        customerAttrs.doAddressUpdatedMessage(shipAddress, 
                CustomerAttributes.MessageType.RESET, "Successfully reset.");
        
        return "editShipAddress";
    }
    
    /*
     * Note: Because ShipAddress is created for each update, the Customer
     * field is not bound
     */
    private String doDelete(ShipAddress shipAddress, Customer customer, 
            RedirectAttributes redirectAttrs) {
        
        System.out.println("UpdateShipAddressController#doDelete: executing");       
       
        shipAddress.setCustomerId(customer); //bind Customer before cloning
        
        ShipAddress cloned = (ShipAddress)new CloneUtil().cloneCustomer(shipAddress);          
        
        Customer updated = customerManager.deleteShipAddress(shipAddress, customer);        
        
        customerAttrs.addDeletedAddress(cloned);
        
        this.removeFromSessionIfSelected(cloned);
        
        this.customerAttrs.setShipAddressSelected(false);  
        
        customerAttrs.doAddressUpdatedMessage(cloned, CustomerAttributes.MessageType.DELETED,
                "Successfully deleted.");
        
        redirectAttrs.addFlashAttribute("customer", updated); //replace customer with updated shipAddress list  
        
        redirectAttrs.addFlashAttribute(ConstantUtil.SHIPADDRESS_UPDATED, true);
       
        return "redirect:/shippingAddress/showSelect";
        
    }
    
    private void removeFromSessionIfSelected(ShipAddress deletedShipTo) {
        
        PostalAddress sessPostal = (PostalAddress)this.session
                .getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        if(sessPostal == null) return;
        
        if(!ShipAddress.class.isAssignableFrom(sessPostal.getClass())) return;
        
        ShipAddress sessShipAddress = (ShipAddress)sessPostal;
        
        if(sessShipAddress.getShipId()
                .intValue() != deletedShipTo.getShipId().intValue()) return;  
        
        this.session.removeAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        
        
    }
    
  
     private void assignErrorAttributes(String updateType, BindingResult shipResult){
         
            this.updateType = updateType;
            
            this.shipAddressBindingResult = shipResult;
            
            this.titleError = "Error";   
     }
    
     private void addUpdateModelAttributes(ModelMap map, Customer customer, ShipAddress shipAddress){
        
        map.addAttribute("constantUtil", this.constantUtil) ;
        map.addAttribute(ShippingAddressController.EDIT_SHIPADDRESS, shipAddress);
        map.addAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer); //customerId
        map.addAttribute(ConstantUtil.CART, cart); //widget
        map.addAttribute("customerAttributes", this.customerAttrs); //reset message
        map.addAttribute("updateShipAddressController", this); //shipAddressBindingResult, updateType, titleError
        map.addAttribute(AddressControllerRest.ADDRESS_TYPE_ENUM, AddressTypeEnum.ShipAddress.name()); //ajax addressSvc parameter
       
    }    
    
     private Customer checkNullOrInvalidCustomer(HttpSession session) throws ConfirmCartException {
        
        Customer customer = (Customer)session.getAttribute("customer");
        
        BindingResult customerResult = (BindingResult)session.getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);
        
        if(customer == null || customerResult == null) {
            String techmessage = EhrLogger.doError(this.getClass().getCanonicalName(), 
                    "updateShipAddress", "Customer object is not in HttpSession");
            String friendly = "You may have accessed an expired update form";
            throw new ConfirmCartException(techmessage, friendly);
        }
        attrsValidator.evaluateConfirmCartState(customer, cart, customerResult);
        return customer;
    } 
    
      
    private String processRedirectToPreviousDeleted(RedirectAttributes redirectAttrs, Short addressId) {
        
        String details = "You may have accessed an expired form." ;
        
        String deletionMsg = customerAttrs.doPreviousDeletionMessage(addressId, details);
        
        redirectAttrs.addFlashAttribute(ConstantUtil.SHIPADDRESS_PREVIOUS_DELETION_MSG,
               deletionMsg );
        
        return "redirect:" + PREVIOUS_DELETED_URL;
    }
    
    private ShipAddress processPreviousDeletion(Short shipId) {
        
        ShipAddress shipAddress = customerManager.getShipAddressById(shipId);
        
        if(shipAddress == null) {
            
                if(customerAttrs.isDeletedAddress(shipId))
                    return null;
                
                else  throw new IllegalArgumentException(
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "processPreviousDeletion", 
                            "getShipAddressById returned null and Id is not in deleted array")
                    );
        }
        
        return shipAddress;
    }
    
  
} //end class
