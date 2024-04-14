/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Film;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("session")
@Component
public class Cart implements Serializable{
    
    public static final Integer WARNING_ITEM_QTY = 100;
    
    private static final int PRECISION = 8;
    private static final int SCALE = 2;
    private static final double TAX_AMOUNT = .085;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    private final MathContext context = new MathContext(PRECISION, ROUNDING_MODE);
    
    private static final double SHIP_FEE_DISCOUNT = .03;
    private static final double SHIP_FEE_AMT = 40.00;
    private static final double SHIP_FEE_CONSTANT = 4.00;
    
    private final HashMap<Short,CartItem> items;
    
    private Integer count = 0; 
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private BigDecimal shippingFee;
    private String formattedTax;
    
    public Cart(){
        items = new HashMap<>();
    }

    public HashMap<Short, CartItem> getItems() {
        return items;
    } 
    
     public List<CartItem> mapAsList() {
      List<CartItem> list = new ArrayList<>();
      list.addAll(items.values());
      return list;
   }
    
     public Integer getCount() {
       this.count = 0;
       for(CartItem it : this.mapAsList())
           count += it.getQty();
       return count;
   }
    
    public void removeAll() {
        items.clear();
        this.count = 0;
       
    }
    
    public void remove(Short id){
       items.remove(id);
   }
    
    public void update(Film f, Integer qty) {
        CartItem itm;
        if(items.containsKey(f.getFilmId())){
            itm = items.get(f.getFilmId());
            itm.setQty(qty);
        }
        else {
            itm = new CartItem(f,qty);
            items.put(f.getFilmId(), itm);
        }       
    } 
   
   public BigDecimal getShippingFee(){
       shippingFee = new BigDecimal("0");
       if(!items.isEmpty())
           shippingFee = this.calcShippingFee();
       return shippingFee;
   }
   
   public BigDecimal getSubtotal(){
       subtotal = new BigDecimal("0.00");
       for(CartItem item : items.values())
           subtotal = subtotal.add(item.getExtPrice(),context);
       subtotal.setScale(SCALE,ROUNDING_MODE);
       return subtotal;
   }
   
   public BigDecimal getTaxAmount(){
       
       taxAmount = new BigDecimal(TAX_AMOUNT);
       taxAmount = getSubtotal().multiply(taxAmount, context);
       taxAmount = taxAmount.setScale(SCALE, ROUNDING_MODE);
       return taxAmount;
   }
   
   public BigDecimal getGrandTotal() {
       this.grandTotal = new BigDecimal("0.00");
       grandTotal = grandTotal.add(getSubtotal()).add(getTaxAmount()).add(getShippingFee());
       return grandTotal;
   }
   
   public String getFormattedTax(){
        
        this.formattedTax = this.format(this.getTaxAmount());
        return formattedTax;
    }
   
   public String getFormattedGrand(){
       
       return this.format(this.getGrandTotal());
       
   }
   
   public String getFormattedSubtotal() {
       
       return this.format(this.getSubtotal());
   }
   
    private String format(BigDecimal value){
        
        String pattern = "#,##0.00";
        DecimalFormat dfmt = new DecimalFormat(pattern);
        String formatted = dfmt.format(value);
        return formatted;
    }
    
    private BigDecimal calcShippingFee() {
        
        Double tsubtotal = this.getSubtotal().doubleValue();
        
        BigDecimal fee = null;
        
        if(tsubtotal > SHIP_FEE_AMT) {
            
             fee = new BigDecimal(SHIP_FEE_DISCOUNT) ;
             fee = fee.multiply(this.getSubtotal(), this.context);
             fee = fee.setScale(SCALE, ROUNDING_MODE);
             return fee;
        }
        return new BigDecimal(SHIP_FEE_CONSTANT);
    }
   
  
}
