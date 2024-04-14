/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import error_util.EhrLogger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import paypal.PayPalExecuteException;
import view.attributes.PaymentAttributes;
/**
 *
 * @author Dinah
 */

public class PayPalStateErrorMappingResolver extends AbstractHandlerExceptionResolver {
    
    private PaymentAttributes paymentAttrs;
    
    private final String viewName = "error/executeState";
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrs) {
        this.paymentAttrs = attrs;
    }    
   
  @Override
  protected ModelAndView doResolveException(HttpServletRequest req,
        HttpServletResponse resp, Object handler, Exception ex) {     
    
    
    if(!PayPalExecuteException.class.equals(ex.getClass()))
         return null; 
    
    paymentAttrs.onPaymentError(this.getClass()); //reset Payment etc. to null   
    
    String url = req.getRequestURL().toString();    
    
    ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName()); 
    
    return mav;
  }
  
  @Override
  protected void logException(Exception ex, HttpServletRequest request){
      
     if(!PayPalExecuteException.class.equals(ex.getClass()))
         return;
     EhrLogger.logException(ex, request, this.getClass());
      
  }  
    
} //end resolver
