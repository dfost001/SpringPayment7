/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interceptors;


import error_util.EhrLogger;
import exceptions.PaymentTimeException;
import exceptions.PaymentStartedException;
import exceptions.PaymentStartedPaymentOnlyException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import view.attributes.PaymentAttributes;


/**
 *
 * @author dinah
 */
public class PayPalPaymentUrlInterceptor extends HandlerInterceptorAdapter{
    
    
    private PaymentAttributes paymentAttrs;
    
   
    public void setPaymentAttrs(PaymentAttributes attribs) {
        this.paymentAttrs = attribs;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws PaymentStartedException {      
        
        PaymentAttributes.TransactionState transState = null;
        
        if(paymentAttrs.isHistoryPayment(request)) {
            
            EhrLogger.printToConsole(this.getClass(), "prehandle","requestURI=" + request.getRequestURI() );
            
            transState = paymentAttrs.evalTransactionState(request);
             
            EhrLogger.printToConsole(this.getClass(), "prehandle","transState=" + transState.name());
           
             switch(transState) {
               
               case PAYER_ID :
                   throw new PaymentStartedException();
                   
               case PAYMENT_ONLY :
                   throw new PaymentStartedPaymentOnlyException();
                   
               case NONE:                    
                   throw new PaymentTimeException();
                   
             } //end switch
        } // end if       
        EhrLogger.printToConsole(this.getClass(), "prehandle","Passing request through. ");
        return true;
    }
    
}
