/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import model.Film;

/**
 *
 * @author Dinah
 */
public class CartItem implements Serializable{
    
    private Film film;
    private Integer qty;
    private BigDecimal extPrice;    
    
    public CartItem(Film film, Integer qty){
        this.film = film;
        this.qty = qty;
        this.calcExtPrice();
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
        this.calcExtPrice();
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }   

    public BigDecimal getExtPrice() {
         calcExtPrice();
         return extPrice;
    }
    
    private void calcExtPrice(){
        this.extPrice = film.getRentalRate();
        BigDecimal q = new BigDecimal(qty.toString());
        extPrice = extPrice.multiply(q);
    }   
    
}
