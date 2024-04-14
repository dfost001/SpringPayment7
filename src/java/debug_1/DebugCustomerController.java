/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug_1;

import dao.CustomerManager;
import java.util.List;
import model.customer.City;
import model.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import view.attributes.CustomerAttributes;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@Scope("request") 
@SessionAttributes("customer")
public class DebugCustomerController {
    
    @Autowired
    private CustomerManager manager;
    
    @Autowired
    private CustomerAttributes attrib;
    
     @RequestMapping(value="/debugCustomerRequest", method=RequestMethod.GET)
    public String customerRequest(@RequestParam("customerId") Short id, ModelMap model) {
        
        Customer customer = manager.customerById(id);
        
        List<City> cities = manager.getCities();
        
        model.addAttribute("cities", cities);
        
        model.addAttribute("customer", customer);
        
        //attrib.setCustomerValid(true); 
        
        debugPrint(customer,"customerRequest");
        
        return "bind_example/debugCustomer";
        
    }
    
    @RequestMapping(value="/debugUpdateCustomer", method=RequestMethod.POST)
    public String updateCustomer(@Valid @ModelAttribute("customer") Customer customer, 
            ModelMap model){
        
        List<City> cities = manager.getCities();
        
        model.addAttribute("cities", cities);
        
        model.addAttribute("customer", customer);
        
       // attrib.setCustomerValid(true);  
        
        debugPrint(customer,"updateCustomer");
        
        return "bind_example/debugCustomer";
    }    
        
    private void debugPrint(Customer customer, String method){
        
        System.out.println("DebugCustomerController#" + method);
        System.out.println("DebugCustomerController#" + method + "storeId="
               + customer.getStore().getStoreId());  
        System.out.println("DebugCustomerController#" + method + "countryId="
               + customer.getAddressId().getCityId().getCountryId().getCountryName());
    }
}
