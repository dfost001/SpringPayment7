/*
 * Note: Exception resolver will also handle REST request URL's
 * Model will be serialized by Jackson2JsonView as long as type accepted
 * by client is not ambiguous.
 * See ContentNegotiatingManagerFactoryBean in viewResolution.xml
 */
package exception_handler;

import error_util.EhrLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import view.attributes.PaymentAttributes;

/**
 *
 * @author Dinah
 *
 * 
 * Note:
 * REST endpoint error will be handled by Jackson2JsonView (see ContentNegotiatingViewResolver) 
 * recoverPath will be constructed for DataAccessException in AjaxErrorController.java
 */
public class DataExceptionResolver extends AbstractHandlerExceptionResolver{
    
    private PaymentAttributes paymentAttrs;
    
    @Required
    public void setPaymentAttrs(PaymentAttributes attrs){
        this.paymentAttrs = attrs;
    }
    
    public DataExceptionResolver(){
       // super.setWarnLogCategory(this.getClass().getCanonicalName());
    }
    
  @Override
  protected void logException(Exception ex, HttpServletRequest request){
      Throwable cause = EhrLogger.findCause(ex, DataAccessException.class);
      if(cause != null)
           EhrLogger.logException(ex, request, this.getClass());
  }
    /*
     * Search is required. If error on deployment, exception will be BeanInstantiationException.
     */
    @Override
    public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
                  Object o, Exception ex) {
        
        Throwable cause = EhrLogger.findCause(ex,DataAccessException.class);
        
        if(cause == null) {
            System.out.println("DataExceptionResolver#doResolve: cause=null");
            return null;
        }
        
        System.out.println("DataExceptionResolver#doResolve: cause=" + cause.getMessage());
        
        paymentAttrs.onPaymentError(this.getClass()); //open up application for another payment
         
        String url = request.getRequestURL().toString();
        
        String viewName = "error/dummyDb";        
        
        ModelAndView mav = EhrLogger.initErrorView(url, ex, viewName, this.getClass().getCanonicalName());   
        
         if(url.contains("/resources")){ //REST endpoint 
                
                viewName = "";
                
                this.reviseRecoverableExceptionAttribute(mav, cause); //set recoverable, simplify name
                
                response.setStatus(500);   //required for JavaScript XHR client   
                
                return mav;
         }
               
        if(RecoverableDataAccessException.class.isAssignableFrom(cause.getClass())
                 || TransientDataAccessException.class.isAssignableFrom(cause.getClass())) {
            
            String recoverPath = this.getRecoverPath(request, url);
            
            mav.addObject("recoverPath", recoverPath); //see dummyDb.jsp for usage       
           
        } 
        
        return mav;
    } 
   
    private String getRecoverPath(HttpServletRequest request, String url){    
       
        if(request.getServletPath().equals("/")
                || request.getServletPath().contains("spring")) { //Error on index view
            url = "/spring/homeOnDbError";
            return url;
        }
        
        url  = request.getQueryString() == null ? url : 
                url + "?" + request.getQueryString();          
        
        String method = request.getMethod();
          
        String recoverPath = method.equalsIgnoreCase("post") ? "/home" : url;          
        
        return recoverPath;
    }
    
    private void reviseRecoverableExceptionAttribute(ModelAndView mav, Throwable ex) {
        
        if (RecoverableDataAccessException.class.isAssignableFrom(ex.getClass())) {
                mav.getModel().put("exceptionName", "RecoverableDataAccessException");
                mav.getModel().put("recoverable", true);
        }
        else if(TransientDataAccessException.class.isAssignableFrom(ex.getClass())){
            mav.getModel().put("exceptionName", "TransientDataAccessException");
            mav.getModel().put("recoverable", true);
        }
    }
    
} //end class
