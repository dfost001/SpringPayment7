/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class Amount {
    
    private String currency ;
    private String total;
    private Details details;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
    
    
}
