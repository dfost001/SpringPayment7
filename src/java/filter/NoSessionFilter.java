/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import exceptions.ClientNoSessionException;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Dinah
 */
//@WebFilter(urlPatterns={"/*"})
public class NoSessionFilter  implements Filter{

    @Override
    public void init(FilterConfig fc) throws ServletException {
       
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        
         HttpServletResponse resp = (HttpServletResponse) response;
          
         HttpServletRequest requ = (HttpServletRequest) request;
          
         evalErrNavigation(requ);
                 
         fc.doFilter(request, response);
         
        // System.out.println("Leaving filter NoSessionFilter");
        
        
    }

    @Override
    public void destroy() {
        
    }
    
      private void evalErrNavigation(HttpServletRequest request) {
        
        String message = "";
        
        String servletPath = request.getServletPath();
        
        String url = request.getRequestURL().toString();  
        
        if(url.contains("resources"))
            return; //static resources 
       
        if(servletPath.equals("/") 
                || servletPath.contains("spring") //pagination url's
                || servletPath.contains("home"))                         
            return ;
        if(servletPath.contains("product") && 
                !servletPath.substring(servletPath.lastIndexOf("/") + 1).equals("update"))
            return;
        if(servletPath.contains("verifyAddress")) //testing Ajax endpoint from non-browser client
            return;
        if(servletPath.contains("deserialize")) //testing Ajax endpoint from non-browser client
            return;
        if(firstRequest(request)) {
            message = "Welcome to our store.";           
            request.setAttribute("url", url);
            request.setAttribute("message", message);
            throw new ClientNoSessionException("Request for session dependent data.");   
        }
    }
     private  boolean firstRequest(HttpServletRequest request) {
        
        String query = request.getQueryString();
        
        if(query != null && !query.trim().isEmpty() && query.contains("jsessionid"))
            return false;
        
        boolean first = true;
        Cookie[] cookies = request.getCookies();
        if(cookies == null)
            return true;
        for(Cookie c : request.getCookies()) {
            if(c.getName().equalsIgnoreCase("jsessionid")) {
                first = false;
                break;
            }
        }
        
        return first;
    }
    
    
}
