/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletContext;

import java.text.MessageFormat;
import javax.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;
//import org.springframework.web.context.annotation.ApplicationScope;

/**
 *
 * @author dinah
 */
@Component
@Scope("application")
public class DebugApplicationContext implements ApplicationContextAware {
    
    private AbstractApplicationContext context;
    private AbstractApplicationContext parent;
    private String logName;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        
        this.context = (AbstractApplicationContext) ac;
        logName = this.getClass().getCanonicalName();
        
    }
    
    @PostConstruct
    public void printContext() {
        
       getActive(); 
       getBeanNames((AbstractApplicationContext)context);
        
    }
    
    private void getActive() {
        
        boolean active = context.isActive();       
        
        parent = (AbstractApplicationContext) context.getParent();
        
        String message = MessageFormat.format("{0}#getActive: parent={1} active={2}"
                ,logName,parent != null,active);
        
       // System.out.println(logName + "#getActive: parent=" + parent != null);
       
       System.out.println(message);
        
        
    }
    
    private void getBeanNames(AbstractApplicationContext ctx) {
        
        String[] names = ctx.getBeanDefinitionNames();
        
        String line = "";
        
        System.out.println("DebugApplicationContext#getBeanNames: Registered Names");
        
        for(String n : names)
            line += n + System.getProperty("line.separator");
        
        System.out.println(line);
    }
}
