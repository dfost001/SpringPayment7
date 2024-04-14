
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Dinah3
 */
public class Payment implements Serializable{
    
    public enum PaymentState {created, approved, failed, canceled, expired};
    
    private String intent;
    private Payer payer;
    private ArrayList<Transaction> transactions;
    private RedirectUrls redirectUrls = null;
    private String id;
    private String createTime;
    private String updateTime;
    private PaymentState state;
    private ArrayList<Link> links;

   

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

   

    public PaymentState getState() {
        return state;
    }

    public void setState(PaymentState state) {
        this.state = state;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(RedirectUrls redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    
  
    
}
