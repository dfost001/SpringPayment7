/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.cart.UpdateCartUtility;
import dao.FilmManager;
import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import model.Category;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import servletContext.ApplicationAttributes;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;

    
@Controller
@Scope("request")
public class CartController implements Serializable{
    
    /**** View Key ****/
    private final String SELECTED_FILM = "selectedFilm";     
    
    @Autowired
    private FilmManager filmManager;    
    
    @Autowired
    private ConstantUtil constantUtil;
    
    @Autowired
    private Cart cart;     
   
    @Autowired
    private  ApplicationAttributes applicationAttributes;   
    
    @Autowired
    private  UpdateCartUtility updateCartUtil;   
    
    @Autowired
    private CustomerAttributes customerAttrs;
    
    private Film selectedFilm; //model attribute 
    
    private Category category;
    
    private String success;    
    
    //private final boolean failOnCartItemNotFound = false;

    public Category getCategory() {
        return category;
    }   

    public String getSuccess() {
        return success;
    }    
    
  /*   @Autowired
    public CartController(Cart cart, ApplicationAttributes applicationAttrs) {
        
        this.updateCartUtil = new UpdateCartUtility(cart, applicationAttrs, failOnCartItemNotFound);
        
        this.applicationAttributes = applicationAttrs;
        
        this.cart = cart;
        
    }*/   
   
    @RequestMapping(value="/product/request", method=RequestMethod.GET)
    public String doProductPage(@RequestParam("id") Short filmId,             
            ModelMap map,
            HttpServletRequest request) {  
        
       selectedFilm = filmManager.getFilmById(filmId); 
       
       this.category = applicationAttributes.categoryByFilm(selectedFilm);       
              
       this.success = (String)map.get("success"); //redirect from update handler
       
       this.addViewAttributes(map);
       
       return "selectedProduct_tile";
    }
    /*
     * Note that the redirect will remove update request from browser cache
     */
    @RequestMapping(value="/product/update", method=RequestMethod.POST)
    public String updateCart(@RequestParam("qty") Integer qty,
            @RequestParam("selectedItem") Short filmId,
            @RequestParam("category") Byte categoryId,
            RedirectAttributes redirectAttrs) throws IOException {          
        
        String tsuccess = updateCartUtil.processHtmlSelectQuantity(categoryId, filmId, qty);  

        redirectAttrs.addFlashAttribute("success", tsuccess);
      
        return "redirect:/product/request?id=" + filmId;
    }
    
   
    private void addViewAttributes(ModelMap map){
        
       // customerAttrs.updateLoginTime();
       
         map.addAttribute("customerAttributes", customerAttrs); //login time
        
        constantUtil.setCurrentUrl("/product/request?id=" + selectedFilm.getFilmId());
       
        map.addAttribute(SELECTED_FILM, this.selectedFilm);
        
        map.addAttribute("cart",cart);           
       
        map.addAttribute("constantUtil", this.constantUtil);        
       
        map.addAttribute("applicationAttributes", this.applicationAttributes); 
        
        map.addAttribute("cartController", this); //success message, Category
    }      
    
    
}//
