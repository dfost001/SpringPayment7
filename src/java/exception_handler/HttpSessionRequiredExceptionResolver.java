/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception_handler;

import com.cart.Cart;
import error_util.EhrLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import view.attributes.PaymentAttributes;
import constants.ExceptionResolverFilterConstants;
import view.attributes.ConstantUtil;

/**
 * 
 * If @ModelAttribute handler parameters are not in the session, Spring throws this exception.
 * The function of this resolver is to return user-friendly errNavigation.jsp instead
 * of an application error view. The exception message is evaluated to determine if
 * the cause of the exception is an @ModelAttribute key. If not, the exception is passed. 
 * 
 * ExceptionResolverFilterConstants is an interface that contains substrings that
 * will be contained in the HttpSessionRequiredException message.
 *  
 */
public class HttpSessionRequiredExceptionResolver 
        extends AbstractHandlerExceptionResolver implements ExceptionResolverFilterConstants {   
    
    /*
     * If a transaction has started, these attributes should never be null.
     * So this resolver throws a nested IllegalArgumentException if payment variables
     * have a value and model attributes are null.
     */
    private PaymentAttributes paymentAttrs;

    @Required
    public void setPaymentAttrs(PaymentAttributes paymentAttrs) {
        this.paymentAttrs = paymentAttrs;
    }  
    
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if(!HttpSessionRequiredException.class.isAssignableFrom(ex.getClass()))
            return;
        EhrLogger.logException(ex, request, this.getClass());
    }
    
   
    @Override
    protected ModelAndView doResolveException
        (HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {             
         
        
        if(!HttpSessionRequiredException.class.isAssignableFrom(ex.getClass()))
            return null;
        
        if(!this.isPaymentFlowAttribute(ex.getMessage())) 
            return null;        
        
        this.throwPaymentInitialized(ex, request);      

        this.throwCartNull(request);
        
        String url = request.getRequestURL().toString();   
        
        String viewName = ConstantUtil.ERR_NAVIGATION_VIEW;
        
        ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());        
     
        mav.addObject(ConstantUtil.CART, 
                (Cart) request.getSession().getAttribute(ConstantUtil.CART));        
        
        return mav;
        
    }
        /* 
         * Constants contain API error message strings
         */
        private boolean isPaymentFlowAttribute(String exceptionMessage) {
            
            if(exceptionMessage.contains(ExceptionResolverFilterConstants.CUSTOMER))
                return true;
            else if(exceptionMessage.contains(ExceptionResolverFilterConstants.SELECTED_SHIP_ADDRESS))
                return true;
            else if(exceptionMessage.contains(ExceptionResolverFilterConstants.BINDING_RESULT))
                return true;
            return false;
        }
        
        private void throwPaymentInitialized(Exception ex, HttpServletRequest request) {
            
           boolean testException = false; 
           
           String url = request.getRequestURL().toString();
            
            if (paymentAttrs.paymentInitialized() || testException) {

                paymentAttrs.onPaymentError(this.getClass()); //reset payment objects after evaluation

                String errMsg = EhrLogger.doError(this.getClass().getCanonicalName(),
                        "doResolveException", "Payment object is initialized and "
                        + " required session attributes are null for url " + url);
                
                paymentAttrs.onPaymentError(this.getClass());

                throw new IllegalArgumentException(errMsg, ex); //processed by error-page in web.xml

            }
            
        }
        
       private void throwCartNull(HttpServletRequest request) {

        HttpSession session = request.getSession();

        Cart cart = (Cart) session.getAttribute("cart");

        String url = request.getRequestURL().toString();

        if (cart == null) {
            throw new IllegalArgumentException(
                    this.getClass().getCanonicalName()
                    + ":" + url
                    + ": cart in session is null");
        }
    }
    
} //end resolver
