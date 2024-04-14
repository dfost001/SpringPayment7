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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import view.attributes.ConstantUtil;
import view.attributes.PaymentAttributes;
import view.attributes.PaymentAttributes.TransactionState;


/**
 * 
 * In the case of a browser GET request for a POST method from history list/favorites)
 * MethodNotSupportedException will be thrown by RequestMappingHandlerMapping
 * If the request is POST for a GET method, a developer error is assumed, and the 
 * exception is passed to the DefaultExceptionResolver.
 * Note: UpdateNotSupportedInterceptor does not execute, so evalTransaction code
 * is also included here.
 * Returns either errTransactionStarted.jsp, errTransactionStartedPaymentOnly.jsp,
 * or errNavigation.jsp  
 * 
 */
public class MyMethodNotSupportedExceptionResolver extends AbstractHandlerExceptionResolver{
    
    private final String TITLE_KEY = "title" ;
    
    /*
     * Property set by Spring framework. Session-scoped proxy.
     */
    private PaymentAttributes paymentAttrs;
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrib) {
        this.paymentAttrs = attrib;
    }
    
    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        if (!HttpRequestMethodNotSupportedException.class.isAssignableFrom(ex.getClass())) 
            return;
        EhrLogger.logException(ex, request, this.getClass());
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
            Object o, Exception ex)  {               
        
        String url = request.getRequestURL().toString();
       
        String viewName = "";
        
        if (!HttpRequestMethodNotSupportedException.class.isAssignableFrom(ex.getClass())) 
            return null; 
        
        if(request.getMethod().equalsIgnoreCase("post"))
            return null; //incorrectly coded POST to GET handler goes to default handler
       
                
        viewName = this.evalTransactionState(request);  //Interceptor not invoked for MethodNotSupported    
              
        HttpSession session = request.getSession();
         
        Cart cart = (Cart)session.getAttribute("cart"); //cart message        
        
        if(cart == null)
                throw new IllegalArgumentException(
                this.getClass().getCanonicalName() +
                 ":"   + url +
                 ": cart in session is null" );
        
        ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());        
        
        mav.addObject("cart",cart);  
        
        mav.addObject("paymentAttributes", paymentAttrs); //link to authorize handler has a time parameter      
        
        return mav;
        
    }  
    
    private String evalTransactionState(HttpServletRequest request) {
        
        String view = "";       
        
         TransactionState transState = paymentAttrs.evalTransactionState(request);
         
           
           switch(transState) {
               
               case PAYER_ID :
                   view = ConstantUtil.ERR_TRANSACTION_STARTED_VIEW;                   
                   break;
               case PAYMENT_ONLY :
                   view = ConstantUtil.ERR_CANCEL_VIEW;
                   break;
               case NONE: 
                   view = ConstantUtil.ERR_NAVIGATION_VIEW;
                   break;
                   
           }
        
         System.out.println(this.getClass().getCanonicalName() + 
                 "#evalTransactionState: viewName=" + view +
                 " state=" + transState.name());   
           
        return view;   
    }
    
} // end class
