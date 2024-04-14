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
 * Not used: not tested
 */
public class MyJacksonObjectMapperFactory {
    
    private final ObjectMapper mapper;
    
    public MyJacksonObjectMapperFactory(){
        
        mapper = new ObjectMapper();
        
        SimpleModule mod = new SimpleModule();
	mod.addDeserializer(String.class, new TextFormatDeserializer());
	mapper.registerModule(mod);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        
    }
    
    public ObjectMapper getObjectMapper() {
        return mapper;
    }
    
}
