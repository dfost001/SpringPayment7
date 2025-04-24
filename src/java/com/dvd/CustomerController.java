
package com.dvd;

import com.cart.Cart;
import dao.CustomerManager;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import formatter.CustomPropertyEditorRegistrar;
import java.io.Serializable;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.customer.AddressTypeEnum;
import model.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restAddressService.AddressControllerRest;


import validation.AddressValidator;
import validation.CityZipValidator;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;

/**
 *
 * @author Dinah
 */
@Controller
@SessionAttributes({
    ConstantUtil.CUSTOMER_SESSION_KEY,
    "statesList", 
    "countryList",
    ConstantUtil.CUST_BINDINGRESULT_KEY})
@Scope("session")
public class CustomerController implements Serializable {      
    
    @Autowired
    private CustomerManager customerManager; //transaction   
    
    @Autowired 
     private AddressValidator addressValidator;    
   
    @Autowired
     private CityZipValidator cityZipValidator;
    
    @Autowired 
    private CustomerAttributes customerAttrib;  //update messages, previous customer 
      
    @Autowired 
    private Cart cart; //evaluate application error
    
    @Autowired 
    private PaymentAttributes paymentAttrs; //contains cart validation
    
    @Autowired
    private ConstantUtil constants;
    
    @Autowired
    private CustomPropertyEditorRegistrar editorRegistrar;     
    
   
   /* @InitBinder
    public void registerBinder(WebDataBinder binder){
        editorRegistrar.registerCustomEditors(binder);
    } */   

    
    @InitBinder({ConstantUtil.CUSTOMER_SESSION_KEY})
    public void initValidator(WebDataBinder binder) {
        
       binder.addValidators(addressValidator, cityZipValidator);    
     
    }  
    
    @RequestMapping(value="/cancelLogin", method=RequestMethod.POST)
    public String cancelLogin(SessionStatus status, HttpServletRequest request, HttpServletResponse response) {
        
        customerAttrib.cancelLogin(request, response); //reset session messages, customerValid, addressResult etc...
        
        request.getSession().removeAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        
        status.setComplete();
       
        return "redirect:/home" ;
        
    }
    /*
     * CancelAndReset only works if redirect is back to selectShipAddressController
     * because the Customer/BindingResult can only be reset by updating the session
     * values with @SessionAttributes defined on the target of the redirect.
     * Done: Button only shown if request is from ShippingAddressController
     * The request for the edit form will always be from the ShipAddressController 
     * or from CheckoutButtonController before the
     * Customer, BindingResult are set into session or if there are errors in the BindingResult
     * see CheckoutButtonController
     * 
     */
    @RequestMapping(value="/customer/cancelEdit", method=RequestMethod.POST)
    public String cancelEditAndReset(
            @ModelAttribute(value="customer", binding=false) Customer customer,
            BindingResult bindingResult,
            RedirectAttributes redirectAttrs) {
        
        String returnUrl = this.customerAttrib.getCancelCustEditUrl();      
        
        if(returnUrl == null || returnUrl.isEmpty()) {
            //Request may be refreshed from browser's expired view
            returnUrl = SpringDbController.HOME_URL; 
        }
        customerAttrib.setCancelCustEditUrl(null);
        
         Short id = customer.getCustomerId();
        
        if(id == null){ //Keep current edits in the session
            
            return "redirect:" + returnUrl; //Can throw Application exception, button should not render
        }    
        
        customer = customerManager.customerById(id);       
            
        customerAttrib.doAddressUpdatedMessage(customer, CustomerAttributes.MessageType.RESET, 
                "Customer successfully reset."); 
        
        redirectAttrs.addFlashAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer);
        
        redirectAttrs.addFlashAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY, bindingResult);    
      
        
        return "redirect:" + returnUrl;
        
    }
    
    /* Since BindingResult is an @SessionAttribute, a technique is required to 
     * eliminate previous errors from the <form:errors> display
     * Since backing object is not intercepted with @Valid
     * and there are no conversion errors (all input is of type String),
     * BindingResult will have no errors, and is returned in the model to update
     * bindingResult in the session.
     * Note: If Conversion errors, cannot remove BindingResult from session if declared
     * with @SessionAttributes at type-level. Will have to revise to template code.
     * Note: BindingResult is not returned in the session if the customer is an
     * uncommitted insert, since we want an error result to remain in the session.
     * However, reset button should not be rendered if customerId is empty
     */
    @RequestMapping(value="/reset", method = RequestMethod.POST)
    public String reset(@ModelAttribute(value="customer", binding=false) Customer c, BindingResult result,
            ModelMap map, HttpSession session){            
       
       
       System.out.println("CustomerController#reset: bindingResult has errors = "
           + result.hasErrors()); //hasErrors = false
        
        Short id = c.getCustomerId();
        
        if(id == null){ //Keep previous BindingResult in the session
            this.addModelAttributes(map, c);
            return "showCustomer";
        }    
        
        c = customerManager.customerById(id);                 
       
            
        customerAttrib.doAddressUpdatedMessage(c, CustomerAttributes.MessageType.RESET, 
                "Customer successfully reset.");
       
        this.addModelAttributes(map,c);       
        
        map.addAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY, result); //update bindingResult in Session
        
        return "showCustomer";
    }    
    
    @RequestMapping(value = "/updateCustomer", method = RequestMethod.POST)
    public String updateCust(@Validated 
            @ModelAttribute(value=ConstantUtil.CUSTOMER_SESSION_KEY) Customer cust,
            BindingResult result,
            ModelMap map, 
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttr) 
              throws ConfirmCartException {          
        
        
        paymentAttrs.evalCartEmpty(cart, this.getClass(), "updateCust");         
        
        boolean changed = true;     

        if (result.hasErrors()) {             
           
           this.addErrModelAttributes(map, cust, result);
            
           return "showCustomer";
        }     
    

        if (cust.getCustomerId() == null) {          
            
            customerManager.insertCustomer(cust);             
            
            
            customerAttrib.doAddressUpdatedMessage(cust, CustomerAttributes.MessageType.CREATED, 
                    "Customer successfully created.");     
           
            changed = true;
           
        } else if(!customerAttrib.customerChanged(cust)) {    
            
            customerAttrib.doAddressUpdatedMessage(cust, 
                    CustomerAttributes.MessageType.SUBMITTED,
                    "Customer has not changed since retrieved or from previous edit.");
            
            changed = false;
            
            EhrLogger.printToConsole(this.getClass(), "updateCustomer", "Customer has NOT changed");
            
        } else {    
            
            EhrLogger.printToConsole(this.getClass(), "updateCustomer", "Customer has changed");
            
            cust.setLastUpdate(new Date());

            customerManager.debugUpdate(cust);

            customerAttrib.doAddressUpdatedMessage(cust, CustomerAttributes.MessageType.UPDATED, 
                     "Customer successfully updated."); 
            
            changed = true;
            
        }           
        
        if(changed) { //required for an insertion and if city-name changed on updated
            cust = customerManager.customerById(cust.getCustomerId()); //fill in city-name  
            customerAttrib.setPrevCustomer(cust); 
        }
        
        customerAttrib.addSvcAnalysisIfPresent(cust, request.getSession());                    
        
        customerAttrib.addNameCookie(cust, request, response);       
        
        redirectAttr.addFlashAttribute(ConstantUtil.CUSTOMER_UPDATED, true); //Used by message-processing in target
        
        redirectAttr.addFlashAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, cust);

        redirectAttr.addFlashAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY, result) ;       

        return "redirect:/shippingAddress/showSelect"; 
    }
    
  
    
    private void addErrModelAttributes(ModelMap map, Customer customer, BindingResult result) {
        
        customerAttrib.setSuccessMessage("");
            
        map.addAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY,result);   
        
        constants.setCustomerTitle(ConstantUtil.ERR_TITLE_CUSTOMER);
            
       this.addModelAttributes(map, customer);  
           
        
    }
    /*
     * Invoked from reset()
     */
    private void addModelAttributes(ModelMap map, Customer customer) {     
        
         map.addAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer); //commandName
         
         map.addAttribute("cart", cart); //cart widget on navigation panel
         
         map.addAttribute("customerAttributes", customerAttrib); //successMessage
         
         map.addAttribute(AddressControllerRest.ADDRESS_TYPE_ENUM, 
                 AddressTypeEnum.Customer.name()); //ajax addressSvc parameter        
         
         map.addAttribute("constantUtil", constants); //property for title used by EL 
       
    } 
    
   
    
    private void debugPrint(Customer c) {
        System.out.println(this.getClass().getCanonicalName() + ": contents of Customer after validation:");
        if(c.getLastName() == null)
            System.out.println("Last name is null");
        else if(c.getLastName().isEmpty())
            System.out.println("Last name is empty");
        System.out.println("phone=" + c.getAddressId().getPhone());
        System.out.println("postalCode=" + c.getAddressId().getPostalCode());
        System.out.println("address1=" + c.getAddressId().getAddress1());
        System.out.println("district=" + c.getAddressId().getDistrict());
        System.out.println("city=" + c.getAddressId().getCityId().getCityName());
        System.out.println("firstName=" + c.getFirstName());
    }
   
 } //end controller   
    
  
    

    
    
    
  

