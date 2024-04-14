/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error_util;

import exception_handler.LoggerResource;
import httpUtil.HttpClientException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Dinah
 */
public class EhrLogger {
    
    public static void printToConsole(Class<?> clazz, String method, String message){
        
        String line = MessageFormat.format("{0}#{1}: {2}", 
                clazz.getCanonicalName(), method, message);
        
        System.out.println(line);
    }  
    
    public static String getStackTrace(Throwable t, String description, String delim){
        
        String trace = description + delim;
        
        for(StackTraceElement el : t.getStackTrace()){
            String line = MessageFormat.format("{0}.{1} ({2}:{3})", 
                    el.getClassName(),el.getMethodName(),el.getFileName(),el.getLineNumber());
            trace += line + delim;
        }
        
        return trace;
        
    }
    
    public static String getMessages(Throwable throwable, String delim) {
        
        Throwable t = throwable;
        
        String msg = "";
        
        while(t != null) {
           
            String line = MessageFormat.format("{0} -> {1}", 
                    t.getClass().getCanonicalName(), t.getMessage());
            
            msg += line + delim;
            t = t.getCause();
        }
        
        return msg;
        
    }
    
    public static Throwable findCause(Exception ex, Class<?> search) {
        
        Throwable hold = ex;
        
        boolean found = false;
        
        while(hold != null) {
            
            if(search.isAssignableFrom(hold.getClass())) {
                found = true;
                break;
            }
            hold = hold.getCause();
        }
        if(found)
            return hold;
        
        return null;    
    }
    
    /*
     * Done: Add an attribute 'className' for REST client
     */
    public static ModelAndView initErrorView(String url, Exception e, 
            String view, String handler) {
        
        ModelAndView mav = new ModelAndView();
        
        mav.setViewName(view);
        
        mav.addObject("url", url);
        mav.addObject("exception", (Throwable)e);
        mav.addObject("trace", getStackTrace(e, url, "<br/>"));
        mav.addObject("messages", getMessages(e,"<br/>"));
        mav.addObject("handler", handler);
        mav.addObject("exceptionName", e.getClass().getCanonicalName());
        
        return mav;
    }
    
    /*public static String genInaccessibleTitle(HttpServletRequest request) {
        
        String uri = request.getRequestURI(); //full path
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length() + 1); //index out of bounds if equal length
        path += "(inaccessible)";
        return path;
        
    }*/
   
    public static String doError(String handler, String method, String message) {
        
        String err = " " + handler + "#" + method + ": " + message + " ";
        return err;
    }
    
    public static void throwIllegalArg(String handler, String method, String message){
        
        String err = EhrLogger.doError(handler, method, message);
        
        throw new IllegalArgumentException(err);        
    }
    
    public static void throwIllegalArg(String handler, String method, 
            String message, Throwable cause) {
        
        String err = EhrLogger.doError(handler, method, message);
        
        throw new IllegalArgumentException(err, cause);  
        
    }
    
   public static void logException(Exception ex, HttpServletRequest request, Class<?> cls){       
       
    Logger myLogger = LoggerResource.produceLogger(cls.getCanonicalName());  
    
    System.out.println("EhrLogger#logException executing: exception=" + ex.getClass().getCanonicalName());
      
    String lineSep = System.getProperty("line.separator") ; 
      
    String url = request.getRequestURL().toString();
    
   // String trace = EhrLogger.getStackTrace(ex, url, lineSep) ;
    
    String messages = EhrLogger.getMessages(ex, lineSep);    
    
    myLogger.info(MessageFormat.format("Invoking class: {0} for request URL {1}", 
            cls.getCanonicalName(), url));
    
    myLogger.log(Level.INFO,messages);
    
    LoggerResource.flush(myLogger);
      
  } 
   
   public static void logHttpClientException(HttpClientException ex, 
           HttpServletRequest req, Class<?> cls) {
       
       
       String debug = ex == null ? "null" : ex.getClass().getCanonicalName();
       
       System.out.println("EhrLogger#logHttpClientException: exception=" + debug);
       
       String lineSep = System.getProperty("line.separator") ; 
       Logger logger = LoggerResource.produceLogger(cls.getCanonicalName());
       
       String line = MessageFormat.format("Handler: {0}", cls.getCanonicalName())
               + lineSep;
       
       line += MessageFormat.format("Response Code: {0}", ex.getResponseCode())
               + lineSep;       
       
       line += MessageFormat.format("Text: {0}", ex.getTextMessage())
               + lineSep;
       
       line += MessageFormat.format("Message: {0}", ex.getMessage())
               + lineSep;  
       
       line += MessageFormat.format("Cause: {0}", ex.getCause())
               + lineSep; 
       
       line += ex.getDebug();
       
       logger.info(line);     
       
       LoggerResource.flush(logger);
       
   }
    
    
}
