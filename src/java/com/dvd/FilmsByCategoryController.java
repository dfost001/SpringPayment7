/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.cart.Cart;
import com.cart.UpdateCartUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import model.Category;
import model.Film;
import servletContext.ApplicationAttributes;
import view.attributes.ConstantUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import view.attributes.CustomerAttributes;



/**
 *
 * @author Dinah
 */

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST) //Problem: Will remove update messages on refresh
public class FilmsByCategoryController implements Serializable{
    
    private static final String DO_EXTRACT = "doExtract";    
   
    @Autowired
    private ApplicationAttributes applicationAttrs;  
    
    @Autowired
    private Cart cart;
    
    @Autowired
    private UpdateCartUtility updateCartUtil ;        
    
    @Autowired
    private ConstantUtil constantUtil;  
    
    @Autowired
    private CustomerAttributes customerAttrs;
    
    private List<Film> filmList;
    
    private Category selectedCategory;
    
    private List<String> errorMessages = new ArrayList<>();
    
    private List<String> successMessages = new ArrayList<>();
    
    

    public List<Film> getFilmList() {
        return filmList;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    } 

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getSuccessMessages() {
        return successMessages;
    }   
    
    @RequestMapping(value="/category/selectedCategory", method=RequestMethod.GET)
    public String filmsByCategory(@RequestParam("categoryId") byte categoryId,
            ModelMap model){          
        
        this.selectedCategory = applicationAttrs.findCategory(categoryId);
        
        this.filmList = applicationAttrs.initSortedFilmsByTitle(selectedCategory);
        
        errorMessages.clear();
        
        successMessages.clear();
        
        if((Boolean)model.get(DO_EXTRACT) != null) { // redirect from update
             this.errorMessages = (ArrayList<String>) model.get("errorMessages");
             this.successMessages = (ArrayList<String>) model.get("successMessages");
        }
        addModelAttributes(model);
         
        return "selectedCategory_tile";
    }
    /*
     * @Param Byte categoryId: @RequestParam is a hidden control.
     * @Param ArrayList<Short> selected filmId parameterValues of checkbox input
     * @Param HttpServletRequest request Used to extract quantity value of text input
     * with the name set to filmId
     *    
     * 
     */
    @RequestMapping(value="/category/updateSelected", method=RequestMethod.POST)
    public String updateCart(@RequestParam("select") ArrayList<Short> selected,
            @RequestParam("categoryId") Byte categoryId,
            HttpServletRequest request, ModelMap model, RedirectAttributes reAttrs){      
        
      //  this.selectedCategory = applicationAttrs.findCategory(categoryId); 
      
       List<String> successList = new ArrayList<>();
       
       List<String> failList = new ArrayList<>();
               
        for(Short id : selected){
            
           String txtQty = request.getParameter(id.toString());
           
           updateCartUtil.processEditNewItem(id, categoryId, txtQty, 
                   successList, failList);
        }
           
        this.addMessagesToModel(reAttrs, successList, failList);
        
        reAttrs.addFlashAttribute(DO_EXTRACT, true);
        
        reAttrs.addAttribute("categoryId", categoryId);
        
        return "redirect:/category/selectedCategory";
    }     
  
    
    private void addMessagesToModel(RedirectAttributes redirectAttrs,
            List<String> success,
            List<String> fail){
        
        redirectAttrs.addFlashAttribute("errorMessages", fail);
        redirectAttrs.addFlashAttribute("successMessages", success);
        
    }   
   
    
    private void addModelAttributes(ModelMap map){   
        
        constantUtil.setCurrentUrl("/category/selectedCategory?categoryId=" +
                    selectedCategory.getCategoryId());      
        
        map.addAttribute("constantUtil", this.constantUtil); //contains forwardUrl property      
       
        map.addAttribute("customerAttributes", customerAttrs); //login times
       
        map.addAttribute("cart", cart); //cart widget
        
        map.addAttribute("applicationAttributes", applicationAttrs); //side bar
        
        map.addAttribute("categoryController", this); //provide EL access to filmList, selectedCategory, messages                  
        
    }
            
}
