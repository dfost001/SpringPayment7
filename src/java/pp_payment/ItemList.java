/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

import java.util.ArrayList;

/**
 *
 * @author Dinah3
 */
public class ItemList {
    
    ArrayList<Item> items;
    ShippingAddress shippingAddress;
    
    public ItemList(){
        items = new ArrayList<Item>();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> item) {
        this.items = item;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

   
    
    
    
}
