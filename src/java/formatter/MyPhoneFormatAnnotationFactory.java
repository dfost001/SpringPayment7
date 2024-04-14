/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.util.HashSet;
import java.util.Set;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

/**
 *
 * @author Dinah
 */
public class MyPhoneFormatAnnotationFactory implements AnnotationFormatterFactory<PhoneFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {

         Set<Class<?>> set = new HashSet<>();
         
         set.add(String.class);
         
         return set;
    }

    @Override
    public Printer<?> getPrinter(PhoneFormat a, Class<?> type) {

         return new PhoneFormatter();        
    }

    @Override
    public Parser<?> getParser(PhoneFormat a, Class<?> type) {
        
        return new PhoneFormatter();        
    }
    
}
