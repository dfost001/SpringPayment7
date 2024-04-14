/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Dinah
 */
//@WebFilter(urlPatterns={"/*"})
public class CacheControlFilter implements Filter{
    
   // private FilterConfig filterConfig = null;
    private Logger logger = null;
    private FileHandler fh = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
         //this.filterConfig = filterConfig;  
         //this.initLogger();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse resp = (HttpServletResponse) response;
          
        HttpServletRequest requ = (HttpServletRequest) request;        
         
        if(this.setCacheControl(requ)) { 
         
           resp.setHeader("Cache-Control", "no-cache,no-store");
           resp.setHeader("Pragma", "no-cache");
           resp.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
           
          // System.out.println("Leaving CacheControl#doFilter with cache-control set:" + requ.getRequestURI());
         }      
          
          chain.doFilter(request,response);            
                     
            /*resp.setHeader("Access-Control-Allow-Headers", "content-type,x-pingother");
            resp.setHeader("Access-Control-Allow-Origin", requ.getHeader("Origin"));
            resp.setHeader("Access-Control-Allow-Methods", "OPTIONS,POST");
            resp.setHeader("Access-Control-Max-Age", "3600");
            resp.setHeader("Content-Type", "text/plain");*/             
            
            // this.logResponseHeaders(resp);         
           
    }

    @Override
    public void destroy() {
         //this.filterConfig = null;    
         if(fh != null) {
            fh.flush();
            fh.close();
         }
    }
    
    private boolean setCacheControl(HttpServletRequest request) {
        
      if(request.getRequestURL().toString().contains("resources/javascript/ajax.js"))
           return true;
      else if(request.getRequestURL().toString().contains("resources/javascript/doLists.js"))
           return true;
      else if(request.getRequestURL().toString().contains("resources/css"))
           return true;
      else if(request.getRequestURL().toString().contains("resources"))
           return false;   //A Rest endpoint     
      
      return true; 
    
    //return false;
      
    } //end setCache
    
  
    
     private void initLogger() {
        
        logger = Logger.getLogger("filter.CacheControlFilter");
        
        
        try{
        fh = new FileHandler("C:\\TEMP\\logs\\SpringDb3\\CacheControlLogger.txt", true);
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        }
        catch(Exception ex){
            System.out.println("Error creating logger:" + ex.getMessage());
            fh.close();
        }
    }//end init 
    
     private void logResponseHeaders(HttpServletResponse resp){
        Collection<String> keys = resp.getHeaderNames();
        String line = "";
        for(String k : keys){
            line += MessageFormat.format("name = {0} value = {1}", k, resp.getHeader(k));
            line += "\r\n";
        }
       
        logger.info(line);
    }
     
     private void logRequestHeaders(HttpServletRequest request) {
         Enumeration<String> keys = request.getHeaderNames();
         String line = "";
        while(keys.hasMoreElements()){
            String key = keys.nextElement();
            line += MessageFormat.format("name = {0} value = {1}", key, request.getHeader(key));
            line += "\r\n";
        }
       
        logger.info(line);
     }
     
     private void logPaths(HttpServletRequest request){
         String uri = request.getRequestURI();
         String url = request.getRequestURL().toString();
         String servletPath = request.getServletPath();
         String line = MessageFormat.format("URI={0} URL={1} Path={2}", uri,url,servletPath);
         logger.info(line);
         
     }
    
}
