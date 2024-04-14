/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import com.google.gson.FieldNamingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import paypal.PayPalPayment;
import pp_payment.PayPalError;
import pp_payment.Payment;
import pp_payment.RefundResponse;
import httpUtil.*;

/**
 *
 * @author Dinah
 */
@Scope("request")
//@Controller
public class PayPalRefundController {
    
    @Autowired
    private PayPalPayment ppPayment;
    
    @Autowired
    private PayPalExecuteController executeController;
    
    @Autowired
    private ApacheConnectBean2 apache;
    
    private RefundResponse refundResponse;
    
    private final String baseUrl = "https://api.sandbox.paypal.com/v1/payments/sale/";
    
    
     @RequestMapping(value="/cancelPayment", method=RequestMethod.GET)
     public String cancelPayment(ModelMap model) throws HttpException{
        
        byte[] response = doCancel();
        this.evaluateHttpResponse(response);
        model.addAttribute("refundResponse", this.refundResponse);
        model.addAttribute("customerOrder", executeController.getOrder());
        return "cancelPayment";
    }
    
   
    private byte[] doCancel() throws HttpConnectException{
        
        String token = ppPayment.getTokenResponse().getAccessToken();
        Payment completed = executeController.getPaymentResponse();
        String endpoint = baseUrl + 
                 completed.getTransactions().get(0)
                .getRelatedResources().get(0)
                .getSale()
                .getId() + "/refund" ;
        String body = "{}";
        apache.setBearerAuthProp(token);
        apache.setAccept("application/json");
        byte[] response = apache.doConnectPost(endpoint, body.getBytes(),"application/json");
        return response;
        
        
    }
    
    private void evaluateHttpResponse(byte[] response) throws HttpClientException {
        
        ResponseUtil util = new ResponseUtil(apache.getResponseCode(),
                apache.getHeaders(),
                response,
                "CancelPaymentController#evaluateHttpResponse");
        
        refundResponse = (RefundResponse) util.processResponse_JSON(RefundResponse.class, 
                PayPalError.class, FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
       
    }
    
}
