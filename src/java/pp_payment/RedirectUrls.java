/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class RedirectUrls {
    
    private String returnUrl;
    private String cancelUrl;
    
    public RedirectUrls(){}
    
    public RedirectUrls(String approve, String cancel) {
        this.returnUrl = approve;
        this.cancelUrl = cancel;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

   
    
}
