/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 *
 * @author Dinah
 */
public class PhoneFormatter implements org.springframework.format.Formatter<String> {
    
    private final String[] formats = {"(###) ###-####", "###-###-####", "###.###.####"};

    @Override
    public String print(String s, Locale locale) {
       
        System.out.println("PhoneFormatter#print: entering print method");
        return MaskUtil.format(s, formats[0]);        
        
    }

    @Override
    public String parse(String string, Locale locale) throws ParseException {     
       System.out.println("PhoneFormatter#parse: entering parse method");
       return MaskUtil.stripEntry(string, formats);
       //return MaskUtil.stripByMask(string, formats[0]);
        
    }
    
}
