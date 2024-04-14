/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 *
 * @author dinah
 */
@Configuration
public class MvcBeans {
    /* Supports JEE @PostConstruct/@PreDestroy as a replacement for implementing 
     * Spring's Initializing/DisposableBean interfaces
     */
    @Bean
    public CommonAnnotationBeanPostProcessor commonAnnotationPostProcessor() {
        
        return new CommonAnnotationBeanPostProcessor();
    }
    
    @Bean
    public MessageSource messageSource() {
        
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        
        source.setBasenames("classpath:messages/ValidationMessages", "classpath:messages/message");
        
        return source;
    }
    
    
    
}
