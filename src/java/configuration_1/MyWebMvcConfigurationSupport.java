/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration_1;

import formatter.MyPhoneFormatAnnotationFactory;
import formatter.annotations.MyTextFormatAnnotationFactory;
import formatter.conversion.SelectShipActionEnumConverter;
import interceptors.NonCurrentUpdateInterceptor;
//import interceptors.PayPalCancelInterceptor;
import interceptors.PayPalPaymentUrlInterceptor;
import interceptors.UpdateNotSupportedInterceptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.validation.Validator;

/**
 *
 * @author dinah
 */
@Configuration
@ComponentScan(basePackages={"com.dvd", 
    "com.cart","dao","restCustomerService",
    "restAddressService","restCityState", "paypal","validation",
    "validation.constraints","exception_handler","view.attributes",
    "intercetors", "servletContext","httpUtil"})
public class MyWebMvcConfigurationSupport extends WebMvcConfigurationSupport {
    
    @Autowired
    private ViewResolutionBeans viewBeans;
    
    @Autowired private ExceptionResolverConfig exceptionConfig;
    
    @Autowired private ApplicationBeans appBeans;   
    
    @Bean
    @Override
    public ViewResolver mvcViewResolver() {
        
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        
        viewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        viewResolver.setContentNegotiationManager(super.mvcContentNegotiationManager());        
        
        List<ViewResolver> resolverList = new ArrayList<>();
        
        resolverList.add(viewBeans.tilesViewResolver());
        
        resolverList.add(viewBeans.jspViewResolver());
        
        List<View> vwlist = new ArrayList<>();
        
        vwlist.add(viewBeans.jacksonView());
        
        viewResolver.setViewResolvers(resolverList);
        
        viewResolver.setDefaultViews(vwlist);
        
        return viewResolver;
        
    } 
    /*
     * Called into at @Bean mvcViewResolver
     * Sets contentNegotiationManager
     */
   /* @Override
    protected void configureViewResolvers(ViewResolverRegistry reg) {
        
        reg.tiles();
        reg.jsp("/WEB-INF/jsp/", ".jsp");
        reg.enableContentNegotiation(true, viewBeans.jacksonView());
        
    }*/   
    
     @Override
     /* Default Media Types are added by super ConfigurationSupport
      * config.mediaType("json", MediaType.APPLICATION_JSON); 
      * config.mediaType("xml", MediaType.APPLICATION_XML);     
      */
    protected void configureContentNegotiation(ContentNegotiationConfigurer config) {
        
        config.ignoreAcceptHeader(false);
        
        config.favorParameter(true); //Favor an Url query-parameter for a REST request
        
        config.parameterName("media"); 
        
        config.favorPathExtension(false); //Does not use suffixes set to a default media-type                      
    }
    /*
     * TrailingSlash deprecated in 6.0 (Default changed from true to false)
     * SuffixPatternMatch removed in 6.0 (Extensions are not mapped)
     */
    @Override
    protected void configurePathMatch(PathMatchConfigurer config) {
        
        config.setPathMatcher(pathMatcher()); //Mathches attribute value to URL
        config.setUrlPathHelper(pathHelper());
        config.setUseRegisteredSuffixPatternMatch(Boolean.TRUE); //Only match against default media-types
        config.setUseSuffixPatternMatch(Boolean.TRUE); //Normally set to false; if true, discards the suffix
        config.setUseTrailingSlashMatch(Boolean.TRUE); //Let a method mapped to "/users" 
                                                       //also match to "/users/"
        
    }
    /*
     * Properties set on RequestMappingHandlerMapping
     */
    @Bean
    public UrlPathHelper pathHelper() {
        
        UrlPathHelper helper = new UrlPathHelper() ;
        
        helper.setAlwaysUseFullPath(false); // Whether to include the servlet's URL pattern
        
        helper.setUrlDecode(true); // If the QueryString contains encoded spaces etc.
            
        return helper;
        
    }
    /*
     * Tests whether a request URL matches a wild-card pattern
     */
    @Bean
    public AntPathMatcher pathMatcher() {
        return new AntPathMatcher();
    }
    
    @Override
    protected  void addResourceHandlers(ResourceHandlerRegistry reg) {
        
        reg.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/")
                .setCachePeriod(60);          
    }
    
     @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {
        
         HandlerExceptionResolver [] customResolvers = {
           exceptionConfig.payPalHttpExceptionResolver(),
           exceptionConfig.payPalStateExceptionResolver(),
           exceptionConfig.dataExceptionResolver(),
           exceptionConfig.httpSessionRequiredResolver(),
           exceptionConfig.myMethodNotSupportedResolver(),
           exceptionConfig.paymentAccessExceptionResolver()};
         
         HandlerExceptionResolver[] defaultResolvers = {
           exceptionConfig.exceptionHandlerExceptionResolver(),
           exceptionConfig.responseStatusExceptionResolver(),
           exceptionConfig.defaultHandlerExceptionResolver()
        };
         
         list.addAll(Arrays.asList(customResolvers));
         
         list.addAll(Arrays.asList(defaultResolvers));
        
    }
   
    
    @Override
    protected void addFormatters(FormatterRegistry reg) {
        
        reg.addFormatterForFieldAnnotation(new MyTextFormatAnnotationFactory());
        
        reg.addFormatterForFieldAnnotation(new MyPhoneFormatAnnotationFactory());
        
        reg.addConverter(new SelectShipActionEnumConverter());
        
    }  
    
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setWriteAcceptCharset(false);
        
        messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new SourceHttpMessageConverter<>());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
                messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        
        MappingJackson2HttpMessageConverter converter = 
                new MappingJackson2HttpMessageConverter();
        
        converter.setObjectMapper(appBeans.objectMapper());
        
        messageConverters.add(converter);       
    }    
   
    @Override
    public void addInterceptors(InterceptorRegistry reg) {     
         
        addUpdateNotSupportedInterceptor(reg);
        addPaymentTimeInterceptor(reg);
        addNonCurrentUpdateInterceptor(reg);
        //addPayPalCancelUrlInterceptor(reg);
        
    }
    
     /*
     * "/paypalExecute" not included to decouple the view
     */
    private void addPaymentTimeInterceptor(InterceptorRegistry reg) {
        
        String[] includePaths = {
            "/approvePayPal",
            "/paypalAuthorizeRedirect",           
            "/paypalReceiptRedirect"};
        
        PayPalPaymentUrlInterceptor interceptor =
                new PayPalPaymentUrlInterceptor();
        
        interceptor.setPaymentAttrs(appBeans.paymentAttributes());
        
        InterceptorRegistration registration = reg.addInterceptor(interceptor);
        
        registration.addPathPatterns(includePaths);        
        
    }
    
    private void addUpdateNotSupportedInterceptor(InterceptorRegistry reg) {
        
        String includePath = "/**";
        
        String[] excludePaths = { "/customerSupport",           
           "/resources/**",
           "/cancelPayPal",          
           "/paypal/execute",
           "/paypal/cancelAuthorize",
           "/approvePayPal",
           "/paypalAuthorizeRedirect",
           "/paypalReceiptRedirect"};
        
        UpdateNotSupportedInterceptor interceptor = new UpdateNotSupportedInterceptor();
        
        interceptor.setPaymentAttrs(appBeans.paymentAttributes());
        
        InterceptorRegistration registration = reg.addInterceptor(interceptor);
        
        registration.addPathPatterns(includePath);
        
        registration.excludePathPatterns(excludePaths);
        
    }  
    
    private void addNonCurrentUpdateInterceptor(InterceptorRegistry reg) {
        
        String[] includePaths = {"/updateCustomer", "/updateShipAddress/submit"};
        
        NonCurrentUpdateInterceptor interceptor = new NonCurrentUpdateInterceptor();
        
        InterceptorRegistration registration = reg.addInterceptor(interceptor);
        
        registration.addPathPatterns(includePaths);  
    }
    
    @Bean
    @Override
    public Validator mvcValidator() {
        return super.mvcValidator();
    }
    
    /* private void addPayPalCancelUrlInterceptor(InterceptorRegistry reg) {
        
        String includePath = "/cancelPayPal";
        
        PayPalCancelInterceptor interceptor = new PayPalCancelInterceptor();
        
        interceptor.setPaymentAttrs(appBeans.paymentAttributes());
        
        InterceptorRegistration registration = reg.addInterceptor(interceptor);
        
        registration.addPathPatterns(includePath);
    } */
    
}
