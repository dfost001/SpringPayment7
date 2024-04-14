/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paypal;

import com.cart.Cart;
import com.cart.CartItem;
import error_util.EhrLogger;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import model.customer.Customer;
import model.customer.PostalAddress;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pp_payment.Address;
import pp_payment.Amount;
import pp_payment.CreditCard;
import pp_payment.CreditCard.AcceptedCard;
import pp_payment.Details;
import pp_payment.FundingInstrument;
import pp_payment.Item;
import pp_payment.ItemList;
import pp_payment.Payee;
import pp_payment.Payer;
import pp_payment.Payment;
import pp_payment.RedirectUrls;
import pp_payment.ShippingAddress;
import pp_payment.Transaction;

/**
 *
 * @author Dinah
 */

public class PaymentInitialize2 {
    
    Payer.PaymentMethod method;
    
    private Payment payment ;   
    
    private Cart cart ;
    
    private PostalAddress deliveryAddress;    
    
    private Customer billingCustomer = null;
    
    private RedirectUrls redirectUrls; 
    
    private int testExpireMonth;
    
    private int testExpireYear;  
    
    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(RedirectUrls redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    public Customer getBillingCustomer() {
        return billingCustomer;
    }

    public void setBillingCustomer(Customer billingCustomer) {
        this.billingCustomer = billingCustomer;
    }
    
    public PaymentInitialize2() {
    
         this.initCreditCardExpire();
    }
    
    
    public Payment initialize(Payer.PaymentMethod method, PostalAddress selectedAddr, Cart cart) {        
        
        this.method = method;
        
        this.cart = cart;
        
        this.deliveryAddress = selectedAddr;
        
        this.checkProperties(method);
        
        payment = new Payment();
        
        payment.setIntent("sale");
        
        payment.setPayer(initPayer());
        
        payment.setTransactions(this.initTransactions());
        
        if(method == Payer.PaymentMethod.paypal){
            payment.setRedirectUrls(this.redirectUrls);
        }  
        
        return payment;
    }
    
   
    
     private Payer initPayer(){
         
        Payer payer = new Payer();
        
        payer.setPaymentMethod(this.method);
        
        if(method == Payer.PaymentMethod.credit_card)
             payer.setFundingInstruments(this.getFundingInstruments()); 
                 
              
        return payer;
    }
     
      private ArrayList<Transaction> initTransactions(){
          
         ArrayList<Transaction> transList = new ArrayList<>();
         
         Transaction t = new Transaction();
         
         t.setAmount(initTransAmount());
         
        // t.setAmount(this.initDebugAmount());
        
         t.setDescription("Sakila DVD Store Order");
         
         t.setItemList(this.initItemList());
         
         t.setPayee(this.initPayee()); //Not supported for method = paypal
        
         transList.add(t);
         
         return transList;
     }
      
      private Payee initPayee() {
          
           if (this.method.equals(Payer.PaymentMethod.paypal))
               return null;
           
           Payee payee = new Payee();
           
           payee.setEmail("dinahfoster07-facilitator@att.net");
           
           payee.setMerchantId("WVW89MDXXYKK6");
           
           return payee;
      }
     
      private ItemList initItemList(){
          
         ItemList itemList = new ItemList();
         
         ShippingAddress shippingAddress = new ShippingAddress();
         
         this.initAddress(shippingAddress, this.deliveryAddress);
         
         itemList.setShippingAddress(shippingAddress);
         
         itemList.setItems(this.initItems());
         
         return itemList;
     }
     
     private Amount initTransAmount(){
         
         Amount amt = new Amount();
         
         Details details = new Details();
         
         details.setFee("0.00");
         
         details.setShipping(this.formatPrice(cart.getShippingFee()));
         
         details.setTax(this.formatPrice(cart.getTaxAmount()));
         
         details.setSubtotal(this.formatPrice(cart.getSubtotal()));
         
         amt.setCurrency("USD");  
         
         amt.setTotal(this.formatPrice(cart.getGrandTotal()));
         
        // amt.setTotal("5.00");
         
         amt.setDetails(details);
         
         return amt;
     }
     
     private Amount initDebugAmount() {
         Amount amt = new Amount();
         Details details = new Details();
         details.setFee("0.00");
         details.setShipping("0.00");
         details.setTax("0.00");
         details.setSubtotal("0.00");
         amt.setCurrency("USD");         
         amt.setTotal("0.00");
        // amt.setTotal("5.00");
         amt.setDetails(details);
         return amt;
     }
     
      private ArrayList<Item> initItems(){
          
         ArrayList<Item> itemList = new ArrayList<>();
         
         for(CartItem it : cart.mapAsList()){
             itemList.add(initItem(it));
         }
         
         return itemList;
     }
     
     private Item initItem(CartItem cartItem){
         
         Item item = new Item();
         item.setCurrency("USD");
         item.setName(cartItem.getFilm().getTitle());
         item.setPrice(formatPrice(cartItem.getFilm().getRentalRate()));
         item.setQuantity(cartItem.getQty().toString());
         
         return item;
     }
    
     /*
      * Only derived ShipAddress has recipientName field
      */
     private void initAddress(Address addr, PostalAddress postalAddress){
        
        if(addr.getClass() == ShippingAddress.class)      
            ((ShippingAddress)addr).setRecipientName(postalAddress.getFirstName() + " " 
                + postalAddress.getLastName());
        
        addr.setCity(postalAddress.getAddressId().getCityId().getCityName());
        
        addr.setCountryCode("US");
        
        addr.setLine1(postalAddress.getAddressId().getAddress1());
        
        addr.setPhone(postalAddress.getAddressId().getPhone());
        
        addr.setPostalCode(postalAddress.getAddressId().getPostalCode());       
        
        addr.setState(postalAddress.getAddressId().getDistrict());
        
        addr.setType(Address.AddressType.residential);
        
      }
     
      private String formatPrice(BigDecimal value){
        
        String pattern = "#,##0.00";
        DecimalFormat dfmt = new DecimalFormat(pattern);
        String formatted = dfmt.format(value);
        return formatted;
    }
      
      private ArrayList<FundingInstrument> getFundingInstruments() {
          
         FundingInstrument funding = new FundingInstrument();
         
    	 funding.setCreditCard(this.initCreditCard(this.billingCustomer));
         
    	// funding.setCreditCard(this.card);
        
    	 ArrayList<FundingInstrument> fundingList = new ArrayList<>();
         
    	 fundingList.add(funding);
         
    	 return fundingList;
          
      }
      
      private CreditCard initCreditCard(Customer customer){
    	 
    	 CreditCard card = new CreditCard();
         
    	 Address billing = new Address();
         
    	 this.initAddress(billing, customer);
         
    	 card.setBillingAddress(billing);
         
    	 card.setCvv2(744);
         
    	 card.setExpireMonth(this.testExpireMonth);
         
    	 card.setExpireYear(this.testExpireYear);
         
    	 card.setFirstName(customer.getFirstName());
         
    	 card.setLastName(customer.getLastName());
         
    	 card.setNumber("4400665401379373");
    	
    	 card.setType(AcceptedCard.visa);
         
    	 return card;
      }	 
      
      private void checkProperties(Payer.PaymentMethod method) {
          
          String err = "";
          
          if(method == null)
              err = "Payer.PaymentMethod required" ;
          
          else {
              if(deliveryAddress == null || cart == null || cart.mapAsList().isEmpty())
                 err += "Customer or Cart is null or mapAsList is empty; " ;
              if (method.equals(Payer.PaymentMethod.paypal) && 
                      redirectUrls == null)
                  err += "PaymentMethod is paypal and RedirectUrls is null; ";
              if(method.equals(Payer.PaymentMethod.credit_card) && 
                      this.billingCustomer == null)
                  err += "PaymentMethod is credit_card and Customer Billing is null; " ;
          
          }
          if(!err.isEmpty())
              throw new IllegalArgumentException(
                      
                      EhrLogger.doError(this.getClass().getCanonicalName(), 
                         "checkProperties", err)
              );
          
      }//end check
      
      private void initCreditCardExpire() {
          
          GregorianCalendar cal = new GregorianCalendar();
          cal.roll(Calendar.MONTH, true);
          this.testExpireYear = cal.get(Calendar.YEAR);
          this.testExpireMonth = cal.get(Calendar.MONTH);
          
    
      }
    
}
