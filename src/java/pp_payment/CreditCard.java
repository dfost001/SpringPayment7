/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class CreditCard {

    public enum AcceptedCard {

        visa, mastercard, discover, amex
    };
    
    public enum ApprovalState {expired,ok}
    
    private String id;  //when using vault
    private String number;
    private AcceptedCard type;
    private int expireMonth;
    private int expireYear;
    private int cvv2;
    private String firstName;
    private String lastName;
    private Address billingAddress;
    private ApprovalState state;
    private String validUntil;


    public int getCvv2() {
        return cvv2;
    }

    public void setCvv2(int cvv2) {
        this.cvv2 = cvv2;
    }

   
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ApprovalState getState() {
        return state;
    }

    public void setState(ApprovalState state) {
        this.state = state;
    }

    public AcceptedCard getType() {
        return type;
    }

    public void setType(AcceptedCard type) {
        this.type = type;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public int getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(int expireMonth) {
        this.expireMonth = expireMonth;
    }

    public int getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(int expireYear) {
        this.expireYear = expireYear;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    
    
    
}
