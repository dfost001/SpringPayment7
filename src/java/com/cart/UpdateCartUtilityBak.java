/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cart;

import error_util.EhrLogger;
import java.util.ArrayList;
import java.util.List;
import model.Film;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
import servletContext.ApplicationAttributes;
       

/**
 *
 * @author dinah
 */
//@Component
//@Scope("prototype")
public class UpdateCartUtilityBak {  
    
    //@Autowired
    private final ApplicationAttributes appAttrs;  
    
    private final Cart cart;   
    
    private final Boolean throwOnCartItemNotFound;
    
    public UpdateCartUtilityBak(Cart cart, 
            ApplicationAttributes applicationAttrs,
            Boolean throwOnCartItemNotFound){
        
        this.cart = cart;
        this.appAttrs = applicationAttrs;
        this.throwOnCartItemNotFound = throwOnCartItemNotFound;
    }
    
    public void processDelete(Short filmId, 
            Byte categoryId,
            List<String> successMessage) {
        
        this.throwNullMessageList(successMessage, "processDelete");
        
        Film film = appAttrs.findFilmByCategoryId(categoryId, filmId);
        
        processCartItemStatus(film, "processDelete");
        
        CartItem item = cart.getItems().get(filmId);
        
        cart.remove(filmId);

        String success = item.getFilm().getTitle() + " with quantity "
                    + item.getQty() + " successfully removed.";

        successMessage.add(success);
        
    }
    
     public String processHtmlSelectQuantity(Byte categoryId, 
            Short filmId,
            Integer qty){
        
        List<String> success = new ArrayList<>(); 
         
        this.processEdit(filmId, 
                categoryId, 
                qty.toString(),
                success, 
                new ArrayList<>()); 
        
        return success.get(0);
    }
    
   
    
    public void processEdit (Short filmId, Byte categoryId, String sqty, 
            List<String> successMessages,
            List<String> failMessages) {     
                      
        this.throwNullMessageList(successMessages, failMessages, "processEdit");
           
        Film film = appAttrs.findFilmByCategoryId(categoryId, filmId);  

        processCartItemStatus(film, "processEdit") ;             
        
        this.processQuantity(sqty, film, failMessages, successMessages);
        
    }
    
    
       
    private void processQuantity(String sqty, Film film, 
            List<String> failMessages, List<String>successMessages) {
        
         Integer qty = this.convertQuantity(sqty, film, failMessages);
        
            if(qty > 0) {
                
                cart.update(film, qty);  
                
                this.doSuccessMessage(film, qty, successMessages);
                
                evalWarningMessage(film, qty, successMessages);
                
            } 
            else if(qty == 0){
                
               cart.remove(film.getFilmId());
               
               this.doSuccessMessage(film, qty, successMessages);
                       
                
            }                   
        
    }
     
    private Integer convertQuantity(String value, Film film, List<String> errorMessages) {
        
         if(value == null || value.isEmpty()) {
            this.doFailureMessage(film, errorMessages, "Did you forget to enter a quantity in the text box?");
            return -1; 
        }
        
        Integer qty = 0;
        
        try {
            qty = new Integer(value);
        }
        catch(NumberFormatException ex){
            this.doFailureMessage(film, errorMessages, 
                    "Unable to convert  '" + value + "' to a number.");
            return -1;
        }
        
        if(qty < 0)
            qty = Math.abs(qty);
        
        return qty;
        
    }  
   
    
    private void processCartItemStatus(Film dbFilm, String method) {
        
        CartItem item = cart.getItems().get(dbFilm.getFilmId());
        
        if(item == null) {
            if(throwOnCartItemNotFound)
                this.throwIllegalArgumentException(method, 
                   "'" + dbFilm.getFilmId() 
                   + "' not found in cart map");
            else return;
        }
        
        
        Film itmFilm = item.getFilm();
        
        //stub for entity comparison - compare ID of object value in map to its key
        if(!itmFilm.getFilmId().equals(dbFilm.getFilmId()))
            this.throwIllegalArgumentException(method, 
                   "'" + dbFilm.getFilmId() 
                   + "' does not equal '" + itmFilm.getFilmId() + "' found in cart map");
    }
    
    private void throwNullMessageList(List<String> messages1, 
            List<String> messages2, String method) {       
        
         if(messages1 == null ||
                messages2 == null)
            this.throwIllegalArgumentException(method,
                    "Message lists may be accumulated, and cannot be null.");             
    }
    
   
    
     private void throwNullMessageList(List<String> messages1, String method) {       
        
         if(messages1 == null)
            this.throwIllegalArgumentException(method,
                    "Message list may be accumulated, and cannot be null.");             
    }
    
     private void throwIllegalArgumentException(String method, String message) {
        
        String err = EhrLogger.doError(this.getClass().getCanonicalName(), method, message);
        throw new IllegalArgumentException(err);
    }
     
     private void doSuccessMessage(Film film, Integer qty, List<String> successMessages) {
          successMessages.add("Id #" + film.getFilmId() + " '" + film.getTitle()
                        + "' successfully updated to quantity "
                        + qty
                        + ".");
     }
     
     private void evalWarningMessage(Film film, Integer qty, List<String> successMessages) {
         
         Integer warningLevel = Cart.WARNING_ITEM_QTY;
         
         String warningMsg = "Warning: ";
         
         if(warningLevel.compareTo(qty) < 0) {
             
             warningMsg += "Id #" + film.getFilmId() + " '" + film.getTitle()
                        + "' exceeds a quantity of " + warningLevel + ". "
                        + "Do you want to re-edit?";
             
             successMessages.add(warningMsg);
         }             
             
     }
     
     private void doFailureMessage(Film film, List<String> errorMessages, String msg) {
         
          errorMessages.add("Film " + film.getFilmId() + " '" +
                    film.getTitle() + "': " + msg);
     }
    
}
