/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restAddressService.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import formatter.annotations.FormatterUtil;
import formatter.annotations.TextFormat;
import java.io.IOException;
import java.lang.reflect.Field;
import restAddressService.addressService.AddrSvcConstants;

/**
 *
 * @author Dinah
 */
public class TextFormatDeserializer extends JsonDeserializer<String>{

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException { 
       
        if(jp.getCurrentToken() == JsonToken.START_OBJECT)
			jp.nextToken();
		
	String fld = jp.getCurrentName();
        String value = jp.getValueAsString();
        String modelFldName = "";
        
       switch(fld){
           
           case AddrSvcConstants.fldStreet :
               modelFldName = "address1";
               break;
           case AddrSvcConstants.fldZip :
               modelFldName = "postalCode";
               break;
               
       }
       
       if(!modelFldName.isEmpty())
         return processFormat(modelFldName, value);
        
       return value;       
        
    }
    
   private String processFormat(String fldName, String fldValue) 
           throws IllegalArgumentException {
       
       System.out.println(this.getClass().getCanonicalName()
         + ": inside processFormat");
       
       Class<?> cls = null;
       
       try {
           cls = Class.forName("model.customer.Address");
       } 
       catch(ClassNotFoundException e) {
           throw new IllegalArgumentException(this.getClass().getName()
            + "#processFormat:Class.forName threw reflection error", e);
       }
       
       Field[] fields = cls.getDeclaredFields();
       
       Field annotated = null;
       
       for(Field f : fields)
           if(f.getName().equals(fldName)) {
               annotated = f;
               break;
           }
       
       if(annotated == null)
            throw new IllegalArgumentException(this.getClass().getName() 
              + "#processFormat:annotated model field not found");
       
       TextFormat a = annotated.getAnnotation(TextFormat.class);
       
       System.out.println(this.getClass().getCanonicalName()
         + ": got annotated field: " + a.getClass().getCanonicalName());
       
       TextFormat.Format [] formats = a.value();
       
       String value="";
     
       value = new FormatterUtil(formats).format(fldValue);
       
       return value;
   }
    
}
