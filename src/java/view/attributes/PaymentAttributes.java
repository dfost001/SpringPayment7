/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.attributes;

import com.cart.Cart;
import com.dvd.ShippingAddressController;
import error_util.EhrLogger;
import exceptions.ConfirmCartException;
import exceptions.ReceiptCartNotEmptyException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import model.customer.Customer;
import model.customer.CustomerOrder;
import model.customer.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.RedirectView;
import paypal.OrderAttributesDebug;
import paypal.PayPalPayment;
import validation.PaymentAttributesValidator;


/**
 * PaymentAttributes is defined in applicationBeans.xml with session-scope
 * and as aop:scoped-proxy, so that it can be accessed from interceptor, and
 * error handlers.
 * 
 * To do: Add RequestingUrl as a parameter to methods invoked from payment flow controllers
 */

public class PaymentAttributes implements Serializable{
    
   @Autowired
   private PayPalPayment paypalPayment;
   
   @Autowired
   private PaymentAttributesValidator attrsValidator;
   
   public enum TransactionState {
       
       PAYER_ID,
       PAYMENT_ONLY,
       NONE
   }
   
   public static final String PaymentTimeKey = "paymentTime";
   
   /* 
    *  Used on errTransactionStarted.jsp as param name on link to /paypalAuthorizeRedirect" 
    */
   private final String timeKeyEL = "paymentTime";
   
  // private Long paymentTime; 
   
   private String paymentTime;
   
   public PaymentAttributes() {    
       /* Bug: Comparisons not meaningful when obtained under different JVM instances
        * For instance, a book-marked URL and the running application if redeployed.
        * 
        */
      // paymentTime = new Long(System.nanoTime()).toString();       
       paymentTime = new Long(System.currentTimeMillis()).toString();
   }
  
  /* EL accessor */ 
  public String getTimeKeyEL() {
      return this.timeKeyEL;
   } 
  
  /*
   * EL accesssor
   */
   public String getPaymentTime() {
        return paymentTime;
    } 
    
   
    
    public void updatePaymentTime() {
          paymentTime = new Long(System.currentTimeMillis()).toString();
    }
    
    public boolean isHistoryPayment(HttpServletRequest request) {
        
         String time = request.getParameter(PaymentTimeKey);
         
         String err = "";
         
         Long lngParamTime = null;
        
        if(time == null || time.trim().isEmpty())
            throw new IllegalArgumentException("Missing parameter 'paymentTime'");
        
        try {
            
            lngParamTime = new Long(time);
            
        } catch(NumberFormatException e) {
            
            err =  EhrLogger.doError(this.getClass().getCanonicalName(), "isHistoryPayment", 
                            "paymentTime request parameter cannot be converted to long: "
                             + time + ": " + e.getMessage());
            
            throw new IllegalArgumentException(err);           
            
        }
        
        Long sysTime = new Long(this.paymentTime);
        
        System.out.println("PaymentAttributes#isHistoryPayment: sysTime=" + sysTime
                + " paramTime=" + lngParamTime);
        
        if(lngParamTime.compareTo(sysTime) > 0){
             err =  EhrLogger.doError(this.getClass().getCanonicalName(), "isHistoryPayment", 
                            "Request parameter time is greater than the system time: "
                + "sysTime=" + sysTime
                + " paramTime=" + lngParamTime); 
            
            throw new IllegalArgumentException(err);   
        }
       
        
        if(lngParamTime < sysTime)
            return true;
        
        return false;
    }  
    
   
    /*
     * Invoked from PayPalRedirectController#redirectToAuthorize, 
    */
    public void invalidPaymentStartedState(
            PostalAddress selectedAddr, Cart cart, Customer customer, BindingResult result)
        throws IllegalStateException {        
        
        boolean test = false;
        
        if(test)
            throw new IllegalStateException(this.getClass().getCanonicalName() 
                    + "#invalidPaymentStartedState: "
                    + "Testing @ControllerAdvice component for invalidPaymentStartedState");
        
        String msg = attrsValidator.validatePaymentStartedWithPayerId(
                cart, selectedAddr, customer, result);
        
        if(!msg.startsWith(PaymentAttributesValidator.ValidatorKey.With_PayerId.name()))            
            throw new IllegalStateException(msg);       
        
    }

    /*
     * 
     * To do: Better evaluation of CustomerOrder, OrderAttributes -
     * Normally, the Persistence provider will throw constraint errors
     * Ensure that delivery address is in Attributes, Customer assigned
     * to Order
     */
    public void invalidPaymentCompletedState(CustomerOrder order, 
            OrderAttributesDebug orderAttrib,
            Cart cart) throws IllegalStateException, ReceiptCartNotEmptyException {        
       
        
        String holdCartMsg = "";
        String msg = "";
        if(cart == null)
            msg = "Cart in the session is null";
        else if(!cart.mapAsList().isEmpty())
            holdCartMsg = "Cart is not empty; ";
        
        if(order == null)
            msg += "CustomerOrder is null; ";        
        if(orderAttrib == null)
            msg += "OrderAttributesDebug is null; ";
        if(this.paypalPayment.getPayment() != null)
            msg += "Payment object is not null; ";
        if(this.paypalPayment.getPayerId() != null)
            msg += "payerId is not null; ";
        
        
        if(msg.length() > 0) {
            msg += holdCartMsg;
            throw new IllegalStateException(msg);
        }
        else if(!holdCartMsg.isEmpty())
            throw new ReceiptCartNotEmptyException (
                "Payment attribute state is valid except cart has items. " +
                "Order is in session, Payment object reset to null, " +
                "payerId reset to null.") ;       
        
    }        
     
     private void removeTransaction() {
         this.paypalPayment.setPayment(null);
         this.paypalPayment.setPayerId(null);
         this.paypalPayment.setTokenResponse(null);
     }
     
     /*
      * 
      * Invoked from PayPalCancelUrlController#cancelPayPal()
      */
     public void resetPayPalCancelUrl(){
         this.removeTransaction();
         this.updatePaymentTime();
     }
     /*
      * Invoked from PayPalExecuteController#cancelAuthorize
      */
     public void payPalCancelAuthorize() {
         this.removeTransaction();
         this.updatePaymentTime();
     }
     public void updateTimeResetOnSupportEntrance() {
         this.removeTransaction();
         this.updatePaymentTime();
     }
      /*
       * To do: error-check the resolver resetting the transaction
       */
      public void onPaymentError(Class<?> clsExceptionResolver) {
          
            this.removeTransaction();
            this.updatePaymentTime();
     }
     
     public RedirectView payPalApprovalUrlWithTime(HttpServletRequest request, String targetUrl, ModelMap model) {
         
         if(isHistoryPayment(request)) //PayPalUrlInterceptor failed to trap error
                 throw new IllegalArgumentException(
                         EhrLogger.doError(this.getClass().getCanonicalName(), "payPalApprovalUrl", 
                                 "PayPal approval URL requested with an out-dated time parameter"));
         
        RedirectView view = initializeRedirectViewWithTime (request,targetUrl,model);
        
        return view; 
     }  
     
     /*
      * Invoked from PayPalExecuteController#executeTransaction before redirecting
      * to receipt handler
      */
     public RedirectView payPalExecuteSuccessWithTime(HttpServletRequest request, String targetUrl) {
         this.removeTransaction();
         this.updatePaymentTime();
         RedirectView view = initializeRedirectViewWithTime(request, targetUrl, new ModelMap());
         return view;
     }  
     
     public RedirectView initializeRedirectViewWithTime(HttpServletRequest request, String targetUrl, ModelMap model) {
         
         String target = makeAbsoluteRedirectUrl(request, targetUrl);         
         
         model.addAttribute(PaymentTimeKey, this.paymentTime);
         
         RedirectView view = new RedirectView(target);         
       
         view.setContextRelative(true); //relative to application not server root
         view.setExposeModelAttributes(true);
         view.setAttributesMap(model);
         return view;
         
     }    
  
     /* 
      * Invoked by 
      * UpdatedNotSupportedInterceptor/PayPalUrlInterceptor/MyMethodNotSupportedExceptionResolver
      */
     public TransactionState evalTransactionState(HttpServletRequest request) {
         
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        PostalAddress address = 
           (PostalAddress)request.getSession().getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        Customer customer = 
            (Customer)request.getSession().getAttribute("customer"); 
        BindingResult result =
            (BindingResult) request.getSession().getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);
         
        String errMsg = 
           this.attrsValidator.validatePaymentStartedWithPayerId(cart, address, customer, result);
        
        if(errMsg.startsWith(PaymentAttributesValidator.ValidatorKey.With_PayerId.name())) {            
            return TransactionState.PAYER_ID;
            
        } else if(errMsg.startsWith(PaymentAttributesValidator.ValidatorKey.Payment_Only.name())) {             
                return TransactionState.PAYMENT_ONLY;
                
        } else if (!errMsg.startsWith(PaymentAttributesValidator.ValidatorKey.Trans_None.name())) {   
            
            EhrLogger.throwIllegalArg(this.getClass().getCanonicalName(), "evalTransactionState", 
                    "Unknown key returned by PaymentAttributesValidator.");
                
        }  
        return TransactionState.NONE;
     }
    
     public TransactionState evalTransactionState2(HttpServletRequest request) {
         
        Cart cart = (Cart)request.getSession().getAttribute("cart");
        PostalAddress address = 
           (PostalAddress)request.getSession().getAttribute(ShippingAddressController.SELECTED_POSTALADDRESS);
        Customer customer = 
            (Customer)request.getSession().getAttribute("customer"); 
        BindingResult result =
            (BindingResult) request.getSession().getAttribute(ConstantUtil.CUST_BINDINGRESULT_KEY);
         
        String errMsg = 
           this.attrsValidator.validatePaymentStartedWithPayerId(cart, address, customer, result);
        
        if(errMsg.isEmpty()) {
            
            return TransactionState.PAYER_ID;
            
        } else {
            
            String err = attrsValidator.validatePaymentStartedPaymentOnly(cart, address, customer, result);
            
            if(err.isEmpty())
                return TransactionState.PAYMENT_ONLY;
            
            else return TransactionState.NONE;
        }      
     }
     /*
      * Invoked by HttpSessionRequired exception resolver to
      * determine whether to handle the exception or throw
      * a nested IllegalArgumentException.
      */
     public boolean paymentInitialized() {
         if(this.paypalPayment.getPayment() == null
                 && this.paypalPayment.getPayerId() == null)
             return false;
         return true;
     }
     
     /*
      * Invoked from CustomerRequestController in case end-user ignores browser
      * warning after emptying the cart.
      */
      public void evalCartEmpty(Cart cart, Class callerClass, String callerMethod) 
              throws IllegalArgumentException, ConfirmCartException {
          
               
        String friendly = "Please add items to your cart."  ;
        
        String technical = "Cart map has no items. Client may have refreshed" +
                " customer form POST request.";
        
        if(cart == null)
            throw new IllegalArgumentException(EhrLogger.doError(callerClass.getCanonicalName(),
                    callerMethod, "Cart is null"));
        
        if(cart.getItems().isEmpty()) {
            technical = EhrLogger.doError(callerClass.getCanonicalName(),
                    callerMethod, technical);
            throw new ConfirmCartException(technical,friendly );
        }
        
    } 
     
    public void throwEmptyCart(Cart cart, Class callerClass, String callerMethod) 
         throws IllegalArgumentException {
        
        String err = "";
        if(cart == null)
             err = "Cart component is null";
        else if(cart.getItems() == null || 
                cart.getItems().isEmpty())
             err = "Cart Map is null or empty";
        if(!err.isEmpty())
            throw new IllegalArgumentException(EhrLogger.doError(callerClass.getCanonicalName(),
                    callerMethod, err));
    }
    
     /*
      * Invoked from:  
      * servletContext.CheckInitializationPostProcessor, 
      * validation.CustomerAttrsValidator#evalConfirmCartException
      */
     public String invalidPaymentInitialization() {
         
         String msg = "";         
        
        if(paypalPayment.getPayment() != null)
             msg+= "pp_payment/Payment object is not null; ";
        if(paypalPayment.getPayerId() != null)
             msg += "paypal/PayPalPayment#payerId is not null";
       
        if(!msg.isEmpty())
                return msg;
        return null;
     }
   
     public static String makeAbsoluteRedirectUrl(HttpServletRequest request, String targetUrl){
         
         String target = targetUrl.trim();
         
         if(!target.startsWith("/"))
             target = "/" + target;
         
         String scheme = request.getScheme();
         String contextRoot = request.getContextPath();
         int port = request.getServerPort();
         String server = request.getServerName();
         
         String url = scheme + "://" + server + ":" + port 
                 + contextRoot + target;
         
         return url;
     }
    
}
