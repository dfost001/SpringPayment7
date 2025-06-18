/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import exceptions.ConfirmCartException;
import exceptions.SelectedShipAddressCompareException;
import java.io.Serializable;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import validation.CompareAddressUtil2;
import validation.CustomerAttrsValidator;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;


/**
 *
 * @author Dinah
 * Throws IllegalStateException if customer validation fails or cart is empty
 */
@Controller
@Scope("request")
@SessionAttributes({ConstantUtil.CUSTOMER_SESSION_KEY, 
                    ConstantUtil.CUST_BINDINGRESULT_KEY, 
                    ShippingAddressController.SELECTED_POSTALADDRESS})
public class ConfirmCartController implements Serializable{     
    
    
    @Autowired
    private CustomerAttrsValidator customerAttrsValidator;
    
    @Autowired
    private CompareAddressUtil2 compareUtil;
    
    @Autowired
    private Cart cart;       
    
    @Autowired
    private CustomerAttributes customerAttributes;
    
    @Autowired
    private PaymentAttributes paymentAttrs;           

     
    /*
     *  Requested from continue button at verifyResults.jsp
     *  End-user may ignore browser-warning on back-navigation, so revalidate and
     *  throw ConfirmCartException, not IllegalStateException
     * 
     */
    @RequestMapping(value="/confirmCart", method=RequestMethod.POST)
    public String confirmCart(HttpSession session,
           @ModelAttribute("customer") Customer customer,
           @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult,
           @ModelAttribute(ShippingAddressController.SELECTED_POSTALADDRESS) PostalAddress selectedShipAddress,
           ModelMap map) throws 
            ConfirmCartException, 
            SelectedShipAddressCompareException {        
        
              
        customerAttrsValidator.evaluateVerifySvcResults(customer,
                cart, bindingResult, selectedShipAddress);
       
        compareUtil.compareSelectedPostalAddressToDb(session, customer,
                selectedShipAddress, "Confirm Cart");  
        
        this.addModelAttributes(map, customer, selectedShipAddress);
        
        return "confirmCart" ;    
    }
    
    @RequestMapping(value="/paypalCheckout", method=RequestMethod.POST) 
    public String doPayPalLogin(HttpSession session,
           @ModelAttribute("customer") Customer customer,
           @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult,
           @ModelAttribute("selectedShipAddress") PostalAddress selectedShipAddress,
           ModelMap map) throws 
            ConfirmCartException, 
            SelectedShipAddressCompareException
             {        
        
        System.out.println("ConfirmCartController#doPayPalLogin executing");
              
        customerAttrsValidator.evaluateVerifySvcResults(customer,
                cart, bindingResult, selectedShipAddress);
       
        compareUtil.compareSelectedPostalAddressToDb(session, customer, 
                selectedShipAddress, "PayPal Payment Submit");       
        
        
        paymentAttrs.updatePaymentTime(); //make receipt url inaccessible
        
        return "forward:/paypalLogin";
    }   
   
     @RequestMapping(value="/paypalDirect", method=RequestMethod.POST) 
    public String doPayPalDirect(HttpSession session,
           @ModelAttribute("customer") Customer customer,
           @ModelAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY) BindingResult bindingResult,
           @ModelAttribute("selectedShipAddress") PostalAddress selectedShipAddress,
           ModelMap map) throws 
            ConfirmCartException, 
            SelectedShipAddressCompareException {        
        
        System.out.println("ConfirmCartController#doPayPalDirect executing");
              
        customerAttrsValidator.evaluateVerifySvcResults(customer,
                cart, bindingResult, selectedShipAddress);
       
        compareUtil.compareSelectedPostalAddressToDb(session, customer, 
                selectedShipAddress, "Direct Payment Submit");          
        
        return "forward:/payPal/processDirectPayment";
    }  
    
         
     @RequestMapping(value="/confirmCart/editShipAddress", method=RequestMethod.GET)
    public String editRequest() {
        
        return "redirect:/shippingAddress/showSelect";
    }
    
    private void addModelAttributes(ModelMap map, Customer customer, PostalAddress selectedShipTo) {     
        
     map.addAttribute("cart",cart);  
     
     map.addAttribute("customer", customer);   
     
     map.addAttribute("customerAttributes", customerAttributes); //successMessage
     
     map.addAttribute(ShippingAddressController.SELECTED_POSTALADDRESS,selectedShipTo);
    
    } 
    
    private void debugPrint(Customer customer) {
        System.out.println("ConfirmCartcontroller#debugPrint");
        System.out.println("first=" + customer.getFirstName());
        System.out.println("last=" + customer.getLastName());
        System.out.println("email=" + customer.getEmail());
        System.out.println("cityName=" +
                customer.getAddressId()
                .getCityId()
                .getCityName());
    }
  
}//end class
