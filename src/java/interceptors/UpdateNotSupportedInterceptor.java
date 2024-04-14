/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interceptors;


import exceptions.PaymentStartedException;
import exceptions.PaymentStartedPaymentOnlyException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import view.attributes.PaymentAttributes;
import view.attributes.PaymentAttributes.TransactionState;

/*
 * Throws PaymentStartedException, PaymentStartedPaymentOnlyException for all
 * application URL's except those excluded, such as /customerSupport.* 
 *
 */

//@Component -- created in MVCConfigurationSupport
public class UpdateNotSupportedInterceptor extends HandlerInterceptorAdapter{
    
    
    private PaymentAttributes paymentAttrs;
    
   
    public void setPaymentAttrs(PaymentAttributes attribs) {
        this.paymentAttrs = attribs;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, 
            Object objHandler)  throws Exception {         
       
      System.out.println(this.getClass().getCanonicalName()
            + ": requestURI=" + request.getRequestURI());      
             
      TransactionState transState = paymentAttrs.evalTransactionState(request);
           
             switch(transState) {
               
               case PAYER_ID :
                   throw new PaymentStartedException();
                   
               case PAYMENT_ONLY :
                   throw new PaymentStartedPaymentOnlyException();
             }
            
     return true;
    } 
    
 

    
}
