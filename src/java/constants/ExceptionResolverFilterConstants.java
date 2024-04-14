/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import com.dvd.ShippingAddressController;
import view.attributes.ConstantUtil;

/**
 *
 * @author dinah
 */
public interface ExceptionResolverFilterConstants {
    
    public static final String BINDING_RESULT = ConstantUtil.CUST_BINDINGRESULT_KEY ;
    public static final String CUSTOMER = ConstantUtil.CUSTOMER_SESSION_KEY ;
    public static final String SELECTED_SHIP_ADDRESS =
            ShippingAddressController.SELECTED_POSTALADDRESS ;
   // public static final String CUSTOMER_ID_REQUIRED = "inputCustId";
    
}
