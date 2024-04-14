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
public class Payer {

    public enum PaymentMethod {

        credit_card, paypal
    }
    
    private PaymentMethod paymentMethod;
    
    private ArrayList<FundingInstrument> fundingInstruments;
    
    private PayerInfo payerInfo;

    public Payer() {
        
       
    }

    public ArrayList<FundingInstrument> getFundingInstruments() {
        return fundingInstruments;
    }

    public void setFundingInstruments(ArrayList<FundingInstrument> fundingInstruments) {
        this.fundingInstruments = fundingInstruments;
    }

    public PayerInfo getPayerInfo() {
        return payerInfo;
    }

    public void setPayerInfo(PayerInfo payerInfo) {
        this.payerInfo = payerInfo;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

 
    
    
}
