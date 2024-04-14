/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import error_util.EhrLogger;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import view.attributes.PaymentAttributes;

/**
 *
 * BUG: reset Payment attributes: Not replicable
 * Sometimes Exception thrown by server when network connection is stopped, and restarted. 
 * This resolver executes. Payment variables are reset. If authorize is attempted again,
 * /execute handler throws IllegalArgumentException: Payment object, payerId are null.
 * FIX: If requestUrl is /execute and type of exception is server's do not reset payment.
 *  
 */
public class MyDefaultHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    
    private PaymentAttributes paymentAttrs;
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrs){
        this.paymentAttrs = attrs;
    }
    
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        EhrLogger.logException(ex, request, this.getClass());
    }
    
     @Override
    public ModelAndView doResolveException(HttpServletRequest req,
        HttpServletResponse resp, Object handler, Exception ex) {   
  
     paymentAttrs.onPaymentError(this.getClass()); //Reset objects, update time
        
     String url = req.getRequestURL().toString();
    
     String viewName =  "error/springFrameworkError";  
     
     EhrLogger.printToConsole(this.getClass(), "doResolveException", "Executing request: "
             + url + " Exception=" + ex.getClass().getCanonicalName());       
     
     if(ex.getClass().equals(HttpMediaTypeNotAcceptableException.class))
           
           return super.doResolveException(req, resp, handler, ex);//throw to container, handled by <error-code> in web.xml     
     
     return EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());        
       
    }
    
}//
