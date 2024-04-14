/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.conversion;

import org.springframework.core.convert.converter.Converter;
import com.dvd.ShippingAddressController.SelectShipAction;
import error_util.EhrLogger;

/**
 *
 * @author dinah
 */
public class SelectShipActionEnumConverter implements Converter<String, SelectShipAction> {

    @Override
    public SelectShipAction convert(String action) {
        
       SelectShipAction actionType = null; 
        
       try {
           
           actionType = Enum.valueOf(SelectShipAction.class, action.trim());
           
       } catch (IllegalArgumentException e) {
           
           String message = EhrLogger.doError(this.getClass().getCanonicalName(), 
                   "convert", "ShipAddressController#doSelectAction method parameter cannot be converted.");
           
           throw new IllegalArgumentException(message, e);
       }
       
       return actionType;
        
    }
    
}
