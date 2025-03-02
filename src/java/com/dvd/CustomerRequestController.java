/*
 * Note:
 * There is no @ModelAttribute on RequestMapping, only on method. If a customer cannot be obtained
 * Spring will raise an error that a Customer is expected.
 */
package com.dvd;

import com.cart.Cart;
import view.attributes.ConstantUtil;
import dao.CustomerManager;
import dao.exception.CustomerNotFoundException;
import exceptions.ConfirmCartException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import model.customer.City;
import model.customer.Customer;
import model.customer.States;
import model.customer.Store;
import model.customer.AddressTypeEnum;
import model.customer.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import restAddressService.AddressControllerRest;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;

/**
 *
 * @author Dinah
 */
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@SessionAttributes({ConstantUtil.CUSTOMER_SESSION_KEY,
     "statesList", "countryList"}) 
                             
public class CustomerRequestController implements Serializable {   
   
    @Autowired
    private CustomerManager customerMgr;   
    
    @Autowired Cart cart;     
    
    @Autowired
    private CustomerAttributes customerAttrs; 
    
    @Autowired 
    private PaymentAttributes paymentAttrs;
    
    @Autowired
    private ConstantUtil constants;
    
    private String idErrorVal;
    
    private String idErrorMsg;     
 
    /*
     * Note: City list is retrieved dynamically via JavaScript depending
     * on Country selection
     */
    @ModelAttribute(value="statesList")
    public void getStatesList(ModelMap map) throws DataAccessException {
        
        List<States> statesList = customerMgr.getStatesList();
        map.addAttribute("statesList", statesList);
    }
    
     @ModelAttribute(value="countryList")
    public void getCountryList(ModelMap map) throws DataAccessException {
        
        List<Country> countryList = customerMgr.getCountries();
        map.addAttribute("countryList", countryList);
    }
    
    /*
     * If this is a login after a cancel, session attributes
     * have been removed. See /CustomerController#cancel
     */
    @ModelAttribute(value=ConstantUtil.CUSTOMER_SESSION_KEY)
    public void findCustomer(@RequestParam("inputCustId") String id, 
            @RequestParam(ConstantUtil.CHECKOUT_COMMAND_NAME) String cmdValue,
            @RequestParam(ConstantUtil.CURRENT_URL_KEY) String redirectUrl,
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap map)
            throws ConfirmCartException {     
        
        System.out.println("Inside CustomerRequestController#findCustomer");
        System.out.println("IdErrorRedirectUrl=" + redirectUrl);           
        
        if(cmdValue.equals(constants.getRegisterValue())) {
            
           return;
        }
        
        Customer customer = null;
        
        this.idErrorMsg = "";
        
        this.idErrorVal = "";             
       
        Short custId = null;
        try {
            custId = new Integer(id).shortValue();
        }
        catch(NumberFormatException fex){      
            
            idErrorMsg = "Id '" + id + "' cannot be converted to a number";
            
            this.idErrorVal = id;
            
            map.addAttribute(ConstantUtil.CURRENT_URL_KEY, redirectUrl);
            
            return;
            
        }        
       
        boolean found = true;
        
        try {
       
           customer = customerMgr.loadByEntityDef(Customer.class, custId);
           
        } catch (CustomerNotFoundException e) {
            
            found = false;
        }  
        
       
        if (!found) {
            
                this.idErrorMsg = "Customer id '" + id + "' cannot be found. Press 'Recover Login'";  
                this.idErrorVal = id;
                map.addAttribute(ConstantUtil.CURRENT_URL_KEY, redirectUrl);                   
                
         } 
         else {              
            
            customerAttrs.addNameCookie(customer, request, response);            
            
            customerAttrs.setPrevCustomer(customer);           
           
            map.addAttribute(ConstantUtil.CUSTOMER_SESSION_KEY,customer); 
            
         }    
    } 
    
    @RequestMapping(value = "/customerRequest", method = RequestMethod.POST)
    public String getInfo(ModelMap map, RedirectAttributes attrib) { 
        
       //Request dispatched with null or empty cart 
        paymentAttrs.throwEmptyCart(cart, CustomerRequestController.class, 
              "getInfo");       
       
       if(map.get(ConstantUtil.CURRENT_URL_KEY) != null) { 
           
           attrib.addFlashAttribute(ConstantUtil.ID_ERROR_VAL, this.idErrorVal);
           attrib.addFlashAttribute(ConstantUtil.ID_ERROR_MESSAGE, this.idErrorMsg);
           
           return "redirect:" + map.get(ConstantUtil.CURRENT_URL_KEY);
       }        
     
       this.addModelAttributes(map);
       
       return "showCustomer"  ;
    } 
 
    /*
     * To do: remove name cookie
     */
    @RequestMapping(value = "/customerRegister", method = RequestMethod.POST)
    public String getNewCustomer(ModelMap map)  
               throws ConfirmCartException {    
       
         paymentAttrs.evalCartEmpty(cart, CustomerRequestController.class, 
              "getNewCustomer");//in case end user ignores document expired warning  
        
        Customer customer = this.newCustomer();       
        
        //customerAttrs.setPrevCustomer(customer); -- not until a successful persist
        
        map.addAttribute(ConstantUtil.CUSTOMER_SESSION_KEY, customer);
        
        this.addModelAttributes(map);
        
        return "showCustomer";
    } 
    
    private void addModelAttributes(ModelMap map) {
        
       this.logContainsCustomer(map);
       
       this.constants.setCustomerTitle(ConstantUtil.TITLE_CUSTOMER);
       
       //Key Properties: customerTitle, checkoutCommandName/Values, loginTimeKey
       map.addAttribute("constantUtil", this.constants);
       
       map.addAttribute(ConstantUtil.CART, cart); //navigation bar
       
       //sessionScope: loginTime (dropDown), formTime
       map.addAttribute("customerAttributes", customerAttrs); 
       
       map.addAttribute(AddressControllerRest.ADDRESS_TYPE_ENUM, 
               AddressTypeEnum.Customer.name()); //ajax address verification
       
    }
    
     private Customer newCustomer(){
        Customer cust = new Customer();
        Store store = customerMgr.getStoreById(new Short("1"));
        cust.setStore(store);
        cust.setCreateDate(new Date());
        cust.setLastUpdate(new Date());
        cust.setActive(true);
        return cust;
    } 
     
    private void logContainsCustomer(ModelMap model) {
        
        if(model.containsAttribute("customer"))
            System.out.println("CustomerRequestController: model contains 'customer'");
        else
           System.out.println("CustomerRequestController: model DOES NOT contain 'customer'");
    } 
    
   /* private void sessionContainsCustomer() {
        
        HttpServletRequest request = 
                (HttpServletRequest)RequestContextHolder
                        .currentRequestAttributes()).getRequest();
    }*/
    
}//end class
    
   
   
    

