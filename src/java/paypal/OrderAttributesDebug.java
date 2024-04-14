/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

import java.io.Serializable;
import pp_payment.Address;
import pp_payment.ShippingAddress;

/**
 *
 * @author Dinah
 */
public class OrderAttributesDebug implements Serializable{
    
    private String resourceId;
    private String orderTotal;
    private String transactionId;
    private ShippingAddress address;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public void setShippingAddress(ShippingAddress address) {
        this.address = address;
    }
    
    
    
}
