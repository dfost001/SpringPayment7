/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;


import com.fasterxml.jackson.databind.ObjectMapper;
import formatter.CustomPropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import restAddressService.jackson.MyJacksonObjectMapper;
import view.attributes.PaymentAttributes;

/**
 *
 * @author dinah
 */
@Configuration
public class ApplicationBeans {
    
    @Bean
    @Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
    public PaymentAttributes paymentAttributes() {
        return new PaymentAttributes();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
       return new MyJacksonObjectMapper();
    }
    
    @Bean
    public static CustomPropertyEditorRegistrar customEditorRegistrar() {
        return new CustomPropertyEditorRegistrar();
    }
    
    @Bean
    public static CustomEditorConfigurer editorConfigurer() {
        
        CustomEditorConfigurer config = new CustomEditorConfigurer();
        
        config.setPropertyEditorRegistrars(
                new PropertyEditorRegistrar[]{customEditorRegistrar()});
        
        return config;
        
    }
    
}
