/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import error_util.EhrLogger;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import httpUtil.HttpException;

/**
 *
 * @author Dinah
 * Note: view can be initialized to empty, or any value, since
 * view resolution depends on content acceptable by client
 * See ContentNegotiatingManagerFactoryBean in viewResolution.xml
 * 
 * To do: logError
 */
@ControllerAdvice(basePackageClasses={AddressControllerRest.class})
public class AddressServiceAdvice  {
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        
        System.out.println("Inside AddressServiceAdvice#handleException");  
        
       
        String url = request.getRequestURL().toString();
        String handler = this.getClass().getCanonicalName();        
       
       String view = "";
       
       /*if(e.getClass().equals(HttpMediaTypeNotAcceptableException.class))
             return null; //throw to container, handled by <error-code> in web.xml  */
        
        response.setStatus(500);
        
        response.setContentType("application/json");
        
        ModelAndView mav = EhrLogger.initErrorView(url, e, view, handler);  
        
        addRecoverableAndLog(e, request, mav);
        
        if(HttpException.class.isAssignableFrom(e.getClass())){
            HttpException httpEx = (HttpException)e;
            mav.addObject("status", httpEx.getResponseCode().toString());   
            
        }   
       
        return mav;       
    }

    private void addRecoverableAndLog(Exception e, HttpServletRequest request,
            ModelAndView mav) {
        
         if(HttpClientException.class.isAssignableFrom(e.getClass())) {
            
            HttpClientException httpEx = (HttpClientException)e;
            EhrLogger.logHttpClientException(httpEx, request, this.getClass());
            
        } else if(HttpConnectException.class.isAssignableFrom(e.getClass())){
            
            HttpConnectException httpEx = (HttpConnectException)e;
            mav.addObject("recoverable", httpEx.getRecoverable());
            EhrLogger.logException(e, request, this.getClass());
        }
        else EhrLogger.logException(e, request, this.getClass());
        
    }    
   
}
