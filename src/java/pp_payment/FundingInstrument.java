/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pp_payment;

/**
 *
 * @author Dinah3
 */
public class FundingInstrument {
    
    private CreditCard creditCard;
    
    //CreditCardToken credit_card_token -- stored credit cards
    
    public FundingInstrument (){}

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    
    
    
    
}
