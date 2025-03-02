/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import error_util.EhrLogger;
import view.attributes.ConstantUtil;
import exceptions.ConfirmCartException;
import exceptions.SelectedShipAddressCompareException;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.PostalAddress;
import model.customer.ShipAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import validation.CompareAddressUtil2;
import validation.CustomerAttrsValidator;
import view.attributes.AddressSvcResult;
/**
 *
 * @author Dinah
 */
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@SessionAttributes({ConstantUtil.CUSTOMER_SESSION_KEY, ConstantUtil.CUST_BINDINGRESULT_KEY, 
    ShippingAddressController.SELECTED_POSTALADDRESS})
public class VerifySvcResultsController {    
    
    private static final String SUBMIT_DISABLED_KEY = "submitDisabled";
    
    @Autowired
    private AddressSvcResult addrResult;    
    
    @Autowired
    private CustomerAttrsValidator attrsValidator;
    
    @Autowired
    private CompareAddressUtil2 compareUtil;
    
    @Autowired
    private Cart cart;     
    
    @RequestMapping(value="/customer/verifySvcResults", method=RequestMethod.GET)
    public String verifyResults(
            @ModelAttribute(ConstantUtil.CUSTOMER_SESSION_KEY) Customer customer, 
            @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult custBindingResult,
            @ModelAttribute(ShippingAddressController.SELECTED_POSTALADDRESS) PostalAddress postalAddress,
            ModelMap model, HttpSession session) 
           
                    throws ConfirmCartException, 
                           SelectedShipAddressCompareException {         
      
     
       checkPostalType(postalAddress)  ;              
        
       attrsValidator.evaluateVerifySvcResults(customer, cart, custBindingResult, postalAddress);  
       
       compareUtil.compareSelectedPostalAddressToDb(session, customer, postalAddress, 
               "Verify Results Compare");       
        
       this.addrResult.showAddressSvcResult(customer, postalAddress, session);       
       
       this.addSubmitDisabledAttribute(model);
        
       this.addModelAttributes(model,customer,postalAddress);
        
       return "verifyResults";
       
    } 
    
    //Currently, not used
    @RequestMapping(value="/verifyResults/editShipAddress", method=RequestMethod.GET)
    public String editRequest() {
        
        return "redirect:/shippingAddress/showSelect";
    }
    
    private void addModelAttributes(ModelMap model, Customer customer, PostalAddress selectedShipAddress) {
        
        model.addAttribute("addressSvcResult", this.addrResult);
        
        model.addAttribute("customer", customer); //optional - in session
        
        model.addAttribute(ShippingAddressController.SELECTED_POSTALADDRESS, selectedShipAddress);
        
        model.addAttribute("cart", cart); //optional - in session      
        
    }
    
    private void addSubmitDisabledAttribute(ModelMap model) {
        
       if(this.addrResult.isDisableContinue())
            model.addAttribute(SUBMIT_DISABLED_KEY, true);
        else model.addAttribute(SUBMIT_DISABLED_KEY, false);
     
    // model.addAttribute(SUBMIT_DISABLED_KEY, false);
        
    }
    
    private void checkPostalType(PostalAddress postalAddress) {
         
          if(!postalAddress.getClass().equals(Customer.class) && 
               !postalAddress.getClass().equals(ShipAddress.class))
               
               throw new IllegalArgumentException(
                    EhrLogger.doError(this.getClass().getCanonicalName(), 
                            "verifyResults",
                            "PostalAddress selectedAddress cannot be cast to type Customer or ShipAddress"));
    }    
   
    
    private String doDebugMessage(Customer customer) {
        String message = "lastName=";
        message += customer.getLastName() == null ? "null" : "empty";
        message += "<br/>";
        message += "customerId=";
        message += customer.getCustomerId() == null ? "null" : customer.getCustomerId();
        message += "<br/>";
        message += "addressId=";
        message += customer.getAddressId() == null ? "null" : customer.getAddressId().toString();
        message += "<br/>";
        if(customer.getAddressId() != null) {
            message += "cityId=";
            message += customer.getAddressId().getCityId() == null ? "null" 
                    : customer.getAddressId().getCityId().toString();
            message += "<br/>";
            message += "district=";
            message += customer.getAddressId().getDistrict() == null ? "null" 
                    : customer.getAddressId().getDistrict();
            message += "<br/>";
        }
      return message;
    }
    
  
    
}
