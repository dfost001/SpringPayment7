/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;


import exception_handler.DataExceptionResolver;
import exception_handler.HttpSessionRequiredExceptionResolver;
import exception_handler.MyDefaultHandlerExceptionResolver;
import exception_handler.MyMethodNotSupportedExceptionResolver;
import exception_handler.PayPalHttpExceptionMappingResolver;
import exception_handler.PayPalStateErrorMappingResolver;
import exception_handler.PaymentAccessExceptionResolver;
import exceptions.ConfirmCartException;
import exceptions.ExpiredLoginRequest;
import exceptions.NonCurrentUpdateRequest;
//import exceptions.LoginIdChangedException;
import exceptions.PaymentStartedException;
import exceptions.PaymentStartedPaymentOnlyException;
import exceptions.PaymentTimeException;
import exceptions.ReceiptCartNotEmptyException;
import exceptions.SelectedShipAddressCompareException;
import httpUtil.HttpClientException;
import httpUtil.HttpConnectException;
import java.util.Arrays;
import java.util.HashMap;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;


/**
 *
 * @author dinah
 */
@Configuration
public class ExceptionResolverConfig {
    
    @Autowired ApplicationBeans appBeans;
    
    @Bean
    public PayPalHttpExceptionMappingResolver payPalHttpExceptionResolver() {
        
        PayPalHttpExceptionMappingResolver resolver = new PayPalHttpExceptionMappingResolver();
        
        HashMap<Class<?>, String> viewMap = new HashMap<>();
        
        viewMap.put(HttpClientException.class, "error/paypalError");
        viewMap.put(HttpConnectException.class, "error/connectionError");
        
        resolver.setViewMapping(viewMap);
        
        resolver.setPaymentAttrs(appBeans.paymentAttributes());       
        
        resolver.setWarnLogCategory(PayPalHttpExceptionMappingResolver.class.getCanonicalName());
        
        return resolver;
        
    } 
    @Bean
    public PayPalStateErrorMappingResolver payPalStateExceptionResolver() {
        
        PayPalStateErrorMappingResolver resolver = 
                new PayPalStateErrorMappingResolver();      
        
        resolver.setPaymentAttrs(appBeans.paymentAttributes());     
        
        return resolver;
    }
    
    @Bean
    public PaymentAccessExceptionResolver paymentAccessExceptionResolver() {        
   
        Class[] mappedExceptions = {ConfirmCartException.class,
           SelectedShipAddressCompareException.class, 
           ExpiredLoginRequest.class,
           NonCurrentUpdateRequest.class,   
           ReceiptCartNotEmptyException.class,
           PaymentStartedPaymentOnlyException.class, 
           PaymentStartedException.class,
           PaymentTimeException.class
                  
        };
        
        PaymentAccessExceptionResolver resolver =
                new PaymentAccessExceptionResolver();
        
        resolver.setExceptionList(Arrays.asList(mappedExceptions));         
        
        Properties mappedViews = new Properties();
        
        mappedViews.setProperty(ConfirmCartException.class.getCanonicalName(),
                "navigation_error/errNavigation"); 
        
        mappedViews.setProperty(SelectedShipAddressCompareException.class.getCanonicalName(),
                "redirect:/shippingAddress/handleRedirectAddressCompareException"); 
        
        mappedViews.setProperty(NonCurrentUpdateRequest.class.getCanonicalName(),
                "navigation_error/errNavigation");  
        
        mappedViews.setProperty(ExpiredLoginRequest.class.getCanonicalName(), 
                "navigation_error/errNavigation");
        
        mappedViews.setProperty(PaymentTimeException.class.getCanonicalName(),
                "navigation_error/errNavigation");      
        
        mappedViews.setProperty(PaymentStartedPaymentOnlyException.class.getCanonicalName(),
                "navigation_error/errTransactionStartedPaymentOnly");
        
        mappedViews.setProperty(PaymentStartedException.class.getCanonicalName(),
                "navigation_error/errTransactionStarted"); 
        
        mappedViews.setProperty(ReceiptCartNotEmptyException.class.getCanonicalName(),
                "navigation_error/errNavigation"); 
        
        resolver.setExceptionMappings(mappedViews);
        
        resolver.setPaymentAttrs(appBeans.paymentAttributes());
        
        return resolver;
        
    }   
    
    @Bean
    public DataExceptionResolver dataExceptionResolver() {
        
        DataExceptionResolver resolver = new DataExceptionResolver() ;
        resolver.setPaymentAttrs(appBeans.paymentAttributes());
        return resolver;
        
    }
    
    @Bean
    public HttpSessionRequiredExceptionResolver httpSessionRequiredResolver() {
        
        HttpSessionRequiredExceptionResolver resolver = 
                new HttpSessionRequiredExceptionResolver();
        resolver.setPaymentAttrs(appBeans.paymentAttributes());
        
        return resolver;
        
    }
    @Bean
    public MyMethodNotSupportedExceptionResolver myMethodNotSupportedResolver() {
       
        MyMethodNotSupportedExceptionResolver resolver = 
                new MyMethodNotSupportedExceptionResolver();
        
        resolver.setPaymentAttrs(appBeans.paymentAttributes());
        
        return resolver;
        
    }
    
    @Bean
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
        
        return new ExceptionHandlerExceptionResolver();
    }
    
    @Bean
    public ResponseStatusExceptionResolver responseStatusExceptionResolver() {
        
        return new ResponseStatusExceptionResolver();
        
    }
    @Bean
    public MyDefaultHandlerExceptionResolver defaultHandlerExceptionResolver() {
        
        MyDefaultHandlerExceptionResolver resolver = new MyDefaultHandlerExceptionResolver();
        
        resolver.setPaymentAttrs(appBeans.paymentAttributes());
        
        return resolver;        
    }
    
   /* @Bean
    public HandlerExceptionResolverComposite compositeExceptionResolver() {
        
        HandlerExceptionResolverComposite composite = new HandlerExceptionResolverComposite();
        
        composite.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        HandlerExceptionResolver [] resolvers = {
           payPalHttpExceptionResolver(),
           exceptionHandlerExceptionResolver(),
           responseStatusExceptionResolver(),
           defaultHandlerExceptionResolver()
        };
        
        List<HandlerExceptionResolver> list = new ArrayList<>();
        
        list.addAll(Arrays.asList(resolvers));
        
        composite.setExceptionResolvers(list);
        
        return composite;
    } */
        
   
    
}
