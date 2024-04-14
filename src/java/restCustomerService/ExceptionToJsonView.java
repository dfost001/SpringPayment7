/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restCustomerService;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import error_util.EhrLogger;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Dinah
 * 
 */
@ControllerAdvice(basePackageClasses={CustomerControllerREST.class})
public class ExceptionToJsonView  {
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        
        System.out.println("Inside ExceptionToJsonView#handleException");
       
        String url = request.getRequestURL().toString();
        String handler = this.getClass().getCanonicalName();
        String view = "";
        
        if(e.getClass().equals(HttpMediaTypeNotAcceptableException.class))
             return null; //throw to container, handled by <error-code> in web.xml  
        
        response.setStatus(500);
        
        response.setContentType("application/json");
        
        ModelAndView mav = EhrLogger.initErrorView(url, e, view, handler);
       
        
        return mav;
        
    }
    
   /* @ExceptionHandler(value={RuntimeException.class})
    
    public ResponseEntity<ErrorResponse> handleApplicationException(Exception e, HttpServletRequest request) {
        System.out.println("Inside @ControllerAdvice restCustomerService.ApplicationException");
        System.out.println("Accept=" + request.getHeader("Accept"));
        ErrorResponse response = this.initResponse(e, request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access-Control-Allow-Origin", request.getHeader("Origin"));
        headers.set("Content-Type", "application/json");
        ResponseEntity<ErrorResponse> entity = new ResponseEntity<ErrorResponse>(response,
                headers,HttpStatus.INTERNAL_SERVER_ERROR);
        return entity;
    }*/
    
   /* private ErrorResponse initResponse(Throwable t, HttpServletRequest request) {
        
        ErrorResponse response = new ErrorResponse();
        response.setHandler(this.getClass().getCanonicalName());
       // response.setUrl(request.getDescription(false));
        String trace = EhrLogger.getStackTrace(t, 
                request.getDescription(false), "<br/>"); 
        response.setUrl("URL");
        String trace = "Not available";
        response.setTrace(trace);
        response.setMessage(t.getMessage());
        return response;
    }*/
    
}
