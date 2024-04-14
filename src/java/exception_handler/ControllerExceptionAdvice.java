/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import error_util.EhrLogger;
import httpUtil.HttpClientException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler; 
import org.springframework.web.servlet.ModelAndView;
import view.attributes.PaymentAttributes;

/**
 * To do: Test if paymentAttrs is autowired
 */
@ControllerAdvice(basePackages={"com.dvd","com.dvd.redirect_control"})
public class ControllerExceptionAdvice {    
    
    @Autowired
    private PaymentAttributes paymentAttrs;
    
    @ExceptionHandler({RuntimeException.class})
    public ModelAndView handleException(Exception ex, HttpServletRequest req) {   
      
        paymentAttrs.onPaymentError(this.getClass());
        
        String url = req.getRequestURL().toString();        
        
        String viewName = "error/javaException2";
        
        this.logException(ex, req);      
        
        return EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());
       
    }
   /*
    * Requires testing: HttpClientException http = (HttpClientException) ex; 
    */ 
    private void logException(Exception ex, HttpServletRequest req) {
        
        Throwable cause = ex.getCause();
        
        String name = cause == null ? "null" : cause.getClass().getCanonicalName();
        
       /* EhrLogger.printToConsole(this.getClass(), "logException", 
                "Cause=" + name);*/
        
        if(cause != null && cause.getClass().equals(HttpClientException.class)) {            
                        
             HttpClientException http = (HttpClientException) cause; 
            
             EhrLogger.logHttpClientException(http, req, this.getClass());
             
            /* EhrLogger.printToConsole(this.getClass(), "logException", 
                "logHttpClient completed"); */
        } 
        
        else EhrLogger.logException(ex, req, this.getClass()); 
    }
    
    
}
