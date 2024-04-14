/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class Address {
    
    public enum AddressType {residential,business,mailbox};
    
    private AddressType type;
    private String line1;
    private String line2;
    private String city;
    private String countryCode;
    private String postalCode;
    private String state;
    private String phone;
    
    public Address(){}

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

   

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

   

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }
    
    
    
}
