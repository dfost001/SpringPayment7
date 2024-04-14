/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config_old;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author Dinah
 */
public class MyWebInitializer {

    //@Override
    public void onStartup(ServletContext sc) throws ServletException {
        
        XmlWebApplicationContext ctx = new XmlWebApplicationContext();
        
        ctx.setConfigLocation("WEB-INF/dispatcher-servlet.xml");
        
        DispatcherServlet dispatcher = new DispatcherServlet(ctx);
        
        dispatcher.setThrowExceptionIfNoHandlerFound(true);
        
        ServletRegistration.Dynamic reg = sc.addServlet("dispatcher", dispatcher);
        
        reg.addMapping("/");
        
        reg.setLoadOnStartup(1);
        
    }//
    
}
