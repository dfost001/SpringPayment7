/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import view.attributes.PaymentAttributes;
import view.attributes.PaymentAttributes.TransactionState;

/**
 *
 * @author Dinah
 */
@Controller
@Scope("request")
public class CustomerSupportController {
    
    @Autowired
    private PaymentAttributes paymentAttrs;
    
    private final String paymentMessage = "Your current transaction has been cancelled. Your cart content remains unchanged." ;
    
    private final String supportMessage = "You may complete your current order by contacting support.";
    
    private Boolean paymentStarted;

    public String getPaymentMessage() {
        return paymentMessage;
    }

    public String getSupportMessage() {
        return supportMessage;
    }

    public Boolean getPaymentStarted() {
        return paymentStarted;
    }
    
    
    @RequestMapping(value="/customerSupport", method=RequestMethod.GET)
    public String doSupport(HttpServletRequest request,ModelMap map) {
        
        TransactionState transState = paymentAttrs.evalTransactionState(request);
        
        if(!TransactionState.NONE.equals(transState) ) {
            paymentStarted = true;
            paymentAttrs.updateTimeResetOnSupportEntrance();
        }
        
        map.addAttribute("customerSupportController", this);
        
        return "customerSupport";
    }
    
}
