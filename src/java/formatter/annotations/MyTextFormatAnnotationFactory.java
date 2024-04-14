/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter.annotations;


import formatter.annotations.TextFormat.Format;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

/**
 *
 * @author Dinah
 */
public class MyTextFormatAnnotationFactory implements AnnotationFormatterFactory <TextFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
       
        List<Class<?>> list = Arrays.asList(String.class);
        
        HashSet<Class<?>> set = new HashSet<>(list);
        
        return set;
    }

    @Override
    public Printer<?> getPrinter(TextFormat a, Class<?> type) {
        
        Format[] formats = a.value();
       
        return new TextFormatter(formats);
    }

    @Override
    public Parser<?> getParser(TextFormat a, Class<?> type) {
        
        Format[] formats = a.value(); //Compile time error for null attribute
       
        return new TextFormatter(formats);
        
    }
    
}
