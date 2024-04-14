/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author Dinah
 * To do: Code an evaluation of an escaped mask char
 * To do: Add regular expression parameter to a validate method 
 * 
 */

public class MaskUtil {
    
    private static final String Space = Character.valueOf((char)32).toString();
    
    private static final String[] PhoneChars = {"(", ")", ".", "-", Space };
    
    /*
     * Variable char substitutes
     */
    private static final String DIGIT = "#";
    private static final String ALPHA = "*";
    private static final String ALNUM = "^"; //digit or letter
    
    public static String stripPhone(String entry) {
        
        List<String> formatting = Arrays.asList(PhoneChars);
       
        String edited = "";
        
        String c;
        
        if(entry == null || entry.trim().isEmpty())
                return "";
        for(int i=0; i<entry.length(); i++) {
            c = entry.substring(i, i+1);
            if(!formatting.contains(c))
                edited += c;
        }
        return edited;
    }
    /*
     * To do: Strip number in form of (+1)(###) ###-####
     */
    public static String stripEntry(String entry, String...formats){
        
        String edited = "";
        
        if(entry == null || entry.trim().isEmpty())
                return "";
        
        entry = entry.trim();
        
        if(entry.substring(0,2).equals("+1"))  
            entry = entry.substring(2);
        
        List<String>symbols = new ArrayList<>();
        
        for(int i=0; i < formats.length; i++) {
            addFormatChars(formats[i],symbols);
        }
        symbols.add("_");
        
        if(!symbols.contains(Space))
        	symbols.add(Space);
        for(int i=0; i<entry.length(); i++) {
            String c = entry.substring(i, i+1);
            if(!symbols.contains(c))
                edited += c;
        }
        return edited; 
    }
    /*
     * Used if there is only one required input format, then depending on the
     * the field, such as phone, validation should be done during formatting.
     */
    public static String stripByMask(String entry, String format){
        throw new UnsupportedOperationException("MaskUtil#stripByMask not yet implemented");
      /*  String edited="";
        if(entry == null || entry.isEmpty())
            return "";
        for(int i=0; i < format.length(); i++){
            if(i == entry.length())
                return edited;
            else if(isVariableChar(format.substring(i, i+1)))
                edited += entry.charAt(i);
        }
        return edited;*/
    }
    /*
     * Method to be used if field is stored with formatting or if a
     * field has an inherent mask, such as an account number.
     */
    public static String validateByMask(String mask, String entry){
       
        throw new UnsupportedOperationException("MaskUtil#validateByMask not yet implemented");
        
    }
    /*
     * To do: validate in a loop for each format, exit if valid found 
     */
    public static String validateByContent(String entry, String... formats){ 	
        
        
        if(entry == null || entry.isEmpty())
          
            return "";  //composite annotation with @Size        
        
        
        int size = stripMaskLiterals(formats[0]).length();        
        	
        String message = compareToFormat(entry, formats[0], size);       
       
        return message;
    }
    /*
     * Assumes entry stripped with corresponding method
     */
    public static String format(String entry, String format) {
        
        System.out.println("Entering MaskUtil#format");
        
        if(entry == null)
            entry = "";
        entry = entry.trim();
        
        StringBuffer sb = new StringBuffer();
      
        int eidx = 0;
        for(int i=0; i < format.length(); i++) {
            if(isVariableChar(format.substring(i,i+1))){
            	if(eidx == entry.length()) {
            			sb.append("_");
                }
            	else {
            	    sb.append(entry.charAt(eidx));
                    eidx++;
            	}
            }             
            else {
                sb.append(format.charAt(i));                                       
            }
        } //end for
        
        if(entry.length() > stripMaskLiterals(format).length())
        	for(int i = eidx; i < entry.length(); i++)
        		sb.append(entry.charAt(i));
        return sb.toString();
    }
    
    private static boolean isVariableChar(String c) {
        if(DIGIT.equals(c) || ALPHA.equals(c) || ALNUM.equals(c))
            return true;
        return false;
    }

    private static String stripMaskLiterals(String mask) {
    	
    	String stripped = "";
    	
    	for(int i=0; i < mask.length(); i++){
    		if(isVariableChar(mask.substring(i, i+1)))
    			stripped += mask.charAt(i);
    	}
    	return stripped;
    }
    
    private static void addFormatChars(String format, List<String> symbols) {       
        
        for(int i=0; i < format.length(); i++) {
            String c = format.substring(i, i+1);
            if(DIGIT.equals(c) || ALPHA.equals(c) || ALNUM.equals(c))
                continue;
            symbols.add(c);
        }
        
    }
    
    private static boolean charValid(String fchar, String echar){
        
        boolean valid = false;
        int error = 0;
        
        Character c = Character.valueOf(echar.charAt(0));
        switch(fchar) {
        case DIGIT:
            if(Character.isDigit(c))
                valid = true;
            break;
        case ALPHA:
            if(Character.isAlphabetic(c))
                valid = true;
            break;
        case ALNUM:
            if(Character.isDigit(c) || Character.isAlphabetic(c))
                valid = true;
            break;
        default:
           error = 1;
        }      
        if(error == 1)
             throw new IllegalArgumentException("MaskUtil"
               + "#charValid:unknown formatting char in mask.");
        return valid;
    }
    
    private static String compareToFormat(String stripped, String format, int size){
        
        int eidx = 0;
        int fidx = 0;
        
        
        String message = "";
        
        while(fidx < format.length()) {
            if(!isVariableChar(format.substring(fidx,fidx+1))){
                    fidx++;
                    continue;
            }
            if(eidx == stripped.length()){ //if format still has length, and entry does not               
                message = "Entry does not have required length of " + size;
                break;
            }
            if(charValid(format.substring(fidx, fidx+1), stripped.substring(eidx, eidx+1))){
                eidx++;
                fidx++;
            }
            else {
                
                message = "Entry contains an invalid character at position " + (fidx + 1); 
                        
                break;
            }
                
        }
        if(message.isEmpty() && stripped.length() > size)
            message = "Your entry is too long. Required size is " + size ;
        return message;
    }
    
}
