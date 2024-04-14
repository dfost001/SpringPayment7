/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import error_util.EhrLogger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.customer.AddressTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Note: All parameters are set with default values, so no null pointers.
 * Fix: xhrStatus == 0 to return error view with link
 * 
 * Currently, Recoverable URL's are hard-coded into JSP
 * Revise: To set recoverable link into the model instead of the boolean
 * 
 * Revise: To notify user if only messages param has a value. Indicates an
 * error on the upload or error model not serialized. (Probably the same as xhrStatus == 0)
 */
@Controller
public class AjaxErrorController {
    
    @RequestMapping(value="handleAjaxError", method=RequestMethod.POST)
    public String handleAjaxError(@RequestParam("status") int status, 
            @RequestParam("url") String url,
            @RequestParam("trace") String trace,
            @RequestParam("recoverable") Boolean recoverable,
            @RequestParam("messages") String messages,
            @RequestParam("exceptionName") String exceptionName,
            @RequestParam("xhrStatus") int xhrStatus,
            @RequestParam("errAddressType") String addressType,
            ModelMap model) {
        
        debugPrintParameter(recoverable);
        
        String view = "error/ajaxError";  
        
        String message = "";

        if (xhrStatus == 0) { //Need to revise to notify end-user
            
            if(AddressTypeEnum.Customer.name().equals(addressType))
               view = "forward:/customerRequest"; //browser did not connect
            else view = "redirect:/shippingAddress/showSelect";
            
        } else if(xhrStatus == 406) { 
            
            message = "<p style='font-size:12pt'>(Status=406)</p>" ;   
            
        } else if(exceptionName.toLowerCase().contains("data")){ //See exception_handler.DataExceptionResolver
            
            evalRecoverableDataException(exceptionName,  model, addressType);            
            
        } else if(exceptionName.toLowerCase().contains("http")) { //See restAddressService.AddressServiceAdvice
            
            evalRecoverableHttpException(exceptionName, model,
                  recoverable, status, addressType);      
            
        }               
         message = "An application error has occurred. "
                 + "Please contact support to complete your order."
                 + message;
         
         if(!model.containsKey("message")) {
             model.addAttribute("message" , message);       
         }   
            model.addAttribute("status", status);
            model.addAttribute("url", url);
            model.addAttribute("trace", trace);
            model.addAttribute("messages", messages);
            model.addAttribute("exceptionName", exceptionName);
            model.addAttribute("xhrStatus", xhrStatus);
      

        return view;
    } 
    
    private void debugPrintParameter(Boolean recoverable) {
        String msg = EhrLogger.doError(this.getClass().getName(), "debugPrintParameter", 
                "recoverable=" + recoverable);
        System.out.println(msg);
    }
    
    /*
     * Can be refined to add recoverable for temporary DataAccessException
    */
    private void evalRecoverableDataException(String exceptionName, ModelMap model,
              String addressType) {            
       
        if (exceptionName.contains("RecoverableDataAccessException")
                || exceptionName.contains("TransientDataAccessException")) {
            
            String message = "Form initialization from database may be recoverable.";
            
            model.addAttribute("recoverable", true);
            this.evalAddressType(model, addressType);            
            
       
            model.addAttribute("message", message);
        }    
    }
    
    private void evalRecoverableHttpException(String exceptionName, ModelMap model,
            boolean recoverable, int httpStatus, String addressType) {      
        
        boolean recoverableHttpStatus = httpStatus >= 500;
        
         if( (exceptionName.contains("HttpConnectException") && recoverable)
                || recoverableHttpStatus) {           
                
                String message = "Address processing error may be recoverable. ";
                 
                 model.addAttribute("recoverable", true);
                 
                 this.evalAddressType(model, addressType);        
         
                 model.addAttribute("message", message);
         
         }
    }
    
    /* AddressType enum is coded as a path variable by JavaScript on requests to
     * AddressControllerRest. ajaxError.jsp will use model attribute to
     * to construct the customer or shipAddress request.
     */
    private void evalAddressType(ModelMap model, String addressType) {
        
        if(AddressTypeEnum.Customer.name().equals(addressType)
                || AddressTypeEnum.ShipAddress.name().equals(addressType))       
        
             model.addAttribute("addressType", addressType);
        
        else throw new IllegalArgumentException(
                EhrLogger.doError(this.getClass().getCanonicalName(), 
                "initReturnUrl", "addressType not found in model.customer.AddressTypeEnum"));
       
        
    }    
      /*
     * Exception thrown up to servlet container. Because returning a ModelAndView
     * converted to JSON from an ExceptionResolver will cause Spring to throw another error
     * see web.xml
     * <error-page>
     *   <error-code>406</error-code>
     *   <location>/ajaxErrorController/handleNotAcceptable</location>
     * </error-page> 
     */
 /*   @RequestMapping(value="/ajaxErrorController/handleNotAcceptable", method=RequestMethod.GET, produces="text/plain")
    public ResponseEntity<String> handleNotAcceptableType(HttpServletRequest request, 
            HttpServletResponse response) {
        
       //String message = (String) request.getAttribute("javax.servlet.error.message");
       
       //System.out.println("AjaxErrorController#handleNotAcceptable executing " + message); //null
       
       String message = "Please set Accept request header to application/json." ;     
        
        HttpHeaders headers = new HttpHeaders();           
        
        headers.setContentType(MediaType.TEXT_PLAIN);
        
        headers.setContentLength(message.length());
        
        ResponseEntity<String> entity = new ResponseEntity<>(message, headers, HttpStatus.NOT_ACCEPTABLE);
        
        return entity;
        
    } */    
    
   /*
    * Serialize as Html since Acceptable type is not supported
    */ 
   @RequestMapping(value="/ajaxErrorController/handleNotAcceptable", method=RequestMethod.GET)
    public String handleNotAcceptableType(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
        
        map.addAttribute("message", "Please set Media parameter or Accept header to application/json.");
        
        response.setStatus(406);
        
        response.setContentType("text/html");
        
        return "error/mediaNotSupported";
    }
    
    
}//end class
