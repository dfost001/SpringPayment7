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
public class Sale {
    
    public enum SalesState {pending,completed,refunded,partially_refunded};
    
    private String id;  //transaction id
    private Amount amount;
    private String description;
    private String createTime;
    private String updateTime;
    private SalesState state;
    private String saleId; //refund id
    private String parentPayment; //id of resource on which trans is based,same as Payment.id
    private ArrayList<Link> links;
    
    public Sale(){}

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getParentPayment() {
        return parentPayment;
    }

    public void setParentPayment(String parentPayment) {
        this.parentPayment = parentPayment;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

   

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   

    public SalesState getState() {
        return state;
    }

    public void setState(SalesState state) {
        this.state = state;
    }

   

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
    
    
    
    
}
