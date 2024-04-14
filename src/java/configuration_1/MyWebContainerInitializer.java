/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;


import javax.servlet.ServletRegistration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 *
 * @author dinah
 */
public class MyWebContainerInitializer extends 
        AbstractAnnotationConfigDispatcherServletInitializer {

    /*
     * Super creates AnnotationConfigWebApplicationContext
     * Calls into getRootConfigClasses
     * see base class ContextLoaderInitializer
    */
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        
        return super.createRootApplicationContext();
    }
    
    @Override
    protected Class<?>[] getRootConfigClasses() {
        
        return new Class[] {MyWebMvcConfigurationSupport.class, 
                ApplicationBeans.class,
                ExceptionResolverConfig.class,
                MvcBeans.class,
                TransactionConfig.class,
                ViewResolutionBeans.class};
        
    }

    /*
     * Super creates AnnotationConfigWebApplicationContext with no configuration
     * beans. See AbstractDispatcherServletInitializer
    */
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        
        return super.createServletApplicationContext();
    }
    
    @Override
    protected Class<?>[] getServletConfigClasses() {

        return null;
        
    }

    @Override
    protected String[] getServletMappings() {
        
        return new String[] {"/"};
       
    }
    /** ServletConfig **/
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic reg) {        
        
        String initParam = "throwExceptionIfNoHandlerFound";
        
        reg.setAsyncSupported(true);
        
        reg.setInitParameter(initParam, "true");
        
    }
    
}
