/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

/**
 *
 * @author dinah
 */

@Configuration
public  class ViewResolutionBeans  {
    
    @Autowired
    private ApplicationBeans appBeans;  
    
    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class); //Optional
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
    
    @Bean
    public MappingJackson2JsonView jacksonView() {
        
        String[] keys = {"url", "trace", "handler", "messages", 
            "exceptionName", "status", "recoverable"};
        
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        
        Set<String> modelKeys = new HashSet<>();
        
        modelKeys.addAll(Arrays.asList(keys));
        
        jsonView.setModelKeys(modelKeys);
        
        jsonView.setObjectMapper(appBeans.objectMapper());
        
        return jsonView;
    }
    
    @Bean
    public TilesViewResolver tilesViewResolver() {
        
        TilesViewResolver resolver = new TilesViewResolver();
        return resolver;
    }
    
    @Bean
    public TilesConfigurer tilesConfigurer() {
        
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        
        tilesConfigurer.setValidateDefinitions(true);
        
        tilesConfigurer.setDefinitions("/WEB-INF/tiles.xml");
        
        return tilesConfigurer;
    }
    
    
} //class
