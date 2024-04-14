/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.cart.CartItem;
import com.cart.UpdateCartUtility;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import servletContext.ApplicationAttributes;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;

/**
 *
 * @author dinah
 */
@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ViewCartController {
    
    private final String DELETE = "Delete" ;
    private final String EDIT = "Submit" ;
    
    @Autowired
    private Cart cart;
    
    @Autowired
    private ApplicationAttributes appAttrs;
    
    @Autowired
    private UpdateCartUtility updateUtil;
    
    @Autowired
    private ConstantUtil constantUtil;
    
    @Autowired CustomerAttributes customerAttrs;
    
    private String emptyCartMessage;
    
    private List<CartItem> cartItems;
    
    private final List<Category> cartItemsCategory;   
            
    private List<String> errors;
    
    private List<String> success;
    
    //private final Boolean failOnCartItemNotFound = true;
    
    
    
    //@Autowired
    public ViewCartController() {        
       
        
        this.cartItemsCategory = new ArrayList<>();
        
        errors = new ArrayList<>();
        
        success = new ArrayList<>();
        
    }

    public String getEmptyCartMessage() {
        return emptyCartMessage;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public List<Category> getCartItemsCategory() {
        return cartItemsCategory;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getSuccess() {
        return success;
    }    
    
    @RequestMapping(value="/viewCart/requestView", method=RequestMethod.GET)
    public String requestView(ModelMap map) {
        
        String emptyCart = "Currently, there are no items in your cart." ;
        
        this.cartItems = cart.mapAsList();       
        
        if(cartItems == null || cartItems.isEmpty())
           this.emptyCartMessage = emptyCart;
         
        this.initCartItemsCategory();
        
        this.errors = (List<String>) map.get("errors");
        
        this.success = (List<String>) map.get("success");
        
        this.addModelAttributes(map);
        
        return "viewCart_tile";
        
    }
    
    @RequestMapping(value="/viewCart/update", method=RequestMethod.POST)
    public String doUpdate(ModelMap model, 
            @RequestParam("command") String action, 
            @RequestParam("filmId") Short filmId, 
            @RequestParam("categoryId") Byte categoryId, 
            @RequestParam("quantity") String quantity,
            RedirectAttributes redirectAttrs) {
        
        if(action.equals(this.EDIT))
            updateUtil.processExistingItem(filmId, categoryId, quantity, success, errors);
        
        else if(action.equals(this.DELETE)) {
            updateUtil.deleteExistingItem(filmId, categoryId, success);
        }        
        redirectAttrs.addFlashAttribute("errors", errors);
        
        redirectAttrs.addFlashAttribute("success", success);
        
        return "redirect:/viewCart/requestView";
        
    }
    
   
    
    private void addModelAttributes(ModelMap map) {       
        
        constantUtil.setCurrentUrl("/viewCart/requestView");
        
        map.addAttribute("customerAttributes", customerAttrs);
        
        map.addAttribute("viewCartController", this); //see properties
        
        map.addAttribute("applicationAttributes", appAttrs); //sidebar
        
        map.addAttribute("cart", cart); //widget
        
        map.addAttribute("constantUtil", this.constantUtil); 
        
        
    }
    
    private void initCartItemsCategory() {       
        
        if(cartItems == null)
           return;
        
        for(CartItem itm : this.cartItems) {
            
            Category category = appAttrs.categoryByFilm(itm.getFilm());            
            
            this.cartItemsCategory.add(category);
        }
             
    }
    
}
