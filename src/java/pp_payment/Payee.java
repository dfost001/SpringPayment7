/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class Payee {
    
    /**
	 * 
	 */ 
	private String merchantId;

	/**
	 * 
	 */ 
	private String email;

	/**
	 * 
	 */ 
	private String phone;

    
    
    public Payee(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

   
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

   
    
    
}
