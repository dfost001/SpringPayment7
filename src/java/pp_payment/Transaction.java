
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
public class Transaction {
    
    private Amount amount;
    private String description;
    private ItemList itemList;
    ArrayList<Resource> relatedResources;
    private Payee payee;
    
    public Transaction(){}

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public void setItemList(ItemList itemList) {
        this.itemList = itemList;
    }

    public ArrayList<Resource> getRelatedResources() {
        return relatedResources;
    }

    public void setRelatedResources(ArrayList<Resource> relatedResources) {
        this.relatedResources = relatedResources;
    }
    
    
    
}
