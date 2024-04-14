/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import com.cart.Cart;
import error_util.EhrLogger;
import exceptions.SelectedShipAddressCompareException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import validation.CompareAddressUtil2;
import view.attributes.PaymentAttributes;

/**
 * Handles: see configuration_1.ExceptionResolverConfig.java
 */
public class PaymentAccessExceptionResolver 
        extends SimpleMappingExceptionResolver {   
    
    private PaymentAttributes paymentAttrs;
    
    private List<Class<?>> exceptionList;

    @Required
    public void setExceptionList(List<Class<?>> exceptionMapping) {
        this.exceptionList = exceptionMapping;
    } 
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrs) {
        this.paymentAttrs = attrs;
    }
    
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if(!exceptionList.contains(ex.getClass()))
           return;
        EhrLogger.logException(ex, request, this.getClass());
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, 
            HttpServletResponse response, Object o, Exception ex) {           
       
       if(!exceptionList.contains(ex.getClass()))
           return null;                 
        
        ModelAndView mav = super.doResolveException(request, response, o, ex);       
        
        String viewName = mav.getViewName();        
        
        String url = request.getRequestURL().toString();       
    
        mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());       
        
        HttpSession session = request.getSession();
        
        Cart cart = (Cart)session.getAttribute("cart");          
       
       // Cart should never be null
       // nested exception thrown up to container
        if(cart == null)
                throw new IllegalArgumentException(
                this.getClass().getCanonicalName() +
                 ":"   + url +
                 ": cart in session is null" );
     
        mav.addObject("cart",cart);   
        
        if(ex.getClass().equals(SelectedShipAddressCompareException.class))
            session.setAttribute(CompareAddressUtil2.COMPARE_EXCEPTION, ex);           
       
        mav.addObject("paymentAttributes", paymentAttrs);  // paymentAttrs#paymentTime on link to authorize view    
        
        mav.addObject("title", ex.getClass().getSimpleName());
        
        return mav;
        
    }   
    
   /* private boolean handleMissingParameterException(Exception ex) {
        
        if(ex.getMessage().contains(ExceptionResolverFilterConstants.CUSTOMER_ID_REQUIRED))
            return true;
        return false;
    }*/
    
}
