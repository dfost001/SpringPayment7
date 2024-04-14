/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config_old;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;
//import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

/**
 *
 * version 4.0 does not have method createDispatcherServlet
 */
public class MyDispatcherServletInitializer {
        
        

    //@Override
    protected WebApplicationContext createServletApplicationContext() {
        
        XmlWebApplicationContext ctx = new XmlWebApplicationContext();
        ctx.setConfigLocation("/WEB-INF/dispatcher-servlet.xml");        
        return ctx;
    }

    //@Override
    protected String[] getServletMappings() {
       return new String[] {"/"} ;     
    }

    //@Override
    protected WebApplicationContext createRootApplicationContext() {
       return null;
    }
    
    
    protected FrameworkServlet createDispatcherServlet(WebApplicationContext ctx) {
        
        DispatcherServlet dispatcher = new DispatcherServlet(ctx);
        
        dispatcher.setThrowExceptionIfNoHandlerFound(true);
        
        return (FrameworkServlet)dispatcher;
                
                
    }
    
    
}
