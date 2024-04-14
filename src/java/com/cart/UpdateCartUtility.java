/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cart;

import error_util.EhrLogger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import servletContext.ApplicationAttributes;
       

/**
 *
 * @author dinah
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class UpdateCartUtility implements Serializable{  
    
    @Autowired
    private ApplicationAttributes appAttrs;  
    
    @Autowired
    private Cart cart;   
    
    private Boolean throwOnCartItemNotFound = false;
    
    private static final String WARNING = "Warning";
    
    public String getWarningKey() {
        
        return WARNING;
    }
    
    public void deleteExistingItem(Short filmId, 
            Byte categoryId,
            List<String> successMessage) {
        
        this.throwNullMessageList(successMessage, "deleteExistingItem");
        
        Film film = appAttrs.findFilmByCategoryId(categoryId, filmId);
        
        this.throwOnCartItemNotFound = true;
        
        processCartItemStatus(film, "deleteExistingItem");
        
        processDeletion(film,successMessage);        
    }
    
     public void deleteItemNoThrow(Short filmId, 
            Byte categoryId,
            List<String> successMessage) {
        
        this.throwNullMessageList(successMessage, "deleteExistingItem");
        
        Film film = appAttrs.findFilmByCategoryId(categoryId, filmId);
        
        this.throwOnCartItemNotFound = false;
        
        processCartItemStatus(film, "deleteExistingItem");
        
        processDeletion(film,successMessage);        
    }
    
    private void processDeletion(Film film, List<String> messages) {
        
        CartItem item = cart.getItems().get(film.getFilmId());
        
        if(item == null) {
            
           String success = "Id #" + film.getFilmId() + " '" + film.getTitle()
                        + "' is currently not in your cart. " ;
                       
           messages.add(success);            
           return;
        }
        
        cart.remove(film.getFilmId());

        String success = "Id #" + film.getFilmId() + " '" +
                      item.getFilm().getTitle() + "' with quantity "
                    + item.getQty() + " successfully removed.";

        messages.add(success);
    }
    
    /*
     * To do: Throw Runtime if quantity is non-numeric or negative
     */
     public String processHtmlSelectQuantity(Byte categoryId, 
            Short filmId,
            Integer qty){
        
        List<String> success = new ArrayList<>(); 
        
        this.throwOnCartItemNotFound = false;
         
        this.processEdit(filmId, 
                categoryId, 
                qty.toString(),
                success, 
                new ArrayList<>()); 
        
        return success.get(0);
    }
    
    public void processEditNewItem(Short filmId, Byte categoryId, String sqty, 
            List<String> successMessages,
            List<String> failMessages) {
        
        this.throwOnCartItemNotFound = false;
        
        processEdit (filmId, categoryId, sqty, successMessages, failMessages);
        
    }
    
     public void processExistingItem(Short filmId, Byte categoryId, String sqty, 
            List<String> successMessages,
            List<String> failMessages) {
        
        this.throwOnCartItemNotFound = true;
        
        processEdit (filmId, categoryId, sqty, successMessages, failMessages);
        
    }
    
    private void processEdit (Short filmId, Byte categoryId, String sqty, 
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
                
                processDeletion(film, successMessages);                
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
        
        if(item == null) { //throw or return
            if(throwOnCartItemNotFound)
                this.throwIllegalArgumentException(method, 
                   "'" + dbFilm.getFilmId() 
                   + "' not found in cart map");
            else return;
        }
        
        
        Film itmFilm = item.getFilm();
        
        // Stub for comparison of Film obtained from Categories (underlying) 
        // Film stored in CartItem
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
         
         String warningMsg = WARNING + ":";
         
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
