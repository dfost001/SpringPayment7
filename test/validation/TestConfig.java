/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import configuration_1.TransactionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author dinah
 * We will instantiate UiEvalUtil to eliminate the scan of MVC scoped beans in the validation package
 */
@Configuration
@Import(TransactionConfig.class)
@ComponentScan(basePackages={"dao", "model.customer"})

public class TestConfig {
    
    @Bean
    public UiEvalUtil uiEvalUtil() {
        
        return new UiEvalUtil();
    }
    
    
}
