/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;


import dao.CustomerManager;
import java.util.List;
import javax.servlet.http.HttpSession;
import model.customer.Customer;
import model.customer.CustomerOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import view.attributes.ConstantUtil;


/**
 *
 * @author Dinah
 */
@Controller
@Scope("request")
@SessionAttributes("customer")
public class OrderHistoryController {
    
    @Autowired
    private CustomerManager customerManager;    
    
    @Autowired
    private ConstantUtil constantUtil;
    
    private static final String NONE_FOUND = "Currently, there are no orders for Customer ID: "; 
    
    private static final String MISSING_ID = "Please complete your registration, or use a different id.";    
    
    private String errIdVal = "";
    
    private String errIdMessage = "";
    
    private List<CustomerOrder> orders = null;
    
    private String noneFoundMessage = "";
    
    private String missingIdMsg = "";
    
    private Customer customer;

    public String getErrIdVal() {
        return errIdVal;
    }

    public String getErrIdMessage() {
        return errIdMessage;
    }

    public List<CustomerOrder> getOrders() {
        return orders;
    }  

    public String getNoneFoundMessage() {
        return noneFoundMessage;
    }
    
    public String getMissingIdMsg() {
        return missingIdMsg;
    }

    public Customer getCustomer() {
        return customer;
    }
    
    
   
    private void addModelAttributes(ModelMap model, String returnUrl) {
        
        model.addAttribute("orderHistoryController", this);
        
        constantUtil.setCurrentUrl(returnUrl);
        
        model.addAttribute("constantUtil", this.constantUtil);
        
    }
    /*
     * If customer in session return history, otherwise return login
     * Customer does NOT go into session here since it is processed at
     * another handler.      
     * Note: if request is from an error view, there will be no return URL
    */
    @RequestMapping(value="/orderHistory/login", method=RequestMethod.GET)
    public String loginForm(@RequestParam
     (value=ConstantUtil.CURRENT_URL_KEY, required=false, defaultValue="none")
     String  returnUrl, 
     HttpSession session,       
     ModelMap map){    
        
        if(returnUrl.equals("none"))
             returnUrl = SpringDbController.HOME_URL;
        
        customer = (Customer)session.getAttribute("customer");
        
        if(customer != null){
           
           if(customer.getCustomerId() != null) {
              
               return this.getOrderHistory(customer.getCustomerId().toString(),returnUrl,map);  
           }
           else {
               missingIdMsg = MISSING_ID;
           }
        }    
        
        this.addModelAttributes(map, returnUrl);       
        
        return "orderHistory/orderHistoryLogin";        
    }
    
    
    
    @RequestMapping(value="/orderHistory/orders", method=RequestMethod.GET)
    public String getOrderHistory(@RequestParam("id") String id, 
            @RequestParam(ConstantUtil.CURRENT_URL_KEY) String returnUrl,     
            ModelMap model){       
        
        if(customer == null)  {  //Procedure may be invoked from loginForm() with Customer in session
            customer = this.processCustomer(id);        
            if(customer == null)  { 
                this.addModelAttributes(model, returnUrl);
                return "orderHistory/orderHistoryLogin";
            }
        }
            
        
        this.orders = customerManager.getOrderHistory(customer.getCustomerId(),
                0, 10);
        
        if(orders == null || orders.isEmpty()) {
            this.noneFoundMessage = NONE_FOUND + "'" + id + "'";
            this.addModelAttributes(model, returnUrl);
            return "orderHistory/orderHistoryLogin";
        }
        
        this.addModelAttributes(model, returnUrl);
        
        return "orderHistory/orders";
    }
    
    @RequestMapping(value="/orderHistory/cancel", method=RequestMethod.GET)
    public String cancelLogin(@RequestParam(ConstantUtil.CURRENT_URL_KEY)String returnUrl){
        
        return "redirect:" + returnUrl;
        
    }
    
    private Customer processCustomer(String id) {
        
        Short customerId = null;
        Customer tcustomer = null;
        
        try {
            
            customerId = Integer.valueOf(id).shortValue();
        }
        catch (NumberFormatException nf){
            this.errIdVal = id;
            this.errIdMessage = "Id '" + id + "' cannot be converted to a number";
            return null;
        }
        
        tcustomer = this.customerManager.customerById(customerId);
        
        if(tcustomer == null) {
            this.errIdVal = id;
            this.errIdMessage = "Id '" + id + "' cannot be found.";
            return null;
        }
        return tcustomer;
    }
    
}
