/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interceptors;


import exceptions.NonCurrentUpdateRequest;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.customer.Customer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import validation.CompareAddressUtil2;
import view.attributes.ConstantUtil;
import view.attributes.CustomerAttributes;

/**
 *
 * @author dinah
 */
public class NonCurrentUpdateInterceptor extends HandlerInterceptorAdapter {   
    
    private final String ADDRESS_TIME = "addressTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        System.out.println("NonCurrentUpdateInterceptor executing: " + request.getServletPath()) ;
        
        return evalExpiredFormTime(request);      
       
         
       // return evalChangedId(request);
    }
    
    private boolean evalExpiredFormTime(HttpServletRequest request)
        throws NonCurrentUpdateRequest {
        
        CustomerAttributes customerAttrs = (CustomerAttributes)
                request.getSession()
                .getAttribute("customerAttributes");    
        
        if(customerAttrs == null)
             throw new IllegalArgumentException("NonCurrentUpdateInterceptor#prehandle: "
                 +  "CustomerAttributes session-scoped component is null"); 
        
        Long currentTime = customerAttrs.getAddressUpdateTime();
        
        String formTime = request.getParameter(ADDRESS_TIME);
        
        System.out.println("NonCurrentUpdateInterceptor#evalExpiredFormTime:" 
                + currentTime + ": " + formTime);
        
        if(formTime == null || formTime.isEmpty())
           throw new IllegalArgumentException("NonCurrentUpdateInterceptor#prehandle: "
                 +  "Missing request parameter 'addressTime'");          
        
        boolean isEqual = new CompareAddressUtil2().isCurrentFormTime(formTime, currentTime,
                this.getClass().getName());
        
         if(!isEqual)
            throw new NonCurrentUpdateRequest();
        
        customerAttrs.setAddressUpdateTime();  
       
        
        return true;
        
        
    }
    
    private boolean evalChangedId(HttpServletRequest request) throws NonCurrentUpdateRequest {
        
         Customer current = (Customer)request.getSession().getAttribute(ConstantUtil.CUSTOMER_SESSION_KEY);
         
         if(current == null) //Data binding or handler will evaluate MissingHttpSessionParameter
             
             return true;   
         
         evalParameter(request); //throw missing request parameter
         
         String paramId = request.getParameter(ConstantUtil.COMPARISON_CUST_ID); 
         
         if(current.getCustomerId() == null) //insert    
             
            if(paramId == null || paramId.trim().isEmpty())                
                   return true;
            else throw new NonCurrentUpdateRequest();
         
         else  if(paramId == null || paramId.trim().isEmpty()) 
             
            throw new NonCurrentUpdateRequest();
         else {       
             
             Short compId = Short.parseShort(paramId);
             if(!compId.equals(current.getCustomerId()))             
                 throw new NonCurrentUpdateRequest();
         }    
         return true;        
    }

   private void evalParameter(HttpServletRequest request){
       
       Map<String,String[]> parameters = request.getParameterMap();
       
       if(!parameters.containsKey(ConstantUtil.COMPARISON_CUST_ID))
             throw new IllegalArgumentException(this.getClass().getCanonicalName()
                + "#prehandle: Missing request parameter ConstantUtil.COMPARISON_CUST_ID");       
    }      
} //end interceptor
