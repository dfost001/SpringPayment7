/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 *
 * @author Dinah
 */
public class MyJacksonObjectMapper extends ObjectMapper{
    
    public MyJacksonObjectMapper() {
        
        super();
        addDeserializer();
        super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }
    
    private void addDeserializer() {
        
        SimpleModule mod = new SimpleModule();
	mod.addDeserializer(String.class, new TextFormatDeserializer());
	super.registerModule(mod);
    }
    
}
