/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import view.attributes.CustomerAttributes;
import view.attributes.PaymentAttributes;

/**
 *
 * @author Dinah
 * Fix: need procedures in components that return a message
 */
@Component
public class CheckInitializationPostProcessor implements BeanPostProcessor{
    
    private PaymentAttributes paymentAttrs ;
    
    private CustomerAttributes customerAttrs;
   

    @Override
    public Object postProcessBeforeInitialization(Object o, String beanName) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String beanName) throws BeansException {        
        
       /* System.out.println(this.getClass().getCanonicalName() +
                ": " + beanName + " created.");    */     
        
        String msg = "";
        
        if(o.getClass().equals(PaymentAttributes.class)) {
            
             paymentAttrs = (PaymentAttributes)o;
             
             if((msg=paymentAttrs.invalidPaymentInitialization()) != null)
                 throwException(beanName, msg);
        }
        else if(o.getClass().equals(CustomerAttributes.class)) {
            
            customerAttrs = (CustomerAttributes)o;
            if(customerAttrs.isShipAddressSelected()) {
                msg = "Property 'CustomerAttributes#shipAddressSelected' incorrectly initialized to true.";
                throwException(beanName, msg);
            }                
        }
        
        return o;
    }
    
    private void throwException(String beanName, String message) {
        
        String err = this.getClass().getCanonicalName() + ":checkInitialzation"
                + " for " + beanName + ": " + message;
        throw new IllegalArgumentException(err);
    }
    
}
